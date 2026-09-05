package com.example.domain.usecase

import com.example.domain.model.Journey
import com.example.domain.repository.JourneyRepository

class CompleteJourneyUseCase(
    private val journeyRepository: JourneyRepository
) {
    suspend operator fun invoke(journeyId: String): Result<Journey> {
        return journeyRepository.completeJourney(journeyId)
    }
}
