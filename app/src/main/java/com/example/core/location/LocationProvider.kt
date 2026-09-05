package com.example.core.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.example.domain.model.UserLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationProvider(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // Default Karachi Clifton coordinates for safety & testing fallback
    val defaultKarachiLocation = UserLocation(
        latitude = 24.8138,
        longitude = 67.0300,
        accuracy = 10f,
        provider = "default_karachi"
    )

    private fun isWithinKarachiBounds(lat: Double, lng: Double): Boolean {
        return lat in 24.65..25.25 && lng in 66.80..67.45
    }

    private fun sanitizeLocation(loc: Location?, providerName: String = "fused"): UserLocation {
        if (loc == null) return defaultKarachiLocation
        return if (isWithinKarachiBounds(loc.latitude, loc.longitude)) {
            UserLocation(
                latitude = loc.latitude,
                longitude = loc.longitude,
                accuracy = loc.accuracy,
                provider = loc.provider ?: providerName
            )
        } else {
            defaultKarachiLocation
        }
    }

    @SuppressLint("MissingPermission")
    fun getLocationUpdates(intervalMs: Long = 3000L): Flow<UserLocation> = callbackFlow {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMs)
            .setMinUpdateIntervalMillis(intervalMs / 2)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { loc ->
                    trySend(sanitizeLocation(loc, "fused_stream"))
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())
        } catch (e: SecurityException) {
            trySend(defaultKarachiLocation)
        } catch (e: Exception) {
            trySend(defaultKarachiLocation)
        }

        awaitClose {
            try {
                fusedLocationClient.removeLocationUpdates(callback)
            } catch (_: Exception) {}
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): UserLocation? = suspendCancellableCoroutine { continuation ->
        try {
            val cancellationTokenSource = CancellationTokenSource()
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location: Location? ->
                if (!continuation.isActive) return@addOnSuccessListener
                if (location != null) {
                    continuation.resume(sanitizeLocation(location, "fused"))
                } else {
                    fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc: Location? ->
                        if (!continuation.isActive) return@addOnSuccessListener
                        if (lastLoc != null) {
                            continuation.resume(sanitizeLocation(lastLoc, "last_known"))
                        } else {
                            continuation.resume(defaultKarachiLocation)
                        }
                    }.addOnFailureListener {
                        if (continuation.isActive) {
                            continuation.resume(defaultKarachiLocation)
                        }
                    }
                }
            }.addOnFailureListener {
                if (continuation.isActive) {
                    continuation.resume(defaultKarachiLocation)
                }
            }

            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        } catch (e: Exception) {
            if (continuation.isActive) {
                continuation.resume(defaultKarachiLocation)
            }
        }
    }
}
