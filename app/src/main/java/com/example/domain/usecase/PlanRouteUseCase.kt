package com.example.domain.usecase

import com.example.domain.model.LatLngPoint
import com.example.domain.model.RaahiRoute
import com.example.domain.repository.RouteRepository
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class PlanRouteUseCase(
    private val routeRepository: RouteRepository
) {

    suspend operator fun invoke(
        origin: LatLngPoint?,
        destination: LatLngPoint?,
        originName: String = "Current Location",
        destinationName: String = "Destination"
    ): Result<List<RaahiRoute>> {
        if (origin == null) {
            return Result.failure(IllegalArgumentException("Origin location is required."))
        }
        if (destination == null) {
            return Result.failure(IllegalArgumentException("Destination location is required."))
        }

        if (!isWithinKarachiBounds(origin.latitude, origin.longitude)) {
            return Result.failure(IllegalArgumentException("Origin location is outside Karachi metropolitan boundary."))
        }
        if (!isWithinKarachiBounds(destination.latitude, destination.longitude)) {
            return Result.failure(IllegalArgumentException("Destination location is outside Karachi metropolitan boundary."))
        }

        // Validate origin and destination are not identical or too close (< 5 meters)
        val distance = calculateDistanceInMeters(
            origin.latitude, origin.longitude,
            destination.latitude, destination.longitude
        )
        if (distance < 5.0) {
            return Result.failure(
                IllegalArgumentException("Origin and destination are too close to calculate a valid route.")
            )
        }

        return routeRepository.getRouteOptions(
            origin = origin,
            destination = destination,
            originName = originName,
            destinationName = destinationName
        )
    }

    private fun isWithinKarachiBounds(latitude: Double, longitude: Double): Boolean {
        // Karachi metropolitan area boundary
        return latitude in 24.65..25.25 && longitude in 66.80..67.45
    }

    private fun calculateDistanceInMeters(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val r = 6371000.0 // Earth radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
