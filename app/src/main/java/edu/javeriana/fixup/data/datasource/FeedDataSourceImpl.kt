package edu.javeriana.fixup.data.datasource

import edu.javeriana.fixup.R
import javax.inject.Inject

/**
 * Implementación concreta de FeedDataSource.
 */
class FeedDataSourceImpl @Inject constructor() : FeedDataSource {
    override suspend fun getCategories(): List<CategoryDto> {
        return listOf(
            CategoryDto(1, "Baños", R.drawable.bano),
            CategoryDto(2, "Iluminación", R.drawable.luz),
            CategoryDto(3, "Cocina", R.drawable.cocina),
            CategoryDto(4, "Exterior", R.drawable.exterior)
        )
    }

    override suspend fun getPublications(): List<PublicationDto> {
        return listOf(
            PublicationDto("1", "Salas a tu medida", "Desde $300.000", R.drawable.sala),
            PublicationDto("2", "¡Arma tu comedor!", "Desde $450.000", R.drawable.comedor),
            PublicationDto("3", "Renovación de Baño", "Desde $800.000", R.drawable.bano),
            PublicationDto("4", "Cocina Integral Moderna", "Desde $1.200.000", R.drawable.cocina)
        )
    }
}
