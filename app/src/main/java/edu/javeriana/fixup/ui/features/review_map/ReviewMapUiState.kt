package edu.javeriana.fixup.ui.features.review_map

import edu.javeriana.fixup.ui.model.ArticleMapModel

data class ReviewMapUiState(
    val isLoading: Boolean = true,
    val articles: List<ArticleMapModel> = emptyList(),
    val selectedArticle: ArticleMapModel? = null,
    val error: String? = null
)
