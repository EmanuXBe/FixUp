package edu.javeriana.fixup.ui.features.publication_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.javeriana.fixup.data.network.dto.ReviewRequestDto
import edu.javeriana.fixup.data.repository.FeedRepository
import edu.javeriana.fixup.data.repository.ReviewRepository
import edu.javeriana.fixup.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PublicationDetailViewModel @Inject constructor(
    private val repository: FeedRepository,
    private val reviewRepository: ReviewRepository,
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
            reviewRepository.getReviewsByServiceId(serviceId.toString()).collect { result ->
                result.onSuccess { reviews ->
                    _uiState.update { it.copy(reviews = reviews, isLoading = false) }
                }.onFailure {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun sendReview(rating: Int, comment: String) {
        val serviceId = _uiState.value.publication?.id ?: return
        val serviceTitle = _uiState.value.publication?.title ?: ""
        val currentUser = authRepository.currentUser

        if (currentUser == null) {
            _uiState.update { it.copy(reviewError = "Debes iniciar sesión para dejar una reseña") }
            return
        }
        
        _uiState.update { it.copy(isSendingReview = true, reviewError = null, reviewSent = false) }
        
        viewModelScope.launch {
            val result = reviewRepository.createReview(serviceId, serviceTitle, rating, comment)
            result.onSuccess { 
                _uiState.update { it.copy(isSendingReview = false, reviewSent = true) }
                loadReviews(serviceId.toIntOrNull() ?: 0) // Recargar lista
            }.onFailure { error ->
                _uiState.update { 
                    it.copy(isSendingReview = false, reviewError = "No se pudo enviar la reseña")
                }
            }
        }
    }

    fun getCurrentUserId(): String? = authRepository.currentUser?.uid

    fun toggleLikeReview(reviewId: String) {
        val userId = getCurrentUserId() ?: return
        val review = _uiState.value.reviews.find { it.id == reviewId } ?: return
        val isLiked = review.likedBy.contains(userId)

        // Optimistic update
        val oldReviews = _uiState.value.reviews
        val newReviews = oldReviews.map {
            if (it.id == reviewId) {
                val newLikedBy = if (isLiked) {
                    it.likedBy.filter { id -> id != userId }
                } else {
                    it.likedBy + userId
                }
                it.copy(likedBy = newLikedBy)
            } else it
        }

        _uiState.update { it.copy(reviews = newReviews) }

        viewModelScope.launch {
            reviewRepository.toggleLike(reviewId, isLiked).onFailure {
                // Rollback on failure
                _uiState.update { it.copy(reviews = oldReviews) }
            }
        }
    }
}
