package edu.javeriana.fixup.data.datasource

// DTOs (Data Transfer Objects)
data class CategoryDto(val id: Int, val name: String, val iconRes: Int)
data class PublicationDto(val id: String, val title: String, val priceText: String, val imageRes: Int)

/**
 * Contrato del Data Source para Feed.
 */
interface FeedDataSource {
    suspend fun getCategories(): List<CategoryDto>
    suspend fun getPublications(): List<PublicationDto>
}
