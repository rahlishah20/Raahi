package com.example.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.CommunitySafetyContextTag
import com.example.domain.model.CommunitySafetyRating
import com.example.ui.theme.GlassSurfaceBorder
import com.example.ui.theme.OnSurfaceLight
import com.example.ui.theme.OnSurfaceVariantLight
import com.example.ui.theme.OutlineVariantLight
import com.example.ui.theme.PrimaryObsidian
import com.example.ui.theme.SecondaryContainerLight
import com.example.ui.theme.SecondarySafetyGreen
import com.example.ui.theme.SurfaceContainerLow

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TripCompletedFeedbackDialog(
    modifier: Modifier = Modifier,
    selectedRating: CommunitySafetyRating?,
    selectedTags: Set<CommunitySafetyContextTag>,
    onSelectRating: (CommunitySafetyRating) -> Unit,
    onToggleTag: (CommunitySafetyContextTag) -> Unit,
    onSubmitFeedback: () -> Unit,
    onDismiss: () -> Unit,
    isSubmitted: Boolean = false
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .padding(horizontal = 20.dp, vertical = 24.dp)
            .testTag("trip_completed_feedback_dialog"),
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
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Header: Top "TRIP COMPLETED" label with (X) button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE8F5E9))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "TRIP COMPLETED",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp
                            ),
                            color = SecondarySafetyGreen
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainerLow)
                            .testTag("dismiss_feedback_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = OnSurfaceLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Headline & Subtext
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "How did the route feel?",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = OnSurfaceLight,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Your feedback helps calibrate Karachi corridor safety intelligence.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariantLight,
                        textAlign = TextAlign.Center
                    )
                }

                if (isSubmitted) {
                    // Success Confirmation View
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFE8F5E9))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(SecondarySafetyGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Text(
                                text = "Feedback Recorded!",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = PrimaryObsidian
                            )
                            Text(
                                text = "Thank you for contributing to safer journeys across Karachi.",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariantLight,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    // 5-Emoji Comfort Rating Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.White)
                            .border(1.dp, OutlineVariantLight.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val emojis = listOf(
                            Pair("😣", CommunitySafetyRating.UNSAFE),
                            Pair("🙁", CommunitySafetyRating.UNSAFE),
                            Pair("😐", CommunitySafetyRating.NEUTRAL),
                            Pair("🙂", CommunitySafetyRating.SAFE),
                            Pair("😊", CommunitySafetyRating.SAFE)
                        )

                        emojis.forEachIndexed { index, (emoji, rating) ->
                            val isSelected = selectedRating == rating && (
                                    (rating == CommunitySafetyRating.UNSAFE && index <= 1) ||
                                            (rating == CommunitySafetyRating.NEUTRAL && index == 2) ||
                                            (rating == CommunitySafetyRating.SAFE && index >= 3)
                                    )

                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) SecondaryContainerLight else Color.Transparent)
                                    .border(
                                        width = if (isSelected) 2.dp else 0.dp,
                                        color = if (isSelected) SecondarySafetyGreen else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { onSelectRating(rating) }
                                    .testTag("feedback_emoji_rating_$index"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = emoji,
                                    fontSize = 26.sp
                                )
                            }
                        }
                    }

                    // "What influenced your rating?" section
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "What influenced your rating?",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            ),
                            color = OnSurfaceLight
                        )

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CommunitySafetyContextTag.entries.forEach { tag ->
                                val isTagSelected = selectedTags.contains(tag)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isTagSelected) PrimaryObsidian else Color.White)
                                        .border(
                                            width = 1.dp,
                                            color = if (isTagSelected) PrimaryObsidian else OutlineVariantLight,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { onToggleTag(tag) }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                        .testTag("feedback_tag_${tag.name}")
                                ) {
                                    Text(
                                        text = tag.label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isTagSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 12.sp
                                        ),
                                        color = if (isTagSelected) Color.White else OnSurfaceLight
                                    )
                                }
                            }
                        }
                    }

                    // Full-width Submit Button
                    Button(
                        onClick = onSubmitFeedback,
                        enabled = selectedRating != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("submit_feedback_button"),
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
                            Text(
                                text = "Submit Feedback",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
