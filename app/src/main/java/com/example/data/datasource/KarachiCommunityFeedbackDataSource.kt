package com.example.data.datasource

import com.example.data.safety.KarachiSafetyDataSource
import com.example.domain.model.CommunityDataConfidence
import com.example.domain.model.CommunityFeedback
import com.example.domain.model.CommunitySafetyAggregate
import com.example.domain.model.CommunitySafetyContextTag
import com.example.domain.model.CommunitySafetyRating
import com.example.domain.model.DataSourceType
import com.example.domain.model.LatLngPoint
import java.util.concurrent.CopyOnWriteArrayList

object KarachiCommunityFeedbackDataSource {

    // Valid Karachi Metropolitan Bounding Box
    private const val KARACHI_MIN_LAT = 24.70
    private const val KARACHI_MAX_LAT = 25.20
    private const val KARACHI_MIN_LNG = 66.80
    private const val KARACHI_MAX_LNG = 67.40

    /**
     * Seeded prototype dataset for Karachi corridors.
     * All items explicitly marked as seeded prototype data.
     */
    val SEEDED_COMMUNITY_FEEDBACK = listOf(
        // Khayaban-e-Iqbal / Clifton Corridor (Positive perceived safety)
        CommunityFeedback(
            id = "seed_fb_clifton_1",
            journeyId = "j_seed_101",
            routeId = "route-clifton-saddar",
            latitude = 24.8250,
            longitude = 67.0330,
            safetyRating = CommunitySafetyRating.SAFE,
            contextualTags = listOf(CommunitySafetyContextTag.GOOD_VISIBILITY, CommunitySafetyContextTag.COMFORTABLE),
            createdAt = System.currentTimeMillis() - 86400000L * 2, // 2 days ago
            sourceType = DataSourceType.PROTOTYPE,
            isSeeded = true
        ),
        CommunityFeedback(
            id = "seed_fb_clifton_2",
            journeyId = "j_seed_102",
            routeId = "route-clifton-saddar",
            latitude = 24.8210,
            longitude = 67.0315,
            safetyRating = CommunitySafetyRating.SAFE,
            contextualTags = listOf(CommunitySafetyContextTag.GOOD_VISIBILITY, CommunitySafetyContextTag.COMFORTABLE),
            createdAt = System.currentTimeMillis() - 86400000L * 4,
            sourceType = DataSourceType.PROTOTYPE,
            isSeeded = true
        ),
        CommunityFeedback(
            id = "seed_fb_clifton_3",
            journeyId = "j_seed_103",
            routeId = "route-clifton-saddar",
            latitude = 24.8450,
            longitude = 67.0250,
            safetyRating = CommunitySafetyRating.SAFE,
            contextualTags = listOf(CommunitySafetyContextTag.COMFORTABLE),
            createdAt = System.currentTimeMillis() - 86400000L * 6,
            sourceType = DataSourceType.PROTOTYPE,
            isSeeded = true
        ),
        CommunityFeedback(
            id = "seed_fb_clifton_4",
            journeyId = "j_seed_104",
            routeId = "route-clifton-saddar",
            latitude = 24.8310,
            longitude = 67.0345,
            safetyRating = CommunitySafetyRating.NEUTRAL,
            contextualTags = listOf(CommunitySafetyContextTag.CROWDED),
            createdAt = System.currentTimeMillis() - 86400000L * 7,
            sourceType = DataSourceType.PROTOTYPE,
            isSeeded = true
        ),

        // Shahrah-e-Faisal Transit Corridor
        CommunityFeedback(
            id = "seed_fb_faisal_1",
            journeyId = "j_seed_201",
            routeId = "route-shahrah-faisal",
            latitude = 24.8640,
            longitude = 67.0620,
            safetyRating = CommunitySafetyRating.SAFE,
            contextualTags = listOf(CommunitySafetyContextTag.GOOD_VISIBILITY),
            createdAt = System.currentTimeMillis() - 86400000L * 1,
            sourceType = DataSourceType.PROTOTYPE,
            isSeeded = true
        ),
        CommunityFeedback(
            id = "seed_fb_faisal_2",
            journeyId = "j_seed_202",
            routeId = "route-shahrah-faisal",
            latitude = 24.8620,
            longitude = 67.0680,
            safetyRating = CommunitySafetyRating.SAFE,
            contextualTags = listOf(CommunitySafetyContextTag.COMFORTABLE, CommunitySafetyContextTag.GOOD_VISIBILITY),
            createdAt = System.currentTimeMillis() - 86400000L * 3,
            sourceType = DataSourceType.PROTOTYPE,
            isSeeded = true
        ),
        CommunityFeedback(
            id = "seed_fb_faisal_3",
            journeyId = "j_seed_203",
            routeId = "route-shahrah-faisal",
            latitude = 24.8730,
            longitude = 67.0640,
            safetyRating = CommunitySafetyRating.NEUTRAL,
            contextualTags = listOf(CommunitySafetyContextTag.CROWDED),
            createdAt = System.currentTimeMillis() - 86400000L * 5,
            sourceType = DataSourceType.PROTOTYPE,
            isSeeded = true
        ),

        // Mai Kolachi Bypass (Lower perceived safety / isolated segment perception)
        CommunityFeedback(
            id = "seed_fb_kolachi_1",
            journeyId = "j_seed_301",
            routeId = "route-mai-kolachi",
            latitude = 24.8300,
            longitude = 66.9950,
            safetyRating = CommunitySafetyRating.UNSAFE,
            contextualTags = listOf(CommunitySafetyContextTag.POOR_LIGHTING, CommunitySafetyContextTag.ISOLATED),
            createdAt = System.currentTimeMillis() - 86400000L * 2,
            sourceType = DataSourceType.PROTOTYPE,
            isSeeded = true
        ),
        CommunityFeedback(
            id = "seed_fb_kolachi_2",
            journeyId = "j_seed_302",
            routeId = "route-mai-kolachi",
            latitude = 24.8380,
            longitude = 67.0010,
            safetyRating = CommunitySafetyRating.NEUTRAL,
            contextualTags = listOf(CommunitySafetyContextTag.LOW_ACTIVITY, CommunitySafetyContextTag.POOR_LIGHTING),
            createdAt = System.currentTimeMillis() - 86400000L * 5,
            sourceType = DataSourceType.PROTOTYPE,
            isSeeded = true
        ),
        CommunityFeedback(
            id = "seed_fb_kolachi_3",
            journeyId = "j_seed_303",
            routeId = "route-mai-kolachi",
            latitude = 24.8260,
            longitude = 66.9900,
            safetyRating = CommunitySafetyRating.UNSAFE,
            contextualTags = listOf(CommunitySafetyContextTag.ISOLATED),
            createdAt = System.currentTimeMillis() - 86400000L * 8,
            sourceType = DataSourceType.PROTOTYPE,
            isSeeded = true
        ),

        // Saddar Bazaar Commercial Zone
        CommunityFeedback(
            id = "seed_fb_saddar_1",
            journeyId = "j_seed_401",
            routeId = "route-saddar-bazaar",
            latitude = 24.8580,
            longitude = 67.0142,
            safetyRating = CommunitySafetyRating.SAFE,
            contextualTags = listOf(CommunitySafetyContextTag.CROWDED, CommunitySafetyContextTag.GOOD_VISIBILITY),
            createdAt = System.currentTimeMillis() - 86400000L * 1,
            sourceType = DataSourceType.PROTOTYPE,
            isSeeded = true
        ),
        CommunityFeedback(
            id = "seed_fb_saddar_2",
            journeyId = "j_seed_402",
            routeId = "route-saddar-bazaar",
            latitude = 24.8552,
            longitude = 67.0189,
            safetyRating = CommunitySafetyRating.NEUTRAL,
            contextualTags = listOf(CommunitySafetyContextTag.CROWDED),
            createdAt = System.currentTimeMillis() - 86400000L * 4,
            sourceType = DataSourceType.PROTOTYPE,
            isSeeded = true
        )
    )

    private val feedbackStore = CopyOnWriteArrayList<CommunityFeedback>().apply {
        addAll(SEEDED_COMMUNITY_FEEDBACK)
    }

    /**
     * Validates that coordinates fall within Karachi metropolitan area.
     */
    fun isWithinKarachi(lat: Double, lng: Double): Boolean {
        if (lat.isNaN() || lng.isNaN() || lat.isInfinite() || lng.isInfinite()) return false
        return lat in KARACHI_MIN_LAT..KARACHI_MAX_LAT && lng in KARACHI_MIN_LNG..KARACHI_MAX_LNG
    }

    /**
     * Submits a new community feedback record into in-memory store.
     */
    fun addFeedback(feedback: CommunityFeedback): Result<Unit> {
        if (!isWithinKarachi(feedback.latitude, feedback.longitude)) {
            return Result.failure(IllegalArgumentException("Coordinates (${feedback.latitude}, ${feedback.longitude}) are outside metropolitan Karachi."))
        }

        // Prevent exact duplicate submission for same journey
        if (feedback.journeyId != null && feedbackStore.any { it.journeyId == feedback.journeyId }) {
            return Result.failure(IllegalStateException("Feedback already submitted for journey ${feedback.journeyId}."))
        }

        feedbackStore.add(0, feedback)
        return Result.success(Unit)
    }

    fun getAllFeedback(): List<CommunityFeedback> = feedbackStore.toList()

    fun clearNonSeededFeedback() {
        feedbackStore.removeIf { !it.isSeeded }
    }

    /**
     * Retrieves feedback matching route ID or points along the polyline within corridor radius.
     */
    fun getFeedbackForRoute(
        routeId: String,
        points: List<LatLngPoint>,
        corridorRadiusMeters: Int = 1200
    ): List<CommunityFeedback> {
        val directRouteMatches = if (routeId.isNotBlank()) {
            feedbackStore.filter { it.routeId != null && it.routeId.equals(routeId, ignoreCase = true) }
        } else {
            emptyList()
        }

        if (directRouteMatches.isNotEmpty() && points.isEmpty()) {
            return directRouteMatches
        }

        // Spatial matching along corridor
        val spatialMatches = if (points.isNotEmpty()) {
            feedbackStore.filter { item ->
                var minDistance = Int.MAX_VALUE
                for (p in points) {
                    val d = KarachiSafetyDataSource.calculateDistanceMeters(p.latitude, p.longitude, item.latitude, item.longitude)
                    if (d < minDistance) {
                        minDistance = d
                    }
                }
                minDistance <= corridorRadiusMeters
            }
        } else {
            emptyList()
        }

        // Combine unique
        val combined = (directRouteMatches + spatialMatches).distinctBy { it.id }
        return combined.sortedByDescending { it.createdAt }
    }

    fun getFeedbackForLocation(
        lat: Double,
        lng: Double,
        radiusMeters: Int = 2000
    ): List<CommunityFeedback> {
        if (!isWithinKarachi(lat, lng)) return emptyList()

        return feedbackStore.filter { item ->
            val dist = KarachiSafetyDataSource.calculateDistanceMeters(lat, lng, item.latitude, item.longitude)
            dist <= radiusMeters
        }.sortedByDescending { it.createdAt }
    }

    /**
     * Deterministic aggregation logic.
     * Computes normalized score (0.0 to 1.0), counts, confidence, and contextual summary.
     */
    fun aggregateFeedback(feedbackList: List<CommunityFeedback>): CommunitySafetyAggregate {
        if (feedbackList.isEmpty()) {
            return CommunitySafetyAggregate(
                totalReports = 0,
                safeCount = 0,
                neutralCount = 0,
                unsafeCount = 0,
                normalizedPerceivedScore = 0.50,
                confidence = CommunityDataConfidence.NO_DATA,
                confidenceScore = 0.0,
                topContextTags = emptyList(),
                summary = "No community perceived-safety reports available for this corridor",
                isAvailable = false
            )
        }

        val total = feedbackList.size
        val safeCount = feedbackList.count { it.safetyRating == CommunitySafetyRating.SAFE }
        val neutralCount = feedbackList.count { it.safetyRating == CommunitySafetyRating.NEUTRAL }
        val unsafeCount = feedbackList.count { it.safetyRating == CommunitySafetyRating.UNSAFE }

        // Temporal relevance: apply gentle decay weight for older reports (> 30 days)
        val now = System.currentTimeMillis()
        val thirtyDaysMs = 86400000L * 30

        var weightedSum = 0.0
        var totalWeight = 0.0

        feedbackList.forEach { fb ->
            val age = (now - fb.createdAt).coerceAtLeast(0L)
            val timeWeight = if (age <= thirtyDaysMs) 1.0 else 0.70
            val ratingScore = fb.safetyRating.scoreValue
            weightedSum += ratingScore * timeWeight
            totalWeight += timeWeight
        }

        val normalizedScore = if (totalWeight > 0.0) {
            (weightedSum / totalWeight).coerceIn(0.0, 1.0)
        } else {
            0.50
        }

        val (confidenceEnum, confidenceScore) = when {
            total >= 6 -> Pair(CommunityDataConfidence.HIGH_CONFIDENCE, 0.95)
            total >= 3 -> Pair(CommunityDataConfidence.MEDIUM_CONFIDENCE, 0.70)
            total >= 1 -> Pair(CommunityDataConfidence.LOW_CONFIDENCE, 0.40)
            else -> Pair(CommunityDataConfidence.NO_DATA, 0.0)
        }

        // Aggregate top context tags
        val tagCounts = mutableMapOf<CommunitySafetyContextTag, Int>()
        feedbackList.flatMap { it.contextualTags }.forEach { tag ->
            tagCounts[tag] = (tagCounts[tag] ?: 0) + 1
        }
        val topTags = tagCounts.entries
            .sortedByDescending { it.value }
            .map { Pair(it.key, it.value) }

        val summary = buildAggregateSummary(
            total = total,
            safeCount = safeCount,
            neutralCount = neutralCount,
            unsafeCount = unsafeCount,
            score = normalizedScore,
            topTags = topTags
        )

        return CommunitySafetyAggregate(
            totalReports = total,
            safeCount = safeCount,
            neutralCount = neutralCount,
            unsafeCount = unsafeCount,
            normalizedPerceivedScore = normalizedScore,
            confidence = confidenceEnum,
            confidenceScore = confidenceScore,
            topContextTags = topTags,
            summary = summary,
            isAvailable = true
        )
    }

    private fun buildAggregateSummary(
        total: Int,
        safeCount: Int,
        neutralCount: Int,
        unsafeCount: Int,
        score: Double,
        topTags: List<Pair<CommunitySafetyContextTag, Int>>
    ): String {
        val safePct = (safeCount * 100 / total)
        val tagHighlight = topTags.firstOrNull()?.first?.label?.let { " (${it.lowercase()} noted by travelers)" } ?: ""

        return when {
            score >= 0.75 -> "Community travelers report a positive perceived safety profile ($safePct% felt safe, $total report${if (total > 1) "s" else ""})$tagHighlight"
            score >= 0.45 -> "Mixed community perceived safety reports ($safePct% felt safe, $total report${if (total > 1) "s" else ""})$tagHighlight"
            else -> "Community feedback indicates a lower perceived-safety profile ($total report${if (total > 1) "s" else ""})$tagHighlight"
        }
    }
}
