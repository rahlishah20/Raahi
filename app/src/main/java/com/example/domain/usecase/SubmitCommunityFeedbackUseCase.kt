package com.example.domain.usecase

import com.example.data.repository.CommunityFeedbackRepositoryImpl
import com.example.domain.model.CommunityFeedback
import com.example.domain.repository.CommunityFeedbackRepository

class SubmitCommunityFeedbackUseCase(
    private val repository: CommunityFeedbackRepository = CommunityFeedbackRepositoryImpl()
) {
    suspend operator fun invoke(feedback: CommunityFeedback): Result<Unit> {
        // Validation checks
        if (feedback.latitude.isNaN() || feedback.longitude.isNaN()) {
            return Result.failure(IllegalArgumentException("Invalid location coordinates."))
        }
        return repository.submitFeedback(feedback)
    }
}
