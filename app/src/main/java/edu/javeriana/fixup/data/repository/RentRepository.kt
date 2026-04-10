package edu.javeriana.fixup.data.repository

import android.net.Uri
import edu.javeriana.fixup.data.datasource.interfaces.RentDataSource
import edu.javeriana.fixup.data.network.api.FixUpApiService
import edu.javeriana.fixup.data.mapper.toDomain
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
            val reviews = apiService.getReviewsByServiceId(serviceId).map { it.toDomain() }
            Result.success(reviews)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createReview(serviceId: Int, rating: Int, comment: String): Result<ReviewModel> {
        return try {
            val request = edu.javeriana.fixup.data.network.model.ReviewRequestDto(
                userId = AppConstants.CURRENT_USER_ID_INT,
                serviceId = serviceId,
                rating = rating.toDouble(),
                comment = comment
            )
            val savedReview = apiService.createReview(request).toDomain()
            Result.success(savedReview)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
