package com.example.data.repository

import com.example.data.datasource.KarachiLocalDataSource
import com.example.domain.model.KarachiDestination
import com.example.domain.repository.DestinationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

class DestinationRepositoryImpl : DestinationRepository {

    private val allDestinations = KarachiLocalDataSource.KARACHI_DESTINATIONS

    override fun getRecentDestinations(): Flow<List<KarachiDestination>> = flow {
        emit(allDestinations.filter { it.isRecent })
    }

    override fun getQuickShortcuts(): Flow<List<KarachiDestination>> = flow {
        emit(KarachiLocalDataSource.QUICK_SHORTCUTS)
    }

    override fun searchDestinations(query: String): Flow<List<KarachiDestination>> = flow {
        val trimmed = query.trim().lowercase()
        if (trimmed.isEmpty()) {
            emit(allDestinations.filter { it.isRecent })
        } else {
            val results = allDestinations.filter { destination ->
                destination.name.lowercase().contains(trimmed) ||
                destination.area.lowercase().contains(trimmed) ||
                destination.address.lowercase().contains(trimmed)
            }
            emit(results)
        }
    }

    override suspend fun getDestinationById(id: String): KarachiDestination? {
        return (allDestinations + KarachiLocalDataSource.QUICK_SHORTCUTS).firstOrNull { it.id == id }
    }
}
