package edu.javeriana.fixup.data.datasource

import edu.javeriana.fixup.R
import edu.javeriana.fixup.ui.features.feed.PublicationCardModel
import edu.javeriana.fixup.ui.model.PropertyModel

class OtherDataSource {
    fun getPropertyDetail(id: String): PropertyModel {
        return PropertyModel(
            id = id,
            title = "Propiedad Detalle",
            description = "Descripción detallada de la propiedad seleccionada.",
            price = 3000000.0,
            bedrooms = 3,
            bathrooms = 2,
            hasParking = true,
            isFeatured = true,
            isNew = true,
            rating = 4.5,
            reviewCount = 20,
            distanceKm = 5.0,
            imageUrls = emptyList()
        )
    }

    fun getPublicationDetail(id: String): PublicationCardModel {
        return PublicationCardModel(id, R.drawable.sala, "Detalle de Publicación", "Desde $500.000")
    }
}
