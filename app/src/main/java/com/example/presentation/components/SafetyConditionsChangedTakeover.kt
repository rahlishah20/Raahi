package com.example.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.SecurityUpdateWarning
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.RaahiRoute
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.CautionAmber
import com.example.ui.theme.ErrorContainerLight
import com.example.ui.theme.GlassSurfaceBorder
import com.example.ui.theme.OnAlertRed
import com.example.ui.theme.OnErrorContainerLight
import com.example.ui.theme.OnPrimaryLight
import com.example.ui.theme.OnSurfaceLight
import com.example.ui.theme.OnSurfaceVariantLight
import com.example.ui.theme.OutlineVariantLight
import com.example.ui.theme.PrimaryObsidian
import com.example.ui.theme.SecondaryContainerLight
import com.example.ui.theme.SecondarySafetyGreen

@Composable
fun SafetyConditionsChangedTakeover(
    modifier: Modifier = Modifier,
    notice: String?,
    currentRoute: RaahiRoute?,
    suggestedRoute: RaahiRoute,
    onSwitchRoute: () -> Unit,
    onDismissRerouteDialog: () -> Unit
) {
    val currentScore = currentRoute?.safetyProfile?.relativeSafetyScore ?: 68
    val alternativeScore = suggestedRoute.safetyProfile?.relativeSafetyScore ?: 88
    val oldScore = 85 // Baseline score before corridor condition drop

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            .padding(horizontal = 20.dp, vertical = 32.dp)
            .testTag("safety_conditions_takeover"),
        contentAlignment = Alignment.Center
    ) {
        RaahiGlassPanel(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            backgroundColor = Color(0xFFFDF8F8),
            borderColor = GlassSurfaceBorder,
            elevation = 20.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Header with Warning Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(AmberContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = Color(0xFF9A5800),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Safety Conditions Changed",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = OnSurfaceLight
                        )
                        Text(
                            text = "Dynamic Corridor Advisory",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = AlertRed
                            )
                        )
                    }
                }

                // Advisory Notice Subtext
                Text(
                    text = notice ?: "Live signals indicate reduced activity and lighting along your current route segment. A safer alternative is available.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    ),
                    color = OnSurfaceVariantLight,
                    modifier = Modifier.fillMaxWidth()
                )

                // 1. Current Route Card (Struck-through old score -> lower red score)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFFFF0EE))
                        .border(1.dp, AlertRed.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                        .padding(16.dp)
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
                                Text(
                                    text = "CURRENT ROUTE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = AlertRed
                                )
                                Text(
                                    text = "• Compromised",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = OnSurfaceVariantLight
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = currentRoute?.title ?: "Primary Corridor",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                ),
                                color = OnSurfaceLight
                            )
                            Text(
                                text = "${currentRoute?.formattedDuration ?: "16 min"} • ${currentRoute?.formattedDistance ?: "6.4 km"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariantLight
                            )
                        }

                        // Struck-through old score -> red new score
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "$oldScore",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    textDecoration = TextDecoration.LineThrough,
                                    fontSize = 16.sp
                                ),
                                color = OnSurfaceVariantLight
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = AlertRed,
                                modifier = Modifier.size(14.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AlertRed)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "$currentScore",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    ),
                                    color = OnAlertRed
                                )
                            }
                        }
                    }
                }

                // 2. Green "Recommended Detour" Card with Alternative Route Score
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFE8F5E9))
                        .border(1.5.dp, SecondarySafetyGreen.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                        .padding(16.dp)
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
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SecondarySafetyGreen)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "RECOMMENDED DETOUR",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Color.White
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = suggestedRoute.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = PrimaryObsidian
                            )
                            Text(
                                text = "${suggestedRoute.formattedDuration} • ${suggestedRoute.formattedDistance} • Well lit & active",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = Color(0xFF005228)
                            )
                        }

                        // Green safety score badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(SecondarySafetyGreen)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = SecondaryContainerLight,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "$alternativeScore",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp
                                    ),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onSwitchRoute,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("switch_to_safer_route_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SecondarySafetyGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AltRoute,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "View Safer Route",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onDismissRerouteDialog,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("keep_current_route_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = OnSurfaceVariantLight
                        )
                    ) {
                        Text(
                            text = "Keep Current Route",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }
    }
}
