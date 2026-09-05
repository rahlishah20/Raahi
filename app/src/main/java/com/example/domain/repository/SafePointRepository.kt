package com.example.domain.repository

import com.example.domain.model.LatLngPoint
import com.example.domain.model.SafePoint
import com.example.domain.model.SafePointCategory
import kotlinx.coroutines.flow.Flow

interface SafePointRepository {
    /**
     * Observes nearby safe points within [radiusMeters] of a coordinate.
     */
    fun getNearbySafePoints(
        latitude: Double,
        longitude: Double,
        radiusMeters: Int = 1500,
        categoryFilter: SafePointCategory? = null,
        onlyVerified: Boolean = false,
        onlyOpen: Boolean = false
    ): Flow<List<SafePoint>>

    /**
     * Retrieves safe points along a route corridor.
     */
    suspend fun getSafePointsForRoute(
        routePoints: List<LatLngPoint>,
        corridorRadiusMeters: Int = 1200,
        categoryFilter: SafePointCategory? = null,
        onlyVerified: Boolean = false,
        onlyOpen: Boolean = false
    ): List<SafePoint>

    /**
     * Retrieves all known safe points.
     */
    suspend fun getAllSafePoints(): List<SafePoint>

    /**
     * Looks up a safe point by unique identifier.
     */
    suspend fun getSafePointById(id: String): SafePoint?
}
