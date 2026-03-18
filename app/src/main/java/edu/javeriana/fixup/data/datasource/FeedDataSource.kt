package edu.javeriana.fixup.data.datasource

import edu.javeriana.fixup.R
import edu.javeriana.fixup.ui.features.feed.CategoryItemModel
import edu.javeriana.fixup.ui.features.feed.PublicationCardModel

class FeedDataSource {
    fun getCategories(): List<CategoryItemModel> {
        return listOf(
            CategoryItemModel(R.drawable.bano, "Baños"),
            CategoryItemModel(R.drawable.luz, "Iluminación"),
            CategoryItemModel(R.drawable.cocina, "Cocina"),
            CategoryItemModel(R.drawable.exterior, "Exterior")
        )
    }

    fun getPublications(): List<PublicationCardModel> {
        return listOf(
            PublicationCardModel("1", R.drawable.sala, "Salas a tu medida", "Desde $300.000"),
            PublicationCardModel("2", R.drawable.comedor, "¡Arma tu comedor!", "Desde $450.000"),
            PublicationCardModel("3", R.drawable.bano, "Renovación de Baño", "Desde $800.000"),
            PublicationCardModel("4", R.drawable.cocina, "Cocina Integral Moderna", "Desde $1.200.000")
        )
    }
}
