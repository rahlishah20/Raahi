package com.example.data.datasource

import com.example.domain.model.DemoEvent
import com.example.domain.model.DemoEventType
import com.example.domain.model.SafetyFactorType
import java.util.concurrent.CopyOnWriteArrayList

object KarachiDynamicConditionDataSource {

    private val activeEvents = CopyOnWriteArrayList<DemoEvent>()

    /**
     * Applies a new or updated DemoEvent into active simulation state.
     */
    fun applyEvent(event: DemoEvent): Result<Unit> {
        return try {
            // Remove any existing event with same ID
            activeEvents.removeIf { it.id == event.id }
            if (event.active) {
                activeEvents.add(event)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun addEvent(event: DemoEvent): Result<Unit> = applyEvent(event)

    /**
     * Resets and clears all active simulated demo events.
     */
    fun resetEvents(): Result<Unit> {
        return try {
            activeEvents.clear()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun clearAllEvents(): Result<Unit> = resetEvents()

    /**
     * Returns a snapshot of all currently active demo events.
     */
    fun getActiveEvents(): List<DemoEvent> {
        return activeEvents.filter { it.active }
    }

    /**
     * Checks if any active demo event matches the target route or corridor key.
     */
    fun matchesTarget(event: DemoEvent, routeId: String, corridorKey: String): Boolean {
        if (!event.active) return false
        val targetRoute = event.targetRouteId
        val targetCorridor = event.targetCorridorKey

        if (targetRoute != null && targetRoute.isNotBlank()) {
            if (routeId.contains(targetRoute, ignoreCase = true) || targetRoute.contains(routeId, ignoreCase = true)) {
                return true
            }
        }

        if (targetCorridor != null && targetCorridor.isNotBlank()) {
            if (corridorKey.contains(targetCorridor, ignoreCase = true) || targetCorridor.contains(corridorKey, ignoreCase = true)) {
                return true
            }
        }

        // If both are null, it applies globally across all corridors
        return targetRoute == null && targetCorridor == null
    }

    /**
     * Computes the adjusted factor value (0.0 to 1.0) given the original baseline value and factor type.
     */
    fun getAdjustedFactorValue(
        factorType: SafetyFactorType,
        baseValue: Double,
        routeId: String = "",
        corridorKey: String = ""
    ): Pair<Double, String?> {
        val matchingEvents = activeEvents.filter { event ->
            event.active && event.targetFactorType == factorType && matchesTarget(event, routeId, corridorKey)
        }

        if (matchingEvents.isEmpty()) {
            return Pair(baseValue.coerceIn(0.0, 1.0), null)
        }

        // Apply most recent matching event
        val latestEvent = matchingEvents.last()
        val adjustedValue = when {
            latestEvent.rawValueOverride != null -> latestEvent.rawValueOverride
            latestEvent.valueMultiplier != null -> (baseValue * latestEvent.valueMultiplier).coerceIn(0.0, 1.0)
            else -> baseValue
        }

        val note = "Simulated contextual condition active: ${latestEvent.description.ifBlank { latestEvent.type.displayName }}"
        return Pair(adjustedValue.coerceIn(0.0, 1.0), note)
    }

    /**
     * Standard deterministic demo scenarios
     */
    fun createScenarioActivityDrop(targetRouteId: String = "route_khayaban_iqbal", targetCorridorKey: String = "khayaban_iqbal"): List<DemoEvent> {
        return listOf(
            DemoEvent(
                id = "demo_event_business_drop",
                type = DemoEventType.BUSINESS_ACTIVITY_CHANGE,
                targetRouteId = targetRouteId,
                targetCorridorKey = targetCorridorKey,
                rawValueOverride = 0.28,
                description = "Simulated late-night closing of commercial storefronts along Khayaban-e-Iqbal"
            ),
            DemoEvent(
                id = "demo_event_pedestrian_drop",
                type = DemoEventType.PEDESTRIAN_ACTIVITY_CHANGE,
                targetRouteId = targetRouteId,
                targetCorridorKey = targetCorridorKey,
                rawValueOverride = 0.26,
                description = "Simulated low foot traffic along Khayaban-e-Iqbal corridor"
            )
        )
    }

    fun createScenarioMultiSignalChange(targetRouteId: String = "route_khayaban_iqbal", targetCorridorKey: String = "khayaban_iqbal"): List<DemoEvent> {
        return listOf(
            DemoEvent(
                id = "demo_event_multi_business",
                type = DemoEventType.BUSINESS_ACTIVITY_CHANGE,
                targetRouteId = targetRouteId,
                targetCorridorKey = targetCorridorKey,
                rawValueOverride = 0.25,
                description = "Simulated commercial activity decrease"
            ),
            DemoEvent(
                id = "demo_event_multi_pedestrian",
                type = DemoEventType.PEDESTRIAN_ACTIVITY_CHANGE,
                targetRouteId = targetRouteId,
                targetCorridorKey = targetCorridorKey,
                rawValueOverride = 0.25,
                description = "Simulated pedestrian activity decrease"
            ),
            DemoEvent(
                id = "demo_event_multi_lighting",
                type = DemoEventType.LIGHTING_CHANGE,
                targetRouteId = targetRouteId,
                targetCorridorKey = targetCorridorKey,
                rawValueOverride = 0.50,
                description = "Simulated lighting maintenance outage"
            )
        )
    }
}
