package edu.javeriana.fixup.data.repository

import android.net.Uri
import edu.javeriana.fixup.data.datasource.FixUpApiService
import edu.javeriana.fixup.data.datasource.RentDataSource
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

    suspend fun getPropertyById(id: Int): Result<PropertyModel> {
        return try {
            val property = dataSource.getPropertyById(id)
            Result.success(property)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReviewsByArticleId(articleId: Int): Result<List<ReviewModel>> {
        return try {
            val reviews = apiService.getReviewsByArticleId(articleId)
            Result.success(reviews)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createReview(review: ReviewModel): Result<ReviewModel> {
        return try {
            val created = apiService.createReview(review)
            Result.success(created)
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
}
