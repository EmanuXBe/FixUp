package edu.javeriana.fixup.ui.features.following_feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.javeriana.fixup.data.repository.AuthRepository
import edu.javeriana.fixup.data.repository.FeedRepository
import edu.javeriana.fixup.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowingFeedViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FollowingFeedUiState())
    val uiState: StateFlow<FollowingFeedUiState> = _uiState.asStateFlow()

    init {
        loadFollowingFeed()
    }

    fun loadFollowingFeed() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val currentUserId = authRepository.currentUser?.uid
            if (currentUserId == null) {
                _uiState.update { it.copy(isLoading = false, error = "Usuario no autenticado") }
                return@launch
            }

            userRepository.getUserById(currentUserId).collect { userResult ->
                userResult.onSuccess { user ->
                    val followingIds = user.following
                    if (followingIds.isEmpty()) {
                        _uiState.update { it.copy(isLoading = false, isFollowingEmpty = true, publications = emptyList()) }
                    } else {
                        val publicationsResult = feedRepository.getFollowingPublications(followingIds)
                        publicationsResult.onSuccess { publications ->
                            _uiState.update { it.copy(
                                isLoading = false, 
                                publications = publications,
                                isFollowingEmpty = false
                            ) }
                        }.onFailure { e ->
                            _uiState.update { it.copy(isLoading = false, error = e.message) }
                        }
                    }
                }.onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
            }
        }
    }
}
