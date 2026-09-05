package com.example.data.repository

import com.example.data.datasource.KarachiDynamicConditionDataSource
import com.example.domain.model.DemoEvent
import com.example.domain.repository.DemoEventRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Backend-ready request structure representing POST /api/demo/events
 */
data class DemoEventApiPayload(
    val eventId: String,
    val type: String,
    val targetRouteId: String?,
    val targetCorridorKey: String?,
    val rawValueOverride: Double?,
    val valueMultiplier: Double?,
    val description: String,
    val timestamp: Long
)

class DemoEventRepositoryImpl(
    private val isBackendAvailable: Boolean = false,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : DemoEventRepository {

    override suspend fun submitDemoEvent(event: DemoEvent): Result<Unit> = withContext(ioDispatcher) {
        if (isBackendAvailable) {
            // Future Remote API call via POST /api/demo/events
            // e.g. apiService.submitDemoEvent(DemoEventApiPayload(...))
        }

        // Local dynamic simulation store
        KarachiDynamicConditionDataSource.applyEvent(event)
    }

    override suspend fun resetDemoEvents(): Result<Unit> = withContext(ioDispatcher) {
        if (isBackendAvailable) {
            // Future Remote API call via POST /api/demo/reset
            // e.g. apiService.resetDemoEvents()
        }

        KarachiDynamicConditionDataSource.resetEvents()
    }

    override fun getActiveDemoEvents(): Flow<List<DemoEvent>> = flow {
        emit(KarachiDynamicConditionDataSource.getActiveEvents())
    }.flowOn(ioDispatcher)

    override suspend fun getActiveEventsSnapshot(): List<DemoEvent> = withContext(ioDispatcher) {
        KarachiDynamicConditionDataSource.getActiveEvents()
    }
}
