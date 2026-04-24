package edu.javeriana.fixup.data.network.dto

import com.google.gson.annotations.SerializedName
import edu.javeriana.fixup.ui.model.FollowUser

data class UserDto(
    @SerializedName("id")
    val id: String? = "",
    @SerializedName("name")
    val name: String? = "",
    @SerializedName("email")
    val email: String? = "",
    @SerializedName("phone")
    val phone: String? = "",
    @SerializedName("address")
    val address: String? = "",
    @SerializedName("role")
    val role: String? = "",
    @SerializedName("profile_image_url")
    val profileImageUrl: String? = "",
    @SerializedName("followers")
    val followers: List<String>? = emptyList(),
    @SerializedName("following")
    val following: List<String>? = emptyList()
)

fun UserDto.toFollowUser(): FollowUser = FollowUser(
    uid         = id.orEmpty(),
    displayName = name.orEmpty(),
    username    = email?.substringBefore("@").orEmpty(),
    photoUrl    = profileImageUrl
)
