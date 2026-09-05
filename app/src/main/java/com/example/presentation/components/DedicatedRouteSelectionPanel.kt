package com.example.presentation.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.RaahiRoute
import com.example.ui.theme.GlassSurfaceBorder
import com.example.ui.theme.OnSurfaceLight
import com.example.ui.theme.OnSurfaceVariantLight
import com.example.ui.theme.OutlineVariantLight
import com.example.ui.theme.PrimaryObsidian
import com.example.ui.theme.SecondaryContainerLight
import com.example.ui.theme.SecondarySafetyGreen
import com.example.ui.theme.SurfaceContainerLowest

@Composable
fun DedicatedRouteSelectionPanel(
    modifier: Modifier = Modifier,
    routes: List<RaahiRoute>,
    selectedRoute: RaahiRoute?,
    onSelectRoute: (RaahiRoute) -> Unit,
    onConfirmRoute: () -> Unit,
    onViewRouteDetails: (RaahiRoute) -> Unit
) {
    RaahiGlassPanel(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        backgroundColor = Color(0xF5FDF8F8),
        borderColor = GlassSurfaceBorder,
        elevation = 16.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Drag handle / pill indicator
            Box(
                modifier = Modifier
                    .size(width = 36.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(OutlineVariantLight)
                    .align(Alignment.CenterHorizontally)
            )

            // Header: Title & Subtitle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Select Route",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = OnSurfaceLight
                    )
                    Text(
                        text = "${routes.size} routes evaluated for safety signals",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariantLight
                    )
                }

                // "View Route Details" text link
                val activeRoute = selectedRoute ?: routes.firstOrNull()
                if (activeRoute != null) {
                    TextButton(
                        onClick = { onViewRouteDetails(activeRoute) },
                        modifier = Modifier.testTag("view_route_details_link")
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
            }

            // Route Cards List
            val anyRecommended = routes.any { it.safetyProfile?.isRecommended == true }
            val recommendedRoute = routes.firstOrNull { it.safetyProfile?.isRecommended == true }

            // Safer Route Recommendation Banner if selected route is not the safest option
            val activeSelected = selectedRoute ?: routes.firstOrNull()
            if (recommendedRoute != null && activeSelected != null && activeSelected.id != recommendedRoute.id) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFF8E1))
                        .border(1.dp, Color(0xFFFFB800).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .clickable { onSelectRoute(recommendedRoute) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("safer_alternative_recommendation_bar")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(0xFF7A4500),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Safer option available: ${recommendedRoute.title} (${recommendedRoute.safetyProfile?.relativeSafetyScore ?: 0} vs ${activeSelected.safetyProfile?.relativeSafetyScore ?: 0})",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            ),
                            color = Color(0xFF7A4500),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "SELECT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = SecondarySafetyGreen
                        )
                    }
                }
            }

            routes.forEachIndexed { index, route ->
                val isSelected = (selectedRoute?.id == route.id) || (selectedRoute == null && index == 0)
                val isRecommended = if (anyRecommended) route.safetyProfile?.isRecommended == true else index == 0
                val safetyScore = route.safetyProfile?.relativeSafetyScore ?: 0

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isSelected) Color(0xFFF4FBF6) else SurfaceContainerLowest)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) SecondarySafetyGreen else OutlineVariantLight.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(18.dp)
                        )
                        .clickable { onSelectRoute(route) }
                        .padding(14.dp)
                        .testTag("route_option_card_${route.id}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Radio Selector
                        Box(
                            modifier = Modifier
                                .size(24.dp)
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

                        // Middle: Via Road + Duration + Distance + Badges
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (isRecommended) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(SecondarySafetyGreen)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "RECOMMENDED",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            ),
                                            color = Color.White
                                        )
                                    }
                                }

                                Text(
                                    text = route.formattedDuration,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    ),
                                    color = PrimaryObsidian
                                )

                                Text(
                                    text = "• ${route.formattedDistance}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariantLight
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = route.title,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp
                                ),
                                color = OnSurfaceLight,
                                maxLines = 1
                            )
                        }

                        // Right: Safety Score Badge (Clickable to open Explain WHY)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (safetyScore >= 80) Color(0xFFE8F5E9)
                                    else if (safetyScore >= 70) Color(0xFFFFF8E1)
                                    else Color(0xFFFFEBEE)
                                )
                                .border(
                                    1.dp,
                                    if (safetyScore >= 80) SecondarySafetyGreen.copy(alpha = 0.5f)
                                    else OutlineVariantLight,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { onViewRouteDetails(route) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("safety_score_badge_${route.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = if (safetyScore >= 80) SecondarySafetyGreen else OnSurfaceVariantLight,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "$safetyScore",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp
                                    ),
                                    color = if (safetyScore >= 80) SecondarySafetyGreen else PrimaryObsidian
                                )
                            }
                        }
                    }
                }
            }

            // Primary Action Button: "Choose Safer Route"
            Button(
                onClick = onConfirmRoute,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("choose_safer_route_button"),
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
                        imageVector = Icons.Default.Navigation,
                        contentDescription = null,
                        tint = SecondaryContainerLight,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Choose Safer Route",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                }
            }
        }
    }
}
