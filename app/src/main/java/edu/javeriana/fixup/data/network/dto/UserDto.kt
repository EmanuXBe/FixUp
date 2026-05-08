package edu.javeriana.fixup.data.network.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    val id: String? = "",
    val name: String? = "",
    val email: String? = "",
    val phone: String? = "",
    val address: String? = "",
    val role: String? = "",
    @SerializedName("profile_image_url")
    val profileImageUrl: String? = "",
    val followers: List<String>? = emptyList(),
    val following: List<String>? = emptyList()
)
