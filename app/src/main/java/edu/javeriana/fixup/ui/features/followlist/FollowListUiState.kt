package edu.javeriana.fixup.ui.features.followlist

import edu.javeriana.fixup.ui.model.FollowUser

enum class FollowListType { FOLLOWERS, FOLLOWING }

data class FollowListUiState(
    val isLoading: Boolean       = false,
    val users: List<FollowUser>  = emptyList(),
    val errorMessage: String?    = null,
    val listType: FollowListType  = FollowListType.FOLLOWERS
)