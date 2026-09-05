package com.example.domain.usecase

import com.example.domain.model.DynamicRecalculationResult
import com.example.domain.model.RaahiRoute
import com.example.domain.repository.DemoEventRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ResetDemoEventsUseCase(
    private val demoEventRepository: DemoEventRepository,
    private val recalculateSafetyUseCase: RecalculateSafetyUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    suspend operator fun invoke(currentRoutes: List<RaahiRoute>): Result<DynamicRecalculationResult> = withContext(dispatcher) {
        val resetResult = demoEventRepository.resetDemoEvents()
        if (resetResult.isFailure) {
            return@withContext Result.failure(resetResult.exceptionOrNull() ?: Exception("Failed to reset demo events"))
        }

        recalculateSafetyUseCase(currentRoutes)
    }
}
