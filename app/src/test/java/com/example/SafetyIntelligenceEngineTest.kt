package com.example

import com.example.domain.model.DataSourceType
import com.example.domain.model.LatLngPoint
import com.example.domain.model.RaahiRoute
import com.example.domain.model.SafetyConfidence
import com.example.domain.model.SafetyFactorType
import com.example.domain.model.SafetySignal
import com.example.domain.safety.SafetyIntelligenceEngineImpl
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SafetyIntelligenceEngineTest {

    private val engine = SafetyIntelligenceEngineImpl()

    private val sampleRoute = RaahiRoute(
        id = "route-clifton-saddar",
        title = "Via Khayaban-e-Iqbal & Club Rd",
        summary = "Well-lit commercial corridor",
        origin = LatLngPoint(24.8138, 67.0300),
        destination = LatLngPoint(24.8607, 67.0104),
        originName = "Clifton Block 4",
        destinationName = "Saddar Bazaar",
        distanceMeters = 6800,
        durationSeconds = 900,
        formattedDistance = "6.8 km",
        formattedDuration = "15 min",
        polylinePoints = listOf(
            LatLngPoint(24.8138, 67.0300),
            LatLngPoint(24.8250, 67.0330),
            LatLngPoint(24.8450, 67.0250),
            LatLngPoint(24.8607, 67.0104)
        ),
        encodedPolyline = ""
    )

    private val sampleAlternativeRoute = RaahiRoute(
        id = "route-mai-kolachi",
        title = "Via Mai Kolachi Bypass",
        summary = "Expressway corridor",
        origin = LatLngPoint(24.8138, 67.0300),
        destination = LatLngPoint(24.8607, 67.0104),
        originName = "Clifton Block 4",
        destinationName = "Saddar Bazaar",
        distanceMeters = 7400,
        durationSeconds = 840,
        formattedDistance = "7.4 km",
        formattedDuration = "14 min",
        polylinePoints = listOf(
            LatLngPoint(24.8138, 67.0300),
            LatLngPoint(24.8300, 66.9950),
            LatLngPoint(24.8607, 67.0104)
        ),
        encodedPolyline = ""
    )

    @Test
    fun `evaluateRouteSafety returns deterministic score within bounds`() = runTest {
        val profile = engine.evaluateRouteSafety(sampleRoute)

        assertNotNull(profile)
        assertTrue("Score must be in 0..100 range", profile.relativeSafetyScore in 0..100)
        assertEquals("Must evaluate all 6 safety factors", 6, profile.factorEvaluations.size)
        assertTrue(profile.positiveFactors.isNotEmpty() || profile.negativeFactors.isNotEmpty())
        assertTrue(profile.explanation.isNotBlank())
        assertFalse(profile.explanation.contains("100% safe"))
        assertNotNull("Community aggregate must be populated", profile.communityAggregate)
    }

    @Test
    fun `missing signals trigger dynamic weight redistribution without crashing`() {
        val partialSignals = listOf(
            SafetySignal(
                factorType = SafetyFactorType.LIGHTING,
                rawValue = 0.90,
                normalizedScore = 0.90,
                confidence = 0.90,
                dataSourceType = DataSourceType.PROTOTYPE,
                sourceDescription = "Lighting available",
                isAvailable = true
            ),
            SafetySignal(
                factorType = SafetyFactorType.PEDESTRIAN_ACTIVITY,
                rawValue = 0.0,
                normalizedScore = 0.0,
                confidence = 0.0,
                dataSourceType = DataSourceType.UNAVAILABLE,
                sourceDescription = "Unavailable",
                isAvailable = false
            ),
            SafetySignal(
                factorType = SafetyFactorType.BUSINESS_ACTIVITY,
                rawValue = 0.80,
                normalizedScore = 0.80,
                confidence = 0.85,
                dataSourceType = DataSourceType.PROTOTYPE,
                sourceDescription = "Business available",
                isAvailable = true
            ),
            SafetySignal(
                factorType = SafetyFactorType.HISTORICAL_INCIDENT,
                rawValue = 0.0,
                normalizedScore = 0.0,
                confidence = 0.0,
                dataSourceType = DataSourceType.UNAVAILABLE,
                sourceDescription = "Unavailable",
                isAvailable = false
            ),
            SafetySignal(
                factorType = SafetyFactorType.SAFE_POINTS,
                rawValue = 0.0,
                normalizedScore = 0.0,
                confidence = 0.0,
                dataSourceType = DataSourceType.UNAVAILABLE,
                sourceDescription = "Unavailable",
                isAvailable = false
            )
        )

        val profile = engine.computeProfileFromSignals(
            routeId = "test-route",
            signals = partialSignals,
            nearbySafePoints = emptyList()
        )

        assertTrue(profile.relativeSafetyScore in 0..100)
        // Effective weights of available factors should equal 1.0 (0.20 + 0.20 = 0.40 total base, so 50% each)
        val availableEvals = profile.factorEvaluations.filter { it.isAvailable }
        val sumEffectiveWeights = availableEvals.sumOf { it.effectiveWeight }
        assertEquals(1.0, sumEffectiveWeights, 0.001)
    }

    @Test
    fun `all unavailable signals return zero score and low confidence gracefully`() {
        val emptySignals = SafetyFactorType.entries.map { factor ->
            SafetySignal(
                factorType = factor,
                rawValue = 0.0,
                normalizedScore = 0.0,
                confidence = 0.0,
                dataSourceType = DataSourceType.UNAVAILABLE,
                sourceDescription = "Unavailable",
                isAvailable = false
            )
        }

        val profile = engine.computeProfileFromSignals(
            routeId = "empty-route",
            signals = emptySignals,
            nearbySafePoints = emptyList()
        )

        assertEquals(0, profile.relativeSafetyScore)
        assertEquals(SafetyConfidence.LOW, profile.confidence)
        assertFalse(profile.isRecommended)
    }

    @Test
    fun `evaluateAndCompareRoutes designates the strongest corridor as recommended`() = runTest {
        val routes = listOf(sampleRoute, sampleAlternativeRoute)
        val compared = engine.evaluateAndCompareRoutes(routes)

        assertEquals(2, compared.size)
        val recommendedCount = compared.count { it.safetyProfile?.isRecommended == true }
        assertEquals(1, recommendedCount)

        val bestRoute = compared.first { it.safetyProfile?.isRecommended == true }
        val otherRoute = compared.first { it.safetyProfile?.isRecommended == false }

        assertTrue(
            bestRoute.safetyProfile!!.relativeSafetyScore >= otherRoute.safetyProfile!!.relativeSafetyScore
        )
    }
}
