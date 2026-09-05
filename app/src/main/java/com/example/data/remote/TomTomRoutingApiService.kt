package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TomTomRoutingApiService {

    @GET("routing/1/calculateRoute/{locations}/json")
    suspend fun calculateRoute(
        @Path("locations", encoded = true) locations: String,
        @Query("key") apiKey: String,
        @Query("maxAlternatives") maxAlternatives: Int = 2,
        @Query("routeType") routeType: String = "fastest",
        @Query("traffic") traffic: Boolean = true,
        @Query("travelMode") travelMode: String = "car",
        @Query("instructionsType") instructionsType: String = "text"
    ): Response<TomTomRouteResponseDto>
}

@JsonClass(generateAdapter = true)
data class TomTomRouteResponseDto(
    @field:Json(name = "formatVersion") val formatVersion: String? = null,
    @field:Json(name = "routes") val routes: List<TomTomCalculatedRouteDto>? = null,
    @field:Json(name = "errorText") val errorText: String? = null,
    @field:Json(name = "detailedError") val detailedError: TomTomDetailedErrorDto? = null
)

@JsonClass(generateAdapter = true)
data class TomTomDetailedErrorDto(
    @field:Json(name = "code") val code: String? = null,
    @field:Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class TomTomCalculatedRouteDto(
    @field:Json(name = "summary") val summary: TomTomRouteSummaryDto? = null,
    @field:Json(name = "legs") val legs: List<TomTomLegDto>? = null,
    @field:Json(name = "sections") val sections: List<TomTomSectionDto>? = null,
    @field:Json(name = "guidance") val guidance: TomTomGuidanceDto? = null
)

@JsonClass(generateAdapter = true)
data class TomTomRouteSummaryDto(
    @field:Json(name = "lengthInMeters") val lengthInMeters: Int? = null,
    @field:Json(name = "travelTimeInSeconds") val travelTimeInSeconds: Long? = null,
    @field:Json(name = "trafficDelayInSeconds") val trafficDelayInSeconds: Long? = null,
    @field:Json(name = "trafficLengthInMeters") val trafficLengthInMeters: Int? = null,
    @field:Json(name = "departureTime") val departureTime: String? = null,
    @field:Json(name = "arrivalTime") val arrivalTime: String? = null
)

@JsonClass(generateAdapter = true)
data class TomTomLegDto(
    @field:Json(name = "summary") val summary: TomTomRouteSummaryDto? = null,
    @field:Json(name = "points") val points: List<TomTomPointDto>? = null
)

@JsonClass(generateAdapter = true)
data class TomTomPointDto(
    @field:Json(name = "latitude") val latitude: Double,
    @field:Json(name = "longitude") val longitude: Double
)

@JsonClass(generateAdapter = true)
data class TomTomSectionDto(
    @field:Json(name = "startPointIndex") val startPointIndex: Int? = null,
    @field:Json(name = "endPointIndex") val endPointIndex: Int? = null,
    @field:Json(name = "sectionType") val sectionType: String? = null,
    @field:Json(name = "travelMode") val travelMode: String? = null
)

@JsonClass(generateAdapter = true)
data class TomTomGuidanceDto(
    @field:Json(name = "instructions") val instructions: List<TomTomInstructionDto>? = null
)

@JsonClass(generateAdapter = true)
data class TomTomInstructionDto(
    @field:Json(name = "routeOffsetInMeters") val routeOffsetInMeters: Int? = null,
    @field:Json(name = "travelTimeInSeconds") val travelTimeInSeconds: Long? = null,
    @field:Json(name = "point") val point: TomTomPointDto? = null,
    @field:Json(name = "instructionType") val instructionType: String? = null,
    @field:Json(name = "street") val street: String? = null,
    @field:Json(name = "message") val message: String? = null,
    @field:Json(name = "combinedDescription") val combinedDescription: String? = null
)
