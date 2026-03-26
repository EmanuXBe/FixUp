package edu.javeriana.fixup.data.datasource

// DTO for Rent
data class PropertyDto(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val bedrooms: Int,
    val bathrooms: Int,
    val hasParking: Boolean,
    val isFeatured: Boolean,
    val isNew: Boolean,
    val rating: Double,
    val reviewCount: Int,
    val distanceKm: Double,
    val imageUrls: List<String>
)

/**
 * Contrato del Data Source para Rent.
 */
interface RentDataSource {
    suspend fun getRentProperties(): List<PropertyDto>
}
