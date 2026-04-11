package edu.javeriana.fixup.data.network.dto

data class CategoryDto(
    val id: Int,
    val name: String,
    val iconRes: Int
)

data class PublicationDto(
    val id: String,
    val title: String,
    val priceText: String,
    val description: String? = null,
    val location: String? = null,
    val imageRes: Int = 0,
    val imageUrl: String? = null
)
