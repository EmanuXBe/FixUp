package edu.javeriana.fixup.ui.model

import androidx.annotation.DrawableRes

data class PublicationUiModel(
    @DrawableRes val imageRes: Int,
    val category: String,
    val title: String,
    val price: String
)