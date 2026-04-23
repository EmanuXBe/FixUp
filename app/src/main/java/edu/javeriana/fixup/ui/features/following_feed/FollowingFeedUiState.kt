package edu.javeriana.fixup.ui.features.following_feed

import edu.javeriana.fixup.ui.features.feed.PublicationCardModel

data class FollowingFeedUiState(
    val publications: List<PublicationCardModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFollowingEmpty: Boolean = false
)
