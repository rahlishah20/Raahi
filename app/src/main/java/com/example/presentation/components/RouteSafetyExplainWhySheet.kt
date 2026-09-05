package com.example.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.DataSourceType
import com.example.domain.model.DemoEvent
import com.example.domain.model.KarachiSafePoint
import com.example.domain.model.RaahiRoute
import com.example.domain.model.SafetyFactorEvaluation
import com.example.domain.model.SafetyFactorType
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.CautionAmber
import com.example.ui.theme.OnSurfaceLight
import com.example.ui.theme.OnSurfaceVariantLight
import com.example.ui.theme.OutlineVariantLight
import com.example.ui.theme.PrimaryObsidian
import com.example.ui.theme.SecondaryContainerLight
import com.example.ui.theme.SecondarySafetyGreen
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest

enum class SafetyTab {
    SCORE,
    ANALYSIS
}

/**
 * Stitch Reference: Safety Sheet & Dialog Container
 *
 * Hosts both the Safety Score view and Safety Analysis view
 * with a top segmented tab switcher, live route comparisons, and deterministic
 * engine outputs.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteSafetyExplainWhySheet(
    route: RaahiRoute,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    allRoutes: List<RaahiRoute> = emptyList(),
    activeDemoEvents: List<DemoEvent> = emptyList(),
    initialTab: SafetyTab = SafetyTab.SCORE,
    onSelectRoute: (RaahiRoute) -> Unit = {},
    onConfirmRoute: () -> Unit = {},
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    var selectedTab by remember(initialTab) { mutableStateOf(initialTab) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFFFDF8F8),
        dragHandle = null,
        modifier = modifier.testTag("explain_why_modal_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 24.dp)
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .size(width = 36.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(OutlineVariantLight)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Header Row: Title & Close Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (selectedTab == SafetyTab.SCORE) "Safety Score" else "Safety Analysis",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = OnSurfaceLight
                    )
                    Text(
                        text = route.title,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = OnSurfaceVariantLight,
                        maxLines = 1
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerLow)
                        .testTag("close_explain_why_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = OnSurfaceLight,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stitch Segmented Tab Switcher: "Safety Score" | "Safety Analysis"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceContainerLow)
                    .border(1.dp, OutlineVariantLight.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .padding(4.dp)
                    .testTag("safety_tabs_segmented_control")
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Safety Score Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedTab == SafetyTab.SCORE) Color.White else Color.Transparent)
                            .clickable { selectedTab = SafetyTab.SCORE }
                            .padding(horizontal = 8.dp)
                            .testTag("safety_score_tab_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = if (selectedTab == SafetyTab.SCORE) SecondarySafetyGreen else OnSurfaceVariantLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Safety Score",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selectedTab == SafetyTab.SCORE) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                ),
                                color = if (selectedTab == SafetyTab.SCORE) PrimaryObsidian else OnSurfaceVariantLight
                            )
                        }
                    }

                    // Safety Analysis Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedTab == SafetyTab.ANALYSIS) Color.White else Color.Transparent)
                            .clickable { selectedTab = SafetyTab.ANALYSIS }
                            .padding(horizontal = 8.dp)
                            .testTag("safety_analysis_tab_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = null,
                                tint = if (selectedTab == SafetyTab.ANALYSIS) SecondarySafetyGreen else OnSurfaceVariantLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Safety Analysis",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selectedTab == SafetyTab.ANALYSIS) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                ),
                                color = if (selectedTab == SafetyTab.ANALYSIS) PrimaryObsidian else OnSurfaceVariantLight
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tab Content Switcher
            AnimatedContent(
                targetState = selectedTab,
                label = "safety_sheet_tab_transition"
            ) { tab ->
                when (tab) {
                    SafetyTab.SCORE -> {
                        SafetyScoreContent(
                            route = route,
                            allRoutes = allRoutes,
                            activeDemoEvents = activeDemoEvents,
                            onSelectRoute = onSelectRoute,
                            onViewSafetyAnalysis = { selectedTab = SafetyTab.ANALYSIS },
                            onConfirmRoute = {
                                onConfirmRoute()
                                onDismiss()
                            }
                        )
                    }
                    SafetyTab.ANALYSIS -> {
                        SafetyAnalysisContent(
                            route = route,
                            activeDemoEvents = activeDemoEvents,
                            onConfirmRoute = {
                                onConfirmRoute()
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Reusable content composable for Safety Score within RouteSafetyExplainWhySheet.
 */
@Composable
fun SafetyScoreContent(
    route: RaahiRoute,
    modifier: Modifier = Modifier,
    allRoutes: List<RaahiRoute> = emptyList(),
    activeDemoEvents: List<DemoEvent> = emptyList(),
    onSelectRoute: (RaahiRoute) -> Unit = {},
    onViewSafetyAnalysis: () -> Unit = {},
    onConfirmRoute: () -> Unit = {}
) {
    val profile = route.safetyProfile
    val score = profile?.relativeSafetyScore ?: 0
    val confidencePct = ((profile?.confidenceScore ?: 0.75) * 100).toInt().coerceIn(40, 99)
    val availableCount = profile?.factorEvaluations?.count { it.isAvailable } ?: 0
    val totalCount = (profile?.factorEvaluations?.size ?: 6).coerceAtLeast(1)
    val coveragePct = ((availableCount.toDouble() / totalCount) * 100).toInt().coerceIn(0, 100)
    val isRecommended = profile?.isRecommended == true
    val otherRoutes = allRoutes.filter { it.id != route.id }
    val recommendedAlternative = otherRoutes.firstOrNull { it.safetyProfile?.isRecommended == true }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. SIMULATION / DYNAMIC EVENTS ACTIVE BANNER ---
        if (activeDemoEvents.any { it.active }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFFF3E0))
                    .border(1.dp, CautionAmber.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .testTag("dynamic_simulation_active_banner")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(AmberContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = Color(0xFF7A4500),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "DEMO SIMULATION ACTIVE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = Color(0xFF7A4500)
                        )
                        Text(
                            text = "Scores recalculated dynamically from simulated corridor conditions.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = OnSurfaceLight
                        )
                    }
                }
            }
        }

        // --- 2. LARGE RELATIVE SAFETY SCORE HERO CARD ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    if (score >= 80) Color(0xFFE8F5E9)
                    else if (score >= 65) Color(0xFFFFF8E1)
                    else Color(0xFFFFEBEE)
                )
                .border(
                    width = 1.dp,
                    color = if (score >= 80) SecondarySafetyGreen.copy(alpha = 0.4f)
                    else if (score >= 65) CautionAmber.copy(alpha = 0.4f)
                    else AlertRed.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(22.dp)
                .testTag("relative_safety_score_card")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "RELATIVE SAFETY SCORE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            letterSpacing = 1.2.sp
                        ),
                        color = if (score >= 80) SecondarySafetyGreen else if (score >= 65) Color(0xFF7A4500) else AlertRed
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$score",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 48.sp
                            ),
                            color = PrimaryObsidian,
                            modifier = Modifier.testTag("score_value_text")
                        )
                        Text(
                            text = "/100",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp
                            ),
                            color = OnSurfaceVariantLight,
                            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = when {
                            score >= 80 -> "Strong Relative Safety Profile"
                            score >= 65 -> "Moderate Relative Safety Profile"
                            else -> "Lower Relative Safety Profile"
                        },
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        ),
                        color = if (score >= 80) SecondarySafetyGreen else if (score >= 65) Color(0xFF7A4500) else AlertRed
                    )
                }

                // Shield Icon with Glow
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            if (score >= 80) SecondarySafetyGreen
                            else if (score >= 65) CautionAmber
                            else AlertRed
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }
        }

        // --- 3. METRICS ROW: CONFIDENCE & DATA COVERAGE ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Confidence Stat Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceContainerLowest)
                    .border(1.dp, OutlineVariantLight.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                    .padding(14.dp)
                    .testTag("confidence_metric_card")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "CONFIDENCE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 0.6.sp
                        ),
                        color = OnSurfaceVariantLight
                    )
                    Text(
                        text = "$confidencePct%",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        ),
                        color = PrimaryObsidian
                    )
                    Text(
                        text = profile?.confidence?.label ?: "Medium Confidence",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = if (confidencePct >= 75) SecondarySafetyGreen else OnSurfaceVariantLight
                    )
                }
            }

            // Data Coverage Stat Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceContainerLowest)
                    .border(1.dp, OutlineVariantLight.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                    .padding(14.dp)
                    .testTag("coverage_metric_card")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "DATA COVERAGE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 0.6.sp
                        ),
                        color = OnSurfaceVariantLight
                    )
                    Text(
                        text = "$coveragePct%",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        ),
                        color = PrimaryObsidian
                    )
                    Text(
                        text = "$availableCount of $totalCount signals active",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = OnSurfaceVariantLight
                    )
                }
            }
        }

        // --- 4. SAFER ROUTE RECOMMENDATION CALLOUT ---
        if (isRecommended) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFF4FBF6))
                    .border(1.dp, SecondarySafetyGreen.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                    .padding(16.dp)
                    .testTag("safer_route_recommendation_banner")
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SecondaryContainerLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = SecondarySafetyGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Stronger Safety Option",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            color = PrimaryObsidian
                        )
                        Text(
                            text = "This route is currently recommended by the RAAHI Safety Intelligence Engine based on aggregated real-time and contextual corridor signals.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp),
                            color = OnSurfaceVariantLight
                        )
                    }
                }
            }
        } else if (recommendedAlternative != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFFFF8E1))
                    .border(1.dp, CautionAmber.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                    .padding(16.dp)
                    .testTag("alternative_safer_route_banner")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.AltRoute,
                            contentDescription = null,
                            tint = CautionAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Alternative Option Recommended",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            color = PrimaryObsidian
                        )
                    }

                    Text(
                        text = "${recommendedAlternative.title} currently presents a stronger relative safety score (${recommendedAlternative.safetyProfile?.relativeSafetyScore ?: 0} vs $score).",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = OnSurfaceLight
                    )

                    OutlinedButton(
                        onClick = { onSelectRoute(recommendedAlternative) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("switch_to_safer_route_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Switch to ${recommendedAlternative.title}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            color = PrimaryObsidian
                        )
                    }
                }
            }
        }

        // --- 5. ROUTE COMPARISON (If multiple evaluated routes exist) ---
        if (allRoutes.size > 1) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Evaluated Karachi Alternatives",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = PrimaryObsidian
                )

                allRoutes.forEach { r ->
                    val isCurrent = r.id == route.id
                    val rScore = r.safetyProfile?.relativeSafetyScore ?: 0
                    val rRecommended = r.safetyProfile?.isRecommended == true

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isCurrent) Color(0xFFF4FBF6) else SurfaceContainerLowest)
                            .border(
                                width = if (isCurrent) 1.5.dp else 1.dp,
                                color = if (isCurrent) SecondarySafetyGreen else OutlineVariantLight.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onSelectRoute(r) }
                            .padding(12.dp)
                            .testTag("route_comparison_item_${r.id}")
                    ) {
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
                                    if (rRecommended) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(SecondarySafetyGreen)
                                                .padding(horizontal = 5.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "SAFEST",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = Color.White
                                            )
                                        }
                                    }

                                    Text(
                                        text = r.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 13.sp
                                        ),
                                        color = PrimaryObsidian
                                    )
                                }

                                Text(
                                    text = "${r.formattedDuration} • ${r.formattedDistance}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = OnSurfaceVariantLight
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (rScore >= 80) Color(0xFFE8F5E9)
                                        else if (rScore >= 65) Color(0xFFFFF8E1)
                                        else Color(0xFFFFEBEE)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "$rScore Score",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    color = if (rScore >= 80) SecondarySafetyGreen else PrimaryObsidian
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 6. "EXPLAIN WHY" SUMMARY CARD ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceContainerLowest)
                .border(1.dp, OutlineVariantLight.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Explain WHY:",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                color = PrimaryObsidian
            )

            // Positive factors
            val positives = profile?.positiveFactors ?: emptyList()
            positives.forEach { factor ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(SecondaryContainerLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = SecondarySafetyGreen,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Text(
                        text = factor,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp),
                        color = OnSurfaceLight,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Negative factors / Caveats
            val negatives = profile?.negativeFactors ?: emptyList()
            negatives.forEach { caveat ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF0EE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(AlertRed)
                        )
                    }
                    Text(
                        text = caveat,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp),
                        color = OnSurfaceVariantLight,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (!profile?.explanation.isNullOrBlank()) {
                HorizontalDivider(color = OutlineVariantLight.copy(alpha = 0.3f))
                Text(
                    text = profile?.explanation.orEmpty(),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    ),
                    color = OnSurfaceVariantLight
                )
            }
        }

        // --- 7. ACTION BUTTONS ---
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Button to View Full Safety Analysis
            OutlinedButton(
                onClick = onViewSafetyAnalysis,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("view_full_safety_analysis_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = null,
                        tint = PrimaryObsidian,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "View Detailed Factor Analysis",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        color = PrimaryObsidian
                    )
                }
            }

            // Button to Start Journey / Confirm Route
            Button(
                onClick = onConfirmRoute,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("start_journey_from_score_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryObsidian,
                    contentColor = Color.White
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = null,
                        tint = SecondaryContainerLight,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Start Journey on This Route",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }

        // --- 8. RESPONSIBLE PROTOCOL DISCLAIMER ---
        Text(
            text = "Scores are calculated deterministically as relative comparative indices across Karachi corridor telemetry. Not an absolute guarantee of safety or crime prediction.",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                lineHeight = 14.sp
            ),
            color = OnSurfaceVariantLight.copy(alpha = 0.7f),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}

/**
 * Reusable content composable for Safety Analysis within RouteSafetyExplainWhySheet.
 */
@Composable
fun SafetyAnalysisContent(
    route: RaahiRoute,
    modifier: Modifier = Modifier,
    activeDemoEvents: List<DemoEvent> = emptyList(),
    onConfirmRoute: () -> Unit = {}
) {
    val profile = route.safetyProfile
    val factorEvaluations = profile?.factorEvaluations ?: emptyList()
    val safePoints = profile?.nearbySafePoints ?: emptyList()
    val communityAggregate = profile?.communityAggregate

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. SIMULATION ACTIVE WARNING ---
        if (activeDemoEvents.any { it.active }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFFF3E0))
                    .border(1.dp, CautionAmber.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .testTag("analysis_simulation_active_banner")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = null,
                        tint = Color(0xFF7A4500),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Simulated conditions active. Factor weights and normalized scores reflect live test parameters.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = Color(0xFF7A4500)
                    )
                }
            }
        }

        // --- 2. FACTOR BREAKDOWN SECTION HEADER ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Safety Factor Breakdown",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = PrimaryObsidian
                )
                Text(
                    text = "Calculated from multi-source Karachi corridor telemetry",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = OnSurfaceVariantLight
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(SurfaceContainerLow)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "${factorEvaluations.count { it.isAvailable }}/${factorEvaluations.size} Signals",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    color = OnSurfaceLight
                )
            }
        }

        // --- 3. INDIVIDUAL FACTOR CARDS ---
        factorEvaluations.forEach { eval ->
            SafetyFactorCard(evaluation = eval)
        }

        // --- 4. VERIFIED SAFE POINTS IN CORRIDOR ---
        if (safePoints.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceContainerLowest)
                    .border(1.dp, OutlineVariantLight.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = SecondarySafetyGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Verified Safe Havens in Corridor",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            color = PrimaryObsidian
                        )
                    }

                    Text(
                        text = "${safePoints.size} found",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = SecondarySafetyGreen
                    )
                }

                safePoints.take(4).forEach { sp ->
                    SafeHavenRow(safePoint = sp)
                }
            }
        }

        // --- 5. COMMUNITY PERCEIVED SAFETY SENTIMENT ---
        if (communityAggregate != null && communityAggregate.isAvailable) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFF1F7F4))
                    .border(1.dp, SecondarySafetyGreen.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
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
                            imageVector = Icons.Default.People,
                            contentDescription = null,
                            tint = SecondarySafetyGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Community Perceived Safety",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            color = PrimaryObsidian
                        )
                    }

                    Text(
                        text = "${communityAggregate.totalReports} traveler reports",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = SecondarySafetyGreen
                    )
                }

                Text(
                    text = communityAggregate.summary,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp),
                    color = PrimaryObsidian
                )

                if (communityAggregate.topContextTags.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (tagPair in communityAggregate.topContextTags.take(3)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = tagPair.first.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = SecondarySafetyGreen
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 6. TRANSPARENT METHODOLOGY FOOTNOTE ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Intelligence Protocol:",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                ),
                color = OnSurfaceLight
            )
            Text(
                text = "Factor scores are normalized between 0 and 100 based on verified municipal lights, cellular pedestrian density, open commercial points of interest, historical incident maps, verified police/hospital proximity, and traveler perception feedback. Factors with unavailable signals have weights re-distributed dynamically.",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                ),
                color = OnSurfaceVariantLight
            )
        }

        // --- 7. PRIMARY ACTION BUTTON ---
        Button(
            onClick = onConfirmRoute,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("start_journey_from_analysis_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryObsidian,
                contentColor = Color.White
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Navigation,
                    contentDescription = null,
                    tint = SecondaryContainerLight,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Confirm & Start Journey",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

/**
 * Individual Factor Card displaying score, weight, source type, and narrative summary.
 */
@Composable
private fun SafetyFactorCard(
    evaluation: SafetyFactorEvaluation,
    modifier: Modifier = Modifier
) {
    val factorIcon = getFactorIcon(evaluation.factorType)
    val isAvailable = evaluation.isAvailable && evaluation.dataSourceType != DataSourceType.UNAVAILABLE
    val score = evaluation.score
    val weightPercent = (evaluation.effectiveWeight * 100).toInt()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainerLowest)
            .border(1.dp, OutlineVariantLight.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(14.dp)
            .testTag("factor_card_${evaluation.factorType.name.lowercase()}")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header: Icon + Title + Data Source Tag
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
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                if (!isAvailable) SurfaceContainerLow
                                else if (evaluation.isPositive) Color(0xFFE8F5E9)
                                else if (score >= 60) Color(0xFFFFF8E1)
                                else Color(0xFFFFEBEE)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = factorIcon,
                            contentDescription = null,
                            tint = if (!isAvailable) OnSurfaceVariantLight
                            else if (evaluation.isPositive) SecondarySafetyGreen
                            else if (score >= 60) CautionAmber
                            else AlertRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = evaluation.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            color = PrimaryObsidian
                        )
                        Text(
                            text = if (isAvailable) "Effective Weight: $weightPercent%" else "Signal Inactive",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = OnSurfaceVariantLight
                        )
                    }
                }

                // Data Source Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when (evaluation.dataSourceType) {
                                DataSourceType.REAL -> Color(0xFFE8F5E9)
                                DataSourceType.PROTOTYPE -> SurfaceContainerLow
                                DataSourceType.UNAVAILABLE -> Color(0xFFFFEBEE)
                            }
                        )
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = when (evaluation.dataSourceType) {
                            DataSourceType.REAL -> "Verified API"
                            DataSourceType.PROTOTYPE -> "Corridor Prototype"
                            DataSourceType.UNAVAILABLE -> "Unavailable"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = when (evaluation.dataSourceType) {
                            DataSourceType.REAL -> SecondarySafetyGreen
                            DataSourceType.PROTOTYPE -> OnSurfaceVariantLight
                            DataSourceType.UNAVAILABLE -> AlertRed
                        }
                    )
                }
            }

            // Score Progress Bar (Only if signal available)
            if (isAvailable) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Normalized Factor Score",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = OnSurfaceVariantLight
                        )
                        Text(
                            text = "$score/100",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = if (evaluation.isPositive) SecondarySafetyGreen else PrimaryObsidian
                        )
                    }

                    // Progress bar track
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFFE5E2E1))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(score / 100f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    if (evaluation.isPositive) SecondarySafetyGreen
                                    else if (score >= 60) CautionAmber
                                    else AlertRed
                                )
                        )
                    }
                }
            }

            // Factor Summary / Engine Explanation
            Text(
                text = evaluation.summary,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                ),
                color = if (isAvailable) OnSurfaceLight else OnSurfaceVariantLight
            )
        }
    }
}

/**
 * Row for individual Safe Haven entry in corridor
 */
@Composable
private fun SafeHavenRow(
    safePoint: KarachiSafePoint,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceContainerLow)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = null,
                tint = SecondarySafetyGreen,
                modifier = Modifier.size(16.dp)
            )
            Column {
                Text(
                    text = safePoint.name,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ),
                    color = PrimaryObsidian,
                    maxLines = 1
                )
                Text(
                    text = safePoint.address.ifBlank { safePoint.category.displayName },
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = OnSurfaceVariantLight,
                    maxLines = 1
                )
            }
        }

        if (safePoint.distanceFromRoute != null) {
            Text(
                text = "${safePoint.distanceFromRoute}m away",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp
                ),
                color = OnSurfaceVariantLight
            )
        }
    }
}

private fun getFactorIcon(factorType: SafetyFactorType): ImageVector {
    return when (factorType) {
        SafetyFactorType.LIGHTING -> Icons.Default.Lightbulb
        SafetyFactorType.PEDESTRIAN_ACTIVITY -> Icons.AutoMirrored.Filled.DirectionsWalk
        SafetyFactorType.BUSINESS_ACTIVITY -> Icons.Default.Store
        SafetyFactorType.HISTORICAL_INCIDENT -> Icons.Default.WarningAmber
        SafetyFactorType.SAFE_POINTS -> Icons.Default.Security
        SafetyFactorType.COMMUNITY_PERCEIVED -> Icons.Default.People
    }
}
