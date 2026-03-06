package edu.javeriana.fixup.ui.features.feed

import edu.javeriana.fixup.R

data class FeedUiState(
    val searchQuery: String = "",
    val categories: List<CategoryItemModel> = listOf(
        CategoryItemModel(R.drawable.bano, "Baños"),
        CategoryItemModel(R.drawable.luz, "Iluminación"),
        CategoryItemModel(R.drawable.cocina, "Cocina"),
        CategoryItemModel(R.drawable.exterior, "Exterior")
    ),
    val publications: List<PublicationCardModel> = listOf(
        PublicationCardModel("1", R.drawable.sala, "Salas a tu medida", "Desde $300.000"),
        PublicationCardModel("2", R.drawable.comedor, "¡Arma tu comedor!", "Desde $450.000"),
        PublicationCardModel("3", R.drawable.bano, "Renovación de Baño", "Desde $800.000"),
        PublicationCardModel("4", R.drawable.cocina, "Cocina Integral Moderna", "Desde $1.200.000")
    ),
    val isLoading: Boolean = false
)

data class CategoryItemModel(val imageRes: Int, val title: String)
data class PublicationCardModel(val id: String, val imageRes: Int, val title: String, val price: String)
