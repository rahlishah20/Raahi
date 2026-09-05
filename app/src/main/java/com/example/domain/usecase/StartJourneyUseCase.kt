package com.example.domain.usecase

import com.example.domain.model.Journey
import com.example.domain.model.RaahiRoute
import com.example.domain.repository.JourneyRepository

class StartJourneyUseCase(
    private val journeyRepository: JourneyRepository
) {
    suspend operator fun invoke(
        selectedRoute: RaahiRoute,
        alternativeRoutes: List<RaahiRoute> = emptyList()
    ): Result<Journey> {
        val plannedResult = journeyRepository.planJourney(selectedRoute, alternativeRoutes)
        if (plannedResult.isFailure) return plannedResult

        val plannedJourney = plannedResult.getOrThrow()
        return journeyRepository.startJourney(plannedJourney.id)
    }
}
