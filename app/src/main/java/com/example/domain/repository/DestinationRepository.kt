package com.example.domain.repository

import com.example.domain.model.KarachiDestination
import kotlinx.coroutines.flow.Flow

interface DestinationRepository {
    fun getRecentDestinations(): Flow<List<KarachiDestination>>
    fun getQuickShortcuts(): Flow<List<KarachiDestination>>
    fun searchDestinations(query: String): Flow<List<KarachiDestination>>
    suspend fun getDestinationById(id: String): KarachiDestination?
}
