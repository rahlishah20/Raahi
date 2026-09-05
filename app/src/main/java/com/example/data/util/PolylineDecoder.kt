package com.example.data.util

import com.example.domain.model.LatLngPoint

object PolylineDecoder {

    /**
     * Decodes a Google Maps / Routes API encoded polyline string into a list of LatLngPoints.
     */
    fun decode(encodedPath: String): List<LatLngPoint> {
        val poly = ArrayList<LatLngPoint>()
        var index = 0
        val len = encodedPath.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                if (index >= len) break
                b = encodedPath[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)

            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                if (index >= len) break
                b = encodedPath[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)

            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val pLat = lat.toDouble() / 1E5
            val pLng = lng.toDouble() / 1E5
            poly.add(LatLngPoint(latitude = pLat, longitude = pLng))
        }

        return poly
    }

    /**
     * Encodes a list of LatLngPoints into an encoded polyline string (useful for tests/caching).
     */
    fun encode(points: List<LatLngPoint>): String {
        var lastLat = 0
        var lastLng = 0
        val result = StringBuilder()

        for (point in points) {
            val lat = (point.latitude * 1e5).toInt()
            val lng = (point.longitude * 1e5).toInt()

            val dLat = lat - lastLat
            val dLng = lng - lastLng

            encodeValue(dLat, result)
            encodeValue(dLng, result)

            lastLat = lat
            lastLng = lng
        }
        return result.toString()
    }

    private fun encodeValue(v: Int, result: StringBuilder) {
        var value = if (v < 0) (v shl 1).inv() else v shl 1
        while (value >= 0x20) {
            result.append(((0x20 or (value and 0x1f)) + 63).toChar())
            value = value shr 5
        }
        result.append((value + 63).toChar())
    }
}
