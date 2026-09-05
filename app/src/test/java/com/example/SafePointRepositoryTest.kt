package com.example

import com.example.data.repository.SafePointRepositoryImpl
import com.example.data.safety.KarachiSafetyDataSource
import com.example.domain.model.SafePoint
import com.example.domain.model.SafePointCategory
import com.example.domain.model.SafePointOpeningStatus
import com.example.domain.safety.SafetySignalNormalizer
import com.example.domain.usecase.GetNearbySafePointsUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SafePointRepositoryTest {

    private lateinit var repository: SafePointRepositoryImpl
    private lateinit var getNearbySafePointsUseCase: GetNearbySafePointsUseCase

    @Before
    fun setUp() {
        repository = SafePointRepositoryImpl()
        getNearbySafePointsUseCase = GetNearbySafePointsUseCase(repository)
    }

    @Test
    fun testFindNearbySafePoints_returnsKarachiSafePoints() = runBlocking {
        // Clifton coordinates
        val points = repository.getNearbySafePoints(24.8138, 67.0300, 3000).first()
        assertTrue("Expected nearby safe points in Clifton area", points.isNotEmpty())
        points.forEach { sp ->
            assertNotNull(sp.name)
            assertTrue(sp.latitude in 24.7..25.1)
            assertTrue(sp.longitude in 66.9..67.3)
        }
    }

    @Test
    fun testFilterByCategory_policeFacility() = runBlocking {
        val policePoints = repository.getNearbySafePoints(
            latitude = 24.8138,
            longitude = 67.0300,
            radiusMeters = 5000,
            categoryFilter = SafePointCategory.POLICE_FACILITY
        ).first()

        assertTrue("Expected police points to be found", policePoints.isNotEmpty())
        policePoints.forEach {
            assertEquals(SafePointCategory.POLICE_FACILITY, it.category)
        }
    }

    @Test
    fun testFilterByVerification_onlyVerified() = runBlocking {
        val verifiedPoints = repository.getNearbySafePoints(
            latitude = 24.8138,
            longitude = 67.0300,
            radiusMeters = 5000,
            onlyVerified = true
        ).first()

        assertTrue("Expected verified points", verifiedPoints.isNotEmpty())
        verifiedPoints.forEach {
            assertTrue("Expected item to be verified", it.verified)
        }
    }

    @Test
    fun testContextualSafePointScoring_rewardsVerifiedAnd24_7() {
        val pointAlwaysOpenVerified = SafePoint(
            id = "test_1",
            name = "Test Police",
            category = SafePointCategory.POLICE_FACILITY,
            latitude = 24.8140,
            longitude = 67.0300,
            address = "Clifton",
            verified = true,
            openingStatus = SafePointOpeningStatus.OPEN,
            distanceToRouteMeters = 50
        )

        val pointClosedUnverified = SafePoint(
            id = "test_2",
            name = "Test Closed Shop",
            category = SafePointCategory.SHOP,
            latitude = 24.8140,
            longitude = 67.0300,
            address = "Clifton",
            verified = false,
            openingStatus = SafePointOpeningStatus.CLOSED,
            distanceToRouteMeters = 800
        )

        val scoreVerified = SafetySignalNormalizer.computeContextualSafePointScore(listOf(pointAlwaysOpenVerified))
        val scoreClosed = SafetySignalNormalizer.computeContextualSafePointScore(listOf(pointClosedUnverified))

        assertTrue("Verified 24/7 close-by safe point should score higher than closed unverified point", scoreVerified > scoreClosed)
    }
}
