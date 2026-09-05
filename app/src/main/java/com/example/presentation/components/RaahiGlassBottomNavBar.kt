package com.example.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GlassFloatingNavBackground
import com.example.ui.theme.OnSurfaceVariantLight
import com.example.ui.theme.PrimaryObsidian

enum class RaahiNavTab {
    EXPLORE,
    HISTORY,
    PROFILE
}

@Composable
fun RaahiGlassBottomNavBar(
    modifier: Modifier = Modifier,
    selectedTab: RaahiNavTab = RaahiNavTab.EXPLORE,
    onTabSelected: (RaahiNavTab) -> Unit = {}
) {
    RaahiGlassPanel(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(9999.dp),
        backgroundColor = GlassFloatingNavBackground,
        elevation = 10.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            NavTabItem(
                label = "Explore",
                icon = Icons.Default.Explore,
                isSelected = selectedTab == RaahiNavTab.EXPLORE,
                onClick = { onTabSelected(RaahiNavTab.EXPLORE) },
                testTag = "nav_tab_explore"
            )

            NavTabItem(
                label = "History",
                icon = Icons.Default.History,
                isSelected = selectedTab == RaahiNavTab.HISTORY,
                onClick = { onTabSelected(RaahiNavTab.HISTORY) },
                testTag = "nav_tab_history"
            )

            NavTabItem(
                label = "Profile",
                icon = Icons.Default.Person,
                isSelected = selectedTab == RaahiNavTab.PROFILE,
                onClick = { onTabSelected(RaahiNavTab.PROFILE) },
                testTag = "nav_tab_profile"
            )
        }
    }
}

@Composable
private fun NavTabItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val contentColor = if (isSelected) PrimaryObsidian else OnSurfaceVariantLight.copy(alpha = 0.7f)

    Column(
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = if (isSelected) MaterialTheme.typography.labelMedium else MaterialTheme.typography.labelSmall,
            color = contentColor,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
