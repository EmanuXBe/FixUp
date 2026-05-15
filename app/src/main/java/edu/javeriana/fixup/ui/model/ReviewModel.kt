package edu.javeriana.fixup.ui.model

data class ReviewModel(
    val id: String = "",
    val userId: String = "",
    val serviceId: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val userName: String = "",
    val date: String = "",
    val authorName: String = "",
    val authorProfileImageUrl: String = "",
    val serviceTitle: String = "",
    val likedBy: List<String> = emptyList(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = 0L
)
