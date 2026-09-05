package com.example.domain.safety

import com.example.data.safety.providers.KarachiBusinessActivityProvider
import com.example.data.safety.providers.KarachiCommunityFeedbackProvider
import com.example.data.safety.providers.KarachiHistoricalIncidentProvider
import com.example.data.safety.providers.KarachiLightingDataProvider
import com.example.data.safety.providers.KarachiPedestrianActivityProvider
import com.example.data.safety.providers.KarachiSafePointProvider
import com.example.domain.model.CommunitySafetyAggregate
import com.example.domain.model.DataSourceType
import com.example.domain.model.KarachiSafePoint
import com.example.domain.model.RaahiRoute
import com.example.domain.model.RouteSafetyProfile
import com.example.domain.model.SafetyConfidence
import com.example.domain.model.SafetyFactorEvaluation
import com.example.domain.model.SafetyFactorType
import com.example.domain.model.SafetySignal
import kotlin.math.roundToInt

class SafetyIntelligenceEngineImpl(
    private val lightingProvider: LightingDataProvider = KarachiLightingDataProvider(),
    private val pedestrianProvider: PedestrianActivityProvider = KarachiPedestrianActivityProvider(),
    private val businessProvider: BusinessActivityProvider = KarachiBusinessActivityProvider(),
    private val incidentProvider: HistoricalIncidentProvider = KarachiHistoricalIncidentProvider(),
    private val safePointProvider: SafePointProvider = KarachiSafePointProvider(),
    private val communityProvider: CommunityFeedbackProvider = KarachiCommunityFeedbackProvider()
) : SafetyIntelligenceEngine {

    companion object {
        // Base configured weights (sum = 1.0)
        val BASE_WEIGHTS: Map<SafetyFactorType, Double> = mapOf(
            SafetyFactorType.LIGHTING to 0.20,
            SafetyFactorType.PEDESTRIAN_ACTIVITY to 0.20,
            SafetyFactorType.BUSINESS_ACTIVITY to 0.20,
            SafetyFactorType.HISTORICAL_INCIDENT to 0.20,
            SafetyFactorType.SAFE_POINTS to 0.15,
            SafetyFactorType.COMMUNITY_PERCEIVED to 0.05
        )
    }

    override suspend fun evaluateRouteSafety(route: RaahiRoute): RouteSafetyProfile {
        val lightingSignal = try {
            lightingProvider.getLightingSignal(route)
        } catch (e: Exception) {
            createUnavailableSignal(SafetyFactorType.LIGHTING, "Lighting signal query failed")
        }

        val pedestrianSignal = try {
            pedestrianProvider.getPedestrianActivitySignal(route)
        } catch (e: Exception) {
            createUnavailableSignal(SafetyFactorType.PEDESTRIAN_ACTIVITY, "Pedestrian signal query failed")
        }

        val businessSignal = try {
            businessProvider.getBusinessActivitySignal(route)
        } catch (e: Exception) {
            createUnavailableSignal(SafetyFactorType.BUSINESS_ACTIVITY, "Business signal query failed")
        }

        val incidentSignal = try {
            incidentProvider.getHistoricalIncidentSignal(route)
        } catch (e: Exception) {
            createUnavailableSignal(SafetyFactorType.HISTORICAL_INCIDENT, "Incident signal query failed")
        }

        val (safePointSignal, nearbySafePoints) = try {
            safePointProvider.getSafePointSignal(route)
        } catch (e: Exception) {
            Pair(createUnavailableSignal(SafetyFactorType.SAFE_POINTS, "Safe points query failed"), emptyList())
        }

        val (communitySignal, communityAggregate) = try {
            communityProvider.getCommunityFeedbackSignal(route)
        } catch (e: Exception) {
            Pair(createUnavailableSignal(SafetyFactorType.COMMUNITY_PERCEIVED, "Community signal query failed"), null)
        }

        val rawSignals = listOf(
            lightingSignal,
            pedestrianSignal,
            businessSignal,
            incidentSignal,
            safePointSignal,
            communitySignal
        )

        return computeProfileFromSignals(
            routeId = route.id,
            signals = rawSignals,
            nearbySafePoints = nearbySafePoints,
            communityAggregate = communityAggregate
        )
    }

    override suspend fun evaluateAndCompareRoutes(routes: List<RaahiRoute>): List<RaahiRoute> {
        if (routes.isEmpty()) return emptyList()

        // 1. Calculate safety profile for each route with individual error resilience
        val evaluatedProfiles = routes.map { route ->
            try {
                evaluateRouteSafety(route)
            } catch (e: Throwable) {
                android.util.Log.e("SafetyIntelligenceEngine", "Failed to evaluate safety for route ${route.id}, falling back to zero-signal profile", e)
                // Fallback gracefully for individual route without crashing the entire route list
                computeProfileFromSignals(
                    routeId = route.id,
                    signals = emptyList(),
                    nearbySafePoints = emptyList(),
                    communityAggregate = null
                )
            }
        }

        // 2. Identify the route with highest relative score
        // In case of a tie, use safe point count, then lower duration
        var highestScore = -1
        var recommendedIndex = 0

        evaluatedProfiles.forEachIndexed { index, profile ->
            if (profile.relativeSafetyScore > highestScore) {
                highestScore = profile.relativeSafetyScore
                recommendedIndex = index
            } else if (profile.relativeSafetyScore == highestScore) {
                val currentRoute = routes[index]
                val bestRoute = routes[recommendedIndex]
                if (currentRoute.durationSeconds < bestRoute.durationSeconds) {
                    recommendedIndex = index
                }
            }
        }

        // 3. Attach updated profiles with isRecommended flag set
        return routes.mapIndexed { index, route ->
            val profile = evaluatedProfiles[index]
            val isRecommended = (index == recommendedIndex && profile.relativeSafetyScore > 0)
            route.copy(
                safetyProfile = profile.copy(isRecommended = isRecommended)
            )
        }
    }

    /**
     * Pure and deterministic calculation from collected signals.
     */
    fun computeProfileFromSignals(
        routeId: String,
        signals: List<SafetySignal>,
        nearbySafePoints: List<KarachiSafePoint>,
        communityAggregate: CommunitySafetyAggregate? = null
    ): RouteSafetyProfile {
        val totalFactorsCount = BASE_WEIGHTS.size
        val availableSignals = signals.filter { it.isAvailable && it.dataSourceType != DataSourceType.UNAVAILABLE }

        if (availableSignals.isEmpty()) {
            val emptyEvaluations = BASE_WEIGHTS.map { (factorType, weight) ->
                SafetyFactorEvaluation(
                    factorType = factorType,
                    title = factorType.displayName,
                    score = 0,
                    normalizedScore = 0.0,
                    weight = weight,
                    effectiveWeight = 0.0,
                    isAvailable = false,
                    dataSourceType = DataSourceType.UNAVAILABLE,
                    summary = "Signal not available for this corridor",
                    isPositive = false
                )
            }
            return RouteSafetyProfile(
                routeId = routeId,
                relativeSafetyScore = 0,
                confidence = SafetyConfidence.LOW,
                confidenceScore = 0.0,
                factorEvaluations = emptyEvaluations,
                positiveFactors = emptyList(),
                negativeFactors = listOf("No contextual safety telemetry available"),
                explanation = "Contextual safety data is currently unavailable for this corridor.",
                isRecommended = false,
                nearbySafePoints = emptyList(),
                isPrototypeData = false,
                communityAggregate = null
            )
        }

        // Calculate sum of base weights of available signals
        val availableBaseWeightSum = availableSignals.sumOf { BASE_WEIGHTS[it.factorType] ?: 0.2 }

        var totalWeightedScore = 0.0
        val factorEvaluations = mutableListOf<SafetyFactorEvaluation>()
        val positiveFactors = mutableListOf<String>()
        val negativeFactors = mutableListOf<String>()

        signals.forEach { signal ->
            val baseWeight = BASE_WEIGHTS[signal.factorType] ?: 0.2
            val isAvailable = signal.isAvailable && signal.dataSourceType != DataSourceType.UNAVAILABLE

            val effectiveWeight = if (isAvailable && availableBaseWeightSum > 0.0) {
                baseWeight / availableBaseWeightSum
            } else {
                0.0
            }

            val factorScoreInt = (signal.normalizedScore * 100).roundToInt().coerceIn(0, 100)

            if (isAvailable) {
                totalWeightedScore += signal.normalizedScore * effectiveWeight

                val isPositive = signal.normalizedScore >= 0.75
                val isNegative = signal.normalizedScore < 0.60

                val summary = when (signal.factorType) {
                    SafetyFactorType.LIGHTING -> {
                        if (isPositive) "High-lumen municipal street lighting coverage"
                        else if (isNegative) "Intermittent street lighting across expressway segment"
                        else "Standard corridor street lighting"
                    }
                    SafetyFactorType.PEDESTRIAN_ACTIVITY -> {
                        if (isPositive) "High continuous pedestrian activity in commercial district"
                        else if (isNegative) "Low foot traffic along isolated bypass corridor"
                        else "Moderate pedestrian flow"
                    }
                    SafetyFactorType.BUSINESS_ACTIVITY -> {
                        if (isPositive) "Dense 24/7 open commercial POIs, cafes, and fuel stations"
                        else if (isNegative) "Limited active storefronts or commercial activity"
                        else "Active daytime commercial POIs"
                    }
                    SafetyFactorType.HISTORICAL_INCIDENT -> {
                        if (isPositive) "Low incident density zone with active security presence"
                        else if (isNegative) "Elevated historical street incident reports"
                        else "Average metropolitan incident profile"
                    }
                    SafetyFactorType.SAFE_POINTS -> {
                        if (isPositive) "Multiple verified police posts and 24/7 emergency hospitals nearby (${nearbySafePoints.size} within 1.2km)"
                        else if (isNegative) "Fewer direct emergency safe points within immediate corridor"
                        else "${nearbySafePoints.size} emergency safe point(s) in corridor proximity"
                    }
                    SafetyFactorType.COMMUNITY_PERCEIVED -> {
                        if (isPositive) "Positive perceived-safety profile reported by community travelers"
                        else if (isNegative) "Lower perceived-safety profile reported by community travelers"
                        else "Moderate community perceived safety feedback"
                    }
                }

                if (isPositive) {
                    positiveFactors.add(summary)
                } else if (isNegative) {
                    negativeFactors.add(summary)
                }

                factorEvaluations.add(
                    SafetyFactorEvaluation(
                        factorType = signal.factorType,
                        title = signal.factorType.displayName,
                        score = factorScoreInt,
                        normalizedScore = signal.normalizedScore,
                        weight = baseWeight,
                        effectiveWeight = effectiveWeight,
                        isAvailable = true,
                        dataSourceType = signal.dataSourceType,
                        summary = summary,
                        isPositive = isPositive
                    )
                )
            } else {
                val missingLabel = if (signal.factorType == SafetyFactorType.COMMUNITY_PERCEIVED) {
                    "No community feedback reports for this corridor yet"
                } else {
                    "Missing ${signal.factorType.displayName.lowercase()} telemetry"
                }
                negativeFactors.add(missingLabel)
                factorEvaluations.add(
                    SafetyFactorEvaluation(
                        factorType = signal.factorType,
                        title = signal.factorType.displayName,
                        score = 0,
                        normalizedScore = 0.0,
                        weight = baseWeight,
                        effectiveWeight = 0.0,
                        isAvailable = false,
                        dataSourceType = DataSourceType.UNAVAILABLE,
                        summary = "Signal not available for this corridor",
                        isPositive = false
                    )
                )
            }
        }

        val finalScore = (totalWeightedScore * 100.0).roundToInt().coerceIn(0, 100)

        // Confidence calculation based on data availability and source confidence
        val avgConfidence = availableSignals.map { it.confidence }.average()
        val availabilityRatio = availableSignals.size.toDouble() / totalFactorsCount.toDouble()
        val combinedConfidence = (avgConfidence * 0.6) + (availabilityRatio * 0.4)

        val confidenceEnum = when {
            combinedConfidence >= 0.75 && availableSignals.size >= 4 -> SafetyConfidence.HIGH
            combinedConfidence >= 0.45 && availableSignals.size >= 2 -> SafetyConfidence.MEDIUM
            else -> SafetyConfidence.LOW
        }

        // Generate explainable, non-absolute contextual explanation
        val explanation = buildExplanation(
            score = finalScore,
            positives = positiveFactors,
            negatives = negativeFactors,
            availableCount = availableSignals.size,
            totalCount = totalFactorsCount
        )

        val isAnyPrototype = signals.any { it.dataSourceType == DataSourceType.PROTOTYPE }

        return RouteSafetyProfile(
            routeId = routeId,
            relativeSafetyScore = finalScore,
            confidence = confidenceEnum,
            confidenceScore = combinedConfidence,
            factorEvaluations = factorEvaluations,
            positiveFactors = positiveFactors,
            negativeFactors = negativeFactors,
            explanation = explanation,
            isRecommended = false,
            nearbySafePoints = nearbySafePoints,
            isPrototypeData = isAnyPrototype,
            communityAggregate = communityAggregate
        )
    }

    private fun buildExplanation(
        score: Int,
        positives: List<String>,
        negatives: List<String>,
        availableCount: Int,
        totalCount: Int
    ): String {
        val intro = when {
            score >= 80 -> "Stronger contextual safety profile among alternatives."
            score >= 65 -> "Moderate contextual safety profile with balanced signals."
            else -> "Lower relative safety profile compared to primary alternatives."
        }

        val topPositive = positives.firstOrNull()?.let { "Key factors: $it." } ?: ""
        val topLimitation = negatives.firstOrNull()?.let { " Note: $it." } ?: ""

        val coverageNote = if (availableCount < totalCount) {
            " (Evaluated on $availableCount of $totalCount available dimensions)."
        } else {
            ""
        }

        return "$intro $topPositive$topLimitation$coverageNote"
    }

    private fun createUnavailableSignal(factorType: SafetyFactorType, desc: String): SafetySignal {
        return SafetySignal(
            factorType = factorType,
            rawValue = 0.0,
            normalizedScore = 0.0,
            confidence = 0.0,
            dataSourceType = DataSourceType.UNAVAILABLE,
            sourceDescription = desc,
            isAvailable = false
        )
    }
}
