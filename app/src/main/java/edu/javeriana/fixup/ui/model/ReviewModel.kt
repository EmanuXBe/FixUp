package edu.javeriana.fixup.ui.model

data class ReviewModel(
    val id: Int = 0,
    val userId: String = "",
    val serviceId: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val userName: String = "",
    val date: String = "",
    val authorName: String = "",
    val authorProfileImageUrl: String = "",
    val serviceTitle: String = ""
)
