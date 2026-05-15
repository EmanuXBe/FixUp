package edu.javeriana.fixup.data.repository

import edu.javeriana.fixup.data.datasource.interfaces.ArticleMapDataSource
import edu.javeriana.fixup.ui.model.ArticleMapModel
import javax.inject.Inject

class ArticleMapRepository @Inject constructor(
    private val dataSource: ArticleMapDataSource
) {
    suspend fun getArticlesWithLocation(): Result<List<ArticleMapModel>> =
        dataSource.getArticlesWithLocation()
}
