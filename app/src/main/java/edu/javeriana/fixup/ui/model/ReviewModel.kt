package edu.javeriana.fixup.ui.model

import com.google.gson.annotations.SerializedName

data class ReviewModel(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("user_id")
    val userId: String = "",
    @SerializedName("user_name")
    val userName: String = "",
    @SerializedName("article_name")
    val articleName: String = "",
    @SerializedName("rating")
    val rating: Int = 0,
    @SerializedName("comment")
    val comment: String = "",
    @SerializedName("user_profile_image")
    val userProfileImage: String = "",
    @SerializedName("created_at")
    val date: String = ""
)
