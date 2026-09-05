package com.example.domain.model

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

enum class JourneyState {
    PLANNED,
    STARTING,
    ACTIVE,
    RECALCULATING,
    ARRIVED,
    COMPLETED,
    CANCELLED,
    ERROR;

    val isTerminal: Boolean
        get() = this == COMPLETED || this == CANCELLED || this == ERROR

    val isActiveOrRecalculating: Boolean
        get() = this == ACTIVE || this == RECALCULATING

    fun canTransitionTo(nextState: JourneyState): Boolean {
        return when (this) {
            PLANNED -> nextState in setOf(STARTING, ACTIVE, CANCELLED, ERROR)
            STARTING -> nextState in setOf(ACTIVE, CANCELLED, ERROR)
            ACTIVE -> nextState in setOf(RECALCULATING, ARRIVED, CANCELLED, ERROR)
            RECALCULATING -> nextState in setOf(ACTIVE, ARRIVED, CANCELLED, ERROR)
            ARRIVED -> nextState in setOf(COMPLETED, CANCELLED)
            COMPLETED -> false // Terminal
            CANCELLED -> false // Terminal
            ERROR -> false // Terminal
        }
    }
}

data class Journey(
    val id: String,
    val selectedRouteId: String,
    val origin: LatLngPoint,
    val destination: LatLngPoint,
    val originName: String,
    val destinationName: String,
    val route: RaahiRoute,
    val startTime: Long = System.currentTimeMillis(),
    val state: JourneyState = JourneyState.PLANNED,
    val currentPosition: LatLngPoint? = origin,
    val progressPercentage: Float = 0.0f,
    val remainingDistanceMeters: Int = route.distanceMeters,
    val remainingDurationSeconds: Long = route.durationSeconds,
    val currentSafetyProfile: RouteSafetyProfile? = route.safetyProfile,
    val selectedSafePoint: SafePoint? = null,
    val lastRecalculationTime: Long = System.currentTimeMillis(),
    val arrivedAt: Long? = null,
    val completedAt: Long? = null,
    val alternativeRoutes: List<RaahiRoute> = emptyList(),
    val currentInstruction: String? = null,
    val currentStepIndex: Int = 0,
    val suggestedAlternativeRoute: RaahiRoute? = null,
    val errorMessage: String? = null
) {
    val formattedRemainingDistance: String
        get() = if (remainingDistanceMeters >= 1000) {
            String.format(java.util.Locale.US, "%.1f km", remainingDistanceMeters / 1000.0)
        } else {
            "$remainingDistanceMeters m"
        }

    val formattedRemainingDuration: String
        get() {
            val minutes = (remainingDurationSeconds / 60).coerceAtLeast(1)
            return if (minutes >= 60) {
                val hours = minutes / 60
                val remMin = minutes % 60
                if (remMin > 0) "$hours hr $remMin min" else "$hours hr"
            } else {
                "$minutes min"
            }
        }
}

object JourneyProgressCalculator {
    const val DEFAULT_ARRIVAL_THRESHOLD_METERS = 75.0 // Configurable threshold

    /**
     * Calculates distance between two LatLng coordinates in meters using the Haversine formula.
     */
    fun calculateDistanceMeters(p1: LatLngPoint, p2: LatLngPoint): Double {
        val r = 6371000.0 // Earth radius in meters
        val dLat = Math.toRadians(p2.latitude - p1.latitude)
        val dLon = Math.toRadians(p2.longitude - p1.longitude)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(p1.latitude)) * cos(Math.toRadians(p2.latitude)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    /**
     * Checks if current position is within arrival threshold of the destination.
     */
    fun isArrivedAtDestination(
        currentLocation: LatLngPoint?,
        destination: LatLngPoint,
        thresholdMeters: Double = DEFAULT_ARRIVAL_THRESHOLD_METERS
    ): Boolean {
        if (currentLocation == null) return false
        if (currentLocation.latitude.isNaN() || currentLocation.longitude.isNaN()) return false
        val dist = calculateDistanceMeters(currentLocation, destination)
        return dist <= thresholdMeters
    }

    /**
     * Finds the closest polyline point index to the user's current location,
     * and calculates remaining distance and progress.
     */
    fun calculateProgress(
        currentLocation: LatLngPoint?,
        route: RaahiRoute
    ): ProgressResult {
        if (currentLocation == null || route.polylinePoints.isEmpty()) {
            return ProgressResult(
                progressPercentage = 0.0f,
                remainingDistanceMeters = route.distanceMeters,
                remainingDurationSeconds = route.durationSeconds,
                closestPointIndex = 0,
                isArrived = false
            )
        }

        val destination = route.destination
        val distToDest = calculateDistanceMeters(currentLocation, destination)
        if (distToDest <= DEFAULT_ARRIVAL_THRESHOLD_METERS) {
            return ProgressResult(
                progressPercentage = 1.0f,
                remainingDistanceMeters = 0,
                remainingDurationSeconds = 0L,
                closestPointIndex = (route.polylinePoints.size - 1).coerceAtLeast(0),
                isArrived = true
            )
        }

        val points = route.polylinePoints
        var minDistance = Double.MAX_VALUE
        var closestIdx = 0

        for (i in points.indices) {
            val d = calculateDistanceMeters(currentLocation, points[i])
            if (d < minDistance) {
                minDistance = d
                closestIdx = i
            }
        }

        // Calculate remaining distance along remaining polyline segments
        var remainingDist = 0.0
        for (i in closestIdx until points.size - 1) {
            remainingDist += calculateDistanceMeters(points[i], points[i + 1])
        }

        // Factor in distance from current location to closest segment
        val distToClosest = calculateDistanceMeters(currentLocation, points[closestIdx])
        if (closestIdx < points.size - 1) {
            remainingDist += distToClosest
        }

        val totalRouteDist = route.distanceMeters.toDouble().coerceAtLeast(1.0)
        val clampedRemaining = remainingDist.toInt().coerceIn(0, route.distanceMeters)
        val progress = (1.0 - (clampedRemaining.toDouble() / totalRouteDist)).toFloat().coerceIn(0.0f, 1.0f)

        // Estimated remaining duration based on speed ratio
        val remainingDur = ((clampedRemaining.toDouble() / totalRouteDist) * route.durationSeconds).toLong().coerceAtLeast(60L)

        return ProgressResult(
            progressPercentage = progress,
            remainingDistanceMeters = clampedRemaining,
            remainingDurationSeconds = remainingDur,
            closestPointIndex = closestIdx,
            isArrived = false
        )
    }

    data class ProgressResult(
        val progressPercentage: Float,
        val remainingDistanceMeters: Int,
        val remainingDurationSeconds: Long,
        val closestPointIndex: Int,
        val isArrived: Boolean
    )
}
