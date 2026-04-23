package edu.javeriana.fixup.data.datasource.impl

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import edu.javeriana.fixup.data.datasource.interfaces.ReviewDataSource
import edu.javeriana.fixup.data.network.dto.ReviewDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReviewDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ReviewDataSource {

    override suspend fun getReviewsByUserId(userId: String): List<ReviewDto> {
        val snapshot = firestore.collection("reviews")
            .whereEqualTo("userId", userId)
            .get()
            .await()
            
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(ReviewDto::class.java)?.copy(id = doc.id)
        }
    }

    override suspend fun createReview(review: ReviewDto) {
        val docRef = firestore.collection("reviews").document()
        firestore.collection("reviews").document(docRef.id).set(review).await()
    }

    override suspend fun toggleLikeReview(reviewId: String, currentUserId: String, isCurrentlyLiked: Boolean) {
        val docRef = firestore.collection("reviews").document(reviewId)
        val update = if (isCurrentlyLiked) {
            FieldValue.arrayRemove(currentUserId)
        } else {
            FieldValue.arrayUnion(currentUserId)
        }
        docRef.update("likedBy", update).await()
    }
}
