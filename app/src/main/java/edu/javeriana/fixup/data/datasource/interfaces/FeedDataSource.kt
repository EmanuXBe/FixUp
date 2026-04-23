package edu.javeriana.fixup.data.datasource.interfaces

import android.net.Uri
import edu.javeriana.fixup.data.network.dto.CategoryDto
import edu.javeriana.fixup.data.network.dto.PublicationDto
import edu.javeriana.fixup.data.network.dto.ReviewRequestDto
import edu.javeriana.fixup.ui.model.PropertyModel
import edu.javeriana.fixup.ui.model.ReviewModel

/**
 * Contrato del Data Source para Feed.
 */
interface FeedDataSource {
    suspend fun getCategories(): List<CategoryDto>
    suspend fun getPublications(): List<PublicationDto>
    suspend fun getFollowingPublications(followingIds: List<String>): List<PublicationDto>
    suspend fun getPublicationById(id: Int): PublicationDto
    suspend fun createPublication(property: PropertyModel, imageUri: Uri): PropertyModel
    suspend fun getReviewsByServiceId(serviceId: Int): List<ReviewModel>
    suspend fun createReview(review: ReviewRequestDto): ReviewModel
}
