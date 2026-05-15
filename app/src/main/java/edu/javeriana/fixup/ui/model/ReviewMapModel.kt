package edu.javeriana.fixup.ui.model

data class ReviewMapModel(
    val id: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val authorName: String = "",
    val comment: String = "",
    val rating: Int = 0,
    val serviceTitle: String = "",
    val timestamp: Long = 0L
)
