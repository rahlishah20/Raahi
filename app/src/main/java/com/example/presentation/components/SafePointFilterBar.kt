package com.example.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.SafePointCategory
import com.example.ui.theme.GlassSurfaceBorder
import com.example.ui.theme.OnSurfaceLight
import com.example.ui.theme.OnSurfaceVariantLight
import com.example.ui.theme.OutlineVariantLight
import com.example.ui.theme.PrimaryObsidian
import com.example.ui.theme.SecondaryContainerLight
import com.example.ui.theme.SecondarySafetyGreen

@Composable
fun SafePointFilterBar(
    modifier: Modifier = Modifier,
    selectedCategory: SafePointCategory?,
    onlyVerified: Boolean,
    onFilterCategory: (SafePointCategory?) -> Unit,
    onToggleOnlyVerified: (Boolean) -> Unit
) {
    RaahiGlassPanel(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        backgroundColor = Color(0xEEFDF8F8),
        borderColor = GlassSurfaceBorder,
        elevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "Verified Only" Toggle Chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (onlyVerified) SecondarySafetyGreen else Color.White)
                    .border(
                        width = 1.dp,
                        color = if (onlyVerified) SecondarySafetyGreen else OutlineVariantLight.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onToggleOnlyVerified(!onlyVerified) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("filter_verified_only_chip")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = if (onlyVerified) Color.White else SecondarySafetyGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Verified Only",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (onlyVerified) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp
                        ),
                        color = if (onlyVerified) Color.White else OnSurfaceLight
                    )
                }
            }

            // "All" Category Chip
            val isAllSelected = selectedCategory == null
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isAllSelected) PrimaryObsidian else Color.White)
                    .border(
                        width = 1.dp,
                        color = if (isAllSelected) PrimaryObsidian else OutlineVariantLight.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onFilterCategory(null) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("filter_all_categories_chip")
            ) {
                Text(
                    text = "All Havens",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 11.sp
                    ),
                    color = if (isAllSelected) Color.White else OnSurfaceLight
                )
            }

            // Primary Safe Point Categories
            val featuredCategories = listOf(
                SafePointCategory.POLICE_FACILITY,
                SafePointCategory.HOSPITAL,
                SafePointCategory.PHARMACY,
                SafePointCategory.PETROL_STATION
            )

            featuredCategories.forEach { category ->
                val isSelected = selectedCategory == category
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) PrimaryObsidian else Color.White)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) PrimaryObsidian else OutlineVariantLight.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onFilterCategory(if (isSelected) null else category) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("filter_category_${category.name}")
                ) {
                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp
                        ),
                        color = if (isSelected) Color.White else OnSurfaceLight
                    )
                }
            }
        }
    }
}
