package edu.javeriana.fixup.ui.features.property_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.javeriana.fixup.data.repository.RentRepository
import edu.javeriana.fixup.ui.model.ReviewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PropertyDetailViewModel @Inject constructor(
    private val repository: RentRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PropertyDetailUiState())
    val uiState: StateFlow<PropertyDetailUiState> = _uiState.asStateFlow()

    private var currentPropertyId: Int? = null

    fun loadProperty(propertyId: String?) {
        val idAsInt = propertyId?.toIntOrNull()
        if (idAsInt == null) {
            _uiState.update { it.copy(error = "ID de propiedad inválido") }
            return
        }
        currentPropertyId = idAsInt

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val propertyResult = repository.getPropertyById(idAsInt)
            
            propertyResult.onSuccess { property ->
                val reviewsResult = repository.getReviewsByArticleId(idAsInt)
                
                _uiState.update { 
                    it.copy(
                        property = property,
                        reviews = reviewsResult.getOrDefault(emptyList()).sortedByDescending { r -> r.date },
                        isLoading = false,
                        error = null
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar la propiedad: ${error.message}"
                    )
                }
            }
        }
    }

    fun onCommentChange(comment: String) {
        _uiState.update { it.copy(newReviewComment = comment) }
    }

    fun onRatingChange(rating: Int) {
        _uiState.update { it.copy(newReviewRating = rating) }
    }

    fun postReview() {
        val propertyId = currentPropertyId ?: return
        val comment = _uiState.value.newReviewComment
        val rating = _uiState.value.newReviewRating

        if (comment.isBlank()) return

        _uiState.update { it.copy(isPostingReview = true) }

        viewModelScope.launch {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val currentDate = dateFormat.format(Date())

            val newReview = ReviewModel(
                articleId = propertyId,
                userName = "Usuario FixUp", // Podría obtenerse de un AuthRepository si estuviera disponible
                rating = rating,
                comment = comment,
                date = currentDate
            )

            val result = repository.createReview(newReview)

            result.onSuccess { postedReview ->
                _uiState.update { state ->
                    state.copy(
                        reviews = (listOf(postedReview) + state.reviews),
                        newReviewComment = "",
                        newReviewRating = 5,
                        isPostingReview = false
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(
                    isPostingReview = false,
                    error = "Error al publicar comentario: ${error.message}"
                )}
            }
        }
    }
}
