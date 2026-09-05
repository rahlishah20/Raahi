package com.example

import com.example.domain.safety.SafetySignalNormalizer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SafetySignalNormalizerTest {

    @Test
    fun `normalizeLinear maps min and max correctly`() {
        val atMin = SafetySignalNormalizer.normalizeLinear(10.0, 10.0, 50.0)
        assertEquals(0.0, atMin, 0.001)

        val atMax = SafetySignalNormalizer.normalizeLinear(50.0, 10.0, 50.0)
        assertEquals(1.0, atMax, 0.001)

        val atMid = SafetySignalNormalizer.normalizeLinear(30.0, 10.0, 50.0)
        assertEquals(0.5, atMid, 0.001)
    }

    @Test
    fun `normalizeLinear clamps values out of bounds`() {
        val belowMin = SafetySignalNormalizer.normalizeLinear(5.0, 10.0, 50.0)
        assertEquals(0.0, belowMin, 0.001)

        val aboveMax = SafetySignalNormalizer.normalizeLinear(70.0, 10.0, 50.0)
        assertEquals(1.0, aboveMax, 0.001)
    }

    @Test
    fun `normalizeLinear handles edge cases without throwing`() {
        val sameMinMax = SafetySignalNormalizer.normalizeLinear(10.0, 10.0, 10.0)
        assertTrue(sameMinMax in 0.0..1.0)

        val nanValue = SafetySignalNormalizer.normalizeLinear(Double.NaN, 0.0, 100.0)
        assertTrue(nanValue in 0.0..1.0)

        val infValue = SafetySignalNormalizer.normalizeLinear(Double.POSITIVE_INFINITY, 0.0, 100.0)
        assertTrue(infValue in 0.0..1.0)
    }

    @Test
    fun `normalizeRatio clamps correctly`() {
        assertEquals(0.85, SafetySignalNormalizer.normalizeRatio(0.85), 0.001)
        assertEquals(1.0, SafetySignalNormalizer.normalizeRatio(1.4), 0.001)
        assertEquals(0.0, SafetySignalNormalizer.normalizeRatio(-0.2), 0.001)
        assertEquals(0.0, SafetySignalNormalizer.normalizeRatio(Double.NaN), 0.001)
    }

    @Test
    fun `normalizeSafePointCount scales monotonically`() {
        val zeroCount = SafetySignalNormalizer.normalizeSafePointCount(0)
        val oneCount = SafetySignalNormalizer.normalizeSafePointCount(1)
        val threeCount = SafetySignalNormalizer.normalizeSafePointCount(3)
        val tenCount = SafetySignalNormalizer.normalizeSafePointCount(10)

        assertTrue(zeroCount < oneCount)
        assertTrue(oneCount < threeCount)
        assertEquals(1.0, tenCount, 0.001)
    }
}
