package com.example.presentation.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContactEmergency
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.BuildConfig
import com.example.presentation.components.RaahiGlassBottomNavBar
import com.example.presentation.components.RaahiGlassPanel
import com.example.presentation.components.RaahiNavTab
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.GlassSurfaceBorder
import com.example.ui.theme.OnSurfaceLight
import com.example.ui.theme.OnSurfaceVariantLight
import com.example.ui.theme.OutlineVariantLight
import com.example.ui.theme.PrimaryObsidian
import com.example.ui.theme.SecondaryContainerLight
import com.example.ui.theme.SecondarySafetyGreen
import com.example.ui.theme.SurfaceContainerLow
import java.io.File
import java.io.FileOutputStream

@Composable
fun ProfileScreen(
    profileName: String = "Haseeb",
    profilePhotoPath: String? = null,
    onUpdateProfileName: (String) -> Unit = {},
    onUpdateProfilePhoto: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    selectedNavTab: RaahiNavTab = RaahiNavTab.PROFILE,
    onNavTabSelected: (RaahiNavTab) -> Unit = {},
    onNavigateToEnvironmentControl: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var showNameEditDialog by remember { mutableStateOf(false) }
    var editingName by remember(profileName) { mutableStateOf(profileName) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            try {
                val file = File(context.filesDir, "profile_photo.jpg")
                context.contentResolver.openInputStream(selectedUri)?.use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                onUpdateProfilePhoto(file.absolutePath)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val maptilerApiKey = remember {
        try {
            val field = BuildConfig::class.java.getField("MAPTILER_API_KEY")
            (field.get(null) as? String)?.trim().orEmpty()
        } catch (e: Throwable) {
            ""
        }
    }

    if (showNameEditDialog) {
        AlertDialog(
            onDismissRequest = { showNameEditDialog = false },
            title = { Text("Edit Profile Name") },
            text = {
                OutlinedTextField(
                    value = editingName,
                    onValueChange = { editingName = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_name_input")
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (editingName.isNotBlank()) {
                            onUpdateProfileName(editingName.trim())
                        }
                        showNameEditDialog = false
                    },
                    modifier = Modifier.testTag("save_profile_name_button")
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNameEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen"),
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
                            .testTag("back_from_profile_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PrimaryObsidian,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = "Traveler Profile",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = PrimaryObsidian
                    )
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 18.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- USER HEADER CARD ---
            RaahiGlassPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                backgroundColor = Color.White,
                borderColor = GlassSurfaceBorder,
                elevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(PrimaryObsidian)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .testTag("profile_avatar_box"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!profilePhotoPath.isNullOrBlank()) {
                            AsyncImage(
                                model = File(profilePhotoPath),
                                contentDescription = "Profile Photo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            val initialChar = profileName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "H"
                            Text(
                                text = initialChar,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SecondaryContainerLight
                                )
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.clickable {
                                editingName = profileName
                                showNameEditDialog = true
                            }
                        ) {
                            Text(
                                text = profileName,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp
                                ),
                                color = PrimaryObsidian,
                                modifier = Modifier.testTag("profile_name_text")
                            )
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified Traveler",
                                tint = SecondarySafetyGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "Karachi Corridor Community Member",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = OnSurfaceVariantLight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFE8F5E9))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Active Safety Contributor",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = SecondarySafetyGreen
                            )
                        }
                    }
                }
            }

            // --- STATS OVERVIEW ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileStatCard(
                    modifier = Modifier.weight(1f),
                    value = "87",
                    label = "Avg Safety Score",
                    icon = Icons.Default.Shield
                )
                ProfileStatCard(
                    modifier = Modifier.weight(1f),
                    value = "14",
                    label = "Safe Trips",
                    icon = Icons.Default.Navigation
                )
                ProfileStatCard(
                    modifier = Modifier.weight(1f),
                    value = "9",
                    label = "Reports Shared",
                    icon = Icons.Default.Feedback
                )
            }

            // --- PROMINENT ENTRY POINT: ENVIRONMENT CONTROL ---
            RaahiGlassPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onNavigateToEnvironmentControl() }
                    .testTag("environment_control_entry_card"),
                shape = RoundedCornerShape(20.dp),
                backgroundColor = Color(0xFFF4FBF6),
                borderColor = SecondarySafetyGreen.copy(alpha = 0.5f),
                elevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(SecondarySafetyGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Environment Control",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = PrimaryObsidian
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AmberContainer)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "DEMO",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    ),
                                    color = Color(0xFF7A4500)
                                )
                            }
                        }
                        Text(
                            text = "Simulate real-time corridor lighting, POI closures, and safety drops",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = OnSurfaceVariantLight
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = SecondarySafetyGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // --- PROMINENT ENTRY POINT: MAP ENGINE ---
            RaahiGlassPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .testTag("maps_credentials_entry_card"),
                shape = RoundedCornerShape(20.dp),
                backgroundColor = Color(0xFFF9FBFB),
                borderColor = GlassSurfaceBorder,
                elevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(SecondaryContainerLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = null,
                            tint = PrimaryObsidian,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Map Engine: MapTiler SDK",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = PrimaryObsidian
                        )
                        Text(
                            text = if (maptilerApiKey.isNotBlank()) "Vector Karachi Style active" else "Default MapTiler Vector Style active",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = OnSurfaceVariantLight
                        )
                    }
                }
            }

            // --- SAFETY PREFERENCES & HELPLINES ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .border(1.dp, OutlineVariantLight.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Emergency Helplines (Karachi)",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = PrimaryObsidian
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sindh Police Emergency",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        color = OnSurfaceLight
                    )
                    Text(
                        text = "15",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SecondarySafetyGreen
                        )
                    )
                }

                HorizontalDivider(color = OutlineVariantLight.copy(alpha = 0.3f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rescue & Ambulance",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        color = OnSurfaceLight
                    )
                    Text(
                        text = "1122",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SecondarySafetyGreen
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ProfileStatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, OutlineVariantLight.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SecondarySafetyGreen,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = PrimaryObsidian
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = OnSurfaceVariantLight,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
