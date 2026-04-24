package edu.javeriana.fixup.ui.features.profile

import edu.javeriana.fixup.ui.model.ReviewModel

data class ProfileUiState(
    val name: String             = "",
    val email: String            = "",
    val phone: String            = "",
    val address: String          = "",
    val role: String             = "",
    val profileImageUrl: String? = null,
    val reviews: List<ReviewModel> = emptyList(),
    val isLoading: Boolean       = false,
    val errorMessage: String?    = null,
    // contadores de seguidores/siguiendo
    val followersCount: Int      = 0,
    val followingCount: Int      = 0,

    )