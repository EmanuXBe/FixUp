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
    val description: String? = null,
    val location: String? = null,
    val imageRes: Int = 0,
    val imageUrl: String? = null,
    val authorId: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val createdAt: Long? = null,
    val likeCount: Int = 0,
    val likedByCurrentUser: Boolean = false
)
