package edu.javeriana.fixup.data.repository

import android.net.Uri
import edu.javeriana.fixup.data.network.model.CategoryDto
import edu.javeriana.fixup.data.network.model.PublicationDto
import edu.javeriana.fixup.data.network.model.ReviewRequestDto
import edu.javeriana.fixup.data.datasource.interfaces.FeedDataSource
import edu.javeriana.fixup.ui.features.feed.CategoryItemModel
import edu.javeriana.fixup.ui.features.feed.PublicationCardModel
import edu.javeriana.fixup.ui.model.PropertyModel
import edu.javeriana.fixup.ui.model.ReviewModel
import javax.inject.Inject

class FeedRepository @Inject constructor(
    private val dataSource: FeedDataSource
) {
    suspend fun getCategories(): Result<List<CategoryItemModel>> {
        return try {
            val dtos = dataSource.getCategories()
            Result.success(dtos.map { it.toUiModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPublications(): Result<List<PublicationCardModel>> {
        return try {
            val dtos = dataSource.getPublications()
            Result.success(dtos.map { it.toUiModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPublicationById(id: Int): Result<PublicationCardModel> {
        return try {
            val dto = dataSource.getPublicationById(id)
            Result.success(dto.toUiModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReviewsByServiceId(serviceId: Int): Result<List<ReviewModel>> {
        return try {
            val reviews = dataSource.getReviewsByServiceId(serviceId)
            Result.success(reviews)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createReview(review: ReviewRequestDto): Result<ReviewModel> {
        return try {
            val created = dataSource.createReview(review)
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Extension functions for mapping (Mappers)
fun CategoryDto.toUiModel() = CategoryItemModel(
    imageRes = this.iconRes,
    title = this.name
)

fun PublicationDto.toUiModel() = PublicationCardModel(
    id = this.id,
    imageUrl = this.imageUrl ?: this.imageRes,
    title = this.title,
    price = this.priceText,
    description = this.description,
    location = this.location
)
