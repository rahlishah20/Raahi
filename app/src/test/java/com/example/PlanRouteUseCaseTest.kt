package com.example

import com.example.data.repository.RouteRepositoryImpl
import com.example.domain.model.LatLngPoint
import com.example.domain.usecase.PlanRouteUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PlanRouteUseCaseTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val repository = RouteRepositoryImpl(ioDispatcher = testDispatcher)
    private val useCase = PlanRouteUseCase(repository)

    @Test
    fun `plan journey between Clifton and Saddar succeeds and returns multiple routes`() = runTest(testDispatcher) {
        val origin = LatLngPoint(24.8138, 67.0300) // Clifton
        val destination = LatLngPoint(24.8607, 67.0104) // Saddar

        val result = useCase(origin, destination, "Clifton", "Saddar")
        assertTrue(result.isSuccess)

        val routes = result.getOrNull()
        assertTrue(routes != null)
        assertTrue(routes!!.size >= 2)

        val primary = routes.first()
        assertTrue(primary.formattedDistance.isNotEmpty())
        assertTrue(primary.formattedDuration.isNotEmpty())
        assertTrue(primary.polylinePoints.isNotEmpty())
    }

    @Test
    fun `rejects origin outside Karachi metropolitan boundary`() = runTest(testDispatcher) {
        val outsideOrigin = LatLngPoint(31.5204, 74.3587) // Lahore coordinates
        val destination = LatLngPoint(24.8607, 67.0104) // Karachi

        val result = useCase(outsideOrigin, destination, "Lahore", "Saddar")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Karachi") == true)
    }

    @Test
    fun `rejects destination identical to origin`() = runTest(testDispatcher) {
        val point = LatLngPoint(24.8138, 67.0300)

        val result = useCase(point, point, "Clifton", "Clifton")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("too close") == true)
    }

    @Test
    fun `rejects null origin or destination gracefully`() = runTest(testDispatcher) {
        val resultNullOrigin = useCase(null, LatLngPoint(24.8607, 67.0104))
        assertTrue(resultNullOrigin.isFailure)

        val resultNullDest = useCase(LatLngPoint(24.8138, 67.0300), null)
        assertTrue(resultNullDest.isFailure)
    }
}
