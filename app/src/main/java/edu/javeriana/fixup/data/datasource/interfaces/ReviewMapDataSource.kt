package edu.javeriana.fixup.data.datasource.interfaces

import edu.javeriana.fixup.ui.model.ReviewMapModel

interface ReviewMapDataSource {
    suspend fun getRecentReviewsWithLocation(since: Long): Result<List<ReviewMapModel>>
}
