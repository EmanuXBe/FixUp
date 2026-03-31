package edu.javeriana.fixup.data.datasource

import edu.javeriana.fixup.R
import edu.javeriana.fixup.ui.features.feed.PublicationCardModel
import edu.javeriana.fixup.ui.model.PropertyModel

class OtherDataSource {
    fun getPropertyDetail(id: String): PropertyModel {
        return PropertyModel(
            id = id.toIntOrNull(),
            title = "Propiedad Detalle",
            description = "Descripción detallada de la propiedad seleccionada.",
            price = 3000000.0,
            location = "Ubicación Predeterminada",
            imageUrl = null
        )
    }

    fun getPublicationDetail(id: String): PublicationCardModel {
        return PublicationCardModel(
            id = id,
            imageUrl = R.drawable.sala,
            title = "Detalle de Publicación",
            price = "Desde $500.000"
        )
    }
}
