package com.example.domain.repository

import com.example.domain.model.DemoEvent
import kotlinx.coroutines.flow.Flow

interface DemoEventRepository {
    suspend fun submitDemoEvent(event: DemoEvent): Result<Unit>
    suspend fun resetDemoEvents(): Result<Unit>
    fun getActiveDemoEvents(): Flow<List<DemoEvent>>
    suspend fun getActiveEventsSnapshot(): List<DemoEvent>
}
