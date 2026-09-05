package com.example.domain.repository

import com.example.domain.model.LatLngPoint
import com.example.domain.model.RaahiRoute

interface RouteRepository {

    /**
     * Computes real route alternatives between Karachi origin and destination coordinates.
     */
    suspend fun getRouteOptions(
        origin: LatLngPoint,
        destination: LatLngPoint,
        originName: String = "Origin",
        destinationName: String = "Destination"
    ): Result<List<RaahiRoute>>
}
