package edu.javeriana.fixup.data.datasource.interfaces

import edu.javeriana.fixup.ui.model.ArticleMapModel

interface ArticleMapDataSource {
    suspend fun getArticlesWithLocation(): Result<List<ArticleMapModel>>
}
