package edu.javeriana.fixup.data.util

import edu.javeriana.fixup.data.network.api.FixUpApiService
import edu.javeriana.fixup.data.network.dto.ReviewRequestDto
import edu.javeriana.fixup.data.network.dto.ServiceDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utilidad para "quemar" datos iniciales en la base de datos (PostgreSQL vía API).
 * Ayuda a verificar que la visualización de artículos y reseñas funciona correctamente.
 */
@Singleton
class DataSeeder @Inject constructor(
    private val apiService: FixUpApiService,
    private val auth: com.google.firebase.auth.FirebaseAuth,
    private val firestore: com.google.firebase.firestore.FirebaseFirestore
) {
    suspend fun seed() = withContext(Dispatchers.IO) {
        try {
            val currentUserId = auth.currentUser?.uid ?: "test_user_1"
            
            // Asegurar que el usuario de prueba existe en Firestore para que el perfil no falle
            val testUser = mapOf(
                "uid" to currentUserId,
                "name" to (auth.currentUser?.displayName ?: "Usuario de Prueba"),
                "email" to (auth.currentUser?.email ?: "test@fixup.com"),
                "role" to "Cliente",
                "phone" to "3001234567"
            )
            firestore.collection("users").document(currentUserId).set(testUser, com.google.firebase.firestore.SetOptions.merge()).await()

            // 1. Crear algunos servicios de prueba
            val service1 = ServiceDto(
                title = "Reparación de Tuberías Premium",
                description = "Servicio especializado en fugas y cambio de grifería con materiales de alta calidad.",
                price = 85000.0,
                category = "Baños",
                imageUrl = "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?q=80&w=1000&auto=format&fit=crop"
            )

            val service2 = ServiceDto(
                title = "Instalación de Luces LED",
                description = "Moderniza tu sala con iluminación indirecta y ahorro de energía.",
                price = 120000.0,
                category = "Iluminación",
                imageUrl = "https://images.unsplash.com/photo-1558211583-d28f630b9eb2?q=80&w=1000&auto=format&fit=crop"
            )

            val resp1 = apiService.createService(service1)
            val resp2 = apiService.createService(service2)

            // 2. Crear reseñas usando el ID del usuario actual
            if (resp1.id != null) {
                apiService.createReview(ReviewRequestDto(
                    userId = currentUserId,
                    serviceId = resp1.id.toString(),
                    rating = 5,
                    comment = "Excelente servicio, muy puntual y limpio."
                ))
            }

            if (resp2.id != null) {
                apiService.createReview(ReviewRequestDto(
                    userId = currentUserId,
                    serviceId = resp2.id.toString(),
                    rating = 4,
                    comment = "Buen trabajo, aunque tardaron un poco más de lo esperado."
                ))
            }

            android.util.Log.d("DataSeeder", "Datos de prueba insertados con éxito")
        } catch (e: Exception) {
            android.util.Log.e("DataSeeder", "Error al sembrar datos: ${e.message}")
        }
    }
}
