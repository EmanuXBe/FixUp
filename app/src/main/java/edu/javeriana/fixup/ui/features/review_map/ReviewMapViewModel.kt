package edu.javeriana.fixup.ui.features.review_map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.javeriana.fixup.data.repository.ReviewMapRepository
import edu.javeriana.fixup.ui.model.ReviewMapModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewMapViewModel @Inject constructor(
    private val repository: ReviewMapRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewMapUiState())
    val uiState: StateFlow<ReviewMapUiState> = _uiState.asStateFlow()

    init {
        loadReviews()
    }

    fun loadReviews() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getReviewsFromLast24h()
                .onSuccess { reviews ->
                    _uiState.update { it.copy(isLoading = false, reviews = reviews) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun onMarkerClick(review: ReviewMapModel) {
        _uiState.update { it.copy(selectedReview = review) }
    }

    fun onDismissInfo() {
        _uiState.update { it.copy(selectedReview = null) }
    }
}
