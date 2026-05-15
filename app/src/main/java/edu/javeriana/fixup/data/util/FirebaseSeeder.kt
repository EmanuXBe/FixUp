package edu.javeriana.fixup.data.util

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.await
import net.datafaker.Faker
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

class FirebaseSeeder @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val faker = Faker()

    suspend fun seedData(currentUserId: String? = null): Result<Unit> {
        return try {
            seedUsers(5)
            seedArticles(10)
            seedReviews(15, currentUserId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun seedUsers(count: Int) {
        val usersCollection = firestore.collection("users")
        repeat(count) {
            val userId = UUID.randomUUID().toString()
            val userMap = hashMapOf(
                "name" to faker.name().fullName(),
                "email" to faker.internet().emailAddress(),
                "phone" to faker.phoneNumber().phoneNumber(),
                "address" to faker.address().fullAddress(),
                "role" to "Cliente",
                "profileImageUrl" to "https://picsum.photos/200/200?random=${faker.number().numberBetween(1, 1000)}"
            )
            usersCollection.document(userId).set(userMap).await()
        }
    }

    private suspend fun seedArticles(count: Int) {
        val articlesCollection = firestore.collection("articles")
        repeat(count) {
            val articleMap = hashMapOf(
                "title" to faker.commerce().productName(),
                "description" to faker.lorem().paragraph(),
                "price" to faker.number().randomDouble(2, 50, 5000),
                "imageUrl" to "https://picsum.photos/400/300?random=${faker.number().numberBetween(1, 1000)}",
                "category" to faker.commerce().department(),
                "authorId" to UUID.randomUUID().toString()
            )
            articlesCollection.add(articleMap).await()
        }
    }

    /**
     * Inserta 5 propiedades de prueba en la colección "properties" con:
     *  - Coordenadas dentro de Bogotá (lat 4.55–4.75 / lng -74.15–-74.00)
     *  - GeoPoint en el campo "location" (tipo nativo Firestore)
     *  - "latitude" y "longitude" como Double independientes (requeridos por FeedFirestoreDataSourceImpl)
     *  - "createdAt" con timestamp aleatorio dentro de las últimas 24 horas (para pasar el filtro del mapa)
     */
    suspend fun seedProperties(currentUserId: String? = null): Result<Unit> {
        return try {
            val userId = currentUserId ?: UUID.randomUUID().toString()
            val now = System.currentTimeMillis()
            val twentyFourHoursMs = 86_400_000L

            data class BogotaProperty(
                val title: String,
                val description: String,
                val price: Double,
                val category: String,
                val imageUrl: String,
                val latitude: Double,
                val longitude: Double
            )

            val properties = listOf(
                BogotaProperty(
                    title = "Apartamento moderno en Chapinero Alto",
                    description = "Hermoso apartamento de 2 habitaciones con balcón y vista panorámica de la ciudad. Cocina integral completamente equipada, sala-comedor amplio y parqueadero cubierto incluido. A 5 minutos del Parque de los Hippies.",
                    price = 2_800_000.0,
                    category = "Apartamento",
                    imageUrl = "https://picsum.photos/seed/bogota_chapinero/800/600",
                    latitude = 4.6477,
                    longitude = -74.0631
                ),
                BogotaProperty(
                    title = "Estudio amoblado en Usaquén",
                    description = "Acogedor estudio totalmente amoblado en el corazón de Usaquén. A pasos de restaurantes, cafeterías y comercio. Administración y servicios públicos incluidos en el canon. Edificio con portería 24 horas.",
                    price = 1_500_000.0,
                    category = "Estudio",
                    imageUrl = "https://picsum.photos/seed/bogota_usaquen/800/600",
                    latitude = 4.6940,
                    longitude = -74.0318
                ),
                BogotaProperty(
                    title = "Apartamento familiar en Suba Compartir",
                    description = "Espacioso apartamento de 3 habitaciones y 2 baños, zona de lavandería, depósito y dos parqueaderos. Conjunto cerrado con piscina, salón comunal y cancha múltiple. Ideal para familias.",
                    price = 2_200_000.0,
                    category = "Apartamento",
                    imageUrl = "https://picsum.photos/seed/bogota_suba/800/600",
                    latitude = 4.7405,
                    longitude = -74.0835
                ),
                BogotaProperty(
                    title = "Penthouse exclusivo en El Chicó",
                    description = "Impresionante penthouse de 4 habitaciones con terraza privada, jacuzzi exterior y vista 360° de Bogotá. Edificio de lujo con portería 24 horas, gimnasio, spa y salón de eventos.",
                    price = 8_500_000.0,
                    category = "Penthouse",
                    imageUrl = "https://picsum.photos/seed/bogota_chico/800/600",
                    latitude = 4.6688,
                    longitude = -74.0532
                ),
                BogotaProperty(
                    title = "Apartamento en Teusaquillo cerca al metro",
                    description = "Bien ubicado apartamento de 2 habitaciones a 3 minutos a pie de la estación de TransMilenio. Cocina equipada, depósito incluido y edificio con vigilancia. Barrio tranquilo y seguro, cerca a universidades.",
                    price = 1_900_000.0,
                    category = "Apartamento",
                    imageUrl = "https://picsum.photos/seed/bogota_teusaquillo/800/600",
                    latitude = 4.6362,
                    longitude = -74.0855
                )
            )

            val batch = firestore.batch()
            properties.forEach { prop ->
                val docRef = firestore.collection("properties").document()
                val randomOffsetMs = Random.nextLong(0L, twentyFourHoursMs)
                val geoPoint = GeoPoint(prop.latitude, prop.longitude)
                batch.set(
                    docRef,
                    hashMapOf(
                        "title"       to prop.title,
                        "description" to prop.description,
                        "price"       to prop.price,
                        "category"    to prop.category,
                        "imageUrl"    to prop.imageUrl,
                        "authorId"    to userId,
                        "likeCount"   to 0,
                        // Coordenadas como GeoPoint nativo (Firestore)
                        "location"    to geoPoint,
                        // Coordenadas como Double individuales (requeridas por FeedFirestoreDataSourceImpl.toPublicationDto)
                        "latitude"    to prop.latitude,
                        "longitude"   to prop.longitude,
                        // Timestamp dentro de las últimas 24 horas para pasar el filtro de getRecentPublications()
                        "createdAt"   to Timestamp(Date(now - randomOffsetMs))
                    )
                )
            }
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Crea fixers cubriendo todas las combinaciones categoría×urgencia que usa el
     * Fixer Assistant (4 categorías × 2 urgencias = 16 perfiles), para que cualquier
     * filtro devuelva resultados. Idempotente: usa IDs deterministas y `set()`, así
     * llamarlo múltiples veces no genera duplicados — solo refresca los documentos.
     */
    suspend fun seedFixers(): Result<Unit> {
        return try {
            data class FixerSpec(val category: String, val slug: String)
            val categories = listOf(
                FixerSpec("Plomería", "plomeria"),
                FixerSpec("Electricidad", "electricidad"),
                FixerSpec("Aseo", "aseo"),
                FixerSpec("Remodelación", "remodelacion")
            )
            val urgencies = listOf("Inmediato" to "inmediato", "Programado" to "programado")
            val usersCollection = firestore.collection("users")
            val batch = firestore.batch()
            categories.forEach { cat ->
                urgencies.forEach { (urgValue, urgSlug) ->
                    repeat(2) { idx ->
                        val docId = "assistant_fixer_${cat.slug}_${urgSlug}_${idx + 1}"
                        val docRef = usersCollection.document(docId)
                        batch.set(
                            docRef,
                            hashMapOf(
                                "name" to faker.name().fullName(),
                                "email" to faker.internet().emailAddress(),
                                "phone" to faker.phoneNumber().phoneNumber(),
                                "address" to "${faker.address().streetAddress()}, Bogotá",
                                "role" to "Fixer",
                                "category" to cat.category,
                                "availability" to urgValue,
                                "profileImageUrl" to "https://i.pravatar.cc/200?u=$docId"
                            )
                        )
                    }
                }
            }
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun seedReviews(count: Int, currentUserId: String?) {
        val reviewsCollection = firestore.collection("reviews")
        repeat(count) {
            val isForCurrentUser = currentUserId != null && it < 5 // Seed 5 reviews for current user
            val userId = if (isForCurrentUser) currentUserId else UUID.randomUUID().toString()
            val authorName = if (isForCurrentUser) "Mi Usuario Debug" else faker.name().fullName()
            
            val reviewMap = hashMapOf(
                "userId" to userId,
                "serviceId" to UUID.randomUUID().toString(),
                "rating" to faker.number().numberBetween(1, 6),
                "comment" to faker.lorem().sentence(),
                "authorName" to authorName,
                "authorProfileImageUrl" to "https://picsum.photos/100/100?random=${faker.number().numberBetween(1, 1000)}",
                "serviceTitle" to faker.commerce().productName(),
                "date" to "2023-10-27"
            )
            reviewsCollection.add(reviewMap).await()
        }
    }
}
