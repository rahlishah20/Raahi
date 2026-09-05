package com.example.domain.model

data class KarachiDestination(
    val id: String,
    val name: String,
    val area: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val category: DestinationCategory = DestinationCategory.LANDMARK,
    val isRecent: Boolean = false,
    val isSaved: Boolean = false
)

enum class DestinationCategory {
    HOME,
    WORK,
    CAFE,
    SHOPPING,
    LANDMARK,
    TRANSIT,
    AREA
}

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float = 0f,
    val provider: String = "gps"
)

enum class LocationPermissionState {
    NOT_REQUESTED,
    GRANTED,
    DENIED,
    PERMANENTLY_DENIED
}
