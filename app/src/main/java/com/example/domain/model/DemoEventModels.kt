package com.example.domain.model

enum class DemoEventType(val displayName: String, val description: String) {
    BUSINESS_ACTIVITY_CHANGE(
        displayName = "Commercial Activity Change",
        description = "Simulated reduction or increase in open POIs and storefront activity"
    ),
    PEDESTRIAN_ACTIVITY_CHANGE(
        displayName = "Pedestrian Foot Traffic Change",
        description = "Simulated reduction or increase in pedestrian presence"
    ),
    LIGHTING_CHANGE(
        displayName = "Municipal Lighting Change",
        description = "Simulated variation in street lighting luminosity"
    ),
    INCIDENT_SIGNAL_CHANGE(
        displayName = "Incident Index Adjustment",
        description = "Simulated variation in historical zone incident profiling"
    ),
    SAFE_POINTS_CHANGE(
        displayName = "Safe Points Operating Status Change",
        description = "Simulated change in emergency safe haven accessibility"
    ),
    COMMUNITY_PERCEIVED_CHANGE(
        displayName = "Community Perception Shift",
        description = "Simulated change in traveler perceived safety sentiment"
    )
}

enum class RecalculationState {
    IDLE,
    RECALCULATING,
    UPDATED,
    ERROR
}

data class DemoEvent(
    val id: String,
    val type: DemoEventType,
    val targetRouteId: String? = null,
    val targetCorridorKey: String? = null,
    val targetFactorType: SafetyFactorType = when (type) {
        DemoEventType.BUSINESS_ACTIVITY_CHANGE -> SafetyFactorType.BUSINESS_ACTIVITY
        DemoEventType.PEDESTRIAN_ACTIVITY_CHANGE -> SafetyFactorType.PEDESTRIAN_ACTIVITY
        DemoEventType.LIGHTING_CHANGE -> SafetyFactorType.LIGHTING
        DemoEventType.INCIDENT_SIGNAL_CHANGE -> SafetyFactorType.HISTORICAL_INCIDENT
        DemoEventType.SAFE_POINTS_CHANGE -> SafetyFactorType.SAFE_POINTS
        DemoEventType.COMMUNITY_PERCEIVED_CHANGE -> SafetyFactorType.COMMUNITY_PERCEIVED
    },
    val rawValueOverride: Double? = null,
    val valueMultiplier: Double? = null,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val active: Boolean = true,
    val isPrototypeSimulation: Boolean = true
) {
    init {
        require(id.isNotBlank()) { "DemoEvent id must not be blank" }
        if (rawValueOverride != null) {
            require(rawValueOverride in 0.0..1.0) { "rawValueOverride must be in 0.0..1.0 range, was $rawValueOverride" }
        }
        if (valueMultiplier != null) {
            require(valueMultiplier >= 0.0) { "valueMultiplier must be non-negative, was $valueMultiplier" }
        }
    }
}

data class DynamicRecalculationResult(
    val recalculatedRoutes: List<RaahiRoute>,
    val previousRecommendedRouteId: String?,
    val newRecommendedRouteId: String?,
    val recommendationChanged: Boolean,
    val summaryNotice: String,
    val activeEventsCount: Int,
    val timestamp: Long = System.currentTimeMillis()
)
