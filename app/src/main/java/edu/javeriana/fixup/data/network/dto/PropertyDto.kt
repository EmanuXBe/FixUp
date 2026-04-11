package edu.javeriana.fixup.data.network.dto

import com.google.gson.annotations.SerializedName

data class PropertyDto(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("price")
    val price: Double? = null,
    @SerializedName("location")
    val location: String? = null,
    @SerializedName("image_url")
    val imageUrl: String? = null
)
