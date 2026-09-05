package com.example.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TurnRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.domain.model.Journey
import com.example.ui.theme.AlertRed
import com.example.ui.theme.GlassSurfaceBorder
import com.example.ui.theme.OnSurfaceLight
import com.example.ui.theme.OnSurfaceVariantLight
import com.example.ui.theme.OutlineVariantLight
import com.example.ui.theme.PrimaryObsidian
import com.example.ui.theme.SecondaryContainerLight
import com.example.ui.theme.SecondarySafetyGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ActiveNavigationOverlay(
    modifier: Modifier = Modifier,
    activeJourney: Journey,
    currentInstruction: String?,
    onCancelJourney: () -> Unit,
    onRecenterClick: () -> Unit,
    onReportIncidentClick: () -> Unit,
    onAdvanceStep: () -> Unit,
    onCompleteJourney: () -> Unit,
    onViewDetails: () -> Unit
) {
    var isMuted by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "nav_dot_pulse")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_alpha"
    )

    // Calculate arrival time
    val arrivalTimeMillis = System.currentTimeMillis() + (activeJourney.remainingDurationSeconds * 1000)
    val arrivalTimeFormatted = remember(activeJourney.remainingDurationSeconds) {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        sdf.format(Date(arrivalTimeMillis))
    }

    val safetyScore = activeJourney.currentSafetyProfile?.relativeSafetyScore
        ?: activeJourney.route.safetyProfile?.relativeSafetyScore
        ?: 87

    val instructionText = currentInstruction
        ?: activeJourney.currentInstruction
        ?: "Head toward ${activeJourney.destinationName}"

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("active_navigation_overlay")
    ) {
        // --- TOP BAR OVERLAY ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: End Navigation (X) Button
            IconButton(
                onClick = onCancelJourney,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xE6FDF8F8))
                    .border(1.dp, GlassSurfaceBorder, CircleShape)
                    .testTag("end_navigation_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "End Navigation",
                    tint = PrimaryObsidian,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Center: "● NAVIGATING" Status Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(PrimaryObsidian)
                    .border(1.dp, GlassSurfaceBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(SecondaryContainerLight.copy(alpha = dotAlpha))
                    )
                    Text(
                        text = "NAVIGATING",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.2.sp
                        ),
                        color = Color.White
                    )
                }
            }

            // Right: Audio Mute Toggle Button
            IconButton(
                onClick = { isMuted = !isMuted },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xE6FDF8F8))
                    .border(1.dp, GlassSurfaceBorder, CircleShape)
                    .testTag("toggle_voice_button")
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = if (isMuted) "Unmute" else "Mute",
                    tint = if (isMuted) OnSurfaceVariantLight else SecondarySafetyGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // --- TURN-BY-TURN INSTRUCTION BANNER (Upper Third) ---
        RaahiGlassPanel(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 68.dp)
                .align(Alignment.TopCenter),
            shape = RoundedCornerShape(20.dp),
            backgroundColor = Color(0xF21C1B1B),
            borderColor = Color(0x33FFFFFF),
            elevation = 12.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SecondarySafetyGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TurnRight,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = instructionText,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = Color.White,
                        maxLines = 2
                    )
                    Text(
                        text = "Toward ${activeJourney.destinationName}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = Color(0xFFC4C7C7)
                    )
                }
            }
        }

        // --- FLOATING MAP ACTION BUTTONS (Right Side) ---
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Incident Report Button
            IconButton(
                onClick = onReportIncidentClick,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xE6FDF8F8))
                    .border(1.dp, GlassSurfaceBorder, CircleShape)
                    .testTag("report_incident_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ReportProblem,
                    contentDescription = "Report Condition",
                    tint = AlertRed,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Recenter Button
            IconButton(
                onClick = onRecenterClick,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xE6FDF8F8))
                    .border(1.dp, GlassSurfaceBorder, CircleShape)
                    .testTag("recenter_navigation_button")
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Recenter",
                    tint = SecondarySafetyGreen,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // --- BOTTOM NAVIGATION SUMMARY CARD ---
        RaahiGlassPanel(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            backgroundColor = Color(0xF8FDF8F8),
            borderColor = GlassSurfaceBorder,
            elevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Top Row: Remaining time & distance + Arrival Time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = activeJourney.formattedRemainingDuration,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 24.sp
                                ),
                                color = PrimaryObsidian
                            )
                            Text(
                                text = "  /  ${activeJourney.formattedRemainingDistance}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp
                                ),
                                color = OnSurfaceVariantLight,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }

                        Text(
                            text = "Arrival at $arrivalTimeFormatted",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            ),
                            color = SecondarySafetyGreen
                        )
                    }

                    // "View Details" button
                    TextButton(
                        onClick = onViewDetails,
                        modifier = Modifier.testTag("nav_view_details_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = SecondarySafetyGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "View Details",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                ),
                                color = SecondarySafetyGreen
                            )
                        }
                    }
                }

                // Green Safety Status Strip
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFE8F5E9))
                        .border(1.dp, SecondarySafetyGreen.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .clickable { onViewDetails() }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(SecondarySafetyGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        Text(
                            text = "Safer Route: $safetyScore/100 — Conditions currently stable",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            color = Color(0xFF005228),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Bottom Simulation Quick Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onAdvanceStep,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("advance_nav_step_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FastForward,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Advance Step",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    Button(
                        onClick = onCompleteJourney,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("arrive_now_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SecondarySafetyGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Arrive Now",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    }
}
