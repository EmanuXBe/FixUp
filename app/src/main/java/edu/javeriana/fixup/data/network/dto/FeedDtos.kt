package edu.javeriana.fixup.data.network.dto

data class CategoryDto(
    val id: Int = 0,
    val name: String = "",
    val iconRes: Int = 0
)

data class PublicationDto(
    val id: String = "",
    val title: String = "",
    val priceText: String = "",
    val description: String? = "",
    val location: String? = "",
    val imageRes: Int = 0,
    val imageUrl: String? = "",
    val authorId: String? = ""
)
