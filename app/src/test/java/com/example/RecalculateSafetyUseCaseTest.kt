package com.example

import com.example.data.datasource.KarachiDynamicConditionDataSource
import com.example.data.repository.DemoEventRepositoryImpl
import com.example.domain.model.DataSourceType
import com.example.domain.model.DemoEvent
import com.example.domain.model.DemoEventType
import com.example.domain.model.LatLngPoint
import com.example.domain.model.RaahiRoute
import com.example.domain.model.SafetyConfidence
import com.example.domain.model.SafetyFactorType
import com.example.domain.model.RouteSafetyProfile
import com.example.domain.safety.SafetyIntelligenceEngineImpl
import com.example.domain.usecase.RecalculateSafetyUseCase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RecalculateSafetyUseCaseTest {

    private lateinit var demoEventRepository: DemoEventRepositoryImpl
    private lateinit var recalculateSafetyUseCase: RecalculateSafetyUseCase
    private val safetyEngine = SafetyIntelligenceEngineImpl()

    @Before
    fun setUp() {
        KarachiDynamicConditionDataSource.clearAllEvents()
        demoEventRepository = DemoEventRepositoryImpl()
        recalculateSafetyUseCase = RecalculateSafetyUseCase(
            safetyEngine = safetyEngine,
            demoEventRepository = demoEventRepository
        )
    }

    @After
    fun tearDown() {
        KarachiDynamicConditionDataSource.clearAllEvents()
    }

    private fun createSampleRoutes(): List<RaahiRoute> {
        val origin = LatLngPoint(24.8607, 67.0011)
        val destination = LatLngPoint(24.8720, 67.0850)

        val route1 = RaahiRoute(
            id = "route_shahrah_faisal",
            title = "via Shahrah-e-Faisal",
            summary = "Primary arterial boulevard",
            origin = origin,
            destination = destination,
            originName = "Clifton",
            destinationName = "Airport",
            distanceMeters = 12000,
            durationSeconds = 1200,
            formattedDistance = "12 km",
            formattedDuration = "20 mins",
            polylinePoints = listOf(
                LatLngPoint(24.8607, 67.0011),
                LatLngPoint(24.8650, 67.0450),
                LatLngPoint(24.8720, 67.0850)
            ),
            encodedPolyline = "mock_poly_1"
        )

        val route2 = RaahiRoute(
            id = "route_university_road",
            title = "via University Road",
            summary = "Secondary arterial route",
            origin = origin,
            destination = destination,
            originName = "Clifton",
            destinationName = "Airport",
            distanceMeters = 13500,
            durationSeconds = 1380,
            formattedDistance = "13.5 km",
            formattedDuration = "23 mins",
            polylinePoints = listOf(
                LatLngPoint(24.8607, 67.0011),
                LatLngPoint(24.8900, 67.0500),
                LatLngPoint(24.9150, 67.0950)
            ),
            encodedPolyline = "mock_poly_2"
        )

        return runBlocking { safetyEngine.evaluateAndCompareRoutes(listOf(route1, route2)) }
    }

    @Test
    fun testRecalculationMaintainsRecommendationWhenNoDynamicEvents() = runBlocking {
        val initialRoutes = createSampleRoutes()
        val initialRecommended = initialRoutes.firstOrNull { it.safetyProfile?.isRecommended == true }
        assertNotNull(initialRecommended)

        val result = recalculateSafetyUseCase(initialRoutes).getOrThrow()

        assertEquals(2, result.recalculatedRoutes.size)
        assertFalse(result.recommendationChanged)
        assertEquals(0, result.activeEventsCount)
        assertEquals(initialRecommended?.id, result.newRecommendedRouteId)
    }

    @Test
    fun testRecalculationDetectsDynamicConditionImpact() = runBlocking {
        val initialRoutes = createSampleRoutes()
        val topRoute = initialRoutes.firstOrNull { it.safetyProfile?.isRecommended == true }!!

        // Apply a severe business reduction on the top route
        val event = DemoEvent(
            id = "severe_market_strike",
            type = DemoEventType.BUSINESS_ACTIVITY_CHANGE,
            targetRouteId = topRoute.id,
            targetFactorType = SafetyFactorType.BUSINESS_ACTIVITY,
            rawValueOverride = 0.10,
            description = "Complete market shutdown along corridor"
        )
        demoEventRepository.submitDemoEvent(event)

        val result = recalculateSafetyUseCase(initialRoutes).getOrThrow()

        assertEquals(1, result.activeEventsCount)
        val recalculatedTopRoute = result.recalculatedRoutes.first { it.id == topRoute.id }
        val businessFactor = recalculatedTopRoute.safetyProfile?.factorEvaluations?.find { it.factorType == SafetyFactorType.BUSINESS_ACTIVITY }

        assertNotNull(businessFactor)
        assertEquals(0.10, businessFactor!!.normalizedScore, 0.01)
    }

    @Test
    fun testEmptyRouteListFailsGracefully() = runBlocking {
        val result = recalculateSafetyUseCase(emptyList())
        assertTrue(result.isFailure)
    }
}
