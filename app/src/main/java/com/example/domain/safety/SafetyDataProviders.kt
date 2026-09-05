package com.example.domain.safety

import com.example.domain.model.KarachiSafePoint
import com.example.domain.model.RaahiRoute
import com.example.domain.model.SafetySignal

interface LightingDataProvider {
    suspend fun getLightingSignal(route: RaahiRoute): SafetySignal
}

interface PedestrianActivityProvider {
    suspend fun getPedestrianActivitySignal(route: RaahiRoute): SafetySignal
}

interface BusinessActivityProvider {
    suspend fun getBusinessActivitySignal(route: RaahiRoute): SafetySignal
}

interface HistoricalIncidentProvider {
    suspend fun getHistoricalIncidentSignal(route: RaahiRoute): SafetySignal
}

interface SafePointProvider {
    suspend fun getSafePointSignal(route: RaahiRoute): Pair<SafetySignal, List<KarachiSafePoint>>
}

interface SafePointDataProvider {
    suspend fun getAllSafePoints(): List<KarachiSafePoint>
    suspend fun getNearbySafePoints(
        latitude: Double,
        longitude: Double,
        radiusMeters: Int
    ): List<KarachiSafePoint>
    suspend fun getSafePointsAlongRoute(
        routePoints: List<com.example.domain.model.LatLngPoint>,
        corridorRadiusMeters: Int
    ): List<KarachiSafePoint>
}

interface TemporalSignalProvider {
    suspend fun getTemporalSignal(route: RaahiRoute, timestamp: Long = System.currentTimeMillis()): SafetySignal
}

interface MobilitySignalProvider {
    suspend fun getMobilitySignal(route: RaahiRoute): SafetySignal
}

interface CommunitySignalProvider {
    suspend fun getCommunitySignal(route: RaahiRoute): SafetySignal
}

interface CommunityFeedbackProvider : CommunitySignalProvider {
    suspend fun getCommunityFeedbackSignal(route: RaahiRoute): Pair<SafetySignal, com.example.domain.model.CommunitySafetyAggregate>
    suspend fun submitFeedback(feedback: com.example.domain.model.CommunityFeedback): Result<Unit>
}
