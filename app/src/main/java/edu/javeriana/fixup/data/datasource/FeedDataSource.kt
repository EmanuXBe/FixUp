package edu.javeriana.fixup.data.datasource

import android.net.Uri
import edu.javeriana.fixup.ui.model.PropertyModel

// DTOs (Data Transfer Objects)
data class CategoryDto(val id: Int, val name: String, val iconRes: Int)
data class PublicationDto(
    val id: String,
    val title: String,
    val priceText: String,
    val description: String? = null,
    val location: String? = null,
    val imageRes: Int = 0,
    val imageUrl: String? = null
)

/**
 * Contrato del Data Source para Feed.
 */
interface FeedDataSource {
    suspend fun getCategories(): List<CategoryDto>
    suspend fun getPublications(): List<PublicationDto>
    suspend fun getPublicationById(id: Int): PublicationDto
    suspend fun createPublication(property: PropertyModel, imageUri: Uri): PropertyModel
}
