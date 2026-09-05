package com.example.data.safety.providers

import com.example.data.datasource.KarachiDynamicConditionDataSource
import com.example.data.safety.KarachiSafetyDataSource
import com.example.domain.model.DataSourceType
import com.example.domain.model.KarachiSafePoint
import com.example.domain.model.RaahiRoute
import com.example.domain.model.SafetyFactorType
import com.example.domain.model.SafetySignal
import com.example.domain.safety.BusinessActivityProvider
import com.example.domain.safety.HistoricalIncidentProvider
import com.example.domain.safety.LightingDataProvider
import com.example.domain.safety.PedestrianActivityProvider
import com.example.domain.safety.SafePointProvider
import com.example.domain.safety.SafetySignalNormalizer

class KarachiLightingDataProvider(
    private val isEnabled: Boolean = true,
    private val forcedDataSourceType: DataSourceType = DataSourceType.PROTOTYPE
) : LightingDataProvider {

    override suspend fun getLightingSignal(route: RaahiRoute): SafetySignal {
        if (!isEnabled || forcedDataSourceType == DataSourceType.UNAVAILABLE) {
            return SafetySignal(
                factorType = SafetyFactorType.LIGHTING,
                rawValue = 0.0,
                normalizedScore = 0.0,
                confidence = 0.0,
                dataSourceType = DataSourceType.UNAVAILABLE,
                sourceDescription = "No verified lighting sensor signal available",
                isAvailable = false
            )
        }

        val profile = KarachiSafetyDataSource.findCorridorProfile(
            routeId = route.id,
            title = route.title,
            points = route.polylinePoints
        )

        val (adjustedValue, dynamicNote) = KarachiDynamicConditionDataSource.getAdjustedFactorValue(
            factorType = SafetyFactorType.LIGHTING,
            baseValue = profile.lightingRatio,
            routeId = route.id,
            corridorKey = profile.corridorKey
        )

        val normalized = SafetySignalNormalizer.normalizeRatio(adjustedValue)
        val sourceDesc = dynamicNote ?: "Karachi Municipal & Major Artery Lighting Index: ${profile.lightingSummary}"

        return SafetySignal(
            factorType = SafetyFactorType.LIGHTING,
            rawValue = adjustedValue,
            normalizedScore = normalized,
            confidence = 0.85,
            dataSourceType = forcedDataSourceType,
            sourceDescription = sourceDesc,
            isAvailable = true
        )
    }
}

class KarachiPedestrianActivityProvider(
    private val isEnabled: Boolean = true,
    private val forcedDataSourceType: DataSourceType = DataSourceType.PROTOTYPE
) : PedestrianActivityProvider {

    override suspend fun getPedestrianActivitySignal(route: RaahiRoute): SafetySignal {
        if (!isEnabled || forcedDataSourceType == DataSourceType.UNAVAILABLE) {
            return SafetySignal(
                factorType = SafetyFactorType.PEDESTRIAN_ACTIVITY,
                rawValue = 0.0,
                normalizedScore = 0.0,
                confidence = 0.0,
                dataSourceType = DataSourceType.UNAVAILABLE,
                sourceDescription = "No pedestrian sensor telemetry available",
                isAvailable = false
            )
        }

        val profile = KarachiSafetyDataSource.findCorridorProfile(
            routeId = route.id,
            title = route.title,
            points = route.polylinePoints
        )

        val (adjustedValue, dynamicNote) = KarachiDynamicConditionDataSource.getAdjustedFactorValue(
            factorType = SafetyFactorType.PEDESTRIAN_ACTIVITY,
            baseValue = profile.pedestrianDensity,
            routeId = route.id,
            corridorKey = profile.corridorKey
        )

        val normalized = SafetySignalNormalizer.normalizeRatio(adjustedValue)
        val sourceDesc = dynamicNote ?: "Karachi Urban Commercial Foot Traffic Profile: ${profile.pedestrianSummary}"

        return SafetySignal(
            factorType = SafetyFactorType.PEDESTRIAN_ACTIVITY,
            rawValue = adjustedValue,
            normalizedScore = normalized,
            confidence = 0.80,
            dataSourceType = forcedDataSourceType,
            sourceDescription = sourceDesc,
            isAvailable = true
        )
    }
}

class KarachiBusinessActivityProvider(
    private val isEnabled: Boolean = true,
    private val forcedDataSourceType: DataSourceType = DataSourceType.PROTOTYPE
) : BusinessActivityProvider {

    override suspend fun getBusinessActivitySignal(route: RaahiRoute): SafetySignal {
        if (!isEnabled || forcedDataSourceType == DataSourceType.UNAVAILABLE) {
            return SafetySignal(
                factorType = SafetyFactorType.BUSINESS_ACTIVITY,
                rawValue = 0.0,
                normalizedScore = 0.0,
                confidence = 0.0,
                dataSourceType = DataSourceType.UNAVAILABLE,
                sourceDescription = "No open POI directory signal available",
                isAvailable = false
            )
        }

        val profile = KarachiSafetyDataSource.findCorridorProfile(
            routeId = route.id,
            title = route.title,
            points = route.polylinePoints
        )

        val (adjustedValue, dynamicNote) = KarachiDynamicConditionDataSource.getAdjustedFactorValue(
            factorType = SafetyFactorType.BUSINESS_ACTIVITY,
            baseValue = profile.businessDensity,
            routeId = route.id,
            corridorKey = profile.corridorKey
        )

        val normalized = SafetySignalNormalizer.normalizeRatio(adjustedValue)
        val sourceDesc = dynamicNote ?: "Karachi 24/7 Commercial & Fuel Station Registry: ${profile.businessSummary}"

        return SafetySignal(
            factorType = SafetyFactorType.BUSINESS_ACTIVITY,
            rawValue = adjustedValue,
            normalizedScore = normalized,
            confidence = 0.88,
            dataSourceType = forcedDataSourceType,
            sourceDescription = sourceDesc,
            isAvailable = true
        )
    }
}

class KarachiHistoricalIncidentProvider(
    private val isEnabled: Boolean = true,
    private val forcedDataSourceType: DataSourceType = DataSourceType.PROTOTYPE
) : HistoricalIncidentProvider {

    override suspend fun getHistoricalIncidentSignal(route: RaahiRoute): SafetySignal {
        if (!isEnabled || forcedDataSourceType == DataSourceType.UNAVAILABLE) {
            return SafetySignal(
                factorType = SafetyFactorType.HISTORICAL_INCIDENT,
                rawValue = 0.0,
                normalizedScore = 0.0,
                confidence = 0.0,
                dataSourceType = DataSourceType.UNAVAILABLE,
                sourceDescription = "No historical incident database connected",
                isAvailable = false
            )
        }

        val profile = KarachiSafetyDataSource.findCorridorProfile(
            routeId = route.id,
            title = route.title,
            points = route.polylinePoints
        )

        val (adjustedValue, dynamicNote) = KarachiDynamicConditionDataSource.getAdjustedFactorValue(
            factorType = SafetyFactorType.HISTORICAL_INCIDENT,
            baseValue = profile.incidentSafetyIndex,
            routeId = route.id,
            corridorKey = profile.corridorKey
        )

        val normalized = SafetySignalNormalizer.normalizeRatio(adjustedValue)
        val sourceDesc = dynamicNote ?: "Karachi Zone Historical Incident Profiling: ${profile.incidentSummary}"

        return SafetySignal(
            factorType = SafetyFactorType.HISTORICAL_INCIDENT,
            rawValue = adjustedValue,
            normalizedScore = normalized,
            confidence = 0.78,
            dataSourceType = forcedDataSourceType,
            sourceDescription = sourceDesc,
            isAvailable = true
        )
    }
}

class KarachiSafePointProvider(
    private val isEnabled: Boolean = true,
    private val forcedDataSourceType: DataSourceType = DataSourceType.PROTOTYPE
) : SafePointProvider {

    override suspend fun getSafePointSignal(route: RaahiRoute): Pair<SafetySignal, List<KarachiSafePoint>> {
        if (!isEnabled || forcedDataSourceType == DataSourceType.UNAVAILABLE) {
            val unavailableSignal = SafetySignal(
                factorType = SafetyFactorType.SAFE_POINTS,
                rawValue = 0.0,
                normalizedScore = 0.0,
                confidence = 0.0,
                dataSourceType = DataSourceType.UNAVAILABLE,
                sourceDescription = "No safe point directory available",
                isAvailable = false
            )
            return Pair(unavailableSignal, emptyList())
        }

        val nearbySafePoints = KarachiSafetyDataSource.findNearbySafePoints(
            points = route.polylinePoints,
            maxDistanceMeters = 1200
        )

        val profile = KarachiSafetyDataSource.findCorridorProfile(
            routeId = route.id,
            title = route.title,
            points = route.polylinePoints
        )

        val count = nearbySafePoints.size
        val baseNormalized = SafetySignalNormalizer.computeContextualSafePointScore(nearbySafePoints, maxCorridorDistanceMeters = 1200)

        val (adjustedNormalized, dynamicNote) = KarachiDynamicConditionDataSource.getAdjustedFactorValue(
            factorType = SafetyFactorType.SAFE_POINTS,
            baseValue = baseNormalized,
            routeId = route.id,
            corridorKey = profile.corridorKey
        )

        val verifiedCount = nearbySafePoints.count { it.verified }
        val nearest = nearbySafePoints.firstOrNull()
        val nearestDesc = if (nearest != null) " (Nearest: ${nearest.name}, ${(nearest.distanceFromRoute ?: nearest.distanceToRouteMeters)}m)" else ""

        val sourceDesc = dynamicNote ?: "Karachi Safe Points Directory: $count safe points ($verifiedCount verified)$nearestDesc"

        val signal = SafetySignal(
            factorType = SafetyFactorType.SAFE_POINTS,
            rawValue = count.toDouble(),
            normalizedScore = adjustedNormalized,
            confidence = if (count > 0) 0.95 else 0.70,
            dataSourceType = forcedDataSourceType,
            sourceDescription = sourceDesc,
            isAvailable = true
        )

        return Pair(signal, nearbySafePoints)
    }
}

class KarachiSafePointDataProvider : com.example.domain.safety.SafePointDataProvider {
    override suspend fun getAllSafePoints(): List<KarachiSafePoint> {
        return KarachiSafetyDataSource.VERIFIED_SAFE_POINTS
    }

    override suspend fun getNearbySafePoints(
        latitude: Double,
        longitude: Double,
        radiusMeters: Int
    ): List<KarachiSafePoint> {
        return KarachiSafetyDataSource.findNearbySafePointsFromLocation(
            lat = latitude,
            lng = longitude,
            radiusMeters = radiusMeters
        )
    }

    override suspend fun getSafePointsAlongRoute(
        routePoints: List<com.example.domain.model.LatLngPoint>,
        corridorRadiusMeters: Int
    ): List<KarachiSafePoint> {
        return KarachiSafetyDataSource.findNearbySafePoints(
            points = routePoints,
            maxDistanceMeters = corridorRadiusMeters
        )
    }
}
