package edu.javeriana.fixup.ui.model

data class ServiceModel(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val providerId: String = "",
    val imageUrl: String = "",
    val rating: Double = 0.0
)
