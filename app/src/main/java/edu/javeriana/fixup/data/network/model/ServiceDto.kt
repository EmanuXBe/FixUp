package edu.javeriana.fixup.data.network.model

import com.google.gson.annotations.SerializedName

data class ServiceDto(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("price")
    val price: Double? = null,
    @SerializedName("category")
    val category: String? = null,
    @SerializedName("provider_id")
    val providerId: String? = null,
    @SerializedName("image_url")
    val imageUrl: String? = null,
    @SerializedName("rating")
    val rating: Double? = null
)
