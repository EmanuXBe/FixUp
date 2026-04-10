package edu.javeriana.fixup.data.repository

import android.net.Uri
import edu.javeriana.fixup.data.datasource.RentDataSource
import edu.javeriana.fixup.data.network.model.ReviewRequestDto
import edu.javeriana.fixup.data.network.service.FixUpApiService
import edu.javeriana.fixup.data.util.AppConstants
import edu.javeriana.fixup.ui.model.PropertyModel
import edu.javeriana.fixup.ui.model.ReviewModel
import javax.inject.Inject

class RentRepository @Inject constructor(
    private val dataSource: RentDataSource,
    private val apiService: FixUpApiService
) {
    suspend fun getProperties(): Result<List<PropertyModel>> {
        return try {
            val properties = dataSource.getRentProperties()
            Result.success(properties)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createProperty(property: PropertyModel, imageUri: Uri): Result<PropertyModel> {
        return try {
            val created = dataSource.createProperty(property, imageUri)
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReviewsByServiceId(serviceId: Int): Result<List<ReviewModel>> {
        return try {
            val reviewDtos = apiService.getReviewsByServiceId(serviceId)
            val reviews = reviewDtos.map { dto ->
                ReviewModel(
                    id = dto.id?.toString() ?: "",
                    userId = dto.userId.toString(),
                    rating = dto.rating,
                    comment = dto.comment,
                    userName = "Usuario ${dto.userId}"
                )
            }
            Result.success(reviews)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createReview(serviceId: Int, rating: Int, comment: String): Result<ReviewModel> {
        return try {
            val request = ReviewRequestDto(
                userId = AppConstants.CURRENT_USER_ID_INT,
                serviceId = serviceId,
                rating = rating,
                comment = comment
            )
            val resultDto = apiService.createReview(request)
            val savedReview = ReviewModel(
                id = resultDto.id?.toString() ?: "",
                userId = resultDto.userId.toString(),
                rating = resultDto.rating,
                comment = resultDto.comment,
                userName = "Usuario ${resultDto.userId}"
            )
            Result.success(savedReview)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
