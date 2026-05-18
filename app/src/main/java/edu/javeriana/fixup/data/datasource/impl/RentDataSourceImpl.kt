package edu.javeriana.fixup.data.datasource.impl

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import edu.javeriana.fixup.data.datasource.interfaces.RentDataSource
import edu.javeriana.fixup.data.mapper.toDomain
import edu.javeriana.fixup.data.network.api.FixUpApiService
import edu.javeriana.fixup.ui.model.PropertyModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
        // Lectura directa desde Firestore: el backend Express no expone GET /api/properties
        // y las propiedades creadas por la app se escriben directamente en la colección
        // `properties` de Firestore. Leer aquí garantiza que las nuevas publicaciones
        // aparezcan en el mapa sin depender del backend.
        return try {
            val snapshot = firestore.collection("properties").get().await()
            val properties = snapshot.documents.map { it.toPropertyModel() }
            if (properties.isEmpty()) mockProperties else properties
        } catch (e: Exception) {
            try {
                apiService.getProperties().map { it.toDomain() }
            } catch (_: Exception) {
                mockProperties
            }
        }
    }

    /**
     * Stream en tiempo real de la colección `properties` usando `addSnapshotListener`.
     * Cada cambio en Firestore (incluyendo escrituras locales con persistencia offline)
     * emite una nueva lista al Flow. Se desuscribe automáticamente al cancelar el Flow.
     */
    override fun observeRentProperties(): Flow<List<PropertyModel>> = callbackFlow {
        // Sin MetadataChanges.INCLUDE: por defecto el listener ya emite tras escrituras
        // locales y server-acks. Incluir metadata duplica emisiones (cache + ack) y dispara
        // recomposiciones redundantes en MapScreen.
        val subscription = firestore.collection("properties")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(snapshot.documents.map { it.toPropertyModel() })
                }
            }
        awaitClose { subscription.remove() }
    }

    private fun DocumentSnapshot.toPropertyModel(): PropertyModel {
        val title = getString("title") ?: getString("titulo") ?: "Sin título"
        val price = getDouble("price") ?: getDouble("precio")
            ?: getLong("price")?.toDouble() ?: getLong("precio")?.toDouble()
        val imageUrl = (get("imagenes") as? List<*>)?.firstOrNull() as? String
            ?: getString("imageUrl") ?: getString("imageurl")
        val geoPoint = try { getGeoPoint("location") } catch (_: Exception) { null }
        val locationMap = get("location") as? Map<*, *>
        val lat = geoPoint?.latitude
            ?: (locationMap?.get("_latitude") as? Number)?.toDouble()
            ?: (locationMap?.get("latitude") as? Number)?.toDouble()
            ?: getDouble("latitude")
        val lng = geoPoint?.longitude
            ?: (locationMap?.get("_longitude") as? Number)?.toDouble()
            ?: (locationMap?.get("longitude") as? Number)?.toDouble()
            ?: getDouble("longitude")
        val locationText = getString("ubicacion") ?: getString("location").takeIf { it != null && locationMap == null && geoPoint == null }
            ?: getString("category") ?: getString("categoria")
        val createdAtMillis: Long? = try { getTimestamp("createdAt")?.toDate()?.time } catch (_: Exception) { null }
            ?: getLong("createdAt")
            ?: getString("createdAt")?.let { iso ->
                try { java.time.Instant.parse(iso).toEpochMilli() } catch (_: Exception) { null }
            }
        return PropertyModel(
            id          = this.id,
            title       = title,
            description = getString("descripcion") ?: getString("description"),
            price       = price,
            location    = locationText,
            imageUrl    = imageUrl,
            latitude    = lat,
            longitude   = lng,
            createdAt   = createdAtMillis
        )
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
        imageUris: List<Uri>,
        latitude: Double,
        longitude: Double
    ): String {
        val imageUrls = imageUris.mapIndexed { index, uri ->
            val ref = storage.reference.child(
                "properties/$userId/${System.currentTimeMillis()}_$index.jpg"
            )
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        }

        // Escritura primaria: Firestore. El backend Express no expone POST /api/properties
        // localmente, así que persistimos en Firestore para que getRentProperties() lo
        // recoja en la próxima lectura del mapa.
        val docRef = firestore.collection("properties").document()
        val data = mapOf(
            "title"       to titulo,
            "titulo"      to titulo,
            "description" to descripcion,
            "descripcion" to descripcion,
            "price"       to precio,
            "precio"      to precio,
            "ubicacion"   to ubicacion,
            "tipo"        to tipo,
            "imageUrl"    to imageUrls.firstOrNull(),
            "imagenes"    to imageUrls,
            "authorId"    to userId,
            "ownerId"     to userId,
            "latitude"    to latitude,
            "longitude"   to longitude,
            "createdAt"   to FieldValue.serverTimestamp(),
            "likeCount"   to 0
        )
        docRef.set(data).await()

        // Antes había un POST a /api/properties como "mejor esfuerzo", pero el backend
        // Express no expone esa ruta (solo /api/users, /api/services, /api/reviews).
        // El round-trip 404 añadía latencia (ampliando la ventana de carrera del doble-tap)
        // sin ningún beneficio. Eliminado a propósito.

        return docRef.id
    }
}
