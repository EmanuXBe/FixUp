package edu.javeriana.fixup.ui.features.user_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.javeriana.fixup.data.repository.AuthRepository
import edu.javeriana.fixup.data.repository.NotificationRepository
import edu.javeriana.fixup.data.repository.ReviewRepository
import edu.javeriana.fixup.data.repository.UserRepository
import edu.javeriana.fixup.ui.model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val reviewRepository: ReviewRepository,
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    fun getCurrentUserId(): String? = authRepository.currentUser?.uid

    fun toggleFollow() {
        val currentUserId = getCurrentUserId() ?: return
        val targetUser = _uiState.value.user ?: return
        val isCurrentlyFollowing = targetUser.followers.contains(currentUserId)

        // Optimistic update
        val oldUser = targetUser
        val newFollowers = if (isCurrentlyFollowing) {
            targetUser.followers.filter { it != currentUserId }
        } else {
            targetUser.followers + currentUserId
        }
        val newUser = targetUser.copy(followers = newFollowers)
        _uiState.update { it.copy(user = newUser) }

        viewModelScope.launch {
            userRepository.toggleFollow(currentUserId, targetUser.id, isCurrentlyFollowing).onSuccess {
                if (!isCurrentlyFollowing) {
                    val name = authRepository.currentUser?.displayName
                        ?: authRepository.currentUser?.email?.substringBefore("@")
                        ?: "Usuario"
                    notificationRepository.notifyFollow(targetUser.id, name)
                }
            }.onFailure {
                _uiState.update { it.copy(user = oldUser) }
            }
        }
    }

    fun loadFollowersUsers() {
        val targetUser = _uiState.value.user ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isFollowListLoading = true) }
            userRepository.getUsersByIds(targetUser.followers).onSuccess { users ->
                _uiState.update { it.copy(followersUsers = users, isFollowListLoading = false) }
            }.onFailure { error ->
                _uiState.update { it.copy(isFollowListLoading = false, error = error.message) }
            }
        }
    }

    fun loadFollowingUsers() {
        val targetUser = _uiState.value.user ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isFollowListLoading = true) }
            userRepository.getUsersByIds(targetUser.following).onSuccess { users ->
                _uiState.update { it.copy(followingUsers = users, isFollowListLoading = false) }
            }.onFailure { error ->
                _uiState.update { it.copy(isFollowListLoading = false, error = error.message) }
            }
        }
    }

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
            reviewRepository.toggleLike(reviewId, isLiked).onSuccess {
                if (!isLiked) {
                    val name = authRepository.currentUser?.displayName
                        ?: authRepository.currentUser?.email?.substringBefore("@")
                        ?: "Usuario"
                    notificationRepository.notifyLike(reviewId, userId, name)
                }
            }.onFailure {
                _uiState.update { it.copy(reviews = oldReviews) }
            }
        }
    }

    fun loadUserProfile(userId: String?) {
        if (userId == null) {
            _uiState.update { it.copy(error = "ID de usuario no proporcionado") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            userRepository.getUserById(userId).collect { result ->
                result.onSuccess { user ->
                    viewModelScope.launch {
                        fetchReviews(userId, user)
                    }
                }.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            }
        }
    }

    private suspend fun fetchReviews(userId: String, user: UserModel) {
        reviewRepository.getReviewsByUserId(userId).collect { result ->
            result.onSuccess { reviews ->
                _uiState.update { 
                    it.copy(
                        user = user,
                        reviews = reviews,
                        isLoading = false,
                        error = null
                    )
                }
            }.onFailure { error ->
                _uiState.update { 
                    it.copy(
                        user = user,
                        isLoading = false,
                        error = "Error al cargar reseñas: ${error.message}"
                    )
                }
            }
        }
    }
}
