package edu.javeriana.fixup.data.repository

import edu.javeriana.fixup.data.datasource.interfaces.ReviewMapDataSource
import edu.javeriana.fixup.ui.model.ReviewMapModel
import javax.inject.Inject

class ReviewMapRepository @Inject constructor(
    private val dataSource: ReviewMapDataSource
) {
    suspend fun getReviewsFromLast24h(): Result<List<ReviewMapModel>> {
        val since = System.currentTimeMillis() - 24 * 60 * 60 * 1000L
        return dataSource.getRecentReviewsWithLocation(since)
    }
}
