package com.example.presentation.history

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Journey
import com.example.presentation.components.RaahiGlassBottomNavBar
import com.example.presentation.components.RaahiGlassPanel
import com.example.presentation.components.RaahiNavTab
import com.example.ui.theme.GlassSurfaceBorder
import com.example.ui.theme.OnSurfaceLight
import com.example.ui.theme.OnSurfaceVariantLight
import com.example.ui.theme.OutlineVariantLight
import com.example.ui.theme.PrimaryObsidian
import com.example.ui.theme.SecondaryContainerLight
import com.example.ui.theme.SecondarySafetyGreen
import com.example.ui.theme.SurfaceContainerLow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    journeyHistory: List<Journey> = emptyList(),
    selectedNavTab: RaahiNavTab = RaahiNavTab.HISTORY,
    onNavTabSelected: (RaahiNavTab) -> Unit = {},
    onBackClick: () -> Unit = {},
    onPlanNewJourney: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("history_screen"),
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
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainerLow)
                            .testTag("back_from_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PrimaryObsidian,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Journey History",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = PrimaryObsidian
                        )
                        Text(
                            text = "Past verified routes & safety logs",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariantLight
                        )
                    }
                }
            }
        },
        bottomBar = {
            RaahiGlassBottomNavBar(
                selectedTab = selectedNavTab,
                onTabSelected = onNavTabSelected,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )
        }
    ) { paddingValues ->
        if (journeyHistory.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1EDEC)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = OnSurfaceVariantLight,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Text(
                        text = "No Past Journeys Yet",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = PrimaryObsidian
                    )

                    Text(
                        text = "Your completed trips and corridor safety logs will appear here automatically.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariantLight,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onPlanNewJourney,
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryObsidian,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Explore Safe Routes",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        } else {
            // History List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(journeyHistory, key = { it.id }) { journey ->
                    HistoryJourneyCard(journey = journey)
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun HistoryJourneyCard(
    journey: Journey
) {
    val dateFormatted = remember(journey.startTime) {
        val sdf = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
        sdf.format(Date(journey.startTime))
    }

    val safetyScore = journey.currentSafetyProfile?.relativeSafetyScore
        ?: journey.route.safetyProfile?.relativeSafetyScore
        ?: 85

    RaahiGlassPanel(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        backgroundColor = Color.White,
        borderColor = OutlineVariantLight.copy(alpha = 0.5f),
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Navigation,
                    contentDescription = null,
                    tint = SecondarySafetyGreen,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = journey.destinationName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = PrimaryObsidian
                )
                Text(
                    text = "From ${journey.originName}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = OnSurfaceVariantLight
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$dateFormatted • ${journey.formattedRemainingDistance}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = OnSurfaceVariantLight
                )
            }

            // Completed Safety Score Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE8F5E9))
                    .border(1.dp, SecondarySafetyGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = SecondarySafetyGreen,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "$safetyScore",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        ),
                        color = SecondarySafetyGreen
                    )
                }
            }
        }
    }
}
