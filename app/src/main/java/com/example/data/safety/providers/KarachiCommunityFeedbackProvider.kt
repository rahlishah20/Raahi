package com.example.data.safety.providers

import com.example.data.datasource.KarachiCommunityFeedbackDataSource
import com.example.data.datasource.KarachiDynamicConditionDataSource
import com.example.data.safety.KarachiSafetyDataSource
import com.example.domain.model.CommunityDataConfidence
import com.example.domain.model.CommunityFeedback
import com.example.domain.model.CommunitySafetyAggregate
import com.example.domain.model.DataSourceType
import com.example.domain.model.RaahiRoute
import com.example.domain.model.SafetyFactorType
import com.example.domain.model.SafetySignal
import com.example.domain.safety.CommunityFeedbackProvider

class KarachiCommunityFeedbackProvider(
    private val isEnabled: Boolean = true,
    private val forcedDataSourceType: DataSourceType = DataSourceType.PROTOTYPE
) : CommunityFeedbackProvider {

    override suspend fun getCommunityFeedbackSignal(route: RaahiRoute): Pair<SafetySignal, CommunitySafetyAggregate> {
        if (!isEnabled || forcedDataSourceType == DataSourceType.UNAVAILABLE) {
            val emptyAggregate = CommunitySafetyAggregate(
                totalReports = 0,
                safeCount = 0,
                neutralCount = 0,
                unsafeCount = 0,
                normalizedPerceivedScore = 0.50,
                confidence = CommunityDataConfidence.NO_DATA,
                confidenceScore = 0.0,
                topContextTags = emptyList(),
                summary = "Community perceived safety data unavailable",
                isAvailable = false
            )
            val unavailableSignal = SafetySignal(
                factorType = SafetyFactorType.COMMUNITY_PERCEIVED,
                rawValue = 0.0,
                normalizedScore = 0.0,
                confidence = 0.0,
                dataSourceType = DataSourceType.UNAVAILABLE,
                sourceDescription = "No community feedback data connected",
                isAvailable = false
            )
            return Pair(unavailableSignal, emptyAggregate)
        }

        val feedbackList = KarachiCommunityFeedbackDataSource.getFeedbackForRoute(
            routeId = route.id,
            points = route.polylinePoints,
            corridorRadiusMeters = 1200
        )

        val aggregate = KarachiCommunityFeedbackDataSource.aggregateFeedback(feedbackList)

        val profile = KarachiSafetyDataSource.findCorridorProfile(
            routeId = route.id,
            title = route.title,
            points = route.polylinePoints
        )

        val (adjustedScore, dynamicNote) = KarachiDynamicConditionDataSource.getAdjustedFactorValue(
            factorType = SafetyFactorType.COMMUNITY_PERCEIVED,
            baseValue = aggregate.normalizedPerceivedScore,
            routeId = route.id,
            corridorKey = profile.corridorKey
        )

        if (!aggregate.isAvailable || aggregate.confidence == CommunityDataConfidence.NO_DATA) {
            if (dynamicNote != null) {
                val demoSignal = SafetySignal(
                    factorType = SafetyFactorType.COMMUNITY_PERCEIVED,
                    rawValue = adjustedScore,
                    normalizedScore = adjustedScore,
                    confidence = 0.70,
                    dataSourceType = forcedDataSourceType,
                    sourceDescription = dynamicNote,
                    isAvailable = true
                )
                return Pair(demoSignal, aggregate.copy(normalizedPerceivedScore = adjustedScore, isAvailable = true))
            }

            val noDataSignal = SafetySignal(
                factorType = SafetyFactorType.COMMUNITY_PERCEIVED,
                rawValue = 0.0,
                normalizedScore = 0.0,
                confidence = 0.0,
                dataSourceType = DataSourceType.UNAVAILABLE,
                sourceDescription = "No community perceived-safety reports available for this corridor",
                isAvailable = false
            )
            return Pair(noDataSignal, aggregate)
        }

        val sourceDesc = dynamicNote ?: aggregate.summary

        val signal = SafetySignal(
            factorType = SafetyFactorType.COMMUNITY_PERCEIVED,
            rawValue = adjustedScore,
            normalizedScore = adjustedScore,
            confidence = aggregate.confidenceScore,
            dataSourceType = forcedDataSourceType,
            sourceDescription = sourceDesc,
            isAvailable = true
        )

        return Pair(signal, aggregate.copy(normalizedPerceivedScore = adjustedScore))
    }

    override suspend fun getCommunitySignal(route: RaahiRoute): SafetySignal {
        return getCommunityFeedbackSignal(route).first
    }

    override suspend fun submitFeedback(feedback: CommunityFeedback): Result<Unit> {
        return KarachiCommunityFeedbackDataSource.addFeedback(feedback)
    }
}
