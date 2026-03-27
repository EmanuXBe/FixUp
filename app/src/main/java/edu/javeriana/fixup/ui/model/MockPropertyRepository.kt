package edu.javeriana.fixup.ui.model

/**
 * Repositorio ficticio actualizado para el nuevo PropertyModel.
 */
object MockPropertyRepository {

    fun getProperties(): List<PropertyModel> {
        return listOf(
            PropertyModel(
                id = "1",
                title = "Apartamento en Chapinero Alto",
                description = "Hermoso apartamento con vista panorámica.",
                price = 2800000.0,
                location = "Chapinero Alto",
                imageUrl = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&q=80&w=800"
            ),
            PropertyModel(
                id = "2",
                title = "Estudio Moderno en Usaquén",
                description = "Acogedor estudio para estrenar.",
                price = 1950000.0,
                location = "Usaquén",
                imageUrl = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&q=80&w=800"
            )
        )
    }
}
