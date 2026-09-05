package com.example.domain.model

data class LatLngPoint(
    val latitude: Double,
    val longitude: Double
)

data class RaahiRoute(
    val id: String,
    val title: String,
    val summary: String,
    val origin: LatLngPoint,
    val destination: LatLngPoint,
    val originName: String,
    val destinationName: String,
    val distanceMeters: Int,
    val durationSeconds: Long,
    val formattedDistance: String,
    val formattedDuration: String,
    val polylinePoints: List<LatLngPoint>,
    val encodedPolyline: String,
    val legs: List<RaahiRouteLeg> = emptyList(),
    val warnings: List<String> = emptyList(),
    val metadata: Map<String, String> = emptyMap(),
    val safetyProfile: RouteSafetyProfile? = null
)

data class RaahiRouteLeg(
    val distanceMeters: Int,
    val durationSeconds: Long,
    val formattedDistance: String,
    val formattedDuration: String,
    val startLocation: LatLngPoint,
    val endLocation: LatLngPoint,
    val steps: List<RaahiRouteStep> = emptyList()
)

data class RaahiRouteStep(
    val instruction: String,
    val distanceMeters: Int,
    val durationSeconds: Long,
    val startLocation: LatLngPoint,
    val endLocation: LatLngPoint
)
