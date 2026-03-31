package edu.javeriana.fixup.ui.features.publication_detail

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
class PublicationDetailViewModel @Inject constructor(
    private val repository: FeedRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PublicationDetailUiState())
    val uiState: StateFlow<PublicationDetailUiState> = _uiState.asStateFlow()

    fun loadPublication(publicationId: String?) {
        val id = publicationId?.toIntOrNull()
        if (id == null) {
            _uiState.update { it.copy(error = "ID de publicación inválido", isLoading = false) }
            return
        }

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val result = repository.getPublicationById(id)
            
            result.onSuccess { publication ->
                _uiState.update { 
                    it.copy(
                        publication = publication,
                        description = publication.description ?: "Sin descripción disponible",
                        isLoading = false,
                        error = null
                    )
                }
            }.onFailure { error ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar la publicación: ${error.message}"
                    )
                }
            }
        }
    }
}
