package com.example.data.repository

import com.example.data.safety.KarachiSafetyDataSource
import com.example.domain.model.LatLngPoint
import com.example.domain.model.SafePoint
import com.example.domain.model.SafePointCategory
import com.example.domain.repository.SafePointRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class SafePointRepositoryImpl(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SafePointRepository {

    override fun getNearbySafePoints(
        latitude: Double,
        longitude: Double,
        radiusMeters: Int,
        categoryFilter: SafePointCategory?,
        onlyVerified: Boolean,
        onlyOpen: Boolean
    ): Flow<List<SafePoint>> = flow {
        if (latitude.isNaN() || longitude.isNaN() || latitude !in -90.0..90.0 || longitude !in -180.0..180.0) {
            emit(emptyList())
            return@flow
        }

        val results = KarachiSafetyDataSource.findNearbySafePointsFromLocation(
            lat = latitude,
            lng = longitude,
            radiusMeters = radiusMeters,
            categoryFilter = categoryFilter,
            onlyVerified = onlyVerified,
            onlyOpen = onlyOpen
        )
        emit(results)
    }.flowOn(ioDispatcher)

    override suspend fun getSafePointsForRoute(
        routePoints: List<LatLngPoint>,
        corridorRadiusMeters: Int,
        categoryFilter: SafePointCategory?,
        onlyVerified: Boolean,
        onlyOpen: Boolean
    ): List<SafePoint> = withContext(ioDispatcher) {
        if (routePoints.isEmpty()) return@withContext emptyList()

        KarachiSafetyDataSource.findNearbySafePoints(
            points = routePoints,
            maxDistanceMeters = corridorRadiusMeters,
            categoryFilter = categoryFilter,
            onlyVerified = onlyVerified,
            onlyOpen = onlyOpen
        )
    }

    override suspend fun getAllSafePoints(): List<SafePoint> = withContext(ioDispatcher) {
        KarachiSafetyDataSource.VERIFIED_SAFE_POINTS
    }

    override suspend fun getSafePointById(id: String): SafePoint? = withContext(ioDispatcher) {
        KarachiSafetyDataSource.VERIFIED_SAFE_POINTS.firstOrNull { it.id == id }
    }
}
