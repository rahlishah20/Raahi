package com.example.domain.usecase

import com.example.domain.model.LatLngPoint
import com.example.domain.model.SafePoint
import com.example.domain.model.SafePointCategory
import com.example.domain.repository.SafePointRepository
import kotlinx.coroutines.flow.Flow

class GetNearbySafePointsUseCase(
    private val safePointRepository: SafePointRepository
) {
    /**
     * Retrieves a flow of safe points near the specified coordinate.
     */
    operator fun invoke(
        latitude: Double,
        longitude: Double,
        radiusMeters: Int = 1500,
        categoryFilter: SafePointCategory? = null,
        onlyVerified: Boolean = false,
        onlyOpen: Boolean = false
    ): Flow<List<SafePoint>> {
        return safePointRepository.getNearbySafePoints(
            latitude = latitude,
            longitude = longitude,
            radiusMeters = radiusMeters,
            categoryFilter = categoryFilter,
            onlyVerified = onlyVerified,
            onlyOpen = onlyOpen
        )
    }

    /**
     * Retrieves safe points along a route polyline corridor.
     */
    suspend fun forRoute(
        routePoints: List<LatLngPoint>,
        corridorRadiusMeters: Int = 1200,
        categoryFilter: SafePointCategory? = null,
        onlyVerified: Boolean = false,
        onlyOpen: Boolean = false
    ): List<SafePoint> {
        return safePointRepository.getSafePointsForRoute(
            routePoints = routePoints,
            corridorRadiusMeters = corridorRadiusMeters,
            categoryFilter = categoryFilter,
            onlyVerified = onlyVerified,
            onlyOpen = onlyOpen
        )
    }

    /**
     * Retrieves a safe point by its identifier.
     */
    suspend fun getById(id: String): SafePoint? {
        return safePointRepository.getSafePointById(id)
    }
}
