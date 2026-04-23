package edu.javeriana.fixup.data.network.dto

import com.google.gson.annotations.SerializedName

data class ReviewDto(
    @SerializedName("id")
    val id: String?,
    @SerializedName("rating")
    val rating: Int?,
    @SerializedName("comment")
    val comment: String?,
    @SerializedName("date")
    val date: String?,
    @SerializedName("authorName")
    val authorName: String?,
    @SerializedName("authorProfileImageUrl")
    val authorProfileImageUrl: String?,
    @SerializedName("user")
    val user: ReviewUserDto?,
    @SerializedName("service")
    val service: ReviewServiceDto?,
    @SerializedName("articleId")
    val articleId: String? = null,
    @SerializedName("articleName")
    val articleName: String? = null,
    @SerializedName("userId")
    val userId: String? = null,
    @SerializedName("likedBy")
    val likedBy: List<String>? = emptyList()
)

data class ReviewUserDto(
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("profileImage")
    val profileImage: String?
)

data class ReviewServiceDto(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("categoria")
    val categoria: String?
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
