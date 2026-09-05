package com.example.domain.usecase

import com.example.domain.model.Journey
import com.example.domain.model.LatLngPoint
import com.example.domain.repository.JourneyRepository

class UpdateJourneyProgressUseCase(
    private val journeyRepository: JourneyRepository
) {
    suspend operator fun invoke(
        journeyId: String,
        currentLocation: LatLngPoint
    ): Result<Journey> {
        return journeyRepository.updateLocation(journeyId, currentLocation)
    }
}
