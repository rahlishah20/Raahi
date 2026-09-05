package com.example.presentation.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.DestinationCategory
import com.example.domain.model.JourneyState
import com.example.domain.model.KarachiDestination
import com.example.domain.model.RaahiRoute
import com.example.presentation.components.ActiveNavigationOverlay
import com.example.presentation.components.AnalyzingSafetySignalsOverlay
import com.example.presentation.components.DedicatedRouteSelectionPanel
import com.example.presentation.components.RaahiGlassBottomActionSheet
import com.example.presentation.components.RaahiGlassBottomNavBar
import com.example.presentation.components.RaahiGlassSearchBar
import com.example.presentation.components.RaahiGlassTopBar
import com.example.presentation.components.RaahiNavTab
import com.example.presentation.components.RouteSafetyExplainWhySheet
import com.example.presentation.components.SafePointDetailSheet
import com.example.presentation.components.SafePointFilterBar
import com.example.presentation.components.SafetyConditionsChangedTakeover
import com.example.presentation.components.TripCompletedFeedbackDialog
import com.example.presentation.map.KarachiMapComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToSearch: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToEnvironmentControl: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }
    var showExplainWhyRoute by remember { mutableStateOf<RaahiRoute?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocation = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocation = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        viewModel.onPermissionResult(fineLocation || coarseLocation)
    }

    LaunchedEffect(Unit) {
        val hasFine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFine || hasCoarse) {
            viewModel.onPermissionResult(true)
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Observe and display toast messages in Snackbar
    LaunchedEffect(state.toastMessage) {
        state.toastMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // --- 1. BASE LAYER: Interactive Karachi Map ---
        KarachiMapComponent(
            modifier = Modifier.fillMaxSize(),
            userLocation = state.userLocation,
            selectedDestination = state.selectedDestination,
            routes = state.routes,
            selectedRoute = state.selectedRoute,
            nearbySafePoints = state.nearbySafePoints,
            selectedSafePoint = state.selectedSafePoint,
            activeJourney = state.activeJourney,
            isLocationPermissionGranted = state.isLocationPermissionGranted,
            onRecenterClick = {
                viewModel.fetchCurrentLocation()
            },
            onSafePointClick = { safePoint ->
                viewModel.onSelectSafePoint(safePoint)
            },
            onMapClick = { point ->
                viewModel.onMapLocationSelected(point.latitude, point.longitude)
            }
        )

        // --- 2. TOP FLOATING OVERLAYS (When not in full-screen navigation) ---
        if (!state.isNavigating) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top App Bar: "RAAHI", Menu, Avatar
                RaahiGlassTopBar(
                    profileName = state.profileName,
                    profilePhotoPath = state.profilePhotoPath,
                    onMenuClick = {},
                    onProfileClick = onNavigateToProfile
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Glass Search Bar
                RaahiGlassSearchBar(
                    selectedDestinationName = state.selectedDestination?.name,
                    placeholder = "Where are you going?",
                    onClick = onNavigateToSearch,
                    onMicClick = onNavigateToSearch
                )

                // Safe Point Filter Bar (When exploring without active multi-route planning)
                if (state.routes.isEmpty() && state.selectedDestination == null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    SafePointFilterBar(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        selectedCategory = state.safePointCategoryFilter,
                        onlyVerified = state.onlyVerifiedSafePoints,
                        onFilterCategory = { category ->
                            viewModel.onFilterSafePointsCategory(category)
                        },
                        onToggleOnlyVerified = { onlyVerified ->
                            viewModel.onToggleOnlyVerified(onlyVerified)
                        }
                    )
                }
            }
        }

        // --- 3. IMMERSIVE FULL-SCREEN ACTIVE NAVIGATION MODE (Gap 5) ---
        if (state.isNavigating && state.activeJourney != null && state.activeJourney?.state != JourneyState.ARRIVED) {
            ActiveNavigationOverlay(
                activeJourney = state.activeJourney!!,
                currentInstruction = state.currentNavigationInstruction,
                onCancelJourney = { viewModel.cancelActiveJourney() },
                onRecenterClick = { viewModel.fetchCurrentLocation() },
                onReportIncidentClick = { viewModel.onOpenFeedbackPrompt() },
                onAdvanceStep = { viewModel.advanceSimulatedStep() },
                onCompleteJourney = { viewModel.completeActiveJourney() },
                onViewDetails = {
                    showExplainWhyRoute = state.activeJourney?.route ?: state.selectedRoute
                }
            )
        }

        // --- 4. BOTTOM FLOATING OVERLAYS & PANELS (When not actively navigating) ---
        if (!state.isNavigating) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Case A: Safe Point Detail View (Gap 8)
                if (state.selectedSafePoint != null) {
                    SafePointDetailSheet(
                        safePoint = state.selectedSafePoint!!,
                        onNavigate = { safePoint ->
                            val dest = KarachiDestination(
                                id = safePoint.id,
                                name = safePoint.name,
                                area = safePoint.address.ifBlank { "Karachi Safe Haven" },
                                address = safePoint.address,
                                latitude = safePoint.latitude,
                                longitude = safePoint.longitude,
                                category = DestinationCategory.LANDMARK
                            )
                            viewModel.onSelectDestination(dest)
                        },
                        onDismiss = {
                            viewModel.onSelectSafePoint(null)
                        }
                    )
                }
                // Case B: Dedicated Multi-Route Selection Screen (Gap 3)
                else if (state.routes.isNotEmpty() && !state.isCalculatingRoutes && !state.isRouteConfirmed) {
                    DedicatedRouteSelectionPanel(
                        routes = state.routes,
                        selectedRoute = state.selectedRoute,
                        onSelectRoute = { route ->
                            viewModel.selectRoute(route)
                        },
                        onConfirmRoute = {
                            viewModel.confirmSelectedRoute()
                            viewModel.startActiveJourney()
                        },
                        onViewRouteDetails = { route ->
                            showExplainWhyRoute = route
                        }
                    )
                }
                // Case C: Standard Bottom Action Sheet (Destination details, Shortcuts, Initial prompts)
                else {
                    RaahiGlassBottomActionSheet(
                        greeting = state.greeting,
                        originName = state.originName,
                        selectedDestination = state.selectedDestination,
                        routes = state.routes,
                        selectedRoute = state.selectedRoute,
                        nearbySafePoints = state.nearbySafePoints,
                        selectedSafePoint = state.selectedSafePoint,
                        isCalculatingRoutes = state.isCalculatingRoutes,
                        routeError = state.routeError,
                        isRouteConfirmed = state.isRouteConfirmed,
                        quickShortcuts = state.quickShortcuts,
                        permissionState = state.permissionState,
                        isFeedbackPromptVisible = state.isFeedbackPromptVisible,
                        feedbackRating = state.feedbackRating,
                        selectedFeedbackTags = state.selectedFeedbackTags,
                        isSubmittingFeedback = state.isSubmittingFeedback,
                        feedbackSubmissionSuccess = state.feedbackSubmissionSuccess,
                        feedbackError = state.feedbackError,
                        recalculationState = state.recalculationState,
                        activeDemoEvents = state.activeDemoEvents,
                        recalculationNotice = state.recalculationNotice,
                        isRecommendationChanged = state.isRecommendationChanged,
                        activeJourney = state.activeJourney,
                        isNavigating = state.isNavigating,
                        suggestedAlternativeRoute = state.suggestedAlternativeRoute,
                        suggestedRerouteNotice = state.suggestedRerouteNotice,
                        showRerouteDialog = state.showRerouteDialog,
                        onStartJourney = { viewModel.startActiveJourney() },
                        onAdvanceSimulationStep = { viewModel.advanceSimulatedStep() },
                        onCompleteJourney = { viewModel.completeActiveJourney() },
                        onCancelJourney = { viewModel.cancelActiveJourney() },
                        onSwitchRoute = { route -> viewModel.switchActiveJourneyRoute(route) },
                        onDismissRerouteDialog = { viewModel.dismissRerouteDialog() },
                        onTriggerDemoActivityDrop = {
                            val recommendedRoute = state.routes.firstOrNull { it.safetyProfile?.isRecommended == true }
                            val event = com.example.domain.model.DemoEvent(
                                id = "demo_act_drop_${System.currentTimeMillis()}",
                                type = com.example.domain.model.DemoEventType.BUSINESS_ACTIVITY_CHANGE,
                                targetRouteId = recommendedRoute?.id,
                                targetFactorType = com.example.domain.model.SafetyFactorType.BUSINESS_ACTIVITY,
                                valueMultiplier = 0.35,
                                description = "Simulated early market & shop closure along primary corridor"
                            )
                            viewModel.applyDemoEvent(event)
                        },
                        onTriggerDemoMultiSignal = {
                            val recommendedRoute = state.routes.firstOrNull { it.safetyProfile?.isRecommended == true }
                            val event1 = com.example.domain.model.DemoEvent(
                                id = "demo_light_drop_${System.currentTimeMillis()}",
                                type = com.example.domain.model.DemoEventType.LIGHTING_CHANGE,
                                targetRouteId = recommendedRoute?.id,
                                targetFactorType = com.example.domain.model.SafetyFactorType.LIGHTING,
                                valueMultiplier = 0.40,
                                description = "Simulated streetlight outage along main avenue"
                            )
                            val event2 = com.example.domain.model.DemoEvent(
                                id = "demo_ped_drop_${System.currentTimeMillis()}",
                                type = com.example.domain.model.DemoEventType.PEDESTRIAN_ACTIVITY_CHANGE,
                                targetRouteId = recommendedRoute?.id,
                                targetFactorType = com.example.domain.model.SafetyFactorType.PEDESTRIAN_ACTIVITY,
                                valueMultiplier = 0.45,
                                description = "Simulated pedestrian density drop"
                            )
                            viewModel.applyDemoScenario(listOf(event1, event2))
                        },
                        onResetDemoSimulation = {
                            viewModel.resetDemoSimulation()
                        },
                        onDismissRecalculationNotice = {
                            viewModel.dismissRecalculationNotice()
                        },
                        onOpenFeedbackPrompt = { viewModel.onOpenFeedbackPrompt() },
                        onDismissFeedbackPrompt = { viewModel.onDismissFeedbackPrompt() },
                        onSelectFeedbackRating = { rating -> viewModel.onSelectFeedbackRating(rating) },
                        onToggleFeedbackTag = { tag -> viewModel.onToggleFeedbackTag(tag) },
                        onSubmitFeedback = { viewModel.onSubmitFeedback() },
                        onResetFeedbackState = { viewModel.onResetFeedbackState() },
                        onOpenSearch = onNavigateToSearch,
                        onSelectShortcut = { destination: KarachiDestination ->
                            viewModel.onSelectDestination(destination)
                        },
                        onSelectRoute = { route: RaahiRoute ->
                            viewModel.selectRoute(route)
                        },
                        onSelectSafePoint = { safePoint ->
                            viewModel.onSelectSafePoint(safePoint)
                        },
                        onConfirmRoute = {
                            viewModel.confirmSelectedRoute()
                        },
                        onRetryRoutes = {
                            viewModel.planJourney()
                        },
                        onClearSelectedDestination = {
                            viewModel.clearSelectedDestination()
                        },
                        onRequestPermission = {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Navigation Bar (Explore, History, Profile) (Gap 9)
                RaahiGlassBottomNavBar(
                    selectedTab = state.selectedNavTab,
                    onTabSelected = { tab ->
                        viewModel.onNavTabSelected(tab)
                        when (tab) {
                            RaahiNavTab.EXPLORE -> {}
                            RaahiNavTab.HISTORY -> onNavigateToHistory()
                            RaahiNavTab.PROFILE -> onNavigateToProfile()
                        }
                    }
                )
            }
        }

        // --- 5. DEDICATED "ANALYZING SAFETY SIGNALS" LOADING SCREEN (Gap 1) ---
        if (state.isCalculatingRoutes) {
            AnalyzingSafetySignalsOverlay(
                originName = state.originName,
                destinationName = state.selectedDestination?.name ?: "Destination"
            )
        }

        // --- 6. FULL-SCREEN "SAFETY CONDITIONS CHANGED" TAKEOVER ALERT (Gap 2) ---
        if (state.showRerouteDialog && state.suggestedAlternativeRoute != null) {
            SafetyConditionsChangedTakeover(
                notice = state.suggestedRerouteNotice,
                currentRoute = state.selectedRoute ?: state.activeJourney?.route,
                suggestedRoute = state.suggestedAlternativeRoute!!,
                onSwitchRoute = {
                    viewModel.switchActiveJourneyRoute(state.suggestedAlternativeRoute!!)
                },
                onDismissRerouteDialog = {
                    viewModel.dismissRerouteDialog()
                }
            )
        }

        // --- 7. DEDICATED "TRIP COMPLETED" FEEDBACK DIALOG (Gap 6) ---
        if (state.isFeedbackPromptVisible) {
            TripCompletedFeedbackDialog(
                selectedRating = state.feedbackRating,
                selectedTags = state.selectedFeedbackTags,
                onSelectRating = { rating ->
                    viewModel.onSelectFeedbackRating(rating)
                },
                onToggleTag = { tag ->
                    viewModel.onToggleFeedbackTag(tag)
                },
                onSubmitFeedback = {
                    viewModel.onSubmitFeedback()
                },
                onDismiss = {
                    viewModel.onDismissFeedbackPrompt()
                },
                isSubmitted = state.feedbackSubmissionSuccess
            )
        }

        // --- 8. ROUTE SAFETY SCORE & SAFETY ANALYSIS MODAL (Stitch Reference) ---
        showExplainWhyRoute?.let { route ->
            val currentRoute = state.routes.firstOrNull { it.id == route.id } ?: route
            RouteSafetyExplainWhySheet(
                route = currentRoute,
                allRoutes = state.routes,
                activeDemoEvents = state.activeDemoEvents,
                onSelectRoute = { newRoute ->
                    viewModel.selectRoute(newRoute)
                    showExplainWhyRoute = newRoute
                },
                onConfirmRoute = {
                    viewModel.selectRoute(currentRoute)
                    viewModel.confirmSelectedRoute()
                    viewModel.startActiveJourney()
                    showExplainWhyRoute = null
                },
                onDismiss = { showExplainWhyRoute = null }
            )
        }

        // --- 9. SNACKBAR HOST (Gap 10) ---
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )
    }
}
