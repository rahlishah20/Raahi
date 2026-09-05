package com.example.data.safety

import com.example.domain.model.KarachiSafePoint
import com.example.domain.model.LatLngPoint
import com.example.domain.model.SafePointType
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object KarachiSafetyDataSource {

    /**
     * Verified and Approved Karachi Safe Points Prototype Dataset
     * Geographically constrained to metropolitan Karachi.
     * All items marked as seeded prototype data with verified/openingStatus flags.
     */
    val VERIFIED_SAFE_POINTS = listOf(
        // Police Facilities
        KarachiSafePoint(
            id = "sp_clifton_police",
            name = "Clifton Police Station",
            category = com.example.domain.model.SafePointCategory.POLICE_FACILITY,
            latitude = 24.8248,
            longitude = 67.0332,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Khayaban-e-Iqbal, Block 5 Clifton, Karachi",
            isSeeded = true
        ),
        KarachiSafePoint(
            id = "sp_saddar_police",
            name = "Saddar Police Station",
            category = com.example.domain.model.SafePointCategory.POLICE_FACILITY,
            latitude = 24.8580,
            longitude = 67.0142,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Mansfield Street, Saddar, Karachi",
            isSeeded = true
        ),
        KarachiSafePoint(
            id = "sp_artillery_police",
            name = "Artillery Maidan Police Station",
            category = com.example.domain.model.SafePointCategory.POLICE_FACILITY,
            latitude = 24.8552,
            longitude = 67.0189,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Burns Road / Sarwar Shaheed Rd, Karachi",
            isSeeded = true
        ),
        KarachiSafePoint(
            id = "sp_cantt_police",
            name = "Cantt Railway Police Post",
            category = com.example.domain.model.SafePointCategory.POLICE_FACILITY,
            latitude = 24.8492,
            longitude = 67.0368,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Cantt Railway Station, Karachi",
            isSeeded = true
        ),
        KarachiSafePoint(
            id = "sp_dha_police",
            name = "Darakhshan Police Station",
            category = com.example.domain.model.SafePointCategory.POLICE_FACILITY,
            latitude = 24.7995,
            longitude = 67.0602,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Khayaban-e-Rahat, Phase 6 DHA, Karachi",
            isSeeded = true
        ),
        KarachiSafePoint(
            id = "sp_pechs_police",
            name = "Ferozabad / PECHS Police Station",
            category = com.example.domain.model.SafePointCategory.POLICE_FACILITY,
            latitude = 24.8730,
            longitude = 67.0640,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Block 2 PECHS, Tariq Road, Karachi",
            isSeeded = true
        ),
        KarachiSafePoint(
            id = "sp_gulshan_police",
            name = "Gulshan-e-Iqbal Police Station",
            category = com.example.domain.model.SafePointCategory.POLICE_FACILITY,
            latitude = 24.9210,
            longitude = 67.0940,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "University Road, Block 13-D Gulshan, Karachi",
            isSeeded = true
        ),

        // Hospitals
        KarachiSafePoint(
            id = "sp_south_city_hospital",
            name = "South City Hospital (24/7 Emergency)",
            category = com.example.domain.model.SafePointCategory.HOSPITAL,
            latitude = 24.8310,
            longitude = 67.0345,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Block 3 Clifton, Karachi",
            isSeeded = true
        ),
        KarachiSafePoint(
            id = "sp_jpmc_hospital",
            name = "Jinnah Postgraduate Medical Centre (JPMC)",
            category = com.example.domain.model.SafePointCategory.HOSPITAL,
            latitude = 24.8520,
            longitude = 67.0460,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Rafiqui Shaheed Rd, Cantt, Karachi",
            isSeeded = true
        ),
        KarachiSafePoint(
            id = "sp_civil_hospital",
            name = "Civil Hospital Karachi (CHS Emergency)",
            category = com.example.domain.model.SafePointCategory.HOSPITAL,
            latitude = 24.8595,
            longitude = 67.0102,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Mission Rd, Near Civil Hospital, Saddar, Karachi",
            isSeeded = true
        ),
        KarachiSafePoint(
            id = "sp_national_medical",
            name = "National Medical Centre",
            category = com.example.domain.model.SafePointCategory.HOSPITAL,
            latitude = 24.8420,
            longitude = 67.0620,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Korangi Rd / DHA Phase 1, Karachi",
            isSeeded = true
        ),
        KarachiSafePoint(
            id = "sp_aga_khan_hospital",
            name = "Aga Khan University Hospital (AKUH)",
            category = com.example.domain.model.SafePointCategory.HOSPITAL,
            latitude = 24.8920,
            longitude = 67.0750,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "National Stadium Road, Karachi",
            isSeeded = true
        ),
        KarachiSafePoint(
            id = "sp_liaquat_national",
            name = "Liaquat National Hospital (Emergency)",
            category = com.example.domain.model.SafePointCategory.HOSPITAL,
            latitude = 24.8950,
            longitude = 67.0720,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Stadium Road, Karachi",
            isSeeded = true
        ),

        // Pharmacies
        KarachiSafePoint(
            id = "sp_dvago_clifton",
            name = "DVAGO 24/7 Pharmacy Clifton",
            category = com.example.domain.model.SafePointCategory.PHARMACY,
            latitude = 24.8210,
            longitude = 67.0315,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Khayaban-e-Iqbal, Block 5 Clifton, Karachi",
            isSeeded = true
        ),
        KarachiSafePoint(
            id = "sp_ke_care_pharmacy",
            name = "K-Electric Medical & 24/7 Pharmacy",
            category = com.example.domain.model.SafePointCategory.PHARMACY,
            latitude = 24.8620,
            longitude = 67.0680,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Shahrah-e-Faisal, Karachi",
            isSeeded = true
        ),
        KarachiSafePoint(
            id = "sp_time_medicos",
            name = "Time Medicos 24/7 Pharmacy",
            category = com.example.domain.model.SafePointCategory.PHARMACY,
            latitude = 24.8935,
            longitude = 67.0760,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Opp. Aga Khan Hospital, Stadium Road, Karachi",
            isSeeded = true
        ),
        KarachiSafePoint(
            id = "sp_servaid_tariq",
            name = "Servaid Pharmacy Tariq Road",
            category = com.example.domain.model.SafePointCategory.PHARMACY,
            latitude = 24.8725,
            longitude = 67.0610,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Tariq Road, PECHS Block 2, Karachi",
            isSeeded = true
        ),
        KarachiSafePoint(
            id = "sp_clinix_saddar",
            name = "Clinix Pharmacy Saddar",
            category = com.example.domain.model.SafePointCategory.PHARMACY,
            latitude = 24.8575,
            longitude = 67.0125,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Preedy Street, Saddar, Karachi",
            isSeeded = true
        ),

        // Petrol Stations
        KarachiSafePoint(
            id = "sp_shell_clifton",
            name = "Shell Select 24/7 Clifton",
            category = com.example.domain.model.SafePointCategory.PETROL_STATION,
            latitude = 24.8190,
            longitude = 67.0340,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Khayaban-e-Iqbal, Clifton, Karachi",
            isSeeded = true
        ),
        KarachiSafePoint(
            id = "sp_total_faisal",
            name = "Total Parco Station Shahrah-e-Faisal",
            category = com.example.domain.model.SafePointCategory.PETROL_STATION,
            latitude = 24.8640,
            longitude = 67.0620,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Shahrah-e-Faisal near FTC, Karachi",
            isSeeded = true
        ),
        KarachiSafePoint(
            id = "sp_pso_clifton",
            name = "PSO House Fuel Station & Mart",
            category = com.example.domain.model.SafePointCategory.PETROL_STATION,
            latitude = 24.8290,
            longitude = 67.0270,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Clifton Road / Marine Drive, Karachi",
            isSeeded = true
        ),
        KarachiSafePoint(
            id = "sp_shell_tariq",
            name = "Shell Fuel Station Tariq Road",
            category = com.example.domain.model.SafePointCategory.PETROL_STATION,
            latitude = 24.8690,
            longitude = 67.0590,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Tariq Road, PECHS, Karachi",
            isSeeded = true
        ),

        // Universities
        KarachiSafePoint(
            id = "sp_iba_city",
            name = "IBA City Campus (Security Post)",
            category = com.example.domain.model.SafePointCategory.UNIVERSITY,
            latitude = 24.8670,
            longitude = 67.0260,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Garden Rd, Saddar, Karachi",
            isSeeded = true
        ),
        KarachiSafePoint(
            id = "sp_iba_main",
            name = "IBA Main Campus Security Gate",
            category = com.example.domain.model.SafePointCategory.UNIVERSITY,
            latitude = 24.9390,
            longitude = 67.1120,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "University Road, Karachi",
            isSeeded = true
        ),
        KarachiSafePoint(
            id = "sp_ned_university",
            name = "NED University Security Hub",
            category = com.example.domain.model.SafePointCategory.UNIVERSITY,
            latitude = 24.9340,
            longitude = 67.1110,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "University Road, Gulshan, Karachi",
            isSeeded = true
        ),

        // Cafés
        KarachiSafePoint(
            id = "sp_boat_basin_cafe",
            name = "Boat Basin 24/7 Safe Haven Hub",
            category = com.example.domain.model.SafePointCategory.CAFE,
            latitude = 24.8236,
            longitude = 67.0321,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Khayaban-e-Roomi, Block 5 Clifton, Karachi",
            isSeeded = true
        ),
        KarachiSafePoint(
            id = "sp_espresso_clifton",
            name = "Espresso Clifton (Late Hours)",
            category = com.example.domain.model.SafePointCategory.CAFE,
            latitude = 24.8150,
            longitude = 67.0290,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Block 4 Clifton, Karachi",
            isSeeded = true
        ),

        // Hotels
        KarachiSafePoint(
            id = "sp_pc_hotel",
            name = "Pearl Continental Hotel (24/7 Concierge)",
            category = com.example.domain.model.SafePointCategory.HOTEL,
            latitude = 24.8510,
            longitude = 67.0295,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Club Road, Civil Lines / Saddar, Karachi",
            isSeeded = true
        ),
        KarachiSafePoint(
            id = "sp_movenpick_hotel",
            name = "Mövenpick Hotel Karachi",
            category = com.example.domain.model.SafePointCategory.HOTEL,
            latitude = 24.8525,
            longitude = 67.0310,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Club Road, Karachi",
            isSeeded = true
        ),
        KarachiSafePoint(
            id = "sp_avari_towers",
            name = "Avari Towers (24/7 Security)",
            category = com.example.domain.model.SafePointCategory.HOTEL,
            latitude = 24.8540,
            longitude = 67.0335,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Fatima Jinnah Rd, Karachi Cantt, Karachi",
            isSeeded = true
        ),

        // Shops
        KarachiSafePoint(
            id = "sp_dolmen_security",
            name = "Dolmen Mall Customer & Security Desk",
            category = com.example.domain.model.SafePointCategory.SHOP,
            latitude = 24.8138,
            longitude = 67.0300,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Marine Drive, Block 4 Clifton, Karachi",
            isSeeded = true
        ),
        KarachiSafePoint(
            id = "sp_lucky_one_security",
            name = "Lucky One Mall Security Center",
            category = com.example.domain.model.SafePointCategory.SHOP,
            latitude = 24.9427,
            longitude = 67.0782,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Rashid Minhas Rd, Karachi",
            isSeeded = true
        ),
        KarachiSafePoint(
            id = "sp_imtiaz_dha",
            name = "Imtiaz Super Market DHA 1",
            category = com.example.domain.model.SafePointCategory.SHOP,
            latitude = 24.8380,
            longitude = 67.0660,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Korangi Road, DHA Phase 1, Karachi",
            isSeeded = true
        ),

        // Workplaces
        KarachiSafePoint(
            id = "sp_hbl_plaza",
            name = "Habib Bank Plaza Security Hub",
            category = com.example.domain.model.SafePointCategory.WORKPLACE,
            latitude = 24.8510,
            longitude = 67.0050,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "I.I. Chundrigar Road, City Financial District, Karachi",
            isSeeded = true
        ),
        KarachiSafePoint(
            id = "sp_ftc_building",
            name = "FTC Building Corporate Security Desk",
            category = com.example.domain.model.SafePointCategory.WORKPLACE,
            latitude = 24.8635,
            longitude = 67.0600,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Shahrah-e-Faisal, Karachi",
            isSeeded = true
        ),
        KarachiSafePoint(
            id = "sp_ocean_tower",
            name = "Ocean Tower Security & Concierge",
            category = com.example.domain.model.SafePointCategory.WORKPLACE,
            latitude = 24.8260,
            longitude = 67.0340,
            verified = true,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.OPEN,
            address = "Khayaban-e-Iqbal, Block 9 Clifton, Karachi",
            isSeeded = true
        ),

        // Unverified / Prototype testing point for validation & filter tests
        KarachiSafePoint(
            id = "sp_unverified_kiosk",
            name = "Unverified Local Information Kiosk",
            category = com.example.domain.model.SafePointCategory.SHOP,
            latitude = 24.8700,
            longitude = 67.0500,
            verified = false,
            openingStatus = com.example.domain.model.SafePointOpeningStatus.UNKNOWN,
            address = "PECHS Commercial Area, Karachi",
            isSeeded = true
        )
    )

    data class CorridorSafetyMetadata(
        val corridorKey: String,
        val lightingRatio: Double, // 0.0 to 1.0
        val pedestrianDensity: Double, // 0.0 to 1.0
        val businessDensity: Double, // 0.0 to 1.0
        val incidentSafetyIndex: Double, // 0.0 to 1.0 (higher = fewer incidents/safer)
        val lightingSummary: String,
        val pedestrianSummary: String,
        val businessSummary: String,
        val incidentSummary: String
    )

    private val CORRIDOR_PROFILES = listOf(
        CorridorSafetyMetadata(
            corridorKey = "khayaban_iqbal",
            lightingRatio = 0.88,
            pedestrianDensity = 0.84,
            businessDensity = 0.90,
            incidentSafetyIndex = 0.85,
            lightingSummary = "Consistently lit primary artery across Clifton & Club Rd",
            pedestrianSummary = "High pedestrian flow around Teen Talwar & commercial plazas",
            businessSummary = "Dense 24/7 open commercial POIs, cafes, and banks",
            incidentSummary = "Well-monitored corridor with frequent law enforcement presence"
        ),
        CorridorSafetyMetadata(
            corridorKey = "shahrah_faisal",
            lightingRatio = 0.92,
            pedestrianDensity = 0.72,
            businessDensity = 0.88,
            incidentSafetyIndex = 0.82,
            lightingSummary = "Multi-lane high-lumen municipal lighting corridor",
            pedestrianSummary = "Moderate pedestrian density with high vehicular volume",
            businessSummary = "Active corporate commercial strip, hotels, and transit nodes",
            incidentSummary = "Regularly patrolled transit corridor"
        ),
        CorridorSafetyMetadata(
            corridorKey = "mai_kolachi",
            lightingRatio = 0.58,
            pedestrianDensity = 0.35,
            businessDensity = 0.40,
            incidentSafetyIndex = 0.65,
            lightingSummary = "Expressway segment with intermittent street lighting",
            pedestrianSummary = "Low pedestrian activity along bypass highway stretch",
            businessSummary = "Limited direct commercial storefronts along expressway",
            incidentSummary = "Bypass corridor with fewer immediate safe havens"
        ),
        CorridorSafetyMetadata(
            corridorKey = "saddar_preedy",
            lightingRatio = 0.78,
            pedestrianDensity = 0.92,
            businessDensity = 0.95,
            incidentSafetyIndex = 0.74,
            lightingSummary = "Moderate urban lighting in dense central bazaar",
            pedestrianSummary = "Very high continuous foot traffic throughout the day and evening",
            businessSummary = "Extensive active market stalls, pharmacies, and storefronts",
            incidentSummary = "High-density market zone with regular city police deployment"
        )
    )

    fun findCorridorProfile(routeId: String, title: String, points: List<LatLngPoint>): CorridorSafetyMetadata {
        val lowerId = routeId.lowercase()
        val lowerTitle = title.lowercase()

        when {
            lowerId.contains("iqbal") || lowerTitle.contains("iqbal") || lowerTitle.contains("club") -> {
                return CORRIDOR_PROFILES[0]
            }
            lowerId.contains("faisal") || lowerTitle.contains("faisal") || lowerTitle.contains("cantt") -> {
                return CORRIDOR_PROFILES[1]
            }
            lowerId.contains("kolachi") || lowerTitle.contains("kolachi") || lowerTitle.contains("bypass") -> {
                return CORRIDOR_PROFILES[2]
            }
            lowerId.contains("saddar") || lowerTitle.contains("saddar") -> {
                return CORRIDOR_PROFILES[3]
            }
        }

        // Contextual analysis based on coordinates if no ID match
        val hasClifton = points.any { it.latitude in 24.80..24.84 && it.longitude in 67.02..67.04 }
        val hasFaisal = points.any { it.latitude in 24.85..24.88 && it.longitude in 67.04..67.08 }
        val hasMaiKolachi = points.any { it.latitude in 24.82..24.85 && it.longitude in 66.98..67.01 }

        return when {
            hasMaiKolachi -> CORRIDOR_PROFILES[2]
            hasFaisal -> CORRIDOR_PROFILES[1]
            hasClifton -> CORRIDOR_PROFILES[0]
            else -> CorridorSafetyMetadata(
                corridorKey = "general_karachi",
                lightingRatio = 0.70,
                pedestrianDensity = 0.65,
                businessDensity = 0.70,
                incidentSafetyIndex = 0.72,
                lightingSummary = "Standard municipal lighting along primary Karachi road network",
                pedestrianSummary = "Regular Karachi urban pedestrian density",
                businessSummary = "Active local commercial shops and convenience stores",
                incidentSummary = "Standard metropolitan safety baseline"
            )
        }
    }

    /**
     * Calculates distance between two points in meters
     */
    fun calculateDistanceMeters(p1Lat: Double, p1Lng: Double, p2Lat: Double, p2Lng: Double): Int {
        val r = 6371000.0
        val dLat = Math.toRadians(p2Lat - p1Lat)
        val dLon = Math.toRadians(p2Lng - p1Lng)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(p1Lat)) * cos(Math.toRadians(p2Lat)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (r * c).toInt()
    }

    /**
     * Finds safe points located within [maxDistanceMeters] of any point on the route polyline.
     */
    fun findNearbySafePoints(
        points: List<LatLngPoint>,
        maxDistanceMeters: Int = 1200,
        categoryFilter: com.example.domain.model.SafePointCategory? = null,
        onlyVerified: Boolean = false,
        onlyOpen: Boolean = false
    ): List<KarachiSafePoint> {
        if (points.isEmpty()) return emptyList()

        val results = mutableListOf<KarachiSafePoint>()

        VERIFIED_SAFE_POINTS
            .filter { sp ->
                (categoryFilter == null || sp.category == categoryFilter) &&
                (!onlyVerified || sp.verified) &&
                (!onlyOpen || sp.openingStatus == com.example.domain.model.SafePointOpeningStatus.OPEN)
            }
            .forEach { safePoint ->
                var minDistance = Int.MAX_VALUE
                for (routePoint in points) {
                    val dist = calculateDistanceMeters(
                        routePoint.latitude, routePoint.longitude,
                        safePoint.latitude, safePoint.longitude
                    )
                    if (dist < minDistance) {
                        minDistance = dist
                    }
                }

                if (minDistance <= maxDistanceMeters) {
                    results.add(
                        safePoint.copy(
                            distanceFromRoute = minDistance,
                            distanceToRouteMeters = minDistance
                        )
                    )
                }
            }

        return results.sortedBy { it.distanceFromRoute ?: it.distanceToRouteMeters }
    }

    /**
     * Finds safe points within [radiusMeters] of a geographic location (e.g. user current location).
     */
    fun findNearbySafePointsFromLocation(
        lat: Double,
        lng: Double,
        radiusMeters: Int = 1500,
        categoryFilter: com.example.domain.model.SafePointCategory? = null,
        onlyVerified: Boolean = false,
        onlyOpen: Boolean = false
    ): List<KarachiSafePoint> {
        if (lat.isNaN() || lng.isNaN() || lat !in -90.0..90.0 || lng !in -180.0..180.0) return emptyList()

        return VERIFIED_SAFE_POINTS
            .filter { sp ->
                (categoryFilter == null || sp.category == categoryFilter) &&
                (!onlyVerified || sp.verified) &&
                (!onlyOpen || sp.openingStatus == com.example.domain.model.SafePointOpeningStatus.OPEN)
            }
            .map { sp ->
                val dist = calculateDistanceMeters(lat, lng, sp.latitude, sp.longitude)
                sp.copy(distanceFromUser = dist)
            }
            .filter { (it.distanceFromUser ?: Int.MAX_VALUE) <= radiusMeters }
            .sortedBy { it.distanceFromUser ?: Int.MAX_VALUE }
    }
}
