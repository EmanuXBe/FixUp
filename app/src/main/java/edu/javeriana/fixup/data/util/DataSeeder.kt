package edu.javeriana.fixup.data.util

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import edu.javeriana.fixup.data.network.api.FixUpApiService
import edu.javeriana.fixup.data.network.dto.ReviewRequestDto
import edu.javeriana.fixup.data.network.dto.ServiceDto
import edu.javeriana.fixup.data.network.dto.UserDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import net.datafaker.Faker
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Utilidad para poblar la base de datos con datos aleatorios realistas usando DataFaker.
 */
@Singleton
class DataSeeder @Inject constructor(
    private val apiService: FixUpApiService,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val faker = Faker()
    private val TAG = "DataSeeder"

    suspend fun seed(count: Int = 10) = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Iniciando sembrado de $count elementos...")
            val currentUserId = auth.currentUser?.uid ?: "test_user_admin"

            // 1. Asegurar que el usuario actual existe en Firestore y Backend
            val currentUserData = UserDto(
                id = currentUserId,
                name = auth.currentUser?.displayName ?: faker.name().fullName(),
                email = auth.currentUser?.email ?: faker.internet().emailAddress(),
                phone = faker.phoneNumber().cellPhone(),
                role = "Admin",
                profileImageUrl = "https://i.pravatar.cc/150?u=$currentUserId"
            )
            
            // Guardar en Firestore
            firestore.collection("users").document(currentUserId)
                .set(currentUserData, SetOptions.merge()).await()
            
            // Intentar guardar en Backend (PostgreSQL)
            try { apiService.createUser(currentUserData) } catch (e: Exception) { Log.e(TAG, "Error sync user to backend") }

            val categories = listOf("Baños", "Iluminación", "Cocina", "Pintura", "Jardinería", "Remodelación General")

            repeat(count) { index ->
                // 2. Crear Servicios Aleatorios
                val service = ServiceDto(
                    title = faker.house().room() + " " + faker.options().option("Premium", "Económico", "Moderno", "Integral"),
                    description = faker.lorem().paragraph(2),
                    price = Random.nextDouble(50000.0, 1000000.0),
                    category = categories.random(),
                    imageUrl = "https://picsum.photos/seed/${faker.internet().uuid()}/800/600",
                    providerId = currentUserId
                )

                val createdService = apiService.createService(service)
                Log.d(TAG, "Servicio creado: ${createdService.title} (ID: ${createdService.id})")

                // 3. Crear Reseñas Aleatorias para cada servicio
                if (createdService.id != null) {
                    repeat(Random.nextInt(1, 4)) {
                        val review = ReviewRequestDto(
                            userId = currentUserId,
                            serviceId = createdService.id.toString(),
                            rating = Random.nextInt(3, 6),
                            comment = faker.yoda().quote() // Comentarios divertidos aleatorios
                        )
                        apiService.createReview(review)
                    }
                }
            }

            Log.d(TAG, "Sembrado completado con éxito")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error crítico en DataSeeder: ${e.message}", e)
            false
        }
    }
}
