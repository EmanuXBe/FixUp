package edu.javeriana.fixup.data.repository

import edu.javeriana.fixup.data.datasource.PropertyDto
import edu.javeriana.fixup.data.datasource.RentDataSource
import edu.javeriana.fixup.ui.model.PropertyModel
import javax.inject.Inject

class RentRepository @Inject constructor(
    private val dataSource: RentDataSource
) {
    suspend fun getProperties(): Result<List<PropertyModel>> {
        return try {
            val dtos = dataSource.getRentProperties()
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Mapper for Rent
fun PropertyDto.toDomain() = PropertyModel(
    id = this.id,
    title = this.title,
    description = this.description,
    price = this.price,
    bedrooms = this.bedrooms,
    bathrooms = this.bathrooms,
    hasParking = this.hasParking,
    isFeatured = this.isFeatured,
    isNew = this.isNew,
    rating = this.rating,
    reviewCount = this.reviewCount,
    distanceKm = this.distanceKm,
    imageUrls = this.imageUrls
)
