package edu.javeriana.fixup.data.network.api

import edu.javeriana.fixup.data.network.dto.CreatePropertyRequestDto
import edu.javeriana.fixup.data.network.dto.CreatePropertyResponseDto
import edu.javeriana.fixup.data.network.dto.FirestorePropertyDto
import edu.javeriana.fixup.data.network.dto.FollowNotificationDto
import edu.javeriana.fixup.data.network.dto.LikeNotificationDto
import edu.javeriana.fixup.data.network.dto.NotificationDto
import edu.javeriana.fixup.data.network.dto.ReviewDto
import edu.javeriana.fixup.data.network.dto.UserDto
import edu.javeriana.fixup.data.network.dto.PropertyDto
import edu.javeriana.fixup.data.network.dto.ReviewRequestDto
import edu.javeriana.fixup.data.network.dto.ServiceDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface FixUpApiService {
    @GET("api/services")
    suspend fun getServices(): List<ServiceDto>

    @GET("api/services/{id}")
    suspend fun getServiceById(@Path("id") id: Int): ServiceDto

    @POST("api/services")
    suspend fun createService(@Body service: ServiceDto): ServiceDto

    @GET("api/reviews/service/{serviceId}")
    suspend fun getReviewsByServiceId(@Path("serviceId") serviceId: Int): List<ReviewDto>

    @GET("api/reviews/user/{userId}")
    suspend fun getUserReviews(@Path("userId") userId: String): Response<List<ReviewDto>>

    @POST("api/reviews")
    suspend fun createReview(@Body review: ReviewRequestDto): ReviewDto

    @PUT("api/reviews/{id}")
    suspend fun updateReview(@Path("id") id: String, @Body review: ReviewRequestDto): ReviewDto

    @DELETE("api/reviews/{id}")
    suspend fun deleteReview(@Path("id") id: String)

    /**
     * Sincronización con el Backend: Registra un usuario en la base de datos PostgreSQL.
     * Se usa el UID de Firebase como identificador principal para mantener la consistencia.
     */
    @POST("api/users")
    suspend fun createUser(@Body user: UserDto): Response<UserDto>

    @POST("notifications/like")
    suspend fun notifyLike(@Body body: LikeNotificationDto): Response<Unit>

    @POST("notifications/follow")
    suspend fun notifyFollow(@Body body: FollowNotificationDto): Response<Unit>

    @GET("api/notifications/{userId}")
    suspend fun getNotifications(@Path("userId") userId: String): List<NotificationDto>

    /**
     * POST /api/properties — Publica un nuevo inmueble en Firestore a través del backend.
     *
     * El cliente sube las imágenes primero a Firebase Storage y envía aquí
     * solo las URLs resultantes (campo "imagenes"). El backend almacena el
     * documento completo en la colección "properties" de Firestore y devuelve
     * el ID autogenerado (propertyId).
     *
     * @param body Datos del inmueble validados antes de enviar (ver CreatePropertyRequestDto)
     * @return CreatePropertyResponseDto con el propertyId generado por Firestore
     */
    /** GET /api/properties — Lista todos los inmuebles publicados en Firestore. */
    @GET("api/properties")
    suspend fun getProperties(): List<FirestorePropertyDto>

    @POST("api/properties")
    suspend fun createProperty(@Body body: CreatePropertyRequestDto): CreatePropertyResponseDto
}
