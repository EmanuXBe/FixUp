package edu.javeriana.fixup.ui.features.publication_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.javeriana.fixup.data.datasource.ReviewRequest
import edu.javeriana.fixup.data.repository.FeedRepository
import edu.javeriana.fixup.ui.model.ReviewModel
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

    private var currentServiceId: Int = 0

    fun loadPublication(publicationId: String?) {
        val id = publicationId?.toIntOrNull()
        if (id == null) {
            _uiState.update { it.copy(error = "ID de publicación inválido", isLoading = false) }
            return
        }
        currentServiceId = id
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            repository.getPublicationById(id)
                .onSuccess { publication ->
                    _uiState.update {
                        it.copy(publication = publication, description = publication.description ?: "Sin descripción disponible", error = null)
                    }
                    loadReviews(id)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = "Error al cargar: ${error.message}") }
                }
        }
    }

    private fun loadReviews(serviceId: Int) {
        viewModelScope.launch {
            repository.getReviewsByServiceId(serviceId)
                .onSuccess { reviews -> _uiState.update { it.copy(reviews = reviews, isLoading = false) } }
                .onFailure { _uiState.update { it.copy(isLoading = false) } }
        }
    }

    // ── Crear review ───────────────────────────────────────────
    fun sendReview(rating: Int, comment: String) {
        _uiState.update { it.copy(isSendingReview = true, reviewError = null, reviewSent = false) }
        viewModelScope.launch {
            val request = ReviewRequest(userId = 1, serviceId = currentServiceId, rating = rating, comment = comment)
            repository.createReview(request)
                .onSuccess {
                    _uiState.update { it.copy(isSendingReview = false, reviewSent = true) }
                    loadReviews(currentServiceId)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isSendingReview = false, reviewError = "No se pudo enviar la reseña: ${error.message}") }
                }
        }
    }

    // ── Editar review ──────────────────────────────────────────
    fun openEditDialog(review: ReviewModel) {
        _uiState.update { it.copy(editingReview = review, showEditDialog = true) }
    }

    fun closeEditDialog() {
        _uiState.update { it.copy(editingReview = null, showEditDialog = false) }
    }

    fun updateReview(reviewId: String, rating: Int, comment: String) {
        _uiState.update { it.copy(isSendingReview = true) }
        viewModelScope.launch {
            val request = ReviewRequest(userId = 1, serviceId = currentServiceId, rating = rating, comment = comment)
            repository.updateReview(reviewId, request)
                .onSuccess {
                    _uiState.update { it.copy(isSendingReview = false, showEditDialog = false, editingReview = null, reviewSent = true) }
                    loadReviews(currentServiceId)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isSendingReview = false, reviewError = "No se pudo editar: ${error.message}") }
                }
        }
    }

    // ── Eliminar review ────────────────────────────────────────
    fun deleteReview(reviewId: String) {
        viewModelScope.launch {
            repository.deleteReview(reviewId)
                .onSuccess { loadReviews(currentServiceId) }
                .onFailure { error ->
                    _uiState.update { it.copy(reviewError = "No se pudo eliminar: ${error.message}") }
                }
        }
    }
}