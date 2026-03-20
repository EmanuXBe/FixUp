package edu.javeriana.fixup.data.datasource

import edu.javeriana.fixup.ui.model.PropertyModel

class RentDataSource {
    fun getRentProperties(): List<PropertyModel> {
        val baseUrl = "https://firebasestorage.googleapis.com/v0/b/fixup-f2128.firebasestorage.app/o/properties%2F"
        val suffix = "?alt=media"

        return listOf(
            PropertyModel(
                id = "1",
                title = "Apartamento Chapinero",
                description = "Moderno apartamento con vista a la ciudad.",
                price = 2500000.0,
                bedrooms = 2,
                bathrooms = 1,
                hasParking = true,
                isFeatured = true,
                isNew = false,
                rating = 4.8,
                reviewCount = 12,
                distanceKm = 1.2,
                imageUrls = listOf(
                    "${baseUrl}chapi.jpeg$suffix",
                    "${baseUrl}apto2.jpeg$suffix",
                    "${baseUrl}apto3.jpeg$suffix"
                )
            ),
            PropertyModel(
                id = "2",
                title = "Casa Campestre",
                description = "Amplia casa rodeada de naturaleza.",
                price = 4200000.0,
                bedrooms = 4,
                bathrooms = 3,
                hasParking = true,
                isFeatured = false,
                isNew = true,
                rating = 4.9,
                reviewCount = 8,
                distanceKm = 15.0,
                imageUrls = listOf(
                    "${baseUrl}apto4.jpeg$suffix",
                    "${baseUrl}apto5.jpeg$suffix"
                )
            )
        )
    }
}
