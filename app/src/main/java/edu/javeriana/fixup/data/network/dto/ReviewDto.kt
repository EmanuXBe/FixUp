package edu.javeriana.fixup.data.network.dto

import com.google.gson.annotations.SerializedName

data class ReviewDto(
    @SerializedName("id")
    val id: String? = "",
    @SerializedName("rating")
    val rating: Int? = 0,
    @SerializedName("comment")
    val comment: String? = "",
    @SerializedName("date")
    val date: String? = "",
    @SerializedName("authorName")
    val authorName: String? = "",
    @SerializedName("serviceTitle")
    val serviceTitle: String? = "",
    @SerializedName("authorProfileImageUrl")
    val authorProfileImageUrl: String? = "",
    @SerializedName("user")
    val user: ReviewUserDto? = ReviewUserDto(),
    @SerializedName("service")
    val service: ReviewServiceDto? = ReviewServiceDto(),
    @SerializedName("articleId")
    val articleId: String? = "",
    @SerializedName("articleName")
    val articleName: String? = "",
    @SerializedName("userId")
    val userId: String? = "",
    @SerializedName("likedBy")
    val likedBy: List<String>? = emptyList()
)

data class ReviewUserDto(
    @SerializedName("id")
    val id: String? = "",
    @SerializedName("name")
    val name: String? = "",
    @SerializedName("profileImage")
    val profileImage: String? = ""
)

data class ReviewServiceDto(
    @SerializedName("id")
    val id: Int? = 0,
    @SerializedName("title")
    val title: String? = "",
    @SerializedName("categoria")
    val categoria: String? = ""
)

data class ReviewRequestDto(
    @SerializedName("user_id")
    val userId: String = "",
    @SerializedName("service_id")
    val serviceId: String = "",
    @SerializedName("rating")
    val rating: Int = 0,
    @SerializedName("comment")
    val comment: String = ""
)
