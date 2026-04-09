package edu.javeriana.fixup.ui.model

data class ReviewModel(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val date: String = ""
)
