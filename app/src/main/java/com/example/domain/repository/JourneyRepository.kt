package com.example.domain.repository

import com.example.domain.model.Journey
import com.example.domain.model.LatLngPoint
import com.example.domain.model.RaahiRoute
import com.example.domain.model.RouteSafetyProfile
import com.example.domain.model.SafePoint
import kotlinx.coroutines.flow.Flow

interface JourneyRepository {
    fun getCurrentJourney(): Flow<Journey?>
    fun getJourneyHistory(): Flow<List<Journey>>
    suspend fun planJourney(
        route: RaahiRoute,
        alternatives: List<RaahiRoute> = emptyList()
    ): Result<Journey>
    suspend fun startJourney(journeyId: String): Result<Journey>
    suspend fun updateLocation(journeyId: String, location: LatLngPoint): Result<Journey>
    suspend fun recalculateSafety(
        journeyId: String,
        newSafetyProfile: RouteSafetyProfile,
        recalculatedAlternatives: List<RaahiRoute> = emptyList()
    ): Result<Journey>
    suspend fun switchRoute(journeyId: String, newRoute: RaahiRoute): Result<Journey>
    suspend fun selectSafePoint(journeyId: String, safePoint: SafePoint?): Result<Journey>
    suspend fun markArrived(journeyId: String): Result<Journey>
    suspend fun completeJourney(journeyId: String): Result<Journey>
    suspend fun cancelJourney(journeyId: String): Result<Journey>
    suspend fun getJourneyById(journeyId: String): Journey?
    suspend fun clearActiveJourney()
}
