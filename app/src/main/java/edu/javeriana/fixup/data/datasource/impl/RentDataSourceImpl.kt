package edu.javeriana.fixup.data.datasource.impl

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import edu.javeriana.fixup.data.datasource.interfaces.RentDataSource
import edu.javeriana.fixup.data.mapper.toDomain
import edu.javeriana.fixup.data.network.api.FixUpApiService
import edu.javeriana.fixup.data.network.dto.CreatePropertyRequestDto
import edu.javeriana.fixup.ui.model.PropertyModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RentDataSourceImpl @Inject constructor(
    private val apiService: FixUpApiService,
    private val storage: FirebaseStorage,
    private val firestore: FirebaseFirestore
) : RentDataSource {

    // ─── Mock de respaldo (debug sin backend o sin datos publicados aún) ──────

    private val mockProperties = listOf(
        PropertyModel(
            id = "101",
            title = "Apartamento en Chapinero",
            description = "Hermoso apartamento amoblado, cerca a universidades y zonas comerciales.",
            price = 2500000.0,
            location = "Chapinero Alto, Bogotá",
            imageUrl = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=60"
        ),
        PropertyModel(
            id = "102",
            title = "Casa Campestre en Chía",
            description = "Amplia casa con zonas verdes, 3 habitaciones, 4 baños y estudio.",
            price = 4800000.0,
            location = "Vía Guaymaral, Chía",
            imageUrl = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=60"
        ),
        PropertyModel(
            id = "103",
            title = "Estudio Loft en El Retiro",
            description = "Moderno loft con acabados industriales. Edificio con gimnasio y terraza.",
            price = 3200000.0,
            location = "Barrio El Retiro, Bogotá",
            imageUrl = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=60"
        ),
        PropertyModel(
            id = "104",
            title = "Local Comercial en Usaquén",
            description = "Excelente ubicación para negocio. Alto tráfico peatonal.",
            price = 6000000.0,
            location = "Plaza de Usaquén, Bogotá",
            imageUrl = "https://images.unsplash.com/photo-1497366216548-37526070297c?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=60"
        )
    )

    override suspend fun getRentProperties(): List<PropertyModel> {
        return try {
            apiService.getProperties().map { it.toDomain() }
        } catch (e: Exception) {
            mockProperties
        }
    }

    override suspend fun getPropertyById(id: String): PropertyModel {
        return try {
            val doc = firestore.collection("properties").document(id).get().await()
            if (!doc.exists()) return mockProperties.find { it.id == id }
                ?: throw Exception("Propiedad no encontrada con id=$id")
            val title = doc.getString("titulo") ?: doc.getString("title") ?: "Sin título"
            val price = doc.getDouble("precio") ?: doc.getDouble("price") ?: 0.0
            val imageUrl = (doc.get("imagenes") as? List<*>)?.firstOrNull() as? String
                ?: doc.getString("imageUrl")
            PropertyModel(
                id          = doc.id,
                title       = title,
                description = doc.getString("descripcion") ?: doc.getString("description"),
                price       = price,
                location    = doc.getString("ubicacion") ?: doc.getString("location"),
                imageUrl    = imageUrl,
                latitude    = doc.getDouble("latitude"),
                longitude   = doc.getDouble("longitude")
            )
        } catch (e: Exception) {
            mockProperties.find { it.id == id }
                ?: throw Exception("Propiedad no encontrada con id=$id")
        }
    }

    /**
     * Publica un inmueble: sube imágenes a Storage y llama al backend.
     *
     * PASO 1: Cada Uri se sube a "properties/{userId}/{timestamp}_{i}.jpg" en Firebase Storage.
     *   - En DEBUG: Storage emulator en 10.0.2.2:9199
     *   - En RELEASE: Firebase Storage real
     *
     * PASO 2: POST /api/properties con las URLs obtenidas.
     *   - En DEBUG y RELEASE: siempre va al backend de producción (Render)
     *
     * @return propertyId generado por Firestore
     */
    override suspend fun createProperty(
        userId: String,
        titulo: String,
        ubicacion: String,
        descripcion: String,
        precio: Double,
        tipo: String,
        imageUris: List<Uri>
    ): String {
        val imageUrls = imageUris.mapIndexed { index, uri ->
            val ref = storage.reference.child(
                "properties/$userId/${System.currentTimeMillis()}_$index.jpg"
            )
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        }

        val response = apiService.createProperty(
            CreatePropertyRequestDto(
                userId      = userId,
                titulo      = titulo,
                ubicacion   = ubicacion,
                descripcion = descripcion,
                precio      = precio,
                tipo        = tipo,
                imagenes    = imageUrls
            )
        )

        return response.propertyId
            ?: throw Exception("El servidor no retornó el ID del inmueble creado.")
    }
}
