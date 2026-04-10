package edu.javeriana.fixup.data.network.dto

import com.google.gson.annotations.SerializedName

data class ReviewDto(
    @SerializedName("id")
    val id: String?,
    @SerializedName("user_id")
    val userId: String?,
    @SerializedName("user_name")
    val userName: String?,
    @SerializedName("rating")
    val rating: Double?,
    @SerializedName("comment")
    val comment: String?,
    @SerializedName("created_at")
    val createdAt: String?
)
