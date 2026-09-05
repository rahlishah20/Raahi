package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GoogleRoutesApiService {

    @POST("directions/v2:computeRoutes")
    suspend fun computeRoutes(
        @Header("X-Goog-Api-Key") apiKey: String,
        @Header("X-Goog-FieldMask") fieldMask: String = "routes.duration,routes.distanceMeters,routes.polyline.encodedPolyline,routes.description,routes.warnings,routes.legs",
        @Body request: ComputeRoutesRequestDto
    ): Response<ComputeRoutesResponseDto>
}

@JsonClass(generateAdapter = true)
data class ComputeRoutesRequestDto(
    @field:Json(name = "origin") val origin: WaypointDto,
    @field:Json(name = "destination") val destination: WaypointDto,
    @field:Json(name = "travelMode") val travelMode: String = "DRIVE",
    @field:Json(name = "routingPreference") val routingPreference: String = "TRAFFIC_AWARE",
    @field:Json(name = "computeAlternativeRoutes") val computeAlternativeRoutes: Boolean = true,
    @field:Json(name = "routeModifiers") val routeModifiers: RouteModifiersDto? = RouteModifiersDto(),
    @field:Json(name = "languageCode") val languageCode: String = "en-US",
    @field:Json(name = "units") val units: String = "METRIC"
)

@JsonClass(generateAdapter = true)
data class WaypointDto(
    @field:Json(name = "location") val location: LocationDto
)

@JsonClass(generateAdapter = true)
data class LocationDto(
    @field:Json(name = "latLng") val latLng: LatLngDto
)

@JsonClass(generateAdapter = true)
data class LatLngDto(
    @field:Json(name = "latitude") val latitude: Double,
    @field:Json(name = "longitude") val longitude: Double
)

@JsonClass(generateAdapter = true)
data class RouteModifiersDto(
    @field:Json(name = "avoidTolls") val avoidTolls: Boolean = false,
    @field:Json(name = "avoidHighways") val avoidHighways: Boolean = false,
    @field:Json(name = "avoidFerries") val avoidFerries: Boolean = false
)

@JsonClass(generateAdapter = true)
data class ComputeRoutesResponseDto(
    @field:Json(name = "routes") val routes: List<RouteDto>? = null,
    @field:Json(name = "fallbackInfo") val fallbackInfo: Map<String, Any>? = null
)

@JsonClass(generateAdapter = true)
data class RouteDto(
    @field:Json(name = "legs") val legs: List<RouteLegDto>? = null,
    @field:Json(name = "distanceMeters") val distanceMeters: Int? = null,
    @field:Json(name = "duration") val duration: String? = null,
    @field:Json(name = "staticDuration") val staticDuration: String? = null,
    @field:Json(name = "polyline") val polyline: PolylineDto? = null,
    @field:Json(name = "description") val description: String? = null,
    @field:Json(name = "warnings") val warnings: List<String>? = null,
    @field:Json(name = "routeLabels") val routeLabels: List<String>? = null
)

@JsonClass(generateAdapter = true)
data class RouteLegDto(
    @field:Json(name = "distanceMeters") val distanceMeters: Int? = null,
    @field:Json(name = "duration") val duration: String? = null,
    @field:Json(name = "staticDuration") val staticDuration: String? = null,
    @field:Json(name = "polyline") val polyline: PolylineDto? = null,
    @field:Json(name = "startLocation") val startLocation: LocationDto? = null,
    @field:Json(name = "endLocation") val endLocation: LocationDto? = null,
    @field:Json(name = "steps") val steps: List<RouteStepDto>? = null
)

@JsonClass(generateAdapter = true)
data class RouteStepDto(
    @field:Json(name = "distanceMeters") val distanceMeters: Int? = null,
    @field:Json(name = "staticDuration") val staticDuration: String? = null,
    @field:Json(name = "polyline") val polyline: PolylineDto? = null,
    @field:Json(name = "startLocation") val startLocation: LocationDto? = null,
    @field:Json(name = "endLocation") val endLocation: LocationDto? = null,
    @field:Json(name = "navigationInstruction") val navigationInstruction: NavigationInstructionDto? = null
)

@JsonClass(generateAdapter = true)
data class NavigationInstructionDto(
    @field:Json(name = "instructions") val instructions: String? = null,
    @field:Json(name = "maneuver") val maneuver: String? = null
)

@JsonClass(generateAdapter = true)
data class PolylineDto(
    @field:Json(name = "encodedPolyline") val encodedPolyline: String? = null
)

