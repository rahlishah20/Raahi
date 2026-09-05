package com.example.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TripOrigin
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.LinearProgressIndicator
import com.example.domain.model.Journey
import com.example.domain.model.JourneyState
import com.example.domain.model.CommunitySafetyAggregate
import com.example.domain.model.CommunitySafetyContextTag
import com.example.domain.model.CommunitySafetyRating
import com.example.domain.model.DestinationCategory
import com.example.domain.model.KarachiDestination
import com.example.domain.model.LocationPermissionState
import com.example.domain.model.RaahiRoute
import com.example.domain.model.RouteSafetyProfile
import com.example.domain.model.SafetyConfidence
import com.example.domain.model.SafePoint
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.CautionAmber
import com.example.ui.theme.OnCautionAmber
import com.example.ui.theme.OnPrimaryLight
import com.example.ui.theme.OnSecondaryContainerLight
import com.example.ui.theme.OnSurfaceLight
import com.example.ui.theme.OnSurfaceVariantLight
import com.example.ui.theme.OutlineVariantLight
import com.example.ui.theme.PrimaryObsidian
import com.example.ui.theme.SecondaryContainerLight
import com.example.ui.theme.SecondarySafetyGreen

@Composable
fun RaahiGlassBottomActionSheet(
    modifier: Modifier = Modifier,
    greeting: String = "Good morning.",
    originName: String = "Current Location (Clifton)",
    selectedDestination: KarachiDestination? = null,
    routes: List<RaahiRoute> = emptyList(),
    selectedRoute: RaahiRoute? = null,
    nearbySafePoints: List<SafePoint> = emptyList(),
    selectedSafePoint: SafePoint? = null,
    isCalculatingRoutes: Boolean = false,
    routeError: String? = null,
    isRouteConfirmed: Boolean = false,
    quickShortcuts: List<KarachiDestination> = emptyList(),
    permissionState: LocationPermissionState = LocationPermissionState.NOT_REQUESTED,
    // Community Perceived Safety Feedback States
    isFeedbackPromptVisible: Boolean = false,
    feedbackRating: CommunitySafetyRating? = null,
    selectedFeedbackTags: Set<CommunitySafetyContextTag> = emptySet(),
    isSubmittingFeedback: Boolean = false,
    feedbackSubmissionSuccess: Boolean = false,
    feedbackError: String? = null,
    // Phase 6: Dynamic Recalculation & Simulation
    recalculationState: com.example.domain.model.RecalculationState = com.example.domain.model.RecalculationState.IDLE,
    activeDemoEvents: List<com.example.domain.model.DemoEvent> = emptyList(),
    recalculationNotice: String? = null,
    isRecommendationChanged: Boolean = false,
    // Phase 7: Active Journey & Navigation
    activeJourney: Journey? = null,
    isNavigating: Boolean = false,
    currentNavigationInstruction: String? = null,
    suggestedAlternativeRoute: RaahiRoute? = null,
    suggestedRerouteNotice: String? = null,
    showRerouteDialog: Boolean = false,
    onStartJourney: () -> Unit = {},
    onAdvanceSimulationStep: () -> Unit = {},
    onCompleteJourney: () -> Unit = {},
    onCancelJourney: () -> Unit = {},
    onSwitchRoute: (RaahiRoute) -> Unit = {},
    onDismissRerouteDialog: () -> Unit = {},
    onTriggerDemoActivityDrop: () -> Unit = {},
    onTriggerDemoMultiSignal: () -> Unit = {},
    onResetDemoSimulation: () -> Unit = {},
    onDismissRecalculationNotice: () -> Unit = {},
    onOpenFeedbackPrompt: () -> Unit = {},
    onDismissFeedbackPrompt: () -> Unit = {},
    onSelectFeedbackRating: (CommunitySafetyRating) -> Unit = {},
    onToggleFeedbackTag: (CommunitySafetyContextTag) -> Unit = {},
    onSubmitFeedback: () -> Unit = {},
    onResetFeedbackState: () -> Unit = {},
    onOpenSearch: () -> Unit = {},
    onSelectShortcut: (KarachiDestination) -> Unit = {},
    onSelectRoute: (RaahiRoute) -> Unit = {},
    onSelectSafePoint: (SafePoint?) -> Unit = {},
    onConfirmRoute: () -> Unit = {},
    onRetryRoutes: () -> Unit = {},
    onClearSelectedDestination: () -> Unit = {},
    onRequestPermission: () -> Unit = {}
) {
    RaahiGlassPanel(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Top Grabber Handle
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(OutlineVariantLight.copy(alpha = 0.5f))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 1. ACTIVE NAVIGATION MODE (Phase 7)
            if (isNavigating && activeJourney != null) {
                ActiveJourneyNavigationPanel(
                    journey = activeJourney,
                    suggestedAlternativeRoute = suggestedAlternativeRoute,
                    suggestedRerouteNotice = suggestedRerouteNotice,
                    showRerouteDialog = showRerouteDialog,
                    isFeedbackPromptVisible = isFeedbackPromptVisible,
                    feedbackRating = feedbackRating,
                    selectedFeedbackTags = selectedFeedbackTags,
                    isSubmittingFeedback = isSubmittingFeedback,
                    feedbackSubmissionSuccess = feedbackSubmissionSuccess,
                    feedbackError = feedbackError,
                    onAdvanceStep = onAdvanceSimulationStep,
                    onCompleteJourney = onCompleteJourney,
                    onCancelJourney = onCancelJourney,
                    onSwitchRoute = onSwitchRoute,
                    onDismissRerouteDialog = onDismissRerouteDialog,
                    onSelectFeedbackRating = onSelectFeedbackRating,
                    onToggleFeedbackTag = onToggleFeedbackTag,
                    onSubmitFeedback = onSubmitFeedback,
                    onResetFeedback = onResetFeedbackState,
                    onSelectSafePoint = onSelectSafePoint
                )
            } else if (selectedDestination != null) {
                // Route Planning & Multiple Routes Presentation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(SecondarySafetyGreen)
                            )
                            Text(
                                text = if (routes.isNotEmpty()) "ROUTES AVAILABLE (${routes.size})" else "PLANNING JOURNEY",
                                style = MaterialTheme.typography.labelSmall,
                                color = SecondarySafetyGreen,
                                letterSpacing = 0.05.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        // Origin -> Destination Summary
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TripOrigin,
                                contentDescription = null,
                                tint = SecondarySafetyGreen,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = originName,
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariantLight,
                                maxLines = 1
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = PrimaryObsidian,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = selectedDestination.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = OnSurfaceLight,
                                maxLines = 1
                            )
                        }
                    }

                    IconButton(
                        onClick = onClearSelectedDestination,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1EDEC))
                            .testTag("clear_destination_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear destination",
                            tint = OnSurfaceVariantLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Loading State
                if (isCalculatingRoutes) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.6f))
                            .padding(16.dp)
                            .testTag("route_loading_indicator"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = PrimaryObsidian,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = "Calculating Karachi routes...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceLight
                        )
                    }
                }

                // Error State
                if (routeError != null && !isCalculatingRoutes) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFFDE8E8))
                            .border(1.dp, AlertRed.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            .padding(14.dp)
                            .testTag("route_error_panel"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = AlertRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = routeError,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF5A1A1A)
                            )
                        }

                        IconButton(
                            onClick = onRetryRoutes,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .testTag("retry_route_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Retry route calculation",
                                tint = PrimaryObsidian,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Multiple Route Option Cards
                if (routes.isNotEmpty() && !isCalculatingRoutes) {
                    // Recalculating in progress indicator
                    if (recalculationState == com.example.domain.model.RecalculationState.RECALCULATING) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE8F0FE))
                                .border(1.dp, Color(0xFF1A73E8).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("recalculation_in_progress_indicator"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp,
                                color = Color(0xFF1A73E8)
                            )
                            Text(
                                text = "Recalculating safety profile from updated signals...",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF1A73E8)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Recalculation Notice Banner
                    if (recalculationNotice != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isRecommendationChanged) Color(0xFFE6F4EA) else Color(0xFFF1F3F4))
                                .border(
                                    1.dp,
                                    if (isRecommendationChanged) SecondarySafetyGreen.copy(alpha = 0.5f) else Color(0xFFDADCE0),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("recalculation_notice_banner"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (isRecommendationChanged) Icons.Default.CheckCircle else Icons.Default.Info,
                                    contentDescription = null,
                                    tint = if (isRecommendationChanged) SecondarySafetyGreen else OnSurfaceVariantLight,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = recalculationNotice,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceLight,
                                    maxLines = 2
                                )
                            }
                            if (activeDemoEvents.isNotEmpty()) {
                                TextButton(
                                    onClick = onResetDemoSimulation,
                                    modifier = Modifier.testTag("reset_demo_button")
                                ) {
                                    Text(
                                        text = "Reset",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = PrimaryObsidian,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                            .verticalScroll(scrollState)
                            .testTag("routes_container"),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        routes.forEachIndexed { index, route ->
                            val isSelected = (selectedRoute?.id == route.id) || (selectedRoute == null && index == 0)
                            RouteOptionCard(
                                route = route,
                                isSelected = isSelected,
                                onClick = { onSelectRoute(route) },
                                testTag = "route_option_${index}"
                            )
                        }

                        // Selected Route Safety Intelligence Analysis Card
                        val activeRoute = selectedRoute ?: routes.firstOrNull()
                        val safetyProfile = activeRoute?.safetyProfile
                        if (safetyProfile != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            RouteSafetyAnalysisCard(
                                profile = safetyProfile,
                                routeTitle = activeRoute.title
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Simulation Trigger Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("simulation_controls_row"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = activeDemoEvents.any { it.type == com.example.domain.model.DemoEventType.BUSINESS_ACTIVITY_CHANGE },
                            onClick = {
                                if (activeDemoEvents.any { it.type == com.example.domain.model.DemoEventType.BUSINESS_ACTIVITY_CHANGE }) {
                                    onResetDemoSimulation()
                                } else {
                                    onTriggerDemoActivityDrop()
                                }
                            },
                            label = {
                                Text(
                                    text = if (activeDemoEvents.any { it.type == com.example.domain.model.DemoEventType.BUSINESS_ACTIVITY_CHANGE }) "Activity Drop Active" else "Simulate Activity Drop",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            modifier = Modifier.testTag("simulate_activity_drop_chip")
                        )

                        if (activeDemoEvents.isNotEmpty()) {
                            AssistChip(
                                onClick = onResetDemoSimulation,
                                label = {
                                    Text(
                                        text = "Reset Context",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                modifier = Modifier.testTag("reset_simulation_chip")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Primary Action Button (Route Selection Confirmation / Start Journey)
                    Button(
                        onClick = {
                            if (!isRouteConfirmed) {
                                onConfirmRoute()
                                onStartJourney()
                            } else {
                                onOpenFeedbackPrompt()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("confirm_route_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryObsidian,
                            contentColor = OnPrimaryLight
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (isRouteConfirmed) Icons.Default.Feedback else Icons.Default.Directions,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = if (isRouteConfirmed) "Share Route Feedback" else "Start Journey (${selectedRoute?.formattedDuration ?: routes.firstOrNull()?.formattedDuration ?: ""})",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null
                            )
                        }
                    }

                    // Community Feedback Card Section (when confirmed or prompted)
                    if (isRouteConfirmed || isFeedbackPromptVisible) {
                        Spacer(modifier = Modifier.height(12.dp))
                        CommunityFeedbackCard(
                            feedbackRating = feedbackRating,
                            selectedTags = selectedFeedbackTags,
                            isSubmitting = isSubmittingFeedback,
                            isSuccess = feedbackSubmissionSuccess,
                            errorMessage = feedbackError,
                            onSelectRating = onSelectFeedbackRating,
                            onToggleTag = onToggleFeedbackTag,
                            onSubmit = onSubmitFeedback,
                            onReset = onResetFeedbackState
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Secondary Change Destination Link
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onOpenSearch)
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Change Destination",
                        style = MaterialTheme.typography.labelLarge,
                        color = OnSurfaceVariantLight
                    )
                }

            } else {
                // Default Home Prompt State (Stitch Design)
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.titleLarge,
                    color = OnSurfaceLight,
                    modifier = Modifier.testTag("home_greeting")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onOpenSearch,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("where_to_go_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryObsidian,
                        contentColor = OnPrimaryLight
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Where do you want to go?",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Proceed to search"
                        )
                    }
                }
            }

            // Location permission fallback indicator if denied
            if (permissionState == LocationPermissionState.DENIED || permissionState == LocationPermissionState.PERMANENTLY_DENIED) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFF3E0))
                        .border(1.dp, Color(0xFFFFB800).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clickable { onRequestPermission() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFFB26A00),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Location access denied. Tap to enable or search manually.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF5E4200),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Quick Destination Chips (shown when no destination is currently active)
            if (selectedDestination == null) {
                Spacer(modifier = Modifier.height(14.dp))

                val scrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    quickShortcuts.forEach { shortcut ->
                        QuickShortcutChip(
                            shortcut = shortcut,
                            onClick = { onSelectShortcut(shortcut) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RouteOptionCard(
    route: RaahiRoute,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val containerBg = if (isSelected) Color.White else Color.White.copy(alpha = 0.45f)
    val borderCol = if (isSelected) PrimaryObsidian else Color.White.copy(alpha = 0.7f)
    val borderWidth = if (isSelected) 1.5.dp else 0.8.dp
    val safetyProfile = route.safetyProfile
    val isRecommended = safetyProfile?.isRecommended == true

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(containerBg)
            .border(borderWidth, borderCol, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .testTag(testTag),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Radio-style selection indicator
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .border(
                            width = if (isSelected) 5.dp else 1.5.dp,
                            color = if (isSelected) PrimaryObsidian else OutlineVariantLight,
                            shape = CircleShape
                        )
                        .background(if (isSelected) Color.White else Color.Transparent)
                )

                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = route.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            ),
                            color = PrimaryObsidian,
                            maxLines = 1
                        )

                        if (isRecommended) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SecondaryContainerLight)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Stronger Profile",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    color = OnSecondaryContainerLight
                                )
                            }
                        }
                    }

                    Text(
                        text = route.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariantLight,
                        maxLines = 1
                    )
                }
            }

            // Metrics: Duration & Distance
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = route.formattedDuration,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isSelected) SecondarySafetyGreen else PrimaryObsidian
                )
                Text(
                    text = route.formattedDistance,
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariantLight
                )
            }
        }

        // Relative Safety Score Pill
        if (safetyProfile != null) {
            val scoreColor = if (safetyProfile.relativeSafetyScore >= 75) SecondarySafetyGreen else CautionAmber
            val scoreBg = if (safetyProfile.relativeSafetyScore >= 75) SecondaryContainerLight.copy(alpha = 0.7f) else AmberContainer.copy(alpha = 0.7f)

            Row(
                modifier = Modifier
                    .padding(start = 32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(scoreBg)
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = scoreColor,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = "${safetyProfile.relativeSafetyScore} Relative Safety Score",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    ),
                    color = PrimaryObsidian
                )
            }
        }
    }
}

@Composable
private fun RouteSafetyAnalysisCard(
    profile: RouteSafetyProfile,
    routeTitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.6f))
            .border(1.dp, Color.White.copy(alpha = 0.9f), RoundedCornerShape(14.dp))
            .padding(12.dp)
            .testTag("safety_intelligence_card"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header: Score & Confidence
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = SecondarySafetyGreen,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Safety Intelligence",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = PrimaryObsidian
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(OutlineVariantLight.copy(alpha = 0.3f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = profile.confidence.label,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = OnSurfaceVariantLight
                )
            }
        }

        // Transparent Explanation
        Text(
            text = profile.explanation,
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceLight,
            lineHeight = 16.sp
        )

        // Factor Breakdown Chips
        if (profile.positiveFactors.isNotEmpty() || profile.negativeFactors.isNotEmpty()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                profile.positiveFactors.take(2).forEach { posFactor ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "+",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = SecondarySafetyGreen
                        )
                        Text(
                            text = posFactor,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = OnSurfaceLight
                        )
                    }
                }

                profile.negativeFactors.take(1).forEach { negFactor ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = CautionAmber
                        )
                        Text(
                            text = negFactor,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = OnSurfaceVariantLight
                        )
                    }
                }
            }
        }

        // Community Perceived Safety Preview Section
        val communityAgg = profile.communityAggregate
        if (communityAgg != null && communityAgg.isAvailable) {
            Spacer(modifier = Modifier.height(2.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF1F6F4))
                    .padding(8.dp)
                    .testTag("community_intelligence_section"),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = null,
                            tint = SecondarySafetyGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "COMMUNITY PERCEIVED SAFETY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = OnSurfaceVariantLight
                        )
                    }
                    Text(
                        text = "${communityAgg.totalReports} report${if (communityAgg.totalReports > 1) "s" else ""}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = SecondarySafetyGreen
                    )
                }

                Text(
                    text = communityAgg.summary,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = PrimaryObsidian
                )
            }
        }

        // Safe Points Corridor Preview
        if (profile.nearbySafePoints.isNotEmpty()) {
            Spacer(modifier = Modifier.height(2.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF6F3F2))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "SAFE HAVENS IN CORRIDOR",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = OnSurfaceVariantLight
                    )
                    Text(
                        text = "${profile.nearbySafePoints.size} found",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = SecondarySafetyGreen
                    )
                }

                profile.nearbySafePoints.take(3).forEach { sp ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (sp.verified) SecondarySafetyGreen else CautionAmber)
                            )
                            Text(
                                text = sp.name,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = PrimaryObsidian,
                                maxLines = 1
                            )
                        }

                        val dist = sp.distanceFromRoute ?: sp.distanceToRouteMeters
                        Text(
                            text = if (dist != null) "${dist}m" else sp.category.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = OnSurfaceVariantLight
                        )
                    }
                }
            }
        }

        // Disclaimer Footnote
        Text(
            text = "Comparative score evaluated against alternative Karachi corridors. Community feedback reflects perceived traveler sentiment, not verified crime statistics.",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = OnSurfaceVariantLight.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun QuickShortcutChip(
    shortcut: KarachiDestination,
    onClick: () -> Unit
) {
    val icon: ImageVector = when (shortcut.category) {
        DestinationCategory.WORK -> Icons.Default.Work
        DestinationCategory.HOME -> Icons.Default.Home
        DestinationCategory.CAFE -> Icons.Default.LocalCafe
        else -> Icons.Default.NearMe
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.5f))
            .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("shortcut_${shortcut.id}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = OnSurfaceVariantLight,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = shortcut.name,
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariantLight
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CommunityFeedbackCard(
    feedbackRating: CommunitySafetyRating?,
    selectedTags: Set<CommunitySafetyContextTag>,
    isSubmitting: Boolean,
    isSuccess: Boolean,
    errorMessage: String?,
    onSelectRating: (CommunitySafetyRating) -> Unit,
    onToggleTag: (CommunitySafetyContextTag) -> Unit,
    onSubmit: () -> Unit,
    onReset: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.85f))
            .border(1.dp, PrimaryObsidian.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            .padding(14.dp)
            .testTag("community_feedback_card"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (isSuccess) {
            // Success Feedback State
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SecondaryContainerLight.copy(alpha = 0.5f))
                    .padding(12.dp)
                    .testTag("feedback_success_message"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = SecondarySafetyGreen,
                    modifier = Modifier.size(22.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Thank you for your feedback!",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = OnSecondaryContainerLight
                    )
                    Text(
                        text = "Your perceived safety input helps improve Karachi corridor intelligence.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = OnSurfaceVariantLight
                    )
                }
            }
        } else {
            // Header & Context Disclaimer
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = PrimaryObsidian,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "How safe did you feel?",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryObsidian
                    )
                }
                Text(
                    text = "Perceived Safety",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = OnSurfaceVariantLight
                )
            }

            Text(
                text = "Share how comfortable you felt traveling this route (not crime data).",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = OnSurfaceVariantLight
            )

            // Rating Selector Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val ratingOptions = listOf(
                    Triple(CommunitySafetyRating.SAFE, "Felt Safe", "feedback_rating_safe"),
                    Triple(CommunitySafetyRating.NEUTRAL, "Neutral", "feedback_rating_neutral"),
                    Triple(CommunitySafetyRating.UNSAFE, "Felt Unsafe", "feedback_rating_unsafe")
                )

                ratingOptions.forEach { (rating, label, testTag) ->
                    val isSelected = feedbackRating == rating
                    val (activeBg, activeCol) = when (rating) {
                        CommunitySafetyRating.SAFE -> Pair(SecondaryContainerLight, SecondarySafetyGreen)
                        CommunitySafetyRating.NEUTRAL -> Pair(AmberContainer, OnCautionAmber)
                        CommunitySafetyRating.UNSAFE -> Pair(Color(0xFFFFDAD6), AlertRed)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) activeBg else Color(0xFFF1EDEC))
                            .border(
                                width = if (isSelected) 1.5.dp else 0.5.dp,
                                color = if (isSelected) activeCol else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { onSelectRating(rating) }
                            .padding(vertical = 10.dp)
                            .testTag(testTag),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            ),
                            color = if (isSelected) PrimaryObsidian else OnSurfaceVariantLight
                        )
                    }
                }
            }

            // Contextual Tag Chips (Multi-select)
            Text(
                text = "What stood out along the route? (Optional)",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                color = OnSurfaceVariantLight
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CommunitySafetyContextTag.entries.forEach { tag ->
                    val isTagSelected = selectedTags.contains(tag)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isTagSelected) PrimaryObsidian else Color(0xFFF1EDEC))
                            .clickable { onToggleTag(tag) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("feedback_tag_${tag.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tag.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = if (isTagSelected) FontWeight.SemiBold else FontWeight.Normal
                            ),
                            color = if (isTagSelected) OnPrimaryLight else OnSurfaceLight
                        )
                    }
                }
            }

            // Error feedback message
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = AlertRed,
                    modifier = Modifier.testTag("feedback_error_message")
                )
            }

            // Submit Button
            Button(
                onClick = onSubmit,
                enabled = feedbackRating != null && !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("submit_feedback_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryObsidian,
                    contentColor = OnPrimaryLight,
                    disabledContainerColor = OutlineVariantLight.copy(alpha = 0.5f),
                    disabledContentColor = OnSurfaceVariantLight
                )
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        color = OnPrimaryLight,
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Submit Perceived Safety Feedback",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ActiveJourneyNavigationPanel(
    journey: Journey,
    suggestedAlternativeRoute: RaahiRoute?,
    suggestedRerouteNotice: String?,
    showRerouteDialog: Boolean,
    isFeedbackPromptVisible: Boolean,
    feedbackRating: CommunitySafetyRating?,
    selectedFeedbackTags: Set<CommunitySafetyContextTag>,
    isSubmittingFeedback: Boolean,
    feedbackSubmissionSuccess: Boolean,
    feedbackError: String?,
    onAdvanceStep: () -> Unit,
    onCompleteJourney: () -> Unit,
    onCancelJourney: () -> Unit,
    onSwitchRoute: (RaahiRoute) -> Unit,
    onDismissRerouteDialog: () -> Unit,
    onSelectFeedbackRating: (CommunitySafetyRating) -> Unit,
    onToggleFeedbackTag: (CommunitySafetyContextTag) -> Unit,
    onSubmitFeedback: () -> Unit,
    onResetFeedback: () -> Unit,
    onSelectSafePoint: (SafePoint?) -> Unit,
    modifier: Modifier = Modifier
) {
    val isArrived = journey.state == JourneyState.ARRIVED
    val isRecalculating = journey.state == JourneyState.RECALCULATING

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("active_journey_navigation_panel"),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Navigation Top Header & End Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isArrived) Color(0xFF1E8E3E) else SecondarySafetyGreen)
                    )
                    Text(
                        text = when {
                            isArrived -> "DESTINATION REACHED"
                            isRecalculating -> "RECALCULATING CONDITIONS"
                            else -> "ACTIVE NAVIGATION"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isArrived) Color(0xFF1E8E3E) else SecondarySafetyGreen,
                        letterSpacing = 0.05.sp
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = journey.destinationName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = OnSurfaceLight,
                    maxLines = 1
                )
            }

            IconButton(
                onClick = onCancelJourney,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1EDEC))
                    .testTag("cancel_journey_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "End Navigation",
                    tint = OnSurfaceVariantLight,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // 2. Dynamic Reroute Alert Banner (When conditions change during journey)
        if (showRerouteDialog && suggestedAlternativeRoute != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AmberContainer)
                    .border(1.dp, CautionAmber.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
                    .testTag("reroute_recommendation_banner")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = OnCautionAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Stronger Safety Profile Available",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = OnCautionAmber
                        )
                    }

                    Text(
                        text = suggestedRerouteNotice
                            ?: "Conditions changed. Another route currently presents a stronger safety profile.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnCautionAmber.copy(alpha = 0.9f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { onSwitchRoute(suggestedAlternativeRoute) },
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("accept_reroute_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryObsidian,
                                contentColor = OnPrimaryLight
                            )
                        ) {
                            Text(
                                text = "Switch to ${suggestedAlternativeRoute.title}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1
                            )
                        }

                        TextButton(
                            onClick = onDismissRerouteDialog,
                            modifier = Modifier
                                .height(38.dp)
                                .testTag("dismiss_reroute_button")
                        ) {
                            Text(
                                text = "Keep Current",
                                style = MaterialTheme.typography.labelMedium,
                                color = OnCautionAmber
                            )
                        }
                    }
                }
            }
        }

        // 3. Navigation Instruction & Progress Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.85f))
                .border(1.dp, OutlineVariantLight.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PrimaryObsidian),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isArrived) Icons.Default.CheckCircle else Icons.Default.Navigation,
                                contentDescription = null,
                                tint = OnPrimaryLight,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = if (isArrived) "You have arrived!" else (journey.currentInstruction ?: "Head towards destination"),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = OnSurfaceLight,
                                maxLines = 2
                            )
                            Text(
                                text = "${journey.formattedRemainingDuration} remaining • ${journey.formattedRemainingDistance}",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariantLight
                            )
                        }
                    }

                    // Live Safety Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SecondaryContainerLight)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = SecondarySafetyGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${journey.currentSafetyProfile?.relativeSafetyScore ?: 85} Safety",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = OnSecondaryContainerLight
                            )
                        }
                    }
                }

                // Progress Bar
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    LinearProgressIndicator(
                        progress = { journey.progressPercentage.coerceIn(0.0f, 1.0f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = SecondarySafetyGreen,
                        trackColor = OutlineVariantLight.copy(alpha = 0.3f)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${(journey.progressPercentage * 100).toInt()}% completed",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = OnSurfaceVariantLight
                        )
                        Text(
                            text = journey.route.title,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = OnSurfaceVariantLight
                        )
                    }
                }
            }
        }

        // 4. Safe Haven Proximity (If active safe point selected or along route)
        if (journey.selectedSafePoint != null) {
            val safePoint = journey.selectedSafePoint
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE8F5E9))
                    .border(1.dp, SecondarySafetyGreen.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .clickable { onSelectSafePoint(safePoint) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = SecondarySafetyGreen,
                    modifier = Modifier.size(16.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Verified Safe Haven: ${safePoint.name}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = "${safePoint.category.displayName} • Tap to highlight on map",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = Color(0xFF2E7D32)
                    )
                }
            }
        }

        // 5. Active Controls: Advance Simulation Step / Complete / Feedback
        if (!isArrived) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Simulation Stepper (Prototype Interactive Feature)
                Button(
                    onClick = onAdvanceStep,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("advance_simulation_step_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryObsidian,
                        contentColor = OnPrimaryLight
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Advance Step",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }

                // Complete Now Button
                Button(
                    onClick = onCompleteJourney,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("complete_journey_early_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF1EDEC),
                        contentColor = OnSurfaceLight
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = PrimaryObsidian
                        )
                        Text(
                            text = "Arrive Now",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = PrimaryObsidian
                        )
                    }
                }
            }
        } else {
            // ARRIVED STATE: Prompt to Rate Safety / Complete
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFE6F4EA))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "You have arrived at ${journey.destinationName}!",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF137333)
                    )
                }

                // Community Feedback Card for Post-Journey
                CommunityFeedbackCard(
                    feedbackRating = feedbackRating,
                    selectedTags = selectedFeedbackTags,
                    isSubmitting = isSubmittingFeedback,
                    isSuccess = feedbackSubmissionSuccess,
                    errorMessage = feedbackError,
                    onSelectRating = onSelectFeedbackRating,
                    onToggleTag = onToggleFeedbackTag,
                    onSubmit = onSubmitFeedback,
                    onReset = onResetFeedback
                )

                Button(
                    onClick = onCompleteJourney,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("finish_journey_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryObsidian,
                        contentColor = OnPrimaryLight
                    )
                ) {
                    Text(
                        text = "Finish & Return Home",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
