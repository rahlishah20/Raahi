package com.example.domain.safety

object SafetySignalNormalizer {

    /**
     * Normalizes a raw numeric value within [min, max] to a standard 0.0 .. 1.0 range.
     * Guaranteed to return a value within [0.0, 1.0] and handles divide-by-zero safely.
     */
    fun normalizeLinear(
        value: Double,
        min: Double,
        max: Double,
        invert: Boolean = false
    ): Double {
        if (value.isNaN() || value.isInfinite()) return 0.5
        if (min.isNaN() || max.isNaN() || min.isInfinite() || max.isInfinite()) return 0.5
        if (max <= min) {
            return if (invert) 0.0 else 1.0
        }

        val clamped = value.coerceIn(min, max)
        val normalized = (clamped - min) / (max - min)

        val result = if (invert) {
            1.0 - normalized
        } else {
            normalized
        }

        return result.coerceIn(0.0, 1.0)
    }

    /**
     * Normalizes ratio / percentage values directly (0.0 to 1.0).
     */
    fun normalizeRatio(ratio: Double): Double {
        if (ratio.isNaN() || ratio.isInfinite()) return 0.0
        return ratio.coerceIn(0.0, 1.0)
    }

    /**
     * Normalizes count of safe points within proximity (e.g. 0 to 5+ points).
     */
    fun normalizeSafePointCount(count: Int, targetMax: Int = 4): Double {
        if (count <= 0) return 0.1
        val ratio = count.toDouble() / targetMax.toDouble().coerceAtLeast(1.0)
        return ratio.coerceIn(0.0, 1.0)
    }

    /**
     * Computes a normalized contextual safety score (0.0 to 1.0) for a collection of nearby safe points.
     * Incorporates:
     * - Quantity (count of safe points)
     * - Proximity (nearest distance)
     * - Verification ratio (verified points give full confidence)
     * - Operating status (open gives higher reassurance than closed/unknown)
     */
    fun computeContextualSafePointScore(
        safePoints: List<com.example.domain.model.SafePoint>,
        maxCorridorDistanceMeters: Int = 1200
    ): Double {
        if (safePoints.isEmpty()) return 0.10

        val count = safePoints.size
        val countFactor = (count.toDouble() / 4.0).coerceIn(0.2, 1.0)

        // Nearest distance factor: closer points offer faster refuge (e.g. <= 300m = 1.0, 1200m = 0.5)
        val nearestDistance = safePoints.minOfOrNull {
            it.distanceFromRoute ?: it.distanceToRouteMeters
        } ?: maxCorridorDistanceMeters
        val proximityFactor = 1.0 - (nearestDistance.toDouble() / (maxCorridorDistanceMeters * 1.5)).coerceIn(0.0, 0.7)

        // Verification factor: percentage of points verified
        val verifiedCount = safePoints.count { it.verified }
        val verificationFactor = (verifiedCount.toDouble() / count.toDouble()).coerceIn(0.3, 1.0)

        // Opening status factor
        val openCount = safePoints.count { it.openingStatus == com.example.domain.model.SafePointOpeningStatus.OPEN }
        val openingFactor = ((openCount.toDouble() + 0.5 * (count - openCount)) / count.toDouble()).coerceIn(0.4, 1.0)

        val combinedScore = (0.35 * countFactor) + (0.25 * proximityFactor) + (0.20 * verificationFactor) + (0.20 * openingFactor)
        return combinedScore.coerceIn(0.0, 1.0)
    }

    /**
     * Normalizes incident risk rating (where 0.0 = high risk, 1.0 = low incident risk).
     */
    fun normalizeIncidentSafety(riskScore: Double): Double {
        if (riskScore.isNaN() || riskScore.isInfinite()) return 0.5
        // If raw risk is 0 (lowest risk) to 10 (highest risk), invert to safety score
        return (1.0 - (riskScore / 10.0)).coerceIn(0.0, 1.0)
    }
}
