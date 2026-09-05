package com.example.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.OnSurfaceLight
import com.example.ui.theme.OnSurfaceVariantLight

@Composable
fun RaahiGlassSearchBar(
    modifier: Modifier = Modifier,
    placeholder: String = "Where are you going?",
    selectedDestinationName: String? = null,
    onClick: () -> Unit = {},
    onMicClick: () -> Unit = {}
) {
    RaahiGlassPanel(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable(onClick = onClick)
            .testTag("home_search_bar"),
        shape = RoundedCornerShape(16.dp),
        elevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = OnSurfaceVariantLight,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = selectedDestinationName ?: placeholder,
                style = MaterialTheme.typography.bodyLarge,
                color = if (selectedDestinationName != null) OnSurfaceLight else OnSurfaceVariantLight,
                modifier = Modifier
                    .weight(1f)
                    .testTag("search_bar_text")
            )

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1EDEC))
                    .clickable { onMicClick() }
                    .testTag("mic_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice search",
                    tint = OnSurfaceVariantLight,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
