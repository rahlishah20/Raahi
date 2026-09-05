package com.example.presentation.components

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.SafePoint
import com.example.domain.model.SafePointCategory
import com.example.domain.model.SafePointOpeningStatus
import com.example.ui.theme.GlassSurfaceBorder
import com.example.ui.theme.OnSurfaceLight
import com.example.ui.theme.OnSurfaceVariantLight
import com.example.ui.theme.OutlineVariantLight
import com.example.ui.theme.PrimaryObsidian
import com.example.ui.theme.SecondaryContainerLight
import com.example.ui.theme.SecondarySafetyGreen
import com.example.ui.theme.SurfaceContainerLow

@Composable
fun SafePointDetailSheet(
    modifier: Modifier = Modifier,
    safePoint: SafePoint,
    onNavigate: (SafePoint) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val categoryIcon = when (safePoint.category) {
        SafePointCategory.POLICE_FACILITY -> Icons.Default.Security
        SafePointCategory.HOSPITAL -> Icons.Default.LocalHospital
        SafePointCategory.PHARMACY -> Icons.Default.LocalPharmacy
        SafePointCategory.PETROL_STATION -> Icons.Default.LocalGasStation
        else -> Icons.Default.LocationOn
    }

    val iconColor = when (safePoint.category) {
        SafePointCategory.POLICE_FACILITY -> SecondarySafetyGreen
        SafePointCategory.HOSPITAL -> Color(0xFFBA1A1A)
        SafePointCategory.PHARMACY -> Color(0xFF006874)
        SafePointCategory.PETROL_STATION -> Color(0xFF9A5800)
        else -> PrimaryObsidian
    }

    val distanceText = when {
        safePoint.distanceFromUser != null -> "${safePoint.distanceFromUser}m from you"
        safePoint.distanceToRouteMeters > 0 -> "${safePoint.distanceToRouteMeters}m from route"
        else -> "Nearby Corridor Safe Haven"
    }

    val phoneNumber = when (safePoint.category) {
        SafePointCategory.POLICE_FACILITY -> "15"
        SafePointCategory.HOSPITAL -> "1122"
        else -> null
    }

    RaahiGlassPanel(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        backgroundColor = Color(0xF8FDF8F8),
        borderColor = GlassSurfaceBorder,
        elevation = 16.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .testTag("safe_point_detail_sheet"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .size(width = 36.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(OutlineVariantLight)
                    .align(Alignment.CenterHorizontally)
            )

            // Header: Category Icon, Name, Verified Label, Dismiss (X)
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
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, OutlineVariantLight.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = categoryIcon,
                            contentDescription = safePoint.category.displayName,
                            tint = iconColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = safePoint.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = OnSurfaceLight,
                            maxLines = 1
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (safePoint.verified) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Verified",
                                        tint = SecondarySafetyGreen,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "Verified Safe Point",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp
                                        ),
                                        color = SecondarySafetyGreen
                                    )
                                }
                                Text(
                                    text = "•",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnSurfaceVariantLight
                                )
                            }

                            Text(
                                text = safePoint.category.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = OnSurfaceVariantLight
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerLow)
                        .testTag("dismiss_safe_point_detail_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = OnSurfaceLight,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Info Details Box: Distance, Opening Status, Address
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, OutlineVariantLight.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = distanceText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            ),
                            color = PrimaryObsidian
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (safePoint.openingStatus == SafePointOpeningStatus.OPEN) Color(0xFFE8F5E9)
                                    else Color(0xFFFFEBEE)
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (safePoint.openingStatus == SafePointOpeningStatus.OPEN) "Open 24/7" else safePoint.openingStatus.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = if (safePoint.openingStatus == SafePointOpeningStatus.OPEN) SecondarySafetyGreen else Color(0xFFBA1A1A)
                            )
                        }
                    }

                    if (safePoint.address.isNotBlank()) {
                        Text(
                            text = safePoint.address,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = OnSurfaceVariantLight
                        )
                    }
                }
            }

            // Action Buttons: "Navigate" (Primary) + Phone "Call" Icon Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Navigate Button
                Button(
                    onClick = { onNavigate(safePoint) },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("navigate_to_safe_point_button"),
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
                            text = "Navigate",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )
                    }
                }

                // Phone Call Icon Button
                if (phoneNumber != null) {
                    IconButton(
                        onClick = {
                            try {
                                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                                context.startActivity(dialIntent)
                            } catch (e: Exception) {
                                // Fallback if no dialer app installed
                            }
                        },
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFE8F5E9))
                            .border(1.dp, SecondarySafetyGreen.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .testTag("call_safe_point_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Call Safe Point",
                            tint = SecondarySafetyGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}
