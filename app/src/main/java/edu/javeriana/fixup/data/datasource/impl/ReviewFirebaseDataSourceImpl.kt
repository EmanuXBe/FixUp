package edu.javeriana.fixup.data.datasource.impl

import com.google.firebase.firestore.FirebaseFirestore
import edu.javeriana.fixup.data.datasource.interfaces.ReviewDataSource
import edu.javeriana.fixup.ui.model.ReviewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReviewFirebaseDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ReviewDataSource {

    override fun getReviewsByUserId(userId: String): Flow<Result<List<ReviewModel>>> = callbackFlow {
        val subscription = firestore.collection("reviews")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    launch {
                        try {
                            val reviews = snapshot.documents.map { doc ->
                                async {
                                    val rating = doc.getLong("rating")?.toInt() ?: 0
                                    val comment = doc.getString("comment") ?: ""
                                    val authorName = doc.getString("authorName") ?: "Usuario"
                                    val authorProfileImageUrl = doc.getString("authorProfileImageUrl") ?: ""
                                    val serviceTitle = doc.getString("serviceTitle") ?: doc.getString("articleName") ?: ""
                                    val serviceId = doc.getString("serviceId") ?: doc.getString("articleId") ?: ""
                                    val likedBy = getLikedUsers(doc.id).getOrDefault(emptyList())
                                    ReviewModel(
                                        id = doc.id,
                                        userId = userId,
                                        serviceId = serviceId,
                                        rating = rating,
                                        comment = comment,
                                        authorName = authorName,
                                        authorProfileImageUrl = authorProfileImageUrl,
                                        serviceTitle = serviceTitle,
                                        date = "",
                                        likedBy = likedBy
                                    )
                                }
                            }.awaitAll()
                            trySend(Result.success(reviews))
                        } catch (e: Exception) {
                            trySend(Result.failure(e))
                        }
                    }
                }
            }
        awaitClose { subscription.remove() }
    }

    override fun getReviewsByServiceId(serviceId: String): Flow<Result<List<ReviewModel>>> = callbackFlow {
        val subscription = firestore.collection("reviews")
            .whereEqualTo("serviceId", serviceId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    launch {
                        try {
                            val reviews = snapshot.documents.map { doc ->
                                async {
                                    val rating = doc.getLong("rating")?.toInt() ?: 0
                                    val comment = doc.getString("comment") ?: ""
                                    val authorName = doc.getString("authorName") ?: "Usuario"
                                    val authorProfileImageUrl = doc.getString("authorProfileImageUrl") ?: ""
                                    val serviceTitle = doc.getString("serviceTitle") ?: ""
                                    val userId = doc.getString("userId") ?: ""
                                    val likedBy = getLikedUsers(doc.id).getOrDefault(emptyList())
                                    ReviewModel(
                                        id = doc.id,
                                        userId = userId,
                                        serviceId = serviceId,
                                        rating = rating,
                                        comment = comment,
                                        authorName = authorName,
                                        authorProfileImageUrl = authorProfileImageUrl,
                                        serviceTitle = serviceTitle,
                                        date = "",
                                        likedBy = likedBy
                                    )
                                }
                            }.awaitAll()
                            trySend(Result.success(reviews))
                        } catch (e: Exception) {
                            trySend(Result.failure(e))
                        }
                    }
                }
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun createReview(review: ReviewModel): Result<Boolean> {
        return try {
            val reviewMap = hashMapOf(
                "userId" to review.userId,
                "serviceId" to review.serviceId,
                "rating" to review.rating,
                "comment" to review.comment,
                "authorName" to review.authorName,
                "authorProfileImageUrl" to review.authorProfileImageUrl,
                "serviceTitle" to review.serviceTitle
            )
            firestore.collection("reviews").add(reviewMap).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addLike(reviewId: String, userId: String): Result<Unit> {
        return try {
            val likeData = hashMapOf("timestamp" to com.google.firebase.Timestamp.now())
            firestore.collection("reviews")
                .document(reviewId)
                .collection("likes")
                .document(userId)
                .set(likeData)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeLike(reviewId: String, userId: String): Result<Unit> {
        return try {
            firestore.collection("reviews")
                .document(reviewId)
                .collection("likes")
                .document(userId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLikedUsers(reviewId: String): Result<List<String>> {
        return try {
            val snapshot = firestore.collection("reviews")
                .document(reviewId)
                .collection("likes")
                .get()
                .await()
            val userIds = snapshot.documents.map { it.id }
            Result.success(userIds)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateReview(reviewId: String, rating: Int, comment: String): Result<Unit> {
        return try {
            firestore.collection("reviews")
                .document(reviewId)
                .update(
                    mapOf(
                        "rating" to rating,
                        "comment" to comment
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteReview(reviewId: String): Result<Unit> {
        return try {
            firestore.collection("reviews")
                .document(reviewId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}