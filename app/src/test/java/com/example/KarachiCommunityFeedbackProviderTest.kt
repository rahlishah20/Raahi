package com.example

import com.example.data.datasource.KarachiCommunityFeedbackDataSource
import com.example.data.safety.providers.KarachiCommunityFeedbackProvider
import com.example.domain.model.CommunitySafetyContextTag
import com.example.domain.model.CommunitySafetyRating
import com.example.domain.model.DataSourceType
import com.example.domain.model.LatLngPoint
import com.example.domain.model.RaahiRoute
import com.example.domain.model.SafetyFactorType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class KarachiCommunityFeedbackProviderTest {

    private lateinit var provider: KarachiCommunityFeedbackProvider

    private val sampleRoute = RaahiRoute(
        id = "test-route-clifton",
        title = "Via Khayaban-e-Iqbal",
        summary = "Clifton to Saddar",
        origin = LatLngPoint(24.8138, 67.0300),
        destination = LatLngPoint(24.8607, 67.0104),
        originName = "Clifton",
        destinationName = "Saddar",
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

    @Before
    fun setUp() {
        provider = KarachiCommunityFeedbackProvider()
    }

    @Test
    fun `getCommunityFeedbackSignal returns valid signal and aggregate for populated corridor`() = runTest {
        val (signal, aggregate) = provider.getCommunityFeedbackSignal(sampleRoute)

        assertNotNull(signal)
        assertEquals(SafetyFactorType.COMMUNITY_PERCEIVED, signal.factorType)
        assertTrue("Signal must be available for sample corridor", signal.isAvailable)
        assertTrue("Normalized score must be between 0 and 1", signal.normalizedScore in 0.0..1.0)
        assertTrue("Confidence must be between 0 and 1", signal.confidence in 0.0..1.0)
        assertEquals(DataSourceType.PROTOTYPE, signal.dataSourceType)
        assertTrue(signal.sourceDescription.contains("perceived"))

        assertNotNull(aggregate)
        assertTrue(aggregate.isAvailable)
        assertTrue(aggregate.totalReports > 0)
        assertTrue(aggregate.safePercentage >= 0.0)
        assertTrue(aggregate.neutralPercentage >= 0.0)
        assertTrue(aggregate.unsafePercentage >= 0.0)
        assertEquals(
            "Percentages must sum to approximately 100",
            100.0,
            aggregate.safePercentage + aggregate.neutralPercentage + aggregate.unsafePercentage,
            1.0
        )
    }

    @Test
    fun `empty corridor returns unavailable signal with zero score gracefully`() = runTest {
        val isolatedRoute = RaahiRoute(
            id = "isolated-route",
            title = "Isolated area",
            summary = "No community reports",
            origin = LatLngPoint(24.7100, 66.8200),
            destination = LatLngPoint(24.7150, 66.8250),
            originName = "Outskirts",
            destinationName = "Outskirts End",
            distanceMeters = 1000,
            durationSeconds = 200,
            formattedDistance = "1.0 km",
            formattedDuration = "3 min",
            polylinePoints = listOf(
                LatLngPoint(24.7100, 66.8200),
                LatLngPoint(24.7150, 66.8250)
            ),
            encodedPolyline = ""
        )

        val (signal, aggregate) = provider.getCommunityFeedbackSignal(isolatedRoute)
        assertEquals(SafetyFactorType.COMMUNITY_PERCEIVED, signal.factorType)
        assertFalse(signal.isAvailable)
        assertEquals(0.0, signal.normalizedScore, 0.0)
        assertEquals(0.0, signal.confidence, 0.0)
        assertEquals(DataSourceType.UNAVAILABLE, signal.dataSourceType)
    }

    @Test
    fun `disabled provider returns unavailable signal cleanly`() = runTest {
        val disabledProvider = KarachiCommunityFeedbackProvider(isEnabled = false)
        val (signal, aggregate) = disabledProvider.getCommunityFeedbackSignal(sampleRoute)

        assertFalse(signal.isAvailable)
        assertFalse(aggregate.isAvailable)
        assertEquals(DataSourceType.UNAVAILABLE, signal.dataSourceType)
    }
}
