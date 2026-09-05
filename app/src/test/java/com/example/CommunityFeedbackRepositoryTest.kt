package com.example

import com.example.data.datasource.KarachiCommunityFeedbackDataSource
import com.example.data.repository.CommunityFeedbackRepositoryImpl
import com.example.domain.model.CommunityFeedback
import com.example.domain.model.CommunitySafetyContextTag
import com.example.domain.model.CommunitySafetyRating
import com.example.domain.model.LatLngPoint
import com.example.domain.model.RaahiRoute
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class CommunityFeedbackRepositoryTest {

    private lateinit var repository: CommunityFeedbackRepositoryImpl

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
        KarachiCommunityFeedbackDataSource.clearNonSeededFeedback()
        repository = CommunityFeedbackRepositoryImpl()
    }

    @Test
    fun `seeded feedback contains valid Karachi coordinates and is labeled SEED`() = runTest {
        val seeded = KarachiCommunityFeedbackDataSource.SEEDED_COMMUNITY_FEEDBACK
        assertTrue("Seeded dataset must not be empty", seeded.isNotEmpty())

        seeded.forEach { feedback ->
            assertTrue("Feedback must have SEED tag: ${feedback.id}", feedback.isSeeded)
            assertTrue("Lat must be within Karachi bounds: ${feedback.latitude}", feedback.latitude in 24.70..25.20)
            assertTrue("Lng must be within Karachi bounds: ${feedback.longitude}", feedback.longitude in 66.80..67.40)
            assertNotNull(feedback.safetyRating)
        }
    }

    @Test
    fun `submitFeedback adds new user feedback successfully`() = runTest {
        val initialCount = repository.getAllFeedback().size

        val newFeedback = CommunityFeedback(
            id = UUID.randomUUID().toString(),
            journeyId = UUID.randomUUID().toString(),
            routeId = "test-route-clifton",
            latitude = 24.8150,
            longitude = 67.0320,
            safetyRating = CommunitySafetyRating.SAFE,
            contextualTags = listOf(CommunitySafetyContextTag.GOOD_VISIBILITY, CommunitySafetyContextTag.COMFORTABLE),
            comment = "Felt calm during evening commute",
            createdAt = System.currentTimeMillis(),
            isSeeded = false
        )

        val result = repository.submitFeedback(newFeedback)
        assertTrue(result.isSuccess)

        val updatedCount = repository.getAllFeedback().size
        assertEquals(initialCount + 1, updatedCount)

        val found = repository.getAllFeedback().firstOrNull { it.id == newFeedback.id }
        assertNotNull(found)
        assertFalse("User submitted feedback must not be marked as seeded", found!!.isSeeded)
    }

    @Test
    fun `submitFeedback rejects coordinates outside Karachi`() = runTest {
        val outOfBoundsFeedback = CommunityFeedback(
            id = UUID.randomUUID().toString(),
            journeyId = UUID.randomUUID().toString(),
            routeId = null,
            latitude = 31.5204, // Lahore
            longitude = 74.3587,
            safetyRating = CommunitySafetyRating.SAFE,
            contextualTags = emptyList(),
            comment = null,
            createdAt = System.currentTimeMillis(),
            isSeeded = false
        )

        val result = repository.submitFeedback(outOfBoundsFeedback)
        assertTrue("Submission outside Karachi must fail", result.isFailure)
    }

    @Test
    fun `getFeedbackForRoute and getAggregateForRoute return valid corridor intelligence`() = runTest {
        val aggregate = repository.getAggregateForRoute(sampleRoute)
        assertTrue("Should compute aggregate for Clifton route corridor", aggregate.isAvailable)
        assertTrue("Should have reports for Clifton route", aggregate.totalReports > 0)
        assertTrue(aggregate.safePercentage >= 0.0)
    }

    @Test
    fun `getFeedbackForLocation returns feedback close to point`() = runTest {
        val cliftonFeedback = repository.getFeedbackForLocation(24.8138, 67.0300, radiusMeters = 2000).first()
        assertTrue(cliftonFeedback.isNotEmpty())
    }
}
