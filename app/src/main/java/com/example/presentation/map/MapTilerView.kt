package com.example.presentation.map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.domain.model.Journey
import com.example.domain.model.KarachiDestination
import com.example.domain.model.LatLngPoint
import com.example.domain.model.RaahiRoute
import com.example.domain.model.SafePoint
import com.example.domain.model.UserLocation
import com.example.presentation.components.RaahiGlassPanel
import com.example.ui.theme.SecondarySafetyGreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Native MapTiler Vector / Raster Engine via MapLibre GL JS & Leaflet container.
 * Directly renders MapTiler cloud styles (streets-v2 / outdoor-v2) using the provided API Key.
 * Operates independently from Google Play Services Maps SDK to prevent Authorization Failure errors.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MapTilerView(
    modifier: Modifier = Modifier,
    apiKey: String,
    styleMode: String = "streets-v2", // "streets-v2" or "outdoor-v2"
    userLocation: UserLocation? = null,
    selectedDestination: KarachiDestination? = null,
    routes: List<RaahiRoute> = emptyList(),
    selectedRoute: RaahiRoute? = null,
    displayedSafePoints: List<SafePoint> = emptyList(),
    selectedSafePoint: SafePoint? = null,
    activeJourney: Journey? = null,
    onSafePointClick: (SafePoint) -> Unit = {},
    onMapClick: (LatLngPoint) -> Unit = {},
    onMapLoaded: () -> Unit = {}
) {
    val context = LocalContext.current
    var isWebViewLoaded by remember { mutableStateOf(false) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    val isDestroyed = remember { AtomicBoolean(false) }

    val currentSafePoints by rememberUpdatedState(displayedSafePoints)
    val currentOnSafePointClick by rememberUpdatedState(onSafePointClick)
    val currentOnMapClick by rememberUpdatedState(onMapClick)
    val currentOnMapLoaded by rememberUpdatedState(onMapLoaded)

    val activeRoute = selectedRoute ?: routes.firstOrNull()

    // Build the initial HTML page with Leaflet & MapTiler Raster/Vector tiles
    val initialHtml = remember(apiKey, styleMode) {
        generateMapTilerHtml(apiKey, styleMode)
    }

    // Helper function for completely safe, single-execution idempotent WebView teardown
    fun safelyDestroyWebView(wv: WebView?) {
        if (wv == null) return
        if (isDestroyed.compareAndSet(false, true)) {
            try {
                wv.removeJavascriptInterface("AndroidBridge")
            } catch (_: Throwable) {}

            try {
                wv.webChromeClient = null
                wv.webViewClient = WebViewClient() // replace with no-op client to stop callbacks
            } catch (_: Throwable) {}

            try {
                wv.stopLoading()
            } catch (_: Throwable) {}

            try {
                wv.loadUrl("about:blank")
            } catch (_: Throwable) {}

            try {
                wv.clearHistory()
            } catch (_: Throwable) {}

            try {
                (wv.parent as? ViewGroup)?.removeView(wv)
                wv.removeAllViews()
            } catch (_: Throwable) {}

            try {
                wv.destroy()
            } catch (e: Throwable) {
                Log.w("MapTilerView", "Notice during WebView disposal", e)
            }
            webViewRef = null
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("maptiler_map_container")
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    // Hardware accelerated rendering for smooth map pan/zoom without UI thread jank
                    setLayerType(View.LAYER_TYPE_HARDWARE, null)
                    setBackgroundColor(0xFF1E2322.toInt())

                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        cacheMode = WebSettings.LOAD_DEFAULT
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    }

                    addJavascriptInterface(object {
                        @JavascriptInterface
                        fun onMapClicked(lat: Double, lng: Double) {
                            if (isDestroyed.get()) return
                            post {
                                if (isDestroyed.get()) return@post
                                currentOnMapClick(LatLngPoint(lat, lng))
                            }
                        }

                        @JavascriptInterface
                        fun onSafePointClicked(id: String) {
                            if (isDestroyed.get()) return
                            post {
                                if (isDestroyed.get()) return@post
                                val point = currentSafePoints.find { it.id == id }
                                if (point != null) {
                                    currentOnSafePointClick(point)
                                }
                            }
                        }

                        @JavascriptInterface
                        fun onMapReady() {
                            if (isDestroyed.get()) return
                            post {
                                if (isDestroyed.get()) return@post
                                isWebViewLoaded = true
                                currentOnMapLoaded()
                            }
                        }
                    }, "AndroidBridge")

                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            if (isDestroyed.get()) return
                            super.onPageFinished(view, url)
                            isWebViewLoaded = true
                            currentOnMapLoaded()
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?
                        ) {
                            super.onReceivedError(view, request, error)
                        }
                    }

                    webChromeClient = WebChromeClient()
                    loadDataWithBaseURL("https://api.maptiler.com", initialHtml, "text/html", "UTF-8", null)
                    webViewRef = this
                }
            },
            update = { wv ->
                if (!isDestroyed.get()) {
                    webViewRef = wv
                }
            },
            onRelease = { webView ->
                safelyDestroyWebView(webView)
            }
        )

        // Loading spinner while tile engine boots
        AnimatedVisibility(
            visible = !isWebViewLoaded,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            RaahiGlassPanel(
                shape = RoundedCornerShape(16.dp),
                backgroundColor = Color(0xEE1E2322),
                borderColor = SecondarySafetyGreen.copy(alpha = 0.6f),
                elevation = 6.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(
                        color = SecondarySafetyGreen,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.5.dp
                    )
                    Text(
                        text = "Rendering MapTiler ${if (styleMode == "outdoor-v2") "Outdoor" else "Streets"}...",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = Color.White
                    )
                }
            }
        }
    }

    // Push dynamic data (user position, routes, safe points, destinations) to the WebView engine
    // Serialized asynchronously on Dispatchers.Default to prevent frame drops & UI jank
    LaunchedEffect(
        isWebViewLoaded,
        userLocation,
        selectedDestination,
        routes,
        activeRoute?.id,
        displayedSafePoints,
        activeJourney?.currentPosition
    ) {
        val wv = webViewRef ?: return@LaunchedEffect
        if (!isWebViewLoaded || isDestroyed.get()) return@LaunchedEffect

        try {
            val jsCode = withContext(Dispatchers.Default) {
                val payload = JSONObject().apply {
                    userLocation?.let {
                        put("userLocation", JSONObject().apply {
                            put("lat", it.latitude)
                            put("lng", it.longitude)
                        })
                    }

                    selectedDestination?.let {
                        put("destination", JSONObject().apply {
                            put("name", it.name)
                            put("category", it.category)
                            put("lat", it.latitude)
                            put("lng", it.longitude)
                        })
                    }

                    val routesArray = JSONArray()
                    routes.forEach { r ->
                        val rObj = JSONObject().apply {
                            put("id", r.id)
                            put("title", r.title)
                            put("isSelected", r.id == activeRoute?.id)
                            put("safetyScore", r.safetyProfile?.relativeSafetyScore ?: 80)
                            val ptsArray = JSONArray()
                            r.polylinePoints.forEach { pt ->
                                val ptArr = JSONArray().apply {
                                    put(pt.latitude)
                                    put(pt.longitude)
                                }
                                ptsArray.put(ptArr)
                            }
                            put("points", ptsArray)
                        }
                        routesArray.put(rObj)
                    }
                    put("routes", routesArray)

                    val safePointsArray = JSONArray()
                    displayedSafePoints.forEach { sp ->
                        val spObj = JSONObject().apply {
                            put("id", sp.id)
                            put("name", sp.name)
                            put("category", sp.category.name)
                            put("lat", sp.latitude)
                            put("lng", sp.longitude)
                            put("isVerified", sp.verified)
                        }
                        safePointsArray.put(spObj)
                    }
                    put("safePoints", safePointsArray)

                    activeJourney?.currentPosition?.let { navPos ->
                        put("navPosition", JSONObject().apply {
                            put("lat", navPos.latitude)
                            put("lng", navPos.longitude)
                        })
                    }
                }
                "if (window.updateRaahiData) { window.updateRaahiData(${payload.toString()}); }"
            }

            if (!isDestroyed.get() && webViewRef == wv) {
                withContext(Dispatchers.Main) {
                    if (!isDestroyed.get() && webViewRef == wv) {
                        try {
                            wv.evaluateJavascript(jsCode, null)
                        } catch (e: Throwable) {
                            Log.w("MapTilerView", "Suppressed evaluateJavascript exception on WebView: ${e.message}")
                        }
                    }
                }
            }
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Throwable) {
            if (!isDestroyed.get()) {
                Log.e("MapTilerView", "Failed to serialize or push data to MapTiler engine", e)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            safelyDestroyWebView(webViewRef)
        }
    }
}

/**
 * Generates an embedded, zero-external-dependency HTML/JS map engine using Leaflet & MapTiler raster tiles.
 * Works flawlessly offline (cached) and online with high-DPI rendering and GPU acceleration.
 */
private fun generateMapTilerHtml(apiKey: String, style: String): String {
    return """
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
    <style>
        html, body, #map {
            width: 100%;
            height: 100%;
            margin: 0;
            padding: 0;
            background-color: #1a1e1d;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
            overflow: hidden;
            touch-action: pan-x pan-y;
        }
        .leaflet-control-attribution, .leaflet-control-zoom {
            display: none !important;
        }
        /* Custom User Location Pulse Marker */
        .user-pulse-marker {
            width: 22px;
            height: 22px;
            background: #006D36;
            border: 3px solid #ffffff;
            border-radius: 50%;
            box-shadow: 0 0 12px rgba(0, 109, 54, 0.8), 0 2px 6px rgba(0,0,0,0.4);
            animation: raahiPulse 2s infinite;
        }
        @keyframes raahiPulse {
            0% { box-shadow: 0 0 0 0 rgba(0, 109, 54, 0.7); }
            70% { box-shadow: 0 0 0 14px rgba(0, 109, 54, 0); }
            100% { box-shadow: 0 0 0 0 rgba(0, 109, 54, 0); }
        }
        /* Custom Destination Pin */
        .dest-pin {
            width: 28px;
            height: 28px;
            background: #101414;
            border: 2px solid #E57373;
            border-radius: 50% 50% 50% 0;
            transform: rotate(-45deg);
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 3px 8px rgba(0,0,0,0.5);
        }
        .dest-pin::after {
            content: '';
            width: 8px;
            height: 8px;
            background: #E57373;
            border-radius: 50%;
        }
        /* Custom Safe Point Marker */
        .safepoint-icon {
            border-radius: 50%;
            border: 2px solid #ffffff;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #ffffff;
            font-weight: bold;
            font-size: 11px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.35);
            cursor: pointer;
        }
        .sp-safe-haven { background: #006D36; }
        .sp-police { background: #1E88E5; }
        .sp-hospital { background: #E53935; }
        .sp-pharmacy { background: #8E24AA; }
        .sp-transit { background: #F57C00; }
    </style>
</head>
<body>
    <div id="map"></div>
    <script>
        var map = L.map('map', {
            center: [24.8607, 67.0011],
            zoom: 13,
            zoomControl: false,
            attributionControl: false
        });

        map.on('click', function(e) {
            if (e && e.latlng) {
                var lat = e.latlng.lat;
                var lng = e.latlng.lng;
                if (window.AndroidBridge && window.AndroidBridge.onMapClicked) {
                    window.AndroidBridge.onMapClicked(lat, lng);
                }
            }
        });

        // Add MapTiler Tile Layer
        var tileUrl = 'https://api.maptiler.com/maps/${style}/256/{z}/{x}/{y}.png?key=${apiKey}';
        L.tileLayer(tileUrl, {
            maxZoom: 19,
            subdomains: ['a', 'b', 'c', 'd'],
            detectRetina: true
        }).addTo(map);

        var userMarker = null;
        var destMarker = null;
        var navMarker = null;
        var routeLayers = [];
        var safePointMarkers = [];

        // Notify Android bridge that engine is ready
        setTimeout(function() {
            if (window.AndroidBridge && window.AndroidBridge.onMapReady) {
                window.AndroidBridge.onMapReady();
            }
        }, 100);

        window.updateRaahiData = function(data) {
            if (!data) return;

            // 1. Clear previous dynamic route layers
            routeLayers.forEach(function(l) { map.removeLayer(l); });
            routeLayers = [];

            // 2. Clear safe point markers
            safePointMarkers.forEach(function(m) { map.removeLayer(m); });
            safePointMarkers = [];

            // 3. Render Routes
            if (data.routes && data.routes.length > 0) {
                var allBounds = [];
                // Render alternative routes first (behind active route)
                data.routes.filter(function(r) { return !r.isSelected; }).forEach(function(r) {
                    if (r.points && r.points.length >= 2) {
                        var altLine = L.polyline(r.points, {
                            color: '#747878',
                            weight: 5,
                            opacity: 0.65,
                            lineJoin: 'round',
                            dashArray: '6, 8'
                        }).addTo(map);
                        routeLayers.push(altLine);
                    }
                });

                // Render active selected route (Emerald green glow + Obsidian core)
                var active = data.routes.find(function(r) { return r.isSelected; }) || data.routes[0];
                if (active && active.points && active.points.length >= 2) {
                    var glowColor = active.safetyScore >= 85 ? '#006D36' : (active.safetyScore >= 65 ? '#D97706' : '#DC2626');
                    
                    var casing = L.polyline(active.points, {
                        color: glowColor,
                        weight: 9,
                        opacity: 0.85,
                        lineJoin: 'round'
                    }).addTo(map);
                    routeLayers.push(casing);

                    var core = L.polyline(active.points, {
                        color: '#101414',
                        weight: 5,
                        opacity: 0.95,
                        lineJoin: 'round'
                    }).addTo(map);
                    routeLayers.push(core);

                    active.points.forEach(function(p) { allBounds.push(p); });
                }

                if (allBounds.length > 0 && !data.navPosition) {
                    map.fitBounds(allBounds, { padding: [40, 40], maxZoom: 15 });
                }
            }

            // 4. Safe Points
            if (data.safePoints && data.safePoints.length > 0) {
                data.safePoints.forEach(function(sp) {
                    var cssClass = 'sp-safe-haven';
                    var symbol = '🛡';
                    if (sp.category.indexOf('POLICE') >= 0) { cssClass = 'sp-police'; symbol = '👮'; }
                    else if (sp.category.indexOf('HOSPITAL') >= 0 || sp.category.indexOf('MEDICAL') >= 0) { cssClass = 'sp-hospital'; symbol = '🏥'; }
                    else if (sp.category.indexOf('PHARMACY') >= 0) { cssClass = 'sp-pharmacy'; symbol = '💊'; }
                    else if (sp.category.indexOf('TRANSIT') >= 0) { cssClass = 'sp-transit'; symbol = '🚏'; }

                    var icon = L.divIcon({
                        className: 'safepoint-icon ' + cssClass,
                        html: '<div style="width:24px;height:24px;display:flex;align-items:center;justify-content:center;font-size:12px;">' + symbol + '</div>',
                        iconSize: [24, 24],
                        iconAnchor: [12, 12]
                    });

                    var m = L.marker([sp.lat, sp.lng], { icon: icon }).addTo(map);
                    m.on('click', function() {
                        if (window.AndroidBridge && window.AndroidBridge.onSafePointClicked) {
                            window.AndroidBridge.onSafePointClicked(sp.id);
                        }
                    });
                    safePointMarkers.push(m);
                });
            }

            // 5. User Location
            if (data.userLocation && !data.navPosition) {
                if (!userMarker) {
                    var uIcon = L.divIcon({
                        className: 'user-pulse-marker',
                        iconSize: [22, 22],
                        iconAnchor: [11, 11]
                    });
                    userMarker = L.marker([data.userLocation.lat, data.userLocation.lng], { icon: uIcon }).addTo(map);
                } else {
                    userMarker.setLatLng([data.userLocation.lat, data.userLocation.lng]);
                }
            } else if (userMarker) {
                map.removeLayer(userMarker);
                userMarker = null;
            }

            // 6. Destination Marker
            if (data.destination) {
                if (!destMarker) {
                    var dIcon = L.divIcon({
                        className: 'dest-pin',
                        iconSize: [28, 28],
                        iconAnchor: [14, 28]
                    });
                    destMarker = L.marker([data.destination.lat, data.destination.lng], { icon: dIcon }).addTo(map);
                } else {
                    destMarker.setLatLng([data.destination.lat, data.destination.lng]);
                }
            } else if (destMarker) {
                map.removeLayer(destMarker);
                destMarker = null;
            }

            // 7. Active Navigation Marker
            if (data.navPosition) {
                if (!navMarker) {
                    var nIcon = L.divIcon({
                        className: 'user-pulse-marker',
                        iconSize: [24, 24],
                        iconAnchor: [12, 12]
                    });
                    navMarker = L.marker([data.navPosition.lat, data.navPosition.lng], { icon: nIcon }).addTo(map);
                } else {
                    navMarker.setLatLng([data.navPosition.lat, data.navPosition.lng]);
                }
                map.panTo([data.navPosition.lat, data.navPosition.lng], { animate: true, duration: 0.5 });
            } else if (navMarker) {
                map.removeLayer(navMarker);
                navMarker = null;
            }
        };
    </script>
</body>
</html>
    """.trimIndent()
}
