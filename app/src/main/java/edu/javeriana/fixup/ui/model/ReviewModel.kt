package edu.javeriana.fixup.ui.model

import com.google.gson.annotations.SerializedName

data class ReviewModel(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("user_id")
    val userId: String = "",
    @SerializedName("user_name")
    val userName: String = "",
    @SerializedName("rating")
    val rating: Double = 0.0,
    @SerializedName("comment")
    val comment: String = "",
    @SerializedName("created_at")
    val date: String = "",
    @SerializedName("service_title")
    val serviceTitle: String? = null
)
