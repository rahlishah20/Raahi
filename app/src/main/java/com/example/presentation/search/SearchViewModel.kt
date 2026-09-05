package com.example.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.domain.model.KarachiDestination
import com.example.domain.repository.DestinationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val destinationRepository: DestinationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchState())
    val uiState: StateFlow<SearchState> = _uiState.asStateFlow()

    init {
        loadRecentDestinations()
    }

    private fun loadRecentDestinations() {
        viewModelScope.launch {
            destinationRepository.getRecentDestinations().collect { recent ->
                _uiState.update {
                    it.copy(
                        recentDestinations = recent,
                        results = if (it.query.isEmpty()) recent else it.results
                    )
                }
            }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _uiState.update { it.copy(query = newQuery, isSearchActive = newQuery.isNotEmpty()) }
        search(newQuery)
    }

    fun clearQuery() {
        _uiState.update {
            it.copy(
                query = "",
                isSearchActive = false,
                results = it.recentDestinations
            )
        }
    }

    private fun search(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            destinationRepository.searchDestinations(query).collect { destinations ->
                _uiState.update {
                    it.copy(
                        results = destinations,
                        isLoading = false
                    )
                }
            }
        }
    }

    class Factory(
        private val destinationRepository: DestinationRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SearchViewModel(destinationRepository) as T
        }
    }
}
