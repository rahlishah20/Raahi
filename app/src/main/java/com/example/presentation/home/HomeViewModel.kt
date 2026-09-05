package com.example.presentation.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.core.location.LocationProvider
import com.example.data.local.ProfilePreferences
import com.example.data.repository.DemoEventRepositoryImpl
import com.example.data.repository.JourneyRepositoryImpl
import com.example.data.repository.RouteRepositoryImpl
import com.example.data.repository.SafePointRepositoryImpl
import com.example.domain.model.CommunityFeedback
import com.example.domain.model.CommunitySafetyContextTag
import com.example.domain.model.CommunitySafetyRating
import com.example.domain.model.DemoEvent
import com.example.domain.model.Journey
import com.example.domain.model.JourneyState
import com.example.domain.model.KarachiDestination
import com.example.domain.model.LatLngPoint
import com.example.domain.model.LocationPermissionState
import com.example.domain.model.RaahiRoute
import com.example.domain.model.RecalculationState
import com.example.domain.model.SafePoint
import com.example.domain.model.SafePointCategory
import com.example.domain.repository.CommunityFeedbackRepository
import com.example.domain.repository.DemoEventRepository
import com.example.domain.repository.DestinationRepository
import com.example.domain.repository.JourneyRepository
import com.example.domain.repository.RouteRepository
import com.example.domain.repository.SafePointRepository
import com.example.domain.usecase.ApplyDemoEventUseCase
import com.example.domain.usecase.CancelJourneyUseCase
import com.example.domain.usecase.CompleteJourneyUseCase
import com.example.domain.usecase.EvaluateRouteSafetyUseCase
import com.example.domain.usecase.GetCommunitySafetySignalUseCase
import com.example.domain.usecase.GetNearbySafePointsUseCase
import com.example.domain.usecase.PlanRouteUseCase
import com.example.domain.usecase.RecalculateActiveJourneySafetyUseCase
import com.example.domain.usecase.RecalculateSafetyUseCase
import com.example.domain.usecase.ResetDemoEventsUseCase
import com.example.domain.usecase.StartJourneyUseCase
import com.example.domain.usecase.SubmitCommunityFeedbackUseCase
import com.example.domain.usecase.SwitchJourneyRouteUseCase
import com.example.domain.usecase.UpdateJourneyProgressUseCase
import com.example.presentation.components.RaahiNavTab
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

class HomeViewModel(
    private val destinationRepository: DestinationRepository,
    private val locationProvider: LocationProvider,
    private val planRouteUseCase: PlanRouteUseCase,
    private val evaluateRouteSafetyUseCase: EvaluateRouteSafetyUseCase = EvaluateRouteSafetyUseCase(),
    private val safePointRepository: SafePointRepository = SafePointRepositoryImpl(),
    private val getNearbySafePointsUseCase: GetNearbySafePointsUseCase = GetNearbySafePointsUseCase(safePointRepository),
    private val submitCommunityFeedbackUseCase: SubmitCommunityFeedbackUseCase = SubmitCommunityFeedbackUseCase(),
    private val getCommunitySafetySignalUseCase: GetCommunitySafetySignalUseCase = GetCommunitySafetySignalUseCase(),
    private val demoEventRepository: DemoEventRepository = DemoEventRepositoryImpl(),
    private val recalculateSafetyUseCase: RecalculateSafetyUseCase = RecalculateSafetyUseCase(demoEventRepository = demoEventRepository),
    private val applyDemoEventUseCase: ApplyDemoEventUseCase = ApplyDemoEventUseCase(demoEventRepository, recalculateSafetyUseCase),
    private val resetDemoEventsUseCase: ResetDemoEventsUseCase = ResetDemoEventsUseCase(demoEventRepository, recalculateSafetyUseCase),
    private val journeyRepository: JourneyRepository = JourneyRepositoryImpl(),
    private val startJourneyUseCase: StartJourneyUseCase = StartJourneyUseCase(journeyRepository),
    private val updateJourneyProgressUseCase: UpdateJourneyProgressUseCase = UpdateJourneyProgressUseCase(journeyRepository),
    private val recalculateActiveJourneySafetyUseCase: RecalculateActiveJourneySafetyUseCase = RecalculateActiveJourneySafetyUseCase(journeyRepository, recalculateSafetyUseCase),
    private val completeJourneyUseCase: CompleteJourneyUseCase = CompleteJourneyUseCase(journeyRepository),
    private val cancelJourneyUseCase: CancelJourneyUseCase = CancelJourneyUseCase(journeyRepository),
    private val switchJourneyRouteUseCase: SwitchJourneyRouteUseCase = SwitchJourneyRouteUseCase(journeyRepository),
    private val profilePreferences: ProfilePreferences? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    private var routeJob: Job? = null
    private var safePointsJob: Job? = null
    private var locationTrackingJob: Job? = null
    private var journeyObserverJob: Job? = null

    init {
        computeGreeting()
        loadQuickShortcuts()
        loadInitialSafePoints()
        observeJourneyState()
        loadProfilePreferences()
    }

    private fun loadProfilePreferences() {
        profilePreferences?.let { prefs ->
            _uiState.update {
                it.copy(
                    profileName = prefs.getProfileName(),
                    profilePhotoPath = prefs.getProfilePhotoPath()
                )
            }
        }
    }

    fun updateProfileName(name: String) {
        _uiState.update { it.copy(profileName = name) }
        profilePreferences?.setProfileName(name)
    }

    fun updateProfilePhoto(path: String) {
        _uiState.update { it.copy(profilePhotoPath = path) }
        profilePreferences?.setProfilePhotoPath(path)
    }

    private fun observeJourneyState() {
        journeyObserverJob?.cancel()
        journeyObserverJob = viewModelScope.launch {
            journeyRepository.getCurrentJourney().collect { journey ->
                _uiState.update {
                    it.copy(
                        activeJourney = journey,
                        isNavigating = journey != null && journey.state.isActiveOrRecalculating,
                        currentNavigationInstruction = journey?.currentInstruction,
                        suggestedAlternativeRoute = journey?.suggestedAlternativeRoute,
                        showRerouteDialog = journey?.suggestedAlternativeRoute != null
                    )
                }
            }
        }
        viewModelScope.launch {
            journeyRepository.getJourneyHistory().collect { history ->
                _uiState.update { it.copy(journeyHistory = history) }
            }
        }
    }

    private fun loadInitialSafePoints() {
        val defaultLoc = locationProvider.defaultKarachiLocation
        loadNearbySafePoints(defaultLoc.latitude, defaultLoc.longitude)
    }

    fun loadNearbySafePoints(lat: Double, lng: Double) {
        safePointsJob?.cancel()
        safePointsJob = viewModelScope.launch {
            val filter = _uiState.value.safePointCategoryFilter
            val onlyVerified = _uiState.value.onlyVerifiedSafePoints
            getNearbySafePointsUseCase(
                latitude = lat,
                longitude = lng,
                radiusMeters = 2500,
                categoryFilter = filter,
                onlyVerified = onlyVerified
            ).collect { points ->
                _uiState.update { it.copy(nearbySafePoints = points) }
            }
        }
    }

    fun onSelectSafePoint(safePoint: SafePoint?) {
        _uiState.update { it.copy(selectedSafePoint = safePoint) }
    }

    fun onFilterSafePointsCategory(category: SafePointCategory?) {
        _uiState.update { it.copy(safePointCategoryFilter = category) }
        val currentLoc = _uiState.value.userLocation ?: locationProvider.defaultKarachiLocation
        loadNearbySafePoints(currentLoc.latitude, currentLoc.longitude)
    }

    fun onToggleOnlyVerified(onlyVerified: Boolean) {
        _uiState.update { it.copy(onlyVerifiedSafePoints = onlyVerified) }
        val currentLoc = _uiState.value.userLocation ?: locationProvider.defaultKarachiLocation
        loadNearbySafePoints(currentLoc.latitude, currentLoc.longitude)
    }

    private fun computeGreeting() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 5..11 -> "Good morning."
            in 12..16 -> "Good afternoon."
            in 17..21 -> "Good evening."
            else -> "Good night."
        }
        _uiState.update { it.copy(greeting = greeting) }
    }

    private fun loadQuickShortcuts() {
        viewModelScope.launch {
            destinationRepository.getQuickShortcuts().collect { shortcuts ->
                _uiState.update { it.copy(quickShortcuts = shortcuts) }
            }
        }
    }

    fun onPermissionResult(granted: Boolean) {
        if (granted) {
            _uiState.update {
                it.copy(
                    permissionState = LocationPermissionState.GRANTED,
                    isLocationPermissionGranted = true
                )
            }
            fetchCurrentLocation()
        } else {
            _uiState.update {
                it.copy(
                    permissionState = LocationPermissionState.DENIED,
                    isLocationPermissionGranted = false
                )
            }
        }
    }

    fun fetchCurrentLocation() {
        viewModelScope.launch {
            val location = locationProvider.getCurrentLocation()
            _uiState.update {
                it.copy(
                    userLocation = location,
                    originName = if (location != null) "Current Location (${formatAreaName(location.latitude, location.longitude)})" else "Current Location (Clifton)"
                )
            }
            if (location != null) {
                loadNearbySafePoints(location.latitude, location.longitude)
            }
            // Recalculate route if a destination is already selected
            val currentDest = _uiState.value.selectedDestination
            if (currentDest != null) {
                planJourney(currentDest)
            }
        }
    }

    fun onMapLocationSelected(latitude: Double, longitude: Double) {
        Log.d("RaahiSafetyEngine", "LOCATION_SELECTED: Captured lat=$latitude, lng=$longitude")
        Log.d("RaahiSafetyEngine", "SAFETY_ENGINE_STARTED: Initializing safety pipeline for selected map location")
        Log.d("RaahiSafetyEngine", "SAFETY_ENGINE_INPUT: Target coordinates ($latitude, $longitude)")

        val areaName = formatAreaName(latitude, longitude)
        val destName = "Selected Location ($areaName)"
        val addressString = String.format(java.util.Locale.US, "%.4f, %.4f", latitude, longitude)

        val destination = KarachiDestination(
            id = "map_selected_${System.currentTimeMillis()}",
            name = destName,
            area = areaName,
            address = addressString,
            latitude = latitude,
            longitude = longitude,
            category = com.example.domain.model.DestinationCategory.LANDMARK
        )

        onSelectDestination(destination)
    }

    fun onSelectDestination(destination: KarachiDestination) {
        Log.d("RaahiSafetyEngine", "LOCATION_SELECTED: Selected destination ${destination.name} (${destination.latitude}, ${destination.longitude})")
        routeJob?.cancel()
        _uiState.update {
            it.copy(
                selectedDestination = destination,
                isRouteConfirmed = false,
                routes = emptyList(),
                selectedRoute = null,
                selectedSafePoint = null,
                routeError = null,
                isCalculatingRoutes = true
            )
        }
        planJourney(destination)
    }

    fun planJourney(destination: KarachiDestination? = _uiState.value.selectedDestination) {
        if (destination == null) return

        val userLoc = _uiState.value.userLocation ?: locationProvider.defaultKarachiLocation
        val originPoint = LatLngPoint(userLoc.latitude, userLoc.longitude)
        val destPoint = LatLngPoint(destination.latitude, destination.longitude)
        val originLabel = _uiState.value.originName

        Log.d("RaahiSafetyEngine", "SAFETY_ENGINE_STARTED: Initiating route safety evaluation for ${destination.name}")
        Log.d("RaahiSafetyEngine", "SAFETY_ENGINE_INPUT: Origin=(${originPoint.latitude}, ${originPoint.longitude}), Dest=(${destPoint.latitude}, ${destPoint.longitude})")

        _uiState.update {
            it.copy(
                isCalculatingRoutes = true,
                routeError = null,
                routes = emptyList(),
                selectedRoute = null,
                isRouteConfirmed = false
            )
        }

        routeJob?.cancel()
        routeJob = viewModelScope.launch {
            try {
                Log.d("RaahiSafetyEngine", "SAFETY_PIPELINE_EXECUTING: Evaluating safety factors across corridors")
                val result = planRouteUseCase(
                    origin = originPoint,
                    destination = destPoint,
                    originName = originLabel,
                    destinationName = destination.name
                )

                result.onSuccess { rawRoutes ->
                    val evaluatedRoutes = evaluateRouteSafetyUseCase(rawRoutes).getOrDefault(rawRoutes)
                    val recommendedRoute = evaluatedRoutes.firstOrNull { it.safetyProfile?.isRecommended == true }
                        ?: evaluatedRoutes.firstOrNull()

                    val score = recommendedRoute?.safetyProfile?.relativeSafetyScore ?: 0
                    Log.d("RaahiSafetyEngine", "SAFETY_SCORE_CALCULATED: Score=$score for recommended route ${recommendedRoute?.title}")
                    Log.d("RaahiSafetyEngine", "SAFETY_ENGINE_COMPLETED: Evaluated ${evaluatedRoutes.size} routes for ${destination.name}")

                    _uiState.update {
                        it.copy(
                            isCalculatingRoutes = false,
                            routes = evaluatedRoutes,
                            selectedRoute = recommendedRoute,
                            routeError = if (evaluatedRoutes.isEmpty()) "No valid Karachi routes found for this path." else null
                        )
                    }
                }.onFailure { error ->
                    Log.w("RaahiSafetyEngine", "SAFETY_ENGINE_NOTICE: Route calculation info: ${error.message}")
                    _uiState.update {
                        it.copy(
                            isCalculatingRoutes = false,
                            routes = emptyList(),
                            selectedRoute = null,
                            routeError = error.localizedMessage ?: "Failed to calculate Karachi routes."
                        )
                    }
                }
            } catch (e: kotlinx.coroutines.CancellationException) {
                Log.d("RaahiSafetyEngine", "SAFETY_ENGINE_CANCELLED: Safety calculation job cancelled due to location update or new selection")
                throw e
            } catch (e: Throwable) {
                Log.e("RaahiSafetyEngine", "SAFETY_ENGINE_ERROR: Unexpected failure during safety evaluation", e)
                _uiState.update {
                    it.copy(
                        isCalculatingRoutes = false,
                        routes = emptyList(),
                        selectedRoute = null,
                        routeError = e.localizedMessage ?: "Failed to calculate Karachi routes."
                    )
                }
            }
        }
    }

    fun selectRoute(route: RaahiRoute) {
        _uiState.update { it.copy(selectedRoute = route) }
    }

    fun confirmSelectedRoute() {
        val selectedRoute = _uiState.value.selectedRoute
        _uiState.update {
            it.copy(
                isRouteConfirmed = true,
                activeRouteCommunityAggregate = selectedRoute?.safetyProfile?.communityAggregate
            )
        }
    }

    fun onOpenFeedbackPrompt() {
        _uiState.update {
            it.copy(
                isFeedbackPromptVisible = true,
                feedbackRating = null,
                selectedFeedbackTags = emptySet(),
                feedbackSubmissionSuccess = false,
                feedbackError = null
            )
        }
    }

    fun onDismissFeedbackPrompt() {
        _uiState.update {
            it.copy(
                isFeedbackPromptVisible = false,
                feedbackError = null
            )
        }
    }

    fun onSelectFeedbackRating(rating: CommunitySafetyRating) {
        _uiState.update { it.copy(feedbackRating = rating, feedbackError = null) }
    }

    fun onToggleFeedbackTag(tag: CommunitySafetyContextTag) {
        _uiState.update { state ->
            val current = state.selectedFeedbackTags
            val updated = if (current.contains(tag)) current - tag else current + tag
            state.copy(selectedFeedbackTags = updated)
        }
    }

    fun onSubmitFeedback(comment: String? = null) {
        val rating = _uiState.value.feedbackRating
        if (rating == null) {
            _uiState.update { it.copy(feedbackError = "Please select how safe you felt.") }
            return
        }

        val activeRoute = _uiState.value.selectedRoute
        val loc = _uiState.value.userLocation ?: locationProvider.defaultKarachiLocation

        val feedback = CommunityFeedback(
            id = UUID.randomUUID().toString(),
            journeyId = UUID.randomUUID().toString(),
            routeId = activeRoute?.id,
            latitude = loc.latitude,
            longitude = loc.longitude,
            safetyRating = rating,
            contextualTags = _uiState.value.selectedFeedbackTags.toList(),
            comment = comment,
            createdAt = System.currentTimeMillis(),
            isSeeded = false
        )

        _uiState.update { it.copy(isSubmittingFeedback = true, feedbackError = null) }

        viewModelScope.launch {
            val result = submitCommunityFeedbackUseCase(feedback)
            result.onSuccess {
                // Refresh aggregate for active route if present
                val updatedAggregate = if (activeRoute != null) {
                    getCommunitySafetySignalUseCase(activeRoute)
                } else {
                    getCommunitySafetySignalUseCase.getForLocation(loc.latitude, loc.longitude)
                }

                _uiState.update {
                    it.copy(
                        isSubmittingFeedback = false,
                        feedbackSubmissionSuccess = true,
                        activeRouteCommunityAggregate = updatedAggregate
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSubmittingFeedback = false,
                        feedbackSubmissionSuccess = false,
                        feedbackError = error.localizedMessage ?: "Failed to submit feedback."
                    )
                }
            }
        }
    }

    fun onResetFeedbackState() {
        _uiState.update {
            it.copy(
                isFeedbackPromptVisible = false,
                feedbackRating = null,
                selectedFeedbackTags = emptySet(),
                isSubmittingFeedback = false,
                feedbackSubmissionSuccess = false,
                feedbackError = null
            )
        }
    }

    fun applyDemoEvent(event: DemoEvent) {
        val currentRoutes = _uiState.value.routes
        if (currentRoutes.isEmpty()) return

        val prevRecommendedTitle = _uiState.value.routes.firstOrNull { it.safetyProfile?.isRecommended == true }?.title

        _uiState.update {
            it.copy(
                recalculationState = RecalculationState.RECALCULATING,
                previousRecommendedRouteTitle = prevRecommendedTitle
            )
        }

        viewModelScope.launch {
            val result = applyDemoEventUseCase(event, currentRoutes)
            result.onSuccess { recalculationResult ->
                val currentSelectedId = _uiState.value.selectedRoute?.id
                val newSelectedRoute = recalculationResult.recalculatedRoutes.firstOrNull { it.id == currentSelectedId }
                    ?: recalculationResult.recalculatedRoutes.firstOrNull { it.safetyProfile?.isRecommended == true }
                    ?: recalculationResult.recalculatedRoutes.firstOrNull()

                val activeJ = _uiState.value.activeJourney
                var rerouteNotice: String? = null
                var suggestedAlt: RaahiRoute? = null

                if (activeJ != null && activeJ.state.isActiveOrRecalculating) {
                    val activeScore = newSelectedRoute?.safetyProfile?.relativeSafetyScore ?: 0
                    val strongerAlt = recalculationResult.recalculatedRoutes.firstOrNull {
                        it.id != newSelectedRoute?.id &&
                                (it.safetyProfile?.relativeSafetyScore ?: 0) > (activeScore + 5)
                    }
                    if (strongerAlt != null) {
                        suggestedAlt = strongerAlt
                        val altScore = strongerAlt.safetyProfile?.relativeSafetyScore ?: 0
                        rerouteNotice = "Conditions changed. Another route currently presents a stronger safety profile ($altScore vs $activeScore)."
                    }
                    recalculateActiveJourneySafetyUseCase(activeJ.id, recalculationResult.recalculatedRoutes)
                }

                _uiState.update {
                    it.copy(
                        routes = recalculationResult.recalculatedRoutes,
                        selectedRoute = newSelectedRoute,
                        recalculationState = RecalculationState.UPDATED,
                        activeDemoEvents = demoEventRepository.getActiveEventsSnapshot(),
                        recalculationNotice = recalculationResult.summaryNotice,
                        isRecommendationChanged = recalculationResult.recommendationChanged,
                        suggestedAlternativeRoute = suggestedAlt,
                        suggestedRerouteNotice = rerouteNotice,
                        showRerouteDialog = suggestedAlt != null
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        recalculationState = RecalculationState.ERROR,
                        routeError = error.localizedMessage ?: "Failed to recalculate safety profiles."
                    )
                }
            }
        }
    }

    fun applyDemoScenario(events: List<DemoEvent>) {
        val currentRoutes = _uiState.value.routes
        if (currentRoutes.isEmpty()) return

        val prevRecommendedTitle = _uiState.value.routes.firstOrNull { it.safetyProfile?.isRecommended == true }?.title

        _uiState.update {
            it.copy(
                recalculationState = RecalculationState.RECALCULATING,
                previousRecommendedRouteTitle = prevRecommendedTitle
            )
        }

        viewModelScope.launch {
            val result = applyDemoEventUseCase.applyEvents(events, currentRoutes)
            result.onSuccess { recalculationResult ->
                val currentSelectedId = _uiState.value.selectedRoute?.id
                val newSelectedRoute = recalculationResult.recalculatedRoutes.firstOrNull { it.id == currentSelectedId }
                    ?: recalculationResult.recalculatedRoutes.firstOrNull { it.safetyProfile?.isRecommended == true }
                    ?: recalculationResult.recalculatedRoutes.firstOrNull()

                val activeJ = _uiState.value.activeJourney
                var rerouteNotice: String? = null
                var suggestedAlt: RaahiRoute? = null

                if (activeJ != null && activeJ.state.isActiveOrRecalculating) {
                    val activeScore = newSelectedRoute?.safetyProfile?.relativeSafetyScore ?: 0
                    val strongerAlt = recalculationResult.recalculatedRoutes.firstOrNull {
                        it.id != newSelectedRoute?.id &&
                                (it.safetyProfile?.relativeSafetyScore ?: 0) > (activeScore + 5)
                    }
                    if (strongerAlt != null) {
                        suggestedAlt = strongerAlt
                        val altScore = strongerAlt.safetyProfile?.relativeSafetyScore ?: 0
                        rerouteNotice = "Conditions changed. Another route currently presents a stronger safety profile ($altScore vs $activeScore)."
                    }
                    recalculateActiveJourneySafetyUseCase(activeJ.id, recalculationResult.recalculatedRoutes)
                }

                _uiState.update {
                    it.copy(
                        routes = recalculationResult.recalculatedRoutes,
                        selectedRoute = newSelectedRoute,
                        recalculationState = RecalculationState.UPDATED,
                        activeDemoEvents = demoEventRepository.getActiveEventsSnapshot(),
                        recalculationNotice = recalculationResult.summaryNotice,
                        isRecommendationChanged = recalculationResult.recommendationChanged,
                        suggestedAlternativeRoute = suggestedAlt,
                        suggestedRerouteNotice = rerouteNotice,
                        showRerouteDialog = suggestedAlt != null
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        recalculationState = RecalculationState.ERROR,
                        routeError = error.localizedMessage ?: "Failed to recalculate safety profiles."
                    )
                }
            }
        }
    }

    fun startActiveJourney() {
        val selectedRoute = _uiState.value.selectedRoute ?: _uiState.value.routes.firstOrNull() ?: return
        val alternatives = _uiState.value.routes.filter { it.id != selectedRoute.id }

        viewModelScope.launch {
            val result = startJourneyUseCase(selectedRoute, alternatives)
            result.onSuccess { journey ->
                _uiState.update {
                    it.copy(
                        activeJourney = journey,
                        isNavigating = true,
                        isRouteConfirmed = true,
                        currentNavigationInstruction = journey.currentInstruction,
                        toastMessage = "Navigation started along ${journey.route.title}"
                    )
                }
                startLocationTracking(journey.id)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(routeError = error.localizedMessage ?: "Unable to start journey.")
                }
            }
        }
    }

    private fun startLocationTracking(journeyId: String) {
        locationTrackingJob?.cancel()
        locationTrackingJob = viewModelScope.launch {
            locationProvider.getLocationUpdates(3000L).collect { userLoc ->
                _uiState.update { it.copy(userLocation = userLoc) }
                if (_uiState.value.isNavigating) {
                    updateJourneyLocationInternal(journeyId, userLoc.latitude, userLoc.longitude)
                }
            }
        }
    }

    fun updateActiveJourneyLocation(lat: Double, lng: Double) {
        val journeyId = _uiState.value.activeJourney?.id ?: return
        viewModelScope.launch {
            updateJourneyLocationInternal(journeyId, lat, lng)
        }
    }

    private suspend fun updateJourneyLocationInternal(journeyId: String, lat: Double, lng: Double) {
        val result = updateJourneyProgressUseCase(journeyId, LatLngPoint(lat, lng))
        result.onSuccess { updatedJourney ->
            val justArrived = updatedJourney.state == JourneyState.ARRIVED
            _uiState.update {
                it.copy(
                    activeJourney = updatedJourney,
                    currentNavigationInstruction = updatedJourney.currentInstruction,
                    isFeedbackPromptVisible = if (justArrived) true else it.isFeedbackPromptVisible,
                    toastMessage = if (justArrived) "Arrived at ${updatedJourney.destinationName}" else it.toastMessage
                )
            }
        }
    }

    /**
     * Advances simulation one step along the active route polyline.
     * Perfect for testing, evaluation, and interactive prototype demo.
     */
    fun advanceSimulatedStep() {
        val journey = _uiState.value.activeJourney ?: return
        val points = journey.route.polylinePoints
        if (points.isEmpty()) return

        val currentIdx = points.indexOfFirst {
            it.latitude == journey.currentPosition?.latitude && it.longitude == journey.currentPosition?.longitude
        }
        val nextIdx = (if (currentIdx == -1) 1 else currentIdx + 1).coerceAtMost(points.size - 1)
        val nextPoint = points[nextIdx]

        viewModelScope.launch {
            updateJourneyLocationInternal(journey.id, nextPoint.latitude, nextPoint.longitude)
        }
    }

    fun completeActiveJourney() {
        val journeyId = _uiState.value.activeJourney?.id ?: return
        locationTrackingJob?.cancel()
        viewModelScope.launch {
            val result = completeJourneyUseCase(journeyId)
            result.onSuccess { completed ->
                _uiState.update {
                    it.copy(
                        activeJourney = completed,
                        isNavigating = false,
                        isFeedbackPromptVisible = true,
                        toastMessage = "Journey completed. Please share your safety feedback."
                    )
                }
            }
        }
    }

    fun cancelActiveJourney() {
        val journeyId = _uiState.value.activeJourney?.id ?: return
        locationTrackingJob?.cancel()
        viewModelScope.launch {
            val result = cancelJourneyUseCase(journeyId)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        activeJourney = null,
                        isNavigating = false,
                        isRouteConfirmed = false,
                        showRerouteDialog = false,
                        suggestedAlternativeRoute = null,
                        suggestedRerouteNotice = null,
                        toastMessage = "Navigation ended."
                    )
                }
            }
        }
    }

    fun switchActiveJourneyRoute(newRoute: RaahiRoute) {
        val journeyId = _uiState.value.activeJourney?.id ?: return
        viewModelScope.launch {
            val result = switchJourneyRouteUseCase(journeyId, newRoute)
            result.onSuccess { updatedJourney ->
                _uiState.update {
                    it.copy(
                        activeJourney = updatedJourney,
                        selectedRoute = newRoute,
                        showRerouteDialog = false,
                        suggestedAlternativeRoute = null,
                        suggestedRerouteNotice = null,
                        toastMessage = "Switched to ${newRoute.title} (${newRoute.safetyProfile?.relativeSafetyScore ?: 0} Safety Score)"
                    )
                }
            }
        }
    }

    fun dismissRerouteDialog() {
        _uiState.update {
            it.copy(
                showRerouteDialog = false,
                suggestedAlternativeRoute = null,
                suggestedRerouteNotice = null
            )
        }
    }

    fun resetDemoSimulation() {
        val currentRoutes = _uiState.value.routes
        if (currentRoutes.isEmpty()) return

        _uiState.update {
            it.copy(
                recalculationState = RecalculationState.RECALCULATING
            )
        }

        viewModelScope.launch {
            val result = resetDemoEventsUseCase(currentRoutes)
            result.onSuccess { recalculationResult ->
                val currentSelectedId = _uiState.value.selectedRoute?.id
                val newSelectedRoute = recalculationResult.recalculatedRoutes.firstOrNull { it.id == currentSelectedId }
                    ?: recalculationResult.recalculatedRoutes.firstOrNull { it.safetyProfile?.isRecommended == true }
                    ?: recalculationResult.recalculatedRoutes.firstOrNull()

                _uiState.update {
                    it.copy(
                        routes = recalculationResult.recalculatedRoutes,
                        selectedRoute = newSelectedRoute,
                        recalculationState = RecalculationState.UPDATED,
                        activeDemoEvents = emptyList(),
                        recalculationNotice = "Safety conditions restored to baseline profile.",
                        isRecommendationChanged = recalculationResult.recommendationChanged
                    )
                }

                val activeJ = _uiState.value.activeJourney
                if (activeJ != null && activeJ.state.isActiveOrRecalculating) {
                    recalculateActiveJourneySafetyUseCase(activeJ.id, recalculationResult.recalculatedRoutes)
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        recalculationState = RecalculationState.ERROR,
                        routeError = error.localizedMessage ?: "Failed to reset safety simulation."
                    )
                }
            }
        }
    }

    fun recalculateRouteSafety() {
        val currentRoutes = _uiState.value.routes
        if (currentRoutes.isEmpty()) return

        _uiState.update {
            it.copy(recalculationState = RecalculationState.RECALCULATING)
        }

        viewModelScope.launch {
            val result = recalculateSafetyUseCase(currentRoutes)
            result.onSuccess { recalculationResult ->
                val currentSelectedId = _uiState.value.selectedRoute?.id
                val newSelectedRoute = recalculationResult.recalculatedRoutes.firstOrNull { it.id == currentSelectedId }
                    ?: recalculationResult.recalculatedRoutes.firstOrNull { it.safetyProfile?.isRecommended == true }
                    ?: recalculationResult.recalculatedRoutes.firstOrNull()

                val activeJ = _uiState.value.activeJourney
                var rerouteNotice: String? = null
                var suggestedAlt: RaahiRoute? = null

                if (activeJ != null && activeJ.state.isActiveOrRecalculating) {
                    val activeScore = newSelectedRoute?.safetyProfile?.relativeSafetyScore ?: 0
                    val strongerAlt = recalculationResult.recalculatedRoutes.firstOrNull {
                        it.id != newSelectedRoute?.id &&
                                (it.safetyProfile?.relativeSafetyScore ?: 0) > (activeScore + 5)
                    }
                    if (strongerAlt != null) {
                        suggestedAlt = strongerAlt
                        val altScore = strongerAlt.safetyProfile?.relativeSafetyScore ?: 0
                        rerouteNotice = "Conditions changed. Another route currently presents a stronger safety profile ($altScore vs $activeScore)."
                    }
                    recalculateActiveJourneySafetyUseCase(activeJ.id, recalculationResult.recalculatedRoutes)
                }

                _uiState.update {
                    it.copy(
                        routes = recalculationResult.recalculatedRoutes,
                        selectedRoute = newSelectedRoute,
                        recalculationState = RecalculationState.UPDATED,
                        activeDemoEvents = demoEventRepository.getActiveEventsSnapshot(),
                        recalculationNotice = recalculationResult.summaryNotice,
                        isRecommendationChanged = recalculationResult.recommendationChanged,
                        suggestedAlternativeRoute = suggestedAlt,
                        suggestedRerouteNotice = rerouteNotice,
                        showRerouteDialog = suggestedAlt != null
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        recalculationState = RecalculationState.ERROR,
                        routeError = error.localizedMessage ?: "Failed to recalculate safety profiles."
                    )
                }
            }
        }
    }

    fun dismissRecalculationNotice() {
        _uiState.update {
            it.copy(
                recalculationNotice = null,
                recalculationState = if (it.activeDemoEvents.isNotEmpty()) RecalculationState.UPDATED else RecalculationState.IDLE
            )
        }
    }

    fun clearSelectedDestination() {
        routeJob?.cancel()
        locationTrackingJob?.cancel()
        _uiState.update {
            it.copy(
                selectedDestination = null,
                routes = emptyList(),
                selectedRoute = null,
                isCalculatingRoutes = false,
                routeError = null,
                isRouteConfirmed = false,
                isNavigating = false,
                activeJourney = null,
                recalculationState = RecalculationState.IDLE,
                recalculationNotice = null,
                isRecommendationChanged = false,
                suggestedAlternativeRoute = null,
                suggestedRerouteNotice = null,
                showRerouteDialog = false
            )
        }
    }

    fun onNavTabSelected(tab: RaahiNavTab) {
        _uiState.update { it.copy(selectedNavTab = tab) }
    }

    private fun formatAreaName(lat: Double, lng: Double): String {
        return when {
            lat < 24.83 && lng < 67.04 -> "Clifton"
            lat < 24.82 && lng >= 67.04 -> "DHA"
            lat in 24.84..24.87 && lng in 67.00..67.04 -> "Saddar"
            lat in 24.85..24.89 && lng in 67.04..67.08 -> "PECHS"
            lat in 24.90..24.95 && lng in 67.06..67.12 -> "Gulshan"
            else -> "Karachi"
        }
    }

    class Factory(
        private val destinationRepository: DestinationRepository,
        private val context: Context,
        private val routeRepository: RouteRepository = RouteRepositoryImpl(),
        private val safePointRepository: SafePointRepository = SafePointRepositoryImpl(),
        private val demoEventRepository: DemoEventRepository = DemoEventRepositoryImpl()
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val locationProv = LocationProvider(context.applicationContext)
            val profilePrefs = ProfilePreferences(context.applicationContext)
            val planUseCase = PlanRouteUseCase(routeRepository)
            val getSafePointsUseCase = GetNearbySafePointsUseCase(safePointRepository)
            val recalculateUseCase = RecalculateSafetyUseCase(demoEventRepository = demoEventRepository)
            val applyEventUseCase = ApplyDemoEventUseCase(demoEventRepository, recalculateUseCase)
            val resetEventsUseCase = ResetDemoEventsUseCase(demoEventRepository, recalculateUseCase)
            return HomeViewModel(
                destinationRepository = destinationRepository,
                locationProvider = locationProv,
                planRouteUseCase = planUseCase,
                safePointRepository = safePointRepository,
                getNearbySafePointsUseCase = getSafePointsUseCase,
                demoEventRepository = demoEventRepository,
                recalculateSafetyUseCase = recalculateUseCase,
                applyDemoEventUseCase = applyEventUseCase,
                resetDemoEventsUseCase = resetEventsUseCase,
                profilePreferences = profilePrefs
            ) as T
        }
    }
}
