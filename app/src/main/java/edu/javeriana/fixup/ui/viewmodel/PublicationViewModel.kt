package edu.javeriana.fixup.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.javeriana.fixup.ui.model.FeedUiState
import edu.javeriana.fixup.ui.model.PublicationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PublicationViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PublicationUiState())
    val uiState: StateFlow<PublicationUiState> = _uiState.asStateFlow()

    fun loadPublication(publicationId: String?) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            // Usamos los datos por defecto de FeedUiState para simular la búsqueda
            val allPublications = FeedUiState().publications
            val publication = allPublications.find { it.id == publicationId }
            
            val description = when(publicationId) {
                "1" -> "Transforma tu espacio con nuestros diseños exclusivos de salas. Utilizamos materiales de alta calidad y nos adaptamos a tus necesidades y presupuesto."
                "2" -> "¡Arma el comedor de tus sueños! Ofrecemos soluciones integrales para que tus cenas familiares sean inolvidables."
                "3" -> "Renovamos tu baño por completo. Incluye cambio de sanitarios, grifería de alta gama y revestimientos modernos."
                "4" -> "Cocina integral con acabados premium. Optimización de espacio y diseño ergonómico para tu comodidad."
                else -> "Transforma tu espacio con nuestros diseños exclusivos. Utilizamos materiales de alta calidad y nos adaptamos a tus necesidades."
            }

            _uiState.update { 
                it.copy(
                    publication = publication,
                    description = description,
                    isLoading = false,
                    error = if (publication == null) "Publicación no encontrada" else null
                )
            }
        }
    }
}
