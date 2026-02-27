package edu.javeriana.fixup.ui.model

/**
 * Repositorio ficticio para gestionar la obtención de propiedades inmobiliarias en Bogotá.
 */
object MockPropertyRepository {

    /**
     * Devuelve una lista de 5 propiedades realistas ubicadas en Bogotá, Colombia.
     *
     * @return List de [PropertyModel].
     */
    fun getProperties(): List<PropertyModel> {
        return listOf(
            PropertyModel(
                id = "1",
                title = "Apartamento en Chapinero Alto",
                description = "Hermoso apartamento con vista panorámica a la ciudad, acabados de lujo y excelente iluminación natural.",
                price = 2800000.0,
                bedrooms = 2,
                bathrooms = 2,
                hasParking = true,
                isFeatured = true,
                isNew = false,
                rating = 4.8,
                reviewCount = 15,
                distanceKm = 1.2,
                imageUrl = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&q=80&w=800"
            ),
            PropertyModel(
                id = "2",
                title = "Estudio Moderno en Usaquén",
                description = "Acogedor estudio para estrenar, ubicado en el corazón de Usaquén, cerca a centros comerciales y parques.",
                price = 1950000.0,
                bedrooms = 1,
                bathrooms = 1,
                hasParking = false,
                isFeatured = false,
                isNew = true,
                rating = 4.5,
                reviewCount = 8,
                distanceKm = 4.5,
                imageUrl = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&q=80&w=800"
            ),
            PropertyModel(
                id = "3",
                title = "Casa Histórica en La Candelaria",
                description = "Espaciosa casa de estilo colonial restaurada, ideal para oficinas o vivienda con ambiente cultural.",
                price = 4200000.0,
                bedrooms = 4,
                bathrooms = 3,
                hasParking = true,
                isFeatured = true,
                isNew = false,
                rating = 4.9,
                reviewCount = 22,
                distanceKm = 0.5,
                imageUrl = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&q=80&w=800"
            ),
            PropertyModel(
                id = "4",
                title = "Apartamento en Cedritos",
                description = "Amplio apartamento familiar con balcón, zonas sociales completas y seguridad las 24 horas.",
                price = 2300000.0,
                bedrooms = 3,
                bathrooms = 2,
                hasParking = true,
                isFeatured = false,
                isNew = false,
                rating = 4.2,
                reviewCount = 30,
                distanceKm = 8.0,
                imageUrl = "https://images.unsplash.com/photo-1493809842364-78817add7ffb?auto=format&fit=crop&q=80&w=800"
            ),
            PropertyModel(
                id = "5",
                title = "Penthouse en Rosales",
                description = "Exclusivo penthouse con terraza privada, ascensor directo y acabados importados.",
                price = 8500000.0,
                bedrooms = 3,
                bathrooms = 4,
                hasParking = true,
                isFeatured = true,
                isNew = false,
                rating = 5.0,
                reviewCount = 5,
                distanceKm = 2.5,
                imageUrl = "https://images.unsplash.com/photo-1600607687920-4e2a09cf159d?auto=format&fit=crop&q=80&w=800"
            )
        )
    }
}
