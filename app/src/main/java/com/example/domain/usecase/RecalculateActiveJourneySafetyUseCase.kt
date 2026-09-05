package com.example.domain.usecase

import com.example.domain.model.Journey
import com.example.domain.model.RaahiRoute
import com.example.domain.repository.JourneyRepository

class RecalculateActiveJourneySafetyUseCase(
    private val journeyRepository: JourneyRepository,
    private val recalculateSafetyUseCase: RecalculateSafetyUseCase = RecalculateSafetyUseCase()
) {
    suspend operator fun invoke(
        journeyId: String,
        currentRoutes: List<RaahiRoute>
    ): Result<Journey> {
        val recalcResult = recalculateSafetyUseCase(currentRoutes)
        if (recalcResult.isFailure) {
            return Result.failure(recalcResult.exceptionOrNull() ?: Exception("Safety recalculation failed"))
        }

        val dynamicResult = recalcResult.getOrThrow()
        val currentJourney = journeyRepository.getJourneyById(journeyId)
            ?: return Result.failure(IllegalStateException("Active journey not found"))

        val activeRecalculatedRoute = dynamicResult.recalculatedRoutes.firstOrNull { it.id == currentJourney.selectedRouteId }
            ?: dynamicResult.recalculatedRoutes.firstOrNull()
            ?: return Result.failure(IllegalStateException("Active route not found in recalculated routes"))

        val newSafetyProfile = activeRecalculatedRoute.safetyProfile
            ?: return Result.failure(IllegalStateException("Safety profile missing in recalculated route"))

        return journeyRepository.recalculateSafety(
            journeyId = journeyId,
            newSafetyProfile = newSafetyProfile,
            recalculatedAlternatives = dynamicResult.recalculatedRoutes
        )
    }
}
