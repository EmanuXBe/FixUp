package edu.javeriana.fixup.data.datasource.impl

import com.google.firebase.firestore.FirebaseFirestore
import edu.javeriana.fixup.data.datasource.interfaces.ArticleMapDataSource
import edu.javeriana.fixup.ui.model.ArticleMapModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ArticleMapFirestoreDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ArticleMapDataSource {

    override suspend fun getArticlesWithLocation(): Result<List<ArticleMapModel>> {
        return try {
            val since = System.currentTimeMillis() - 86_400_000L

            val snapshot = firestore.collection("properties")
                .get()
                .await()

            val articles = snapshot.documents.mapNotNull { doc ->
                val lat = doc.getDouble("latitude") ?: return@mapNotNull null
                val lng = doc.getDouble("longitude") ?: return@mapNotNull null
                if (lat == 0.0 && lng == 0.0) return@mapNotNull null

                val createdAt = doc.getTimestamp("createdAt")?.toDate()?.time
                    ?: doc.getLong("createdAt")
                if (createdAt != null && createdAt < since) return@mapNotNull null

                val price = doc.getDouble("price") ?: doc.getDouble("precio") ?: 0.0

                ArticleMapModel(
                    id = doc.id,
                    latitude = lat,
                    longitude = lng,
                    title = doc.getString("title") ?: doc.getString("titulo") ?: "Sin título",
                    price = "\$${"%,.0f".format(price)}/mes",
                    category = doc.getString("category") ?: doc.getString("tipo") ?: "",
                    authorId = doc.getString("userId") ?: doc.getString("authorId") ?: "",
                    location = doc.getString("location") ?: doc.getString("ubicacion") ?: ""
                )
            }
            Result.success(articles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
