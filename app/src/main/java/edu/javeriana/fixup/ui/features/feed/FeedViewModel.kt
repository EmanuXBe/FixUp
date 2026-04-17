package edu.javeriana.fixup.ui.features.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.javeriana.fixup.data.repository.FeedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: FeedRepository,
    private val dataSeeder: edu.javeriana.fixup.data.util.DataSeeder // Inyectamos el seeder
) : ViewModel() {
    private val _uiState = MutableStateFlow(FeedUiState(isLoading = true))
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        // Ejecutamos el quemado de datos al iniciar (solo para pruebas)
        viewModelScope.launch {
            dataSeeder.seed()
            loadFeedData()
        }
    }

    private fun loadFeedData() {
        // Cargamos categorías primero ya que suelen ser locales o más rápidas
        viewModelScope.launch {
            val categoriesResult = repository.getCategories()
            _uiState.update { it.copy(
                categories = categoriesResult.getOrDefault(emptyList()),
                isConnected = categoriesResult.isSuccess
            ) }
        }

        // Cargamos publicaciones en paralelo
        viewModelScope.launch {
            val publicationsResult = repository.getPublications()
            _uiState.update { it.copy(
                publications = publicationsResult.getOrDefault(emptyList()),
                isLoading = false,
                isConnected = publicationsResult.isSuccess
            ) }
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery) }
    }
}
