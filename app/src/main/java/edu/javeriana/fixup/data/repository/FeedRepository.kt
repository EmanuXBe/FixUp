package edu.javeriana.fixup.data.repository

import android.net.Uri
import edu.javeriana.fixup.data.datasource.CategoryDto
import edu.javeriana.fixup.data.datasource.FeedDataSource
import edu.javeriana.fixup.data.datasource.PublicationDto
import edu.javeriana.fixup.data.datasource.ReviewRequest
import edu.javeriana.fixup.ui.features.feed.CategoryItemModel
import edu.javeriana.fixup.ui.features.feed.PublicationCardModel
import edu.javeriana.fixup.ui.model.PropertyModel
import edu.javeriana.fixup.ui.model.ReviewModel
import javax.inject.Inject

class FeedRepository @Inject constructor(
    private val dataSource: FeedDataSource
) {
    suspend fun getCategories(): Result<List<CategoryItemModel>> = try {
        Result.success(dataSource.getCategories().map { it.toUiModel() })
    } catch (e: Exception) { Result.failure(e) }

    suspend fun getPublications(): Result<List<PublicationCardModel>> = try {
        Result.success(dataSource.getPublications().map { it.toUiModel() })
    } catch (e: Exception) { Result.failure(e) }

    suspend fun getPublicationById(id: Int): Result<PublicationCardModel> = try {
        Result.success(dataSource.getPublicationById(id).toUiModel())
    } catch (e: Exception) { Result.failure(e) }

    suspend fun createPublication(property: PropertyModel, imageUri: Uri): Result<PropertyModel> = try {
        Result.success(dataSource.createPublication(property, imageUri))
    } catch (e: Exception) { Result.failure(e) }

    suspend fun getReviewsByServiceId(serviceId: Int): Result<List<ReviewModel>> = try {
        Result.success(dataSource.getReviewsByServiceId(serviceId))
    } catch (e: Exception) { Result.failure(e) }

    suspend fun createReview(review: ReviewRequest): Result<ReviewModel> = try {
        Result.success(dataSource.createReview(review))
    } catch (e: Exception) { Result.failure(e) }

    suspend fun updateReview(reviewId: String, review: ReviewRequest): Result<ReviewModel> = try {
        Result.success(dataSource.updateReview(reviewId, review))
    } catch (e: Exception) { Result.failure(e) }

    suspend fun deleteReview(reviewId: String): Result<Unit> = try {
        dataSource.deleteReview(reviewId)
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }
}

fun CategoryDto.toUiModel() = CategoryItemModel(imageRes = this.iconRes, title = this.name)

fun PublicationDto.toUiModel() = PublicationCardModel(
    id = this.id,
    imageUrl = this.imageUrl ?: this.imageRes,
    title = this.title,
    price = this.priceText,
    description = this.description,
    location = this.location
)