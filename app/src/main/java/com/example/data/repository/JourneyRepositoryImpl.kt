package com.example.data.repository

import com.example.domain.model.Journey
import com.example.domain.model.JourneyProgressCalculator
import com.example.domain.model.JourneyState
import com.example.domain.model.LatLngPoint
import com.example.domain.model.RaahiRoute
import com.example.domain.model.RouteSafetyProfile
import com.example.domain.model.SafePoint
import com.example.domain.repository.JourneyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class JourneyRepositoryImpl : JourneyRepository {

    private val _currentJourney = MutableStateFlow<Journey?>(null)
    private val _journeyHistory = MutableStateFlow<List<Journey>>(emptyList())

    override fun getCurrentJourney(): Flow<Journey?> = _currentJourney.asStateFlow()

    override fun getJourneyHistory(): Flow<List<Journey>> = _journeyHistory.asStateFlow()

    override suspend fun planJourney(
        route: RaahiRoute,
        alternatives: List<RaahiRoute>
    ): Result<Journey> {
        val journeyId = "journey_${UUID.randomUUID().toString().take(8)}"
        val initialInstruction = generateInstructionForStep(route, 0)

        val journey = Journey(
            id = journeyId,
            selectedRouteId = route.id,
            origin = route.origin,
            destination = route.destination,
            originName = route.originName,
            destinationName = route.destinationName,
            route = route,
            startTime = System.currentTimeMillis(),
            state = JourneyState.PLANNED,
            currentPosition = route.origin,
            progressPercentage = 0.0f,
            remainingDistanceMeters = route.distanceMeters,
            remainingDurationSeconds = route.durationSeconds,
            currentSafetyProfile = route.safetyProfile,
            selectedSafePoint = route.safetyProfile?.nearbySafePoints?.firstOrNull(),
            lastRecalculationTime = System.currentTimeMillis(),
            alternativeRoutes = alternatives,
            currentInstruction = initialInstruction,
            currentStepIndex = 0
        )

        _currentJourney.value = journey
        return Result.success(journey)
    }

    override suspend fun startJourney(journeyId: String): Result<Journey> {
        val current = _currentJourney.value
            ?: return Result.failure(IllegalStateException("No active journey found"))

        if (current.id != journeyId) {
            return Result.failure(IllegalArgumentException("Journey ID mismatch: expected ${current.id}, got $journeyId"))
        }

        if (!current.state.canTransitionTo(JourneyState.ACTIVE)) {
            return Result.failure(IllegalStateException("Cannot start journey from state: ${current.state}"))
        }

        val updated = current.copy(
            state = JourneyState.ACTIVE,
            startTime = System.currentTimeMillis(),
            currentInstruction = generateInstructionForStep(current.route, 0)
        )
        _currentJourney.value = updated
        return Result.success(updated)
    }

    override suspend fun updateLocation(journeyId: String, location: LatLngPoint): Result<Journey> {
        val current = _currentJourney.value
            ?: return Result.failure(IllegalStateException("No active journey found"))

        if (current.id != journeyId) {
            return Result.failure(IllegalArgumentException("Journey ID mismatch"))
        }

        if (current.state.isTerminal) {
            return Result.failure(IllegalStateException("Cannot update location for completed/cancelled journey in state: ${current.state}"))
        }

        val progressResult = JourneyProgressCalculator.calculateProgress(location, current.route)
        val isArrived = progressResult.isArrived || JourneyProgressCalculator.isArrivedAtDestination(location, current.destination)

        val nextState = if (isArrived && (current.state == JourneyState.ACTIVE || current.state == JourneyState.RECALCULATING)) {
            JourneyState.ARRIVED
        } else {
            current.state
        }

        val stepIdx = determineStepIndex(progressResult.closestPointIndex, current.route)
        val instruction = if (isArrived) {
            "You have arrived at ${current.destinationName}"
        } else {
            generateInstructionForStep(current.route, stepIdx)
        }

        val updated = current.copy(
            currentPosition = location,
            progressPercentage = progressResult.progressPercentage,
            remainingDistanceMeters = progressResult.remainingDistanceMeters,
            remainingDurationSeconds = progressResult.remainingDurationSeconds,
            state = nextState,
            arrivedAt = if (isArrived && current.arrivedAt == null) System.currentTimeMillis() else current.arrivedAt,
            currentInstruction = instruction,
            currentStepIndex = stepIdx
        )

        _currentJourney.value = updated
        return Result.success(updated)
    }

    override suspend fun recalculateSafety(
        journeyId: String,
        newSafetyProfile: RouteSafetyProfile,
        recalculatedAlternatives: List<RaahiRoute>
    ): Result<Journey> {
        val current = _currentJourney.value
            ?: return Result.failure(IllegalStateException("No active journey found"))

        if (current.id != journeyId) {
            return Result.failure(IllegalArgumentException("Journey ID mismatch"))
        }

        // Compare alternatives to find if another route has a stronger safety profile
        val strongerAlt = recalculatedAlternatives.firstOrNull {
            it.id != current.selectedRouteId &&
                    (it.safetyProfile?.relativeSafetyScore ?: 0) > (newSafetyProfile.relativeSafetyScore + 5)
        }

        val updated = current.copy(
            currentSafetyProfile = newSafetyProfile,
            route = current.route.copy(safetyProfile = newSafetyProfile),
            alternativeRoutes = recalculatedAlternatives,
            suggestedAlternativeRoute = strongerAlt,
            lastRecalculationTime = System.currentTimeMillis()
        )

        _currentJourney.value = updated
        return Result.success(updated)
    }

    override suspend fun switchRoute(journeyId: String, newRoute: RaahiRoute): Result<Journey> {
        val current = _currentJourney.value
            ?: return Result.failure(IllegalStateException("No active journey found"))

        if (current.id != journeyId) {
            return Result.failure(IllegalArgumentException("Journey ID mismatch"))
        }

        val currentPos = current.currentPosition ?: current.origin
        val progressResult = JourneyProgressCalculator.calculateProgress(currentPos, newRoute)

        val updated = current.copy(
            selectedRouteId = newRoute.id,
            route = newRoute,
            currentSafetyProfile = newRoute.safetyProfile,
            remainingDistanceMeters = progressResult.remainingDistanceMeters,
            remainingDurationSeconds = progressResult.remainingDurationSeconds,
            progressPercentage = progressResult.progressPercentage,
            suggestedAlternativeRoute = null,
            currentInstruction = generateInstructionForStep(newRoute, 0),
            currentStepIndex = 0
        )

        _currentJourney.value = updated
        return Result.success(updated)
    }

    override suspend fun selectSafePoint(journeyId: String, safePoint: SafePoint?): Result<Journey> {
        val current = _currentJourney.value
            ?: return Result.failure(IllegalStateException("No active journey found"))

        if (current.id != journeyId) {
            return Result.failure(IllegalArgumentException("Journey ID mismatch"))
        }

        val updated = current.copy(selectedSafePoint = safePoint)
        _currentJourney.value = updated
        return Result.success(updated)
    }

    override suspend fun markArrived(journeyId: String): Result<Journey> {
        val current = _currentJourney.value
            ?: return Result.failure(IllegalStateException("No active journey found"))

        if (current.id != journeyId) {
            return Result.failure(IllegalArgumentException("Journey ID mismatch"))
        }

        if (!current.state.canTransitionTo(JourneyState.ARRIVED)) {
            return Result.failure(IllegalStateException("Cannot transition to ARRIVED from ${current.state}"))
        }

        val updated = current.copy(
            state = JourneyState.ARRIVED,
            progressPercentage = 1.0f,
            remainingDistanceMeters = 0,
            remainingDurationSeconds = 0L,
            arrivedAt = System.currentTimeMillis(),
            currentInstruction = "You have arrived at ${current.destinationName}"
        )

        _currentJourney.value = updated
        return Result.success(updated)
    }

    override suspend fun completeJourney(journeyId: String): Result<Journey> {
        val current = _currentJourney.value
            ?: return Result.failure(IllegalStateException("No active journey found"))

        if (current.id != journeyId) {
            return Result.failure(IllegalArgumentException("Journey ID mismatch"))
        }

        if (!current.state.canTransitionTo(JourneyState.COMPLETED)) {
            return Result.failure(IllegalStateException("Cannot transition to COMPLETED from ${current.state}"))
        }

        val completed = current.copy(
            state = JourneyState.COMPLETED,
            completedAt = System.currentTimeMillis(),
            currentInstruction = "Journey completed"
        )

        _currentJourney.value = completed
        _journeyHistory.value = listOf(completed) + _journeyHistory.value
        return Result.success(completed)
    }

    override suspend fun cancelJourney(journeyId: String): Result<Journey> {
        val current = _currentJourney.value
            ?: return Result.failure(IllegalStateException("No active journey found"))

        if (current.id != journeyId) {
            return Result.failure(IllegalArgumentException("Journey ID mismatch"))
        }

        if (!current.state.canTransitionTo(JourneyState.CANCELLED)) {
            return Result.failure(IllegalStateException("Cannot transition to CANCELLED from ${current.state}"))
        }

        val cancelled = current.copy(
            state = JourneyState.CANCELLED,
            completedAt = System.currentTimeMillis(),
            currentInstruction = "Navigation cancelled"
        )

        _currentJourney.value = cancelled
        return Result.success(cancelled)
    }

    override suspend fun getJourneyById(journeyId: String): Journey? {
        val current = _currentJourney.value
        if (current?.id == journeyId) return current
        return _journeyHistory.value.firstOrNull { it.id == journeyId }
    }

    override suspend fun clearActiveJourney() {
        _currentJourney.value = null
    }

    private fun determineStepIndex(closestPointIdx: Int, route: RaahiRoute): Int {
        val totalPts = route.polylinePoints.size
        if (totalPts <= 1) return 0
        val ratio = closestPointIdx.toDouble() / totalPts.toDouble()
        val numSteps = if (route.legs.isNotEmpty() && route.legs[0].steps.isNotEmpty()) {
            route.legs[0].steps.size
        } else {
            3
        }
        return (ratio * numSteps).toInt().coerceIn(0, (numSteps - 1).coerceAtLeast(0))
    }

    private fun generateInstructionForStep(route: RaahiRoute, stepIndex: Int): String {
        val leg = route.legs.firstOrNull()
        if (leg != null && leg.steps.isNotEmpty()) {
            val step = leg.steps.getOrNull(stepIndex) ?: leg.steps.last()
            return step.instruction
        }

        // Descriptive Karachi Navigation Guidance
        val corridorName = route.title.replace("Via ", "").trim()
        return when (stepIndex) {
            0 -> "Head towards $corridorName. Maintain situational awareness."
            1 -> "Continue on $corridorName. High commercial visibility zone."
            2 -> "Follow signs towards ${route.destinationName} along well-lit corridor."
            else -> "Approaching ${route.destinationName} on the right."
        }
    }
}
