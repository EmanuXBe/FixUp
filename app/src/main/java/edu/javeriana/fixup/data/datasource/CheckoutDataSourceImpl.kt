package edu.javeriana.fixup.data.datasource

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import edu.javeriana.fixup.R
import edu.javeriana.fixup.ui.model.PropertyModel
import edu.javeriana.fixup.ui.model.ReviewModel
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class CheckoutDataSourceImpl @Inject constructor(
    private val apiService: FixUpApiService,
    private val storage: FirebaseStorage
) : FeedDataSource {

    override suspend fun getCategories() = listOf(
        CategoryDto(1, "Baños", R.drawable.bano),
        CategoryDto(2, "Iluminación", R.drawable.luz),
        CategoryDto(3, "Cocina", R.drawable.cocina),
        CategoryDto(4, "Exterior", R.drawable.exterior)
    )

    override suspend fun getPublications(): List<PublicationDto> =
        apiService.getServices().map { it.toDto() }

    override suspend fun getPublicationById(id: Int): PublicationDto =
        apiService.getServiceById(id).toDto()

    override suspend fun createPublication(property: PropertyModel, imageUri: Uri): PropertyModel {
        val filename = UUID.randomUUID().toString()
        val ref = storage.getReference("publications/$filename.jpg")
        ref.putFile(imageUri).await()
        val downloadUrl = ref.downloadUrl.await().toString()
        return apiService.createService(property.copy(imageUrl = downloadUrl))
    }

    override suspend fun getReviewsByServiceId(serviceId: Int): List<ReviewModel> = try {
        apiService.getReviewsByServiceId(serviceId)
    } catch (e: Exception) { emptyList() }

    override suspend fun createReview(review: ReviewRequest): ReviewModel =
        apiService.createReview(review)

    override suspend fun updateReview(reviewId: String, review: ReviewRequest): ReviewModel =
        apiService.updateReview(reviewId, review)

    override suspend fun deleteReview(reviewId: String) =
        apiService.deleteReview(reviewId)

    private fun PropertyModel.toDto() = PublicationDto(
        id = this.id.toString(),
        title = this.title ?: "Sin título",
        priceText = "Desde $${this.price ?: 0.0}",
        description = this.description,
        location = this.location,
        imageUrl = this.imageUrl
    )
}