package edu.javeriana.fixup.data.util

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import net.datafaker.Faker
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Puebla Firestore con datos falsos realistas usando DataFaker.
 * Escribe directamente en Firestore (visible en el emulador en localhost:4000).
 */
@Singleton
class DataSeeder @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val faker = Faker()
    private val TAG = "DataSeeder"

    suspend fun seed(count: Int = 10): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Iniciando sembrado de $count elementos...")
            val currentUserId = auth.currentUser?.uid ?: "test_user_admin"

            // 1. Usuarios falsos
            val userIds = mutableListOf(currentUserId)
            repeat(count) {
                val userId = "user_fake_$it"
                userIds.add(userId)
                firestore.collection("users").document(userId).set(
                    mapOf(
                        "name"            to faker.name().fullName(),
                        "email"           to faker.internet().emailAddress(),
                        "phone"           to faker.phoneNumber().cellPhone(),
                        "role"            to listOf("Fixer", "Client").random(),
                        "address"         to faker.address().streetAddress(),
                        "profileImageUrl" to "https://i.pravatar.cc/150?u=$userId"
                    )
                ).await()
            }

            // Asegurar que el usuario actual también existe
            firestore.collection("users").document(currentUserId).set(
                mapOf(
                    "name"            to (auth.currentUser?.displayName ?: faker.name().fullName()),
                    "email"           to (auth.currentUser?.email ?: faker.internet().emailAddress()),
                    "phone"           to faker.phoneNumber().cellPhone(),
                    "role"            to "Fixer",
                    "address"         to faker.address().streetAddress(),
                    "profileImageUrl" to "https://i.pravatar.cc/150?u=$currentUserId"
                )
            ).await()

            val categories = listOf("Baños", "Iluminación", "Cocina", "Pintura", "Jardinería", "Remodelación General")

            // 2. Artículos y reseñas (collection "articles" que lee FeedFirestoreDataSourceImpl)
            repeat(count) { index ->
                val articleId = "article_fake_$index"
                val authorId = userIds.random()

                firestore.collection("articles").document(articleId).set(
                    mapOf(
                        "title"       to "${categories.random()} ${faker.options().option("Premium", "Económico", "Moderno", "Integral")}",
                        "description" to faker.lorem().paragraph(2),
                        "price"       to Random.nextDouble(50_000.0, 1_000_000.0),
                        "category"    to categories.random(),
                        "imageUrl"    to "https://picsum.photos/seed/$articleId/800/600",
                        "authorId"    to authorId,
                        "rating"      to Random.nextDouble(3.0, 5.0)
                    )
                ).await()

                // 3. Reseñas del artículo — incluye coordenadas y timestamp para el mapa
                repeat(Random.nextInt(1, 4)) { r ->
                    val reviewId = "review_fake_${index}_$r"
                    val reviewerId = userIds.random()
                    // Coordenadas aleatorias en área de Bogotá
                    val lat = 4.5 + Random.nextDouble() * 0.35
                    val lng = -74.2 + Random.nextDouble() * 0.35
                    // Timestamp en las últimas 24h
                    val hoursAgo = Random.nextLong(0L, 24L)
                    val ts = System.currentTimeMillis() - hoursAgo * 60 * 60 * 1000L
                    firestore.collection("reviews").document(reviewId).set(
                        mapOf(
                            "serviceId"    to articleId,
                            "userId"       to reviewerId,
                            "rating"       to Random.nextInt(3, 6),
                            "comment"      to faker.lorem().sentence(10),
                            "authorName"   to faker.name().fullName(),
                            "serviceTitle" to "Artículo prueba $index",
                            "latitude"     to lat,
                            "longitude"    to lng,
                            "timestamp"    to ts
                        )
                    ).await()
                }
            }

            // 4. Relaciones de seguimiento: el usuario actual sigue a los primeros 3 usuarios falsos
            //    y esos usuarios se siguen entre sí, para que el Following Feed tenga contenido.
            val timestamp = mapOf("timestamp" to com.google.firebase.Timestamp.now())
            val fakeUserIds = userIds.filter { it != currentUserId }.take(3)
            fakeUserIds.forEach { targetId ->
                // currentUser → sigue a targetId
                firestore.collection("users").document(currentUserId)
                    .collection("following").document(targetId).set(timestamp).await()
                // targetId → es seguido por currentUser
                firestore.collection("users").document(targetId)
                    .collection("followers").document(currentUserId).set(timestamp).await()
            }

            Log.d(TAG, "Sembrado completado: $count artículos, usuarios, reseñas y ${fakeUserIds.size} seguimientos en Firestore")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error en DataSeeder: ${e.message}", e)
            false
        }
    }
}
