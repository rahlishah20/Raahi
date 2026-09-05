package com.example.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.PrimaryObsidian
import java.io.File

@Composable
fun RaahiGlassTopBar(
    profileName: String = "Haseeb",
    profilePhotoPath: String? = null,
    modifier: Modifier = Modifier,
    onMenuClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    RaahiGlassPanel(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .testTag("menu_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = PrimaryObsidian
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "RAAHI",
                style = MaterialTheme.typography.headlineMedium.copy(
                    letterSpacing = (-0.03).sp
                ),
                color = PrimaryObsidian,
                modifier = Modifier.testTag("app_title")
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.White.copy(alpha = 0.8f), CircleShape)
                    .background(Color(0xFFE5E2E1))
                    .clickable(onClick = onProfileClick)
                    .testTag("profile_button"),
                contentAlignment = Alignment.Center
            ) {
                if (!profilePhotoPath.isNullOrBlank()) {
                    AsyncImage(
                        model = File(profilePhotoPath),
                        contentDescription = "User Profile Avatar",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color(0xFF444748),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
