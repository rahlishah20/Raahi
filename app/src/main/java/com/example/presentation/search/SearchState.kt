package com.example.presentation.search

import com.example.domain.model.KarachiDestination

data class SearchState(
    val query: String = "",
    val results: List<KarachiDestination> = emptyList(),
    val recentDestinations: List<KarachiDestination> = emptyList(),
    val isLoading: Boolean = false,
    val isSearchActive: Boolean = false
)
