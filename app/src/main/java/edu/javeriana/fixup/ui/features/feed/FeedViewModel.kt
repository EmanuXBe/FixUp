package edu.javeriana.fixup.ui.features.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.javeriana.fixup.data.repository.FeedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedViewModel(
    private val repository: FeedRepository = FeedRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(FeedUiState(isLoading = true))
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        loadFeedData()
    }

    private fun loadFeedData() {
        viewModelScope.launch {
            val categoriesResult = repository.getCategories()
            val publicationsResult = repository.getPublications()

            _uiState.update { state ->
                state.copy(
                    categories = categoriesResult.getOrDefault(emptyList()),
                    publications = publicationsResult.getOrDefault(emptyList()),
                    isLoading = false
                )
            }
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery) }
    }
}
