package com.example

import com.example.data.remote.TomTomCalculatedRouteDto
import com.example.data.remote.TomTomGuidanceDto
import com.example.data.remote.TomTomInstructionDto
import com.example.data.remote.TomTomLegDto
import com.example.data.remote.TomTomPointDto
import com.example.data.remote.TomTomRouteResponseDto
import com.example.data.remote.TomTomRouteSummaryDto
import com.example.data.remote.TomTomRoutingApiService
import com.example.data.repository.RouteRepositoryImpl
import com.example.domain.model.LatLngPoint
import com.example.domain.usecase.EvaluateRouteSafetyUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RouteRepositoryTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Test
    fun `calculates realistic Karachi routes with duration and distance via fallback`() = runTest(testDispatcher) {
        val fallbackRepo = RouteRepositoryImpl(
            customTomTomApiKey = "",
            customGoogleApiKey = "",
            ioDispatcher = testDispatcher
        )
        val clifton = LatLngPoint(24.8138, 67.0300)
        val saddar = LatLngPoint(24.8607, 67.0104)

        val result = fallbackRepo.getRouteOptions(clifton, saddar, "Clifton", "Saddar")
        assertTrue(result.isSuccess)

        val routes = result.getOrNull()
        assertNotNull(routes)
        assertEquals(3, routes!!.size)

        val route1 = routes[0]
        assertEquals("Via Khayaban-e-Iqbal & Club Rd", route1.title)
        assertEquals("5.8 km", route1.formattedDistance)
        assertEquals("11 min", route1.formattedDuration)
        assertTrue(route1.polylinePoints.size >= 5)

        val route2 = routes[1]
        assertEquals("Via Shahrah-e-Faisal & Cantt", route2.title)
        assertEquals("6.4 km", route2.formattedDistance)
        assertEquals("14 min", route2.formattedDuration)

        val route3 = routes[2]
        assertEquals("Via Mai Kolachi Bypass", route3.title)
        assertEquals("7.1 km", route3.formattedDistance)
        assertEquals("17 min", route3.formattedDuration)
    }

    @Test
    fun `integrates TomTom Routing API response into Raahi domain routes`() = runTest(testDispatcher) {
        val mockTomTomApi = object : TomTomRoutingApiService {
            override suspend fun calculateRoute(
                locations: String,
                apiKey: String,
                maxAlternatives: Int,
                routeType: String,
                traffic: Boolean,
                travelMode: String,
                instructionsType: String
            ): Response<TomTomRouteResponseDto> {
                val mockResponse = TomTomRouteResponseDto(
                    routes = listOf(
                        TomTomCalculatedRouteDto(
                            summary = TomTomRouteSummaryDto(
                                lengthInMeters = 5400,
                                travelTimeInSeconds = 720
                            ),
                            legs = listOf(
                                TomTomLegDto(
                                    summary = TomTomRouteSummaryDto(
                                        lengthInMeters = 5400,
                                        travelTimeInSeconds = 720
                                    ),
                                    points = listOf(
                                        TomTomPointDto(24.8138, 67.0300),
                                        TomTomPointDto(24.8300, 67.0320),
                                        TomTomPointDto(24.8607, 67.0104)
                                    )
                                )
                            ),
                            guidance = TomTomGuidanceDto(
                                instructions = listOf(
                                    TomTomInstructionDto(
                                        street = "Khayaban-e-Shamsheer",
                                        message = "Turn left onto Khayaban-e-Shamsheer",
                                        combinedDescription = "Turn left onto Khayaban-e-Shamsheer",
                                        routeOffsetInMeters = 1200,
                                        travelTimeInSeconds = 180,
                                        point = TomTomPointDto(24.8138, 67.0300)
                                    )
                                )
                            )
                        )
                    )
                )
                return Response.success(mockResponse)
            }
        }

        val repository = RouteRepositoryImpl(
            tomTomApiService = mockTomTomApi,
            customTomTomApiKey = "mock_tomtom_key",
            ioDispatcher = testDispatcher
        )

        val clifton = LatLngPoint(24.8138, 67.0300)
        val saddar = LatLngPoint(24.8607, 67.0104)

        val result = repository.getRouteOptions(clifton, saddar, "Clifton", "Saddar")
        assertTrue(result.isSuccess)

        val routes = result.getOrNull()
        assertNotNull(routes)
        assertEquals(1, routes!!.size)

        val route = routes[0]
        assertEquals("Via Khayaban-e-Shamsheer", route.title)
        assertEquals("5.4 km", route.formattedDistance)
        assertEquals("12 min", route.formattedDuration)
        assertEquals(3, route.polylinePoints.size)
        assertEquals("TomTom", route.metadata["provider"])

        // Verify existing Raahi safety calculation evaluates TomTom routes perfectly
        val safetyUseCase = EvaluateRouteSafetyUseCase()
        val evaluated = safetyUseCase(routes).getOrNull()
        assertNotNull(evaluated)
        assertEquals(1, evaluated!!.size)
        assertNotNull(evaluated[0].safetyProfile)
        assertTrue(evaluated[0].safetyProfile!!.relativeSafetyScore in 1..100)
    }

    @Test
    fun `falls back gracefully when TomTom returns HTTP error`() = runTest(testDispatcher) {
        val errorTomTomApi = object : TomTomRoutingApiService {
            override suspend fun calculateRoute(
                locations: String,
                apiKey: String,
                maxAlternatives: Int,
                routeType: String,
                traffic: Boolean,
                travelMode: String,
                instructionsType: String
            ): Response<TomTomRouteResponseDto> {
                return Response.error(403, "Forbidden".toResponseBody())
            }
        }

        val repository = RouteRepositoryImpl(
            tomTomApiService = errorTomTomApi,
            customTomTomApiKey = "invalid_key",
            ioDispatcher = testDispatcher
        )

        val clifton = LatLngPoint(24.8138, 67.0300)
        val saddar = LatLngPoint(24.8607, 67.0104)

        val result = repository.getRouteOptions(clifton, saddar, "Clifton", "Saddar")
        assertTrue(result.isSuccess)

        val routes = result.getOrNull()
        assertNotNull(routes)
        assertTrue(routes!!.isNotEmpty())
        assertEquals("Via Khayaban-e-Iqbal & Club Rd", routes[0].title)
    }

    @Test
    fun `generates valid polyline points for general Karachi locations`() = runTest(testDispatcher) {
        val fallbackRepo = RouteRepositoryImpl(
            customTomTomApiKey = "",
            customGoogleApiKey = "",
            ioDispatcher = testDispatcher
        )
        val origin = LatLngPoint(24.8021, 67.0654) // DHA Phase 6
        val destination = LatLngPoint(24.9180, 67.0971) // Gulshan-e-Iqbal

        val result = fallbackRepo.getRouteOptions(origin, destination, "DHA", "Gulshan")
        assertTrue(result.isSuccess)

        val routes = result.getOrNull()
        assertNotNull(routes)
        assertTrue(routes!!.isNotEmpty())
        assertTrue(routes[0].polylinePoints.isNotEmpty())
    }
}
