package com.example

import com.example.data.util.PolylineDecoder
import com.example.domain.model.LatLngPoint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PolylineDecoderTest {

    @Test
    fun `encode and decode preserves coordinates accurately`() {
        val points = listOf(
            LatLngPoint(24.8138, 67.0300), // Clifton
            LatLngPoint(24.8350, 67.0335), // Teen Talwar
            LatLngPoint(24.8607, 67.0104)  // Saddar
        )

        val encoded = PolylineDecoder.encode(points)
        assertTrue(encoded.isNotEmpty())

        val decoded = PolylineDecoder.decode(encoded)
        assertEquals(points.size, decoded.size)

        for (i in points.indices) {
            assertEquals(points[i].latitude, decoded[i].latitude, 0.0001)
            assertEquals(points[i].longitude, decoded[i].longitude, 0.0001)
        }
    }

    @Test
    fun `empty encoded string decodes to empty list`() {
        val decoded = PolylineDecoder.decode("")
        assertTrue(decoded.isEmpty())
    }
}
