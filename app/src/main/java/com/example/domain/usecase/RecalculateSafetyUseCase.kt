package com.example.domain.usecase

import com.example.domain.model.DynamicRecalculationResult
import com.example.domain.model.RaahiRoute
import com.example.domain.repository.DemoEventRepository
import com.example.domain.safety.SafetyIntelligenceEngine
import com.example.domain.safety.SafetyIntelligenceEngineImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecalculateSafetyUseCase(
    private val safetyEngine: SafetyIntelligenceEngine = SafetyIntelligenceEngineImpl(),
    private val demoEventRepository: DemoEventRepository? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    suspend operator fun invoke(currentRoutes: List<RaahiRoute>): Result<DynamicRecalculationResult> = withContext(ioDispatcher) {
        if (currentRoutes.isEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("Cannot recalculate safety for an empty route list."))
        }

        try {
            val previousRecommended = currentRoutes.firstOrNull { it.safetyProfile?.isRecommended == true }
            val previousRecommendedRouteId = previousRecommended?.id

            // Run pure recalculation through the Safety Intelligence Engine
            val recalculatedRoutes = safetyEngine.evaluateAndCompareRoutes(currentRoutes)

            val newRecommended = recalculatedRoutes.firstOrNull { it.safetyProfile?.isRecommended == true }
            val newRecommendedRouteId = newRecommended?.id

            val recommendationChanged = previousRecommendedRouteId != null &&
                    newRecommendedRouteId != null &&
                    previousRecommendedRouteId != newRecommendedRouteId

            val activeEvents = demoEventRepository?.getActiveEventsSnapshot() ?: emptyList()

            val summaryNotice = when {
                recommendationChanged && newRecommended != null -> {
                    "Conditions changed: ${newRecommended.title} now presents a stronger relative safety profile based on available signals."
                }
                activeEvents.isNotEmpty() -> {
                    "Safety conditions updated based on active contextual signals."
                }
                else -> {
                    "Route safety profiles evaluated against baseline contextual telemetry."
                }
            }

            Result.success(
                DynamicRecalculationResult(
                    recalculatedRoutes = recalculatedRoutes,
                    previousRecommendedRouteId = previousRecommendedRouteId,
                    newRecommendedRouteId = newRecommendedRouteId,
                    recommendationChanged = recommendationChanged,
                    summaryNotice = summaryNotice,
                    activeEventsCount = activeEvents.size
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
