package com.example.domain.model

enum class CommunitySafetyRating(val label: String, val scoreValue: Double) {
    SAFE("Felt Safe", 1.0),
    NEUTRAL("Felt Neutral", 0.5),
    UNSAFE("Felt Unsafe", 0.0)
}

enum class CommunitySafetyContextTag(val label: String) {
    GOOD_VISIBILITY("Good Visibility"),
    COMFORTABLE("Comfortable"),
    POOR_LIGHTING("Poor Lighting"),
    LOW_ACTIVITY("Low Activity"),
    CROWDED("Crowded"),
    ISOLATED("Isolated"),
    OTHER("Other")
}

enum class CommunityDataConfidence(val label: String, val numericConfidence: Double) {
    NO_DATA("No Community Data", 0.0),
    LOW_CONFIDENCE("Limited Community Reports", 0.40),
    MEDIUM_CONFIDENCE("Moderate Community Reports", 0.70),
    HIGH_CONFIDENCE("High Community Reports", 0.95)
}

data class CommunityFeedback(
    val id: String,
    val journeyId: String? = null,
    val routeId: String? = null,
    val latitude: Double,
    val longitude: Double,
    val safetyRating: CommunitySafetyRating,
    val contextualTags: List<CommunitySafetyContextTag> = emptyList(),
    val comment: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val sourceType: DataSourceType = DataSourceType.PROTOTYPE,
    val isSeeded: Boolean = false,
    val confidence: Double = 1.0
)

data class CommunitySafetyAggregate(
    val totalReports: Int,
    val safeCount: Int,
    val neutralCount: Int,
    val unsafeCount: Int,
    val normalizedPerceivedScore: Double, // 0.0 to 1.0
    val confidence: CommunityDataConfidence,
    val confidenceScore: Double,
    val topContextTags: List<Pair<CommunitySafetyContextTag, Int>> = emptyList(),
    val summary: String,
    val isAvailable: Boolean
) {
    val safePercentage: Double
        get() = if (totalReports > 0) (safeCount.toDouble() / totalReports) * 100.0 else 0.0

    val neutralPercentage: Double
        get() = if (totalReports > 0) (neutralCount.toDouble() / totalReports) * 100.0 else 0.0

    val unsafePercentage: Double
        get() = if (totalReports > 0) (unsafeCount.toDouble() / totalReports) * 100.0 else 0.0
}
