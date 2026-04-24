package edu.javeriana.fixup.ui.features.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.javeriana.fixup.data.repository.AuthRepository
import edu.javeriana.fixup.data.repository.ProfileRepository
import edu.javeriana.fixup.data.repository.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val defaultImageUrl = "https://firebasestorage.googleapis.com/v0/b/fixup-f2128.firebasestorage.app/o/WhatsApp%20Image%202026-03-18%20at%205.27.50%20PM.jpeg?alt=media&token=7d9a7e23-31b0-4f0a-b705-c7c9d71abe64"

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            name = profileRepository.currentUser?.email?.substringBefore("@") ?: "Usuario",
            email = profileRepository.currentUser?.email ?: "",
            address = "Bogotá, Colombia",
            phone = "Sin número",
            role = "Cliente",
            profileImageUrl = profileRepository.currentUser?.photoUrl?.toString() ?: defaultImageUrl,
            isLoading = false
        )
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
        loadUserReviews()
    }

    private fun loadUserData() {
        val user = profileRepository.currentUser ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            profileRepository.getUserData(user.uid).onSuccess { data ->
                if (data != null) {
                    _uiState.update {
                        it.copy(
                            name    = data["name"] as? String ?: user.displayName ?: user.email?.substringBefore("@") ?: "Usuario",
                            phone   = data["phone"] as? String ?: "Sin número",
                            address = data["address"] as? String ?: "Sin dirección",
                            role    = data["role"] as? String ?: "Cliente",
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }

            // Cargamos contadores de seguidores/siguiendo
            profileRepository.getFollowCounts(user.uid).onSuccess { (followers, following) ->
                _uiState.update {
                    it.copy(followersCount = followers, followingCount = following)
                }
            }
        }
    }

    fun updateProfileInfo(name: String, email: String, phone: String, address: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            profileRepository.updateProfileData(name, email, phone, address).onSuccess {
                _uiState.update {
                    it.copy(name = name, email = email, phone = phone, address = address, isLoading = false)
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    private fun loadUserReviews() {
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            profileRepository.getReviewsByUserId(uid).onSuccess { reviews ->
                _uiState.update { it.copy(reviews = reviews, isLoading = false) }
            }.onFailure { error ->
                _uiState.update { it.copy(reviews = emptyList(), isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun deleteReview(reviewId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            profileRepository.deleteReview(reviewId).onSuccess {
                _uiState.update { state ->
                    state.copy(reviews = state.reviews.filter { it.id != reviewId }, isLoading = false)
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun updateReview(reviewId: String, rating: Int, comment: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            profileRepository.updateReview(reviewId, rating, comment).onSuccess {
                _uiState.update { state ->
                    state.copy(
                        reviews = state.reviews.map {
                            if (it.id == reviewId) it.copy(rating = rating, comment = comment) else it
                        },
                        isLoading = false
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun uploadProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            profileRepository.updateProfileImage(imageUri).onSuccess { newUrl ->
                val freshUrl = if (newUrl.contains("?")) "$newUrl&t=${System.currentTimeMillis()}" else "$newUrl?t=${System.currentTimeMillis()}"
                _uiState.update { it.copy(profileImageUrl = freshUrl, isLoading = false) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun resetErrorMessage() { _uiState.update { it.copy(errorMessage = null) } }
    fun getCurrentUserId(): String? = authRepository.currentUser?.uid

    fun toggleLikeReview(reviewId: String) {
        val userId = authRepository.currentUser?.uid ?: return
        val review = _uiState.value.reviews.find { it.id == reviewId } ?: return
        val isLiked = review.likedBy.contains(userId)
        val oldReviews = _uiState.value.reviews
        val newReviews = oldReviews.map {
            if (it.id == reviewId) {
                val newLikedBy = if (isLiked) it.likedBy.filter { id -> id != userId } else it.likedBy + userId
                it.copy(likedBy = newLikedBy)
            } else it
        }
        _uiState.update { it.copy(reviews = newReviews) }
        viewModelScope.launch {
            reviewRepository.toggleLike(reviewId, isLiked).onFailure {
                _uiState.update { it.copy(reviews = oldReviews) }
            }
        }
    }
}