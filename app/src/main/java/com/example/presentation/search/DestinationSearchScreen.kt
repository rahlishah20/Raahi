package com.example.presentation.search

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NorthWest
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.KarachiDestination
import com.example.presentation.components.RaahiGlassPanel
import com.example.ui.theme.GlassInputBackground
import com.example.ui.theme.GlassSurfaceBackground
import com.example.ui.theme.OnPrimaryLight
import com.example.ui.theme.OnSurfaceLight
import com.example.ui.theme.OnSurfaceVariantLight
import com.example.ui.theme.PrimaryObsidian
import com.example.ui.theme.SecondaryContainerLight
import com.example.ui.theme.SecondarySafetyGreen

@Composable
fun DestinationSearchScreen(
    viewModel: SearchViewModel,
    onNavigateBack: () -> Unit = {},
    onDestinationSelected: (KarachiDestination) -> Unit = {},
    onCurrentLocationSelected: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var isSelectionHandled by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF9F7F6))) {
        // Glass Overlay Container
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            RaahiGlassPanel(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("search_glass_panel"),
                shape = RoundedCornerShape(24.dp),
                backgroundColor = GlassSurfaceBackground,
                elevation = 10.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Back Action Button
                    IconButton(
                        onClick = {
                            if (!isSelectionHandled) {
                                isSelectionHandled = true
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .testTag("search_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back",
                            tint = PrimaryObsidian,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Glass Search Input Field
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(GlassInputBackground)
                            .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = OnSurfaceVariantLight,
                                modifier = Modifier.size(22.dp)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Box(modifier = Modifier.weight(1f)) {
                                if (state.query.isEmpty()) {
                                    Text(
                                        text = "Where to?",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = OnSurfaceVariantLight.copy(alpha = 0.6f)
                                    )
                                }
                                BasicTextField(
                                    value = state.query,
                                    onValueChange = { viewModel.onQueryChanged(it) },
                                    textStyle = MaterialTheme.typography.titleMedium.copy(color = PrimaryObsidian),
                                    singleLine = true,
                                    cursorBrush = SolidColor(PrimaryObsidian),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .focusRequester(focusRequester)
                                        .testTag("destination_search_input")
                                )
                            }

                            if (state.query.isNotEmpty()) {
                                IconButton(
                                    onClick = { viewModel.clearQuery() },
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE5E2E1))
                                        .testTag("clear_search_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear search",
                                        tint = OnSurfaceVariantLight,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Destinations List
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .testTag("destinations_list")
                    ) {
                        // Current Location Item
                        item {
                            CurrentLocationItem(
                                onClick = {
                                    if (!isSelectionHandled) {
                                        isSelectionHandled = true
                                        onCurrentLocationSelected()
                                    }
                                }
                            )
                            HorizontalDivider(
                                color = Color.White.copy(alpha = 0.3f),
                                thickness = 0.5.dp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        // Section Header
                        item {
                            Text(
                                text = if (state.query.isEmpty()) "RECENT" else "KARACHI LOCATIONS",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariantLight.copy(alpha = 0.8f),
                                letterSpacing = 0.08.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 10.dp)
                            )
                        }

                        if (state.isLoading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = SecondarySafetyGreen,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        } else if (state.results.isEmpty() && state.query.isNotEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No Karachi destinations found matching \"${state.query}\".",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = OnSurfaceVariantLight
                                    )
                                }
                            }
                        } else {
                            items(
                                items = state.results,
                                key = { it.id }
                            ) { destination ->
                                DestinationResultItem(
                                    destination = destination,
                                    onClick = {
                                        if (!isSelectionHandled) {
                                            isSelectionHandled = true
                                            onDestinationSelected(destination)
                                        }
                                    }
                                )
                                HorizontalDivider(
                                    color = Color.White.copy(alpha = 0.2f),
                                    thickness = 0.5.dp
                                )
                            }
                        }

                        // Saved Places Button
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("saved_places_button"),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryObsidian,
                                    contentColor = OnPrimaryLight
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bookmark,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "View Saved Places",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrentLocationItem(
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 10.dp)
            .testTag("current_location_item"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(SecondaryContainerLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = null,
                tint = SecondarySafetyGreen,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = "Current Location",
                style = MaterialTheme.typography.titleMedium,
                color = PrimaryObsidian
            )
            Text(
                text = "Use your device location in Karachi",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariantLight
            )
        }
    }
}

@Composable
private fun DestinationResultItem(
    destination: KarachiDestination,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 12.dp)
            .testTag("destination_item_${destination.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1EDEC)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (destination.isRecent) Icons.Default.History else Icons.Default.LocationOn,
                contentDescription = null,
                tint = OnSurfaceVariantLight,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = destination.name,
                style = MaterialTheme.typography.bodyLarge,
                color = PrimaryObsidian
            )
            Text(
                text = destination.address,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariantLight,
                maxLines = 1
            )
        }

        Icon(
            imageVector = Icons.Default.NorthWest,
            contentDescription = "Select destination",
            tint = OnSurfaceVariantLight.copy(alpha = 0.5f),
            modifier = Modifier.size(18.dp)
        )
    }
}
