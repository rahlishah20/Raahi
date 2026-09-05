package com.example.presentation.map

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.domain.model.Journey
import com.example.domain.model.KarachiDestination
import com.example.domain.model.LatLngPoint
import com.example.domain.model.RaahiRoute
import com.example.domain.model.SafePoint
import com.example.domain.model.UserLocation
import com.example.presentation.components.RaahiGlassPanel
import com.example.ui.theme.CautionAmber
import com.example.ui.theme.PrimaryObsidian
import com.example.ui.theme.SecondarySafetyGreen

@Composable
fun KarachiMapComponent(
    modifier: Modifier = Modifier,
    userLocation: UserLocation? = null,
    selectedDestination: KarachiDestination? = null,
    routes: List<RaahiRoute> = emptyList(),
    selectedRoute: RaahiRoute? = null,
    nearbySafePoints: List<SafePoint> = emptyList(),
    selectedSafePoint: SafePoint? = null,
    activeJourney: Journey? = null,
    isLocationPermissionGranted: Boolean = false,
    onRecenterClick: () -> Unit = {},
    onSafePointClick: (SafePoint) -> Unit = {},
    onMapClick: (LatLngPoint) -> Unit = {}
) {
    val maptilerApiKey = remember {
        val key = try {
            val field = BuildConfig::class.java.getField("MAPTILER_API_KEY")
            (field.get(null) as? String)?.trim().orEmpty()
        } catch (e: Throwable) {
            Log.e("KarachiMapComponent", "Failed to retrieve MAPTILER_API_KEY from BuildConfig", e)
            ""
        }
        if (key.isNotBlank() && key != "YOUR_MAPTILER_API_KEY") key else "dLggrfwsbIFlX8Ky2lmT"
    }
    val hasMaptilerKey = maptilerApiKey.isNotBlank()

    var isMapLoaded by remember { mutableStateOf(false) }

    // Determine displayed safe points: Route safe points if route active, else nearby points
    val displayedSafePoints = if (selectedRoute != null && selectedRoute.safetyProfile?.nearbySafePoints?.isNotEmpty() == true) {
        selectedRoute.safetyProfile?.nearbySafePoints.orEmpty()
    } else {
        nearbySafePoints
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("karachi_map_view")
    ) {
        if (hasMaptilerKey) {
            MapTilerView(
                modifier = Modifier.fillMaxSize(),
                apiKey = maptilerApiKey,
                styleMode = "streets-v2",
                userLocation = userLocation,
                selectedDestination = selectedDestination,
                routes = routes,
                selectedRoute = selectedRoute,
                displayedSafePoints = displayedSafePoints,
                selectedSafePoint = selectedSafePoint,
                activeJourney = activeJourney,
                onSafePointClick = onSafePointClick,
                onMapClick = onMapClick,
                onMapLoaded = { isMapLoaded = true }
            )
        } else {
            // Inline error / placeholder state when MAPTILER_API_KEY is missing/blank
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1E2322))
                    .padding(24.dp)
                    .testTag("maptiler_missing_key_placeholder"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = null,
                        tint = CautionAmber,
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        text = "Map unavailable — check MAPTILER_API_KEY configuration",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        ),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Floating Map Controls (Right Side)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .padding(bottom = 60.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Recenter / Location button
            RaahiGlassPanel(
                shape = CircleShape,
                elevation = 4.dp
            ) {
                IconButton(
                    onClick = onRecenterClick,
                    modifier = Modifier
                        .size(46.dp)
                        .testTag("recenter_map_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Recenter on Karachi",
                        tint = if (isLocationPermissionGranted) SecondarySafetyGreen else PrimaryObsidian,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}
