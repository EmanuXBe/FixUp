package edu.javeriana.fixup.ui.features.profile

import edu.javeriana.fixup.ui.model.ReviewModel

data class ProfileUiState(
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val role: String = "",
    val profileImageUrl: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val reviews: List<ReviewModel> = emptyList(),
    val followersCount: Long = 0,
    val followingCount: Long = 0
)
