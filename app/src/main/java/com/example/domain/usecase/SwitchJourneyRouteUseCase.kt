package com.example.domain.usecase

import com.example.domain.model.Journey
import com.example.domain.model.RaahiRoute
import com.example.domain.repository.JourneyRepository

class SwitchJourneyRouteUseCase(
    private val journeyRepository: JourneyRepository
) {
    suspend operator fun invoke(journeyId: String, newRoute: RaahiRoute): Result<Journey> {
        return journeyRepository.switchRoute(journeyId, newRoute)
    }
}
