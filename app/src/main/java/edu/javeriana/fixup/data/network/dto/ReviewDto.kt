package edu.javeriana.fixup.data.network.dto

import com.google.gson.annotations.SerializedName

data class ReviewDto(
    @SerializedName("id")
    val id: String?,
    @SerializedName("user_id")
    val userId: String?,
    @SerializedName("user_name")
    val userName: String?,
    @SerializedName("article_name")
    val articleName: String?,
    @SerializedName("rating")
    val rating: Double?,
    @SerializedName("comment")
    val comment: String?,
    @SerializedName("user_profile_image")
    val userProfileImage: String?,
    @SerializedName("created_at")
    val createdAt: String?
)

data class ReviewRequestDto(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("service_id")
    val serviceId: String,
    @SerializedName("rating")
    val rating: Int,
    @SerializedName("comment")
    val comment: String
)
