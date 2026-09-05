package com.example.presentation.home

import com.example.domain.model.KarachiDestination
import com.example.domain.model.LocationPermissionState
import com.example.domain.model.RaahiRoute
import com.example.domain.model.UserLocation
import com.example.presentation.components.RaahiNavTab

data class HomeState(
    val userLocation: UserLocation? = null,
    val selectedDestination: KarachiDestination? = null,
    val originName: String = "Current Location (Clifton)",
    val routes: List<RaahiRoute> = emptyList(),
    val selectedRoute: RaahiRoute? = null,
    val isCalculatingRoutes: Boolean = false,
    val routeError: String? = null,
    val isRouteConfirmed: Boolean = false,
    val quickShortcuts: List<KarachiDestination> = emptyList(),
    val permissionState: LocationPermissionState = LocationPermissionState.NOT_REQUESTED,
    val isLocationPermissionGranted: Boolean = false,
    val greeting: String = "Good morning.",
    val isMapReady: Boolean = true,
    val selectedNavTab: RaahiNavTab = RaahiNavTab.EXPLORE,
    val toastMessage: String? = null,
    val nearbySafePoints: List<com.example.domain.model.SafePoint> = emptyList(),
    val selectedSafePoint: com.example.domain.model.SafePoint? = null,
    val safePointCategoryFilter: com.example.domain.model.SafePointCategory? = null,
    val onlyVerifiedSafePoints: Boolean = false,
    // Community Perceived Safety Feedback
    val isFeedbackPromptVisible: Boolean = false,
    val feedbackRating: com.example.domain.model.CommunitySafetyRating? = null,
    val selectedFeedbackTags: Set<com.example.domain.model.CommunitySafetyContextTag> = emptySet(),
    val isSubmittingFeedback: Boolean = false,
    val feedbackSubmissionSuccess: Boolean = false,
    val feedbackError: String? = null,
    val activeRouteCommunityAggregate: com.example.domain.model.CommunitySafetyAggregate? = null,
    // Phase 6: Dynamic Safety Recalculation & Simulation
    val recalculationState: com.example.domain.model.RecalculationState = com.example.domain.model.RecalculationState.IDLE,
    val activeDemoEvents: List<com.example.domain.model.DemoEvent> = emptyList(),
    val recalculationNotice: String? = null,
    val isRecommendationChanged: Boolean = false,
    val previousRecommendedRouteTitle: String? = null,
    // Phase 7: Active Journey & Navigation
    val activeJourney: com.example.domain.model.Journey? = null,
    val isNavigating: Boolean = false,
    val currentNavigationInstruction: String? = null,
    val suggestedRerouteNotice: String? = null,
    val showRerouteDialog: Boolean = false,
    val suggestedAlternativeRoute: RaahiRoute? = null,
    val journeyHistory: List<com.example.domain.model.Journey> = emptyList(),
    val profileName: String = "Haseeb",
    val profilePhotoPath: String? = null
)
