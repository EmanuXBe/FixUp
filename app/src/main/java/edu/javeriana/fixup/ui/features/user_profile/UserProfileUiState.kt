package edu.javeriana.fixup.ui.features.user_profile

import edu.javeriana.fixup.ui.model.ReviewModel
import edu.javeriana.fixup.ui.model.UserModel

data class UserProfileUiState(
    val user: UserModel? = null,
    val reviews: List<ReviewModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
