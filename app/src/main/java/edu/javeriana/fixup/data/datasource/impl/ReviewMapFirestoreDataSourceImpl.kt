package edu.javeriana.fixup.data.datasource.impl

import com.google.firebase.firestore.FirebaseFirestore
import edu.javeriana.fixup.data.datasource.interfaces.ReviewMapDataSource
import edu.javeriana.fixup.ui.model.ReviewMapModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReviewMapFirestoreDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ReviewMapDataSource {

    override suspend fun getRecentReviewsWithLocation(since: Long): Result<List<ReviewMapModel>> {
        return try {
            val snapshot = firestore.collection("reviews")
                .whereGreaterThan("timestamp", since)
                .get()
                .await()

            val reviews = snapshot.documents.mapNotNull { doc ->
                val lat = doc.getDouble("latitude") ?: return@mapNotNull null
                val lng = doc.getDouble("longitude") ?: return@mapNotNull null
                if (lat == 0.0 && lng == 0.0) return@mapNotNull null

                ReviewMapModel(
                    id = doc.id,
                    latitude = lat,
                    longitude = lng,
                    authorName = doc.getString("authorName") ?: "Usuario",
                    comment = doc.getString("comment") ?: "",
                    rating = doc.getLong("rating")?.toInt() ?: 0,
                    serviceTitle = doc.getString("serviceTitle") ?: doc.getString("articleName") ?: "",
                    timestamp = doc.getLong("timestamp") ?: 0L
                )
            }
            Result.success(reviews)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
