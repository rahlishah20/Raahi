package com.example.domain.model

enum class SafetyFactorType(val displayName: String) {
    LIGHTING("Lighting Coverage"),
    PEDESTRIAN_ACTIVITY("Pedestrian Activity"),
    BUSINESS_ACTIVITY("Commercial & POI Activity"),
    HISTORICAL_INCIDENT("Historical Incident Profile"),
    SAFE_POINTS("Verified Safe Points Proximity"),
    COMMUNITY_PERCEIVED("Community Perceived Safety")
}

enum class DataSourceType(val label: String) {
    REAL("Verified API / Real-time"),
    PROTOTYPE("Karachi Corridor Prototype Data"),
    UNAVAILABLE("Data Signal Unavailable")
}

enum class SafetyConfidence(val label: String) {
    HIGH("High Confidence"),
    MEDIUM("Medium Confidence"),
    LOW("Limited Confidence")
}

data class SafetySignal(
    val factorType: SafetyFactorType,
    val rawValue: Double,
    val normalizedScore: Double,
    val confidence: Double,
    val dataSourceType: DataSourceType,
    val sourceDescription: String,
    val isAvailable: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

data class SafetyFactorEvaluation(
    val factorType: SafetyFactorType,
    val title: String,
    val score: Int,
    val normalizedScore: Double,
    val weight: Double,
    val effectiveWeight: Double,
    val isAvailable: Boolean,
    val dataSourceType: DataSourceType,
    val summary: String,
    val isPositive: Boolean
)

enum class SafePointCategory(val displayName: String) {
    PHARMACY("Pharmacy"),
    HOSPITAL("Hospital"),
    PETROL_STATION("Petrol Station"),
    UNIVERSITY("University"),
    CAFE("Café"),
    HOTEL("Hotel"),
    SHOP("Shop"),
    POLICE_FACILITY("Police Facility"),
    WORKPLACE("Workplace")
}

enum class SafePointOpeningStatus(val label: String) {
    OPEN("Open"),
    CLOSED("Closed"),
    UNKNOWN("Hours Unknown")
}

enum class SafePointType(val label: String) {
    POLICE_STATION("Police Station"),
    HOSPITAL("24/7 Hospital / Emergency"),
    EMERGENCY_CENTRE("Rescue / Emergency Post"),
    TRANSIT_HUB("Transit / Security Post")
}

data class KarachiSafePoint(
    val id: String,
    val name: String,
    val category: SafePointCategory = SafePointCategory.POLICE_FACILITY,
    val latitude: Double,
    val longitude: Double,
    val verified: Boolean = true,
    val openingStatus: SafePointOpeningStatus = SafePointOpeningStatus.OPEN,
    val address: String = "",
    val distanceFromUser: Int? = null,
    val distanceFromRoute: Int? = null,
    val isSeeded: Boolean = true,
    val type: SafePointType = when (category) {
        SafePointCategory.POLICE_FACILITY -> SafePointType.POLICE_STATION
        SafePointCategory.HOSPITAL -> SafePointType.HOSPITAL
        else -> SafePointType.EMERGENCY_CENTRE
    },
    val distanceToRouteMeters: Int = distanceFromRoute ?: 0
) {
    constructor(
        id: String,
        name: String,
        type: SafePointType,
        latitude: Double,
        longitude: Double,
        address: String = "",
        distanceToRouteMeters: Int = 0
    ) : this(
        id = id,
        name = name,
        category = when (type) {
            SafePointType.POLICE_STATION -> SafePointCategory.POLICE_FACILITY
            SafePointType.HOSPITAL -> SafePointCategory.HOSPITAL
            SafePointType.EMERGENCY_CENTRE -> SafePointCategory.POLICE_FACILITY
            SafePointType.TRANSIT_HUB -> SafePointCategory.WORKPLACE
        },
        latitude = latitude,
        longitude = longitude,
        verified = true,
        openingStatus = SafePointOpeningStatus.OPEN,
        address = address,
        distanceFromUser = null,
        distanceFromRoute = distanceToRouteMeters,
        isSeeded = true,
        type = type,
        distanceToRouteMeters = distanceToRouteMeters
    )
}

typealias SafePoint = KarachiSafePoint

data class RouteSafetyProfile(
    val routeId: String,
    val relativeSafetyScore: Int,
    val confidence: SafetyConfidence,
    val confidenceScore: Double,
    val factorEvaluations: List<SafetyFactorEvaluation>,
    val positiveFactors: List<String>,
    val negativeFactors: List<String>,
    val explanation: String,
    val isRecommended: Boolean = false,
    val evaluatedAt: Long = System.currentTimeMillis(),
    val nearbySafePoints: List<KarachiSafePoint> = emptyList(),
    val isPrototypeData: Boolean = true,
    val communityAggregate: CommunitySafetyAggregate? = null
)
