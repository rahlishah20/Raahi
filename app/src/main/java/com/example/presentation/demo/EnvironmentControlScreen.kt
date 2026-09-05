package com.example.presentation.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.DemoEvent
import com.example.domain.model.DemoEventType
import com.example.domain.model.SafetyFactorType
import com.example.presentation.components.RaahiGlassPanel
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.CautionAmber
import com.example.ui.theme.GlassSurfaceBorder
import com.example.ui.theme.OnSurfaceLight
import com.example.ui.theme.OnSurfaceVariantLight
import com.example.ui.theme.OutlineVariantLight
import com.example.ui.theme.PrimaryObsidian
import com.example.ui.theme.SecondaryContainerLight
import com.example.ui.theme.SecondarySafetyGreen
import com.example.ui.theme.SurfaceContainerLow

@Composable
fun EnvironmentControlScreen(
    modifier: Modifier = Modifier,
    activeDemoEvents: List<DemoEvent> = emptyList(),
    onApplyEvent: (DemoEvent) -> Unit,
    onApplyScenario: (List<DemoEvent>) -> Unit,
    onResetSimulation: () -> Unit,
    onBackClick: () -> Unit
) {
    var selectedScenario by remember { mutableStateOf("baseline") }

    val hasActiveEvents = activeDemoEvents.any { it.active }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("environment_control_screen"),
        containerColor = Color(0xFFFDF8F8),
        topBar = {
            RaahiGlassPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                backgroundColor = Color(0xF5FDF8F8),
                borderColor = GlassSurfaceBorder
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainerLow)
                            .testTag("back_from_env_control_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PrimaryObsidian,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AmberContainer)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "DEMO / SIMULATED",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 9.sp,
                                        letterSpacing = 0.8.sp
                                    ),
                                    color = Color(0xFF7A4500)
                                )
                            }
                        }
                        Text(
                            text = "Environment Control",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            ),
                            color = PrimaryObsidian
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Introductory Subtext
            Text(
                text = "Simulate real-time Karachi corridor condition adjustments and observe safety recalculations dynamically.",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                ),
                color = OnSurfaceVariantLight
            )

            // --- SECTION 1: SCENARIO SELECTOR ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Scenario Selector",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = PrimaryObsidian
                )

                // Scenario 1: Current Baseline
                ScenarioCard(
                    title = "Current Baseline",
                    subtitle = "Normal daylight / open market corridor operations",
                    isSelected = selectedScenario == "baseline" && !hasActiveEvents,
                    onClick = {
                        selectedScenario = "baseline"
                        onResetSimulation()
                    }
                )

                // Scenario 2: Businesses Closing
                ScenarioCard(
                    title = "Businesses Closing",
                    subtitle = "Simulates evening shop closures & POI activity drop across Saddar",
                    isSelected = selectedScenario == "closing",
                    onClick = {
                        selectedScenario = "closing"
                        val event = DemoEvent(
                            id = "demo-closing-${System.currentTimeMillis()}",
                            type = DemoEventType.BUSINESS_ACTIVITY_CHANGE,
                            targetFactorType = SafetyFactorType.BUSINESS_ACTIVITY,
                            valueMultiplier = 0.40,
                            description = "Commercial POI closures across commercial district"
                        )
                        onApplyEvent(event)
                    }
                )

                // Scenario 3: Rerouting Recommended
                ScenarioCard(
                    title = "Rerouting Recommended",
                    subtitle = "Simulates streetlight failure + pedestrian drop, triggering safer detour alert",
                    isSelected = selectedScenario == "reroute",
                    badge = "Triggers Advisory",
                    onClick = {
                        selectedScenario = "reroute"
                        val lightingEvent = DemoEvent(
                            id = "demo-lighting-${System.currentTimeMillis()}",
                            type = DemoEventType.LIGHTING_CHANGE,
                            targetFactorType = SafetyFactorType.LIGHTING,
                            valueMultiplier = 0.30,
                            description = "Municipal streetlight circuit outage"
                        )
                        val pedEvent = DemoEvent(
                            id = "demo-ped-${System.currentTimeMillis()}",
                            type = DemoEventType.PEDESTRIAN_ACTIVITY_CHANGE,
                            targetFactorType = SafetyFactorType.PEDESTRIAN_ACTIVITY,
                            valueMultiplier = 0.35,
                            description = "Severe drop in pedestrian foot traffic"
                        )
                        onApplyScenario(listOf(lightingEvent, pedEvent))
                    }
                )
            }

            HorizontalDivider(color = OutlineVariantLight.copy(alpha = 0.4f))

            // --- SECTION 2: ENVIRONMENT VARIABLES GRID ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Environment Variables",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = PrimaryObsidian
                )

                // Grid of 4 Variable Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    VariableCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Nightlight,
                        title = "Advance Time",
                        subtitle = "Late Night Shift",
                        onClick = {
                            val event = DemoEvent(
                                id = "demo-night-${System.currentTimeMillis()}",
                                type = DemoEventType.LIGHTING_CHANGE,
                                targetFactorType = SafetyFactorType.LIGHTING,
                                valueMultiplier = 0.45,
                                description = "Late night visibility conditions"
                            )
                            onApplyEvent(event)
                        }
                    )

                    VariableCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Store,
                        title = "Close Businesses",
                        subtitle = "POI Activity Drop",
                        onClick = {
                            val event = DemoEvent(
                                id = "demo-bus-${System.currentTimeMillis()}",
                                type = DemoEventType.BUSINESS_ACTIVITY_CHANGE,
                                targetFactorType = SafetyFactorType.BUSINESS_ACTIVITY,
                                valueMultiplier = 0.35,
                                description = "Commercial shop closures"
                            )
                            onApplyEvent(event)
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    VariableCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.DirectionsWalk,
                        title = "Reduce Foot Traffic",
                        subtitle = "Low Pedestrian Count",
                        onClick = {
                            val event = DemoEvent(
                                id = "demo-foot-${System.currentTimeMillis()}",
                                type = DemoEventType.PEDESTRIAN_ACTIVITY_CHANGE,
                                targetFactorType = SafetyFactorType.PEDESTRIAN_ACTIVITY,
                                valueMultiplier = 0.40,
                                description = "Pedestrian density drop"
                            )
                            onApplyEvent(event)
                        }
                    )

                    VariableCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.ReportProblem,
                        title = "Add Incident",
                        subtitle = "Zone Advisory Spike",
                        onClick = {
                            val event = DemoEvent(
                                id = "demo-inc-${System.currentTimeMillis()}",
                                type = DemoEventType.INCIDENT_SIGNAL_CHANGE,
                                targetFactorType = SafetyFactorType.HISTORICAL_INCIDENT,
                                valueMultiplier = 1.6,
                                description = "Active advisory on corridor segment"
                            )
                            onApplyEvent(event)
                        }
                    )
                }

                // Disable Transport / Safe Points Switch
                var disableTransport by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(1.dp, OutlineVariantLight.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Disable Public Transit & Safe Haven Posts",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                ),
                                color = OnSurfaceLight
                            )
                            Text(
                                text = "Simulate isolated transit corridors",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariantLight
                            )
                        }

                        Switch(
                            checked = disableTransport,
                            onCheckedChange = { checked ->
                                disableTransport = checked
                                val event = DemoEvent(
                                    id = "demo-sp-${System.currentTimeMillis()}",
                                    type = DemoEventType.SAFE_POINTS_CHANGE,
                                    targetFactorType = SafetyFactorType.SAFE_POINTS,
                                    valueMultiplier = if (checked) 0.30 else 1.0,
                                    description = if (checked) "Transit hubs and posts inactive" else "Safe points online"
                                )
                                onApplyEvent(event)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SecondarySafetyGreen
                            )
                        )
                    }
                }
            }

            // --- SECTION 3: RESET CONTROLS ---
            Button(
                onClick = {
                    selectedScenario = "baseline"
                    onResetSimulation()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("reset_demo_simulation_button"),
                shape = RoundedCornerShape(16.dp),
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
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Reset All Simulation Variables",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ScenarioCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    badge: String? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) Color(0xFFF4FBF6) else Color.White)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) SecondarySafetyGreen else OutlineVariantLight.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Radio circle
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) SecondarySafetyGreen else Color.Transparent)
                    .border(
                        width = 2.dp,
                        color = if (isSelected) SecondarySafetyGreen else OutlineVariantLight,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        color = PrimaryObsidian
                    )

                    if (badge != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AmberContainer)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badge,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color(0xFF7A4500)
                            )
                        }
                    }
                }

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = OnSurfaceVariantLight
                )
            }
        }
    }
}

@Composable
private fun VariableCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, OutlineVariantLight.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1EDEC)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryObsidian,
                    modifier = Modifier.size(18.dp)
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                ),
                color = PrimaryObsidian
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = OnSurfaceVariantLight,
                maxLines = 1
            )
        }
    }
}
