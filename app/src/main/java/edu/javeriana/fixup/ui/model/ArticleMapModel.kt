package edu.javeriana.fixup.ui.model

data class ArticleMapModel(
    val id: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val title: String = "",
    val price: String = "",
    val category: String = "",
    val authorId: String = "",
    val location: String = ""
)
