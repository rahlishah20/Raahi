package com.example.domain.usecase

import com.example.domain.model.DemoEvent
import com.example.domain.model.DynamicRecalculationResult
import com.example.domain.model.RaahiRoute
import com.example.domain.repository.DemoEventRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ApplyDemoEventUseCase(
    private val demoEventRepository: DemoEventRepository,
    private val recalculateSafetyUseCase: RecalculateSafetyUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    suspend operator fun invoke(
        event: DemoEvent,
        currentRoutes: List<RaahiRoute>
    ): Result<DynamicRecalculationResult> = withContext(dispatcher) {
        val submitResult = demoEventRepository.submitDemoEvent(event)
        if (submitResult.isFailure) {
            return@withContext Result.failure(submitResult.exceptionOrNull() ?: Exception("Failed to apply demo event"))
        }

        recalculateSafetyUseCase(currentRoutes)
    }

    suspend fun applyEvents(
        events: List<DemoEvent>,
        currentRoutes: List<RaahiRoute>
    ): Result<DynamicRecalculationResult> = withContext(dispatcher) {
        for (event in events) {
            val res = demoEventRepository.submitDemoEvent(event)
            if (res.isFailure) {
                return@withContext Result.failure(res.exceptionOrNull() ?: Exception("Failed to apply demo event ${event.id}"))
            }
        }
        recalculateSafetyUseCase(currentRoutes)
    }
}
