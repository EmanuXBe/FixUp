package edu.javeriana.fixup.data.repository

import edu.javeriana.fixup.data.datasource.FeedDataSource
import edu.javeriana.fixup.ui.features.feed.CategoryItemModel
import edu.javeriana.fixup.ui.features.feed.PublicationCardModel

class FeedRepository(
    private val dataSource: FeedDataSource = FeedDataSource()
) {
    fun getCategories(): Result<List<CategoryItemModel>> {
        return try {
            Result.success(dataSource.getCategories())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getPublications(): Result<List<PublicationCardModel>> {
        return try {
            Result.success(dataSource.getPublications())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
