package edu.javeriana.fixup.data.network.model

import com.google.gson.annotations.SerializedName

data class ReviewDto(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("service_id")
    val serviceId: Int,
    @SerializedName("rating")
    val rating: Double,
    @SerializedName("comment")
    val comment: String
)

data class ReviewRequestDto(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("service_id")
    val serviceId: Int,
    @SerializedName("rating")
    val rating: Double,
    @SerializedName("comment")
    val comment: String
)
