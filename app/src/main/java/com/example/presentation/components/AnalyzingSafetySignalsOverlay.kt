package com.example.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlassSurfaceBackground
import com.example.ui.theme.GlassSurfaceBorder
import com.example.ui.theme.OnSurfaceLight
import com.example.ui.theme.OnSurfaceVariantLight
import com.example.ui.theme.OutlineVariantLight
import com.example.ui.theme.PrimaryObsidian
import com.example.ui.theme.SecondaryContainerLight
import com.example.ui.theme.SecondarySafetyGreen

@Composable
fun AnalyzingSafetySignalsOverlay(
    modifier: Modifier = Modifier,
    originName: String = "Current Location",
    destinationName: String = "Destination"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar_transition")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_rotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "radar_pulse"
    )

    val progressAnim by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.92f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "progress_anim"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .padding(24.dp)
            .testTag("analyzing_safety_signals_overlay"),
        contentAlignment = Alignment.Center
    ) {
        RaahiGlassPanel(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            backgroundColor = Color(0xF2FDF8F8),
            borderColor = GlassSurfaceBorder,
            elevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Centered Radar / Target with Animated Rotating Arc
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(92.dp)) {
                        val strokeWidth = 3.dp.toPx()
                        val diameter = size.minDimension - strokeWidth
                        val radius = diameter / 2f
                        val center = Offset(size.width / 2f, size.height / 2f)

                        // Outer static ring
                        drawCircle(
                            color = Color(0x33006D36),
                            radius = radius,
                            center = center,
                            style = Stroke(width = 1.5.dp.toPx())
                        )

                        // Middle static ring
                        drawCircle(
                            color = Color(0x22006D36),
                            radius = radius * 0.65f,
                            center = center,
                            style = Stroke(width = 1.dp.toPx())
                        )

                        // Animated scanning arc
                        drawArc(
                            color = SecondarySafetyGreen,
                            startAngle = rotation,
                            sweepAngle = 110f,
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = Size(diameter, diameter),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        // Secondary counter-rotating small arc
                        drawArc(
                            color = Color(0xFF67FB98),
                            startAngle = -rotation * 1.5f,
                            sweepAngle = 60f,
                            useCenter = false,
                            topLeft = Offset(center.x - radius * 0.65f, center.y - radius * 0.65f),
                            size = Size(radius * 1.3f, radius * 1.3f),
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    // Center target shield icon
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(PrimaryObsidian),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = SecondaryContainerLight,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Headline & Subtext
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Analyzing available safety signals...",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = OnSurfaceLight
                    )
                    Text(
                        text = "Evaluating route conditions, lighting, and activity density.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariantLight,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 3-Item Checklist with Animated Dot Indicators
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.8f))
                        .border(1.dp, OutlineVariantLight.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ChecklistItem(
                        title = "Checking street lighting",
                        detail = "Municipal & corridor luminosity levels",
                        isCompleted = progressAnim > 0.45f
                    )
                    ChecklistItem(
                        title = "Verifying business activity",
                        detail = "Commercial storefronts & POI density",
                        isCompleted = progressAnim > 0.70f
                    )
                    ChecklistItem(
                        title = "Scanning community reports",
                        detail = "Recent perceived traveler sentiments",
                        isCompleted = progressAnim > 0.88f
                    )
                }

                // Bottom Progress Bar
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { progressAnim },
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
                            text = "Calibrating Karachi Safety Intelligence",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = OnSurfaceVariantLight
                        )
                        Text(
                            text = "${(progressAnim * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = SecondarySafetyGreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChecklistItem(
    title: String,
    detail: String,
    isCompleted: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (isCompleted) SecondaryContainerLight else Color(0xFFF1EDEC))
                .border(
                    width = 1.dp,
                    color = if (isCompleted) SecondarySafetyGreen else OutlineVariantLight,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = SecondarySafetyGreen,
                    modifier = Modifier.size(14.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(SecondarySafetyGreen)
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isCompleted) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 13.sp
                ),
                color = if (isCompleted) PrimaryObsidian else OnSurfaceLight
            )
            Text(
                text = detail,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = OnSurfaceVariantLight
            )
        }
    }
}
