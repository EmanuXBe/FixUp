package edu.javeriana.fixup.ui.features.publication_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.javeriana.fixup.data.network.dto.ReviewRequestDto
import edu.javeriana.fixup.data.repository.AuthRepository
import edu.javeriana.fixup.data.repository.FeedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PublicationDetailViewModel @Inject constructor(
    private val repository: FeedRepository,
    private val authRepository: AuthRepository
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
            val publicationResult = repository.getPublicationById(id)
            
            publicationResult.onSuccess { publication ->
                _uiState.update { 
                    it.copy(
                        publication = publication,
                        description = publication.description ?: "Sin descripción disponible",
                        error = null
                    )
                }
                loadReviews(id)
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

    private fun loadReviews(serviceId: Int) {
        viewModelScope.launch {
            val reviewsResult = repository.getReviewsByServiceId(serviceId)
            reviewsResult.onSuccess { reviews ->
                _uiState.update { it.copy(reviews = reviews, isLoading = false) }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun sendReview(rating: Int, comment: String) {
        val serviceId = _uiState.value.publication?.id?.toIntOrNull() ?: return
        val currentUser = authRepository.currentUser

        if (currentUser == null) {
            _uiState.update { it.copy(reviewError = "Debes iniciar sesión para dejar una reseña") }
            return
        }
        
        _uiState.update { it.copy(isSendingReview = true, reviewError = null, reviewSent = false) }
        
        viewModelScope.launch {
            val request = ReviewRequestDto(
                userId = currentUser.uid,
                serviceId = serviceId.toString(),
                rating = rating,
                comment = comment
            )
            
            val result = repository.createReview(request)
            result.onSuccess { 
                _uiState.update { it.copy(isSendingReview = false, reviewSent = true) }
                loadReviews(serviceId) // Recargar lista
            }.onFailure { error ->
                _uiState.update { 
                    it.copy(isSendingReview = false, reviewError = "No se pudo enviar la reseña")
                }
            }
        }
    }
}
