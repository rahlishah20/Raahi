package com.example

import com.example.data.datasource.KarachiDynamicConditionDataSource
import com.example.domain.model.DemoEvent
import com.example.domain.model.DemoEventType
import com.example.domain.model.SafetyFactorType
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DynamicConditionDataSourceTest {

    @Before
    fun setUp() {
        KarachiDynamicConditionDataSource.clearAllEvents()
    }

    @After
    fun tearDown() {
        KarachiDynamicConditionDataSource.clearAllEvents()
    }

    @Test
    fun testAddEventAndGetActiveEvents() {
        val event = DemoEvent(
            id = "test_event_1",
            type = DemoEventType.BUSINESS_ACTIVITY_CHANGE,
            targetFactorType = SafetyFactorType.BUSINESS_ACTIVITY,
            valueMultiplier = 0.50,
            description = "Test business activity decrease"
        )

        KarachiDynamicConditionDataSource.addEvent(event)
        val active = KarachiDynamicConditionDataSource.getActiveEvents()

        assertEquals(1, active.size)
        assertEquals("test_event_1", active.first().id)
    }

    @Test
    fun testAdjustedFactorValueWithMultiplier() {
        val event = DemoEvent(
            id = "test_mult_event",
            type = DemoEventType.BUSINESS_ACTIVITY_CHANGE,
            targetFactorType = SafetyFactorType.BUSINESS_ACTIVITY,
            valueMultiplier = 0.50,
            description = "50% reduction in commercial activity"
        )
        KarachiDynamicConditionDataSource.addEvent(event)

        val baseValue = 0.80
        val (adjusted, note) = KarachiDynamicConditionDataSource.getAdjustedFactorValue(
            factorType = SafetyFactorType.BUSINESS_ACTIVITY,
            baseValue = baseValue
        )

        assertEquals(0.40, adjusted, 0.001)
        assertNotNull(note)
        assertTrue(note!!.contains("50% reduction in commercial activity") || note.contains("Simulated"))
    }

    @Test
    fun testAdjustedFactorValueWithAbsoluteOverride() {
        val event = DemoEvent(
            id = "test_override_event",
            type = DemoEventType.LIGHTING_CHANGE,
            targetFactorType = SafetyFactorType.LIGHTING,
            rawValueOverride = 0.25,
            description = "Streetlights non-operational"
        )
        KarachiDynamicConditionDataSource.addEvent(event)

        val (adjusted, note) = KarachiDynamicConditionDataSource.getAdjustedFactorValue(
            factorType = SafetyFactorType.LIGHTING,
            baseValue = 0.90
        )

        assertEquals(0.25, adjusted, 0.001)
        assertNotNull(note)
        assertTrue(note!!.contains("Streetlights non-operational"))
    }

    @Test
    fun testCorridorSpecificEventTargeting() {
        val event = DemoEvent(
            id = "corridor_event",
            type = DemoEventType.INCIDENT_SIGNAL_CHANGE,
            targetCorridorKey = "shahrah_e_faisal",
            targetFactorType = SafetyFactorType.HISTORICAL_INCIDENT,
            rawValueOverride = 0.30,
            description = "Incident surge along Shahrah-e-Faisal"
        )
        KarachiDynamicConditionDataSource.addEvent(event)

        // Querying for matching corridor
        val (adjMatching, noteMatching) = KarachiDynamicConditionDataSource.getAdjustedFactorValue(
            factorType = SafetyFactorType.HISTORICAL_INCIDENT,
            baseValue = 0.85,
            corridorKey = "shahrah_e_faisal"
        )
        assertEquals(0.30, adjMatching, 0.001)
        assertNotNull(noteMatching)

        // Querying for non-matching corridor
        val (adjOther, noteOther) = KarachiDynamicConditionDataSource.getAdjustedFactorValue(
            factorType = SafetyFactorType.HISTORICAL_INCIDENT,
            baseValue = 0.85,
            corridorKey = "clifton_beach_avenue"
        )
        assertEquals(0.85, adjOther, 0.001)
        assertNull(noteOther)
    }

    @Test
    fun testClearAllEventsRestoresBaseline() {
        val event = DemoEvent(
            id = "test_event_to_clear",
            type = DemoEventType.PEDESTRIAN_ACTIVITY_CHANGE,
            targetFactorType = SafetyFactorType.PEDESTRIAN_ACTIVITY,
            valueMultiplier = 0.20
        )
        KarachiDynamicConditionDataSource.addEvent(event)
        assertEquals(1, KarachiDynamicConditionDataSource.getActiveEvents().size)

        KarachiDynamicConditionDataSource.clearAllEvents()
        assertEquals(0, KarachiDynamicConditionDataSource.getActiveEvents().size)

        val (adjusted, note) = KarachiDynamicConditionDataSource.getAdjustedFactorValue(
            factorType = SafetyFactorType.PEDESTRIAN_ACTIVITY,
            baseValue = 0.75
        )
        assertEquals(0.75, adjusted, 0.001)
        assertNull(note)
    }
}
