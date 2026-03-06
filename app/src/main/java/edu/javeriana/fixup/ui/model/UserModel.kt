package edu.javeriana.fixup.ui.model

data class UserModel(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val role: String,
    val profileImageUrl: String? = null
)
