package edu.javeriana.fixup.ui.features.followlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.javeriana.fixup.data.repository.UserRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class FollowListViewModel @Inject constructor(
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: String = checkNotNull(savedStateHandle["userId"])
    private val typeArg: String = checkNotNull(savedStateHandle["listType"])

    private val _uiState = MutableStateFlow(FollowListUiState())
    val uiState: StateFlow<FollowListUiState> = _uiState.asStateFlow()

    init {
        val type = if (typeArg == "following") FollowListType.FOLLOWING else FollowListType.FOLLOWERS
        _uiState.update { it.copy(listType = type) }
        loadList(type)
    }

    private fun loadList(type: FollowListType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = when (type) {
                FollowListType.FOLLOWERS -> userRepository.getFollowers(userId)
                FollowListType.FOLLOWING -> userRepository.getFollowing(userId)
            }
            result.onSuccess { users ->
                _uiState.update { it.copy(isLoading = false, users = users) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }
}