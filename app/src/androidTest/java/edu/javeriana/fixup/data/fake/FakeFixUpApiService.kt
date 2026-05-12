package edu.javeriana.fixup.data.fake

import edu.javeriana.fixup.data.network.api.FixUpApiService
import edu.javeriana.fixup.data.network.dto.FollowNotificationDto
import edu.javeriana.fixup.data.network.dto.LikeNotificationDto
import edu.javeriana.fixup.data.network.dto.NotificationDto
import edu.javeriana.fixup.data.network.dto.CreatePropertyRequestDto
import edu.javeriana.fixup.data.network.dto.CreatePropertyResponseDto
import edu.javeriana.fixup.data.network.dto.FirestorePropertyDto
import edu.javeriana.fixup.data.network.dto.ReviewDto
import edu.javeriana.fixup.data.network.dto.ReviewRequestDto
import edu.javeriana.fixup.data.network.dto.ServiceDto
import edu.javeriana.fixup.data.network.dto.UserDto
import retrofit2.Response

class FakeFixUpApiService : FixUpApiService {

    override suspend fun getServices(): List<ServiceDto> {
        throw UnsupportedOperationException("No se usa en estas pruebas")
    }

    override suspend fun getServiceById(id: Int): ServiceDto {
        throw UnsupportedOperationException("No se usa en estas pruebas")
    }

    override suspend fun createService(service: ServiceDto): ServiceDto {
        throw UnsupportedOperationException("No se usa en estas pruebas")
    }

    override suspend fun getReviewsByServiceId(serviceId: Int): List<ReviewDto> {
        throw UnsupportedOperationException("No se usa en estas pruebas")
    }

    override suspend fun getUserReviews(userId: String): Response<List<ReviewDto>> {
        throw UnsupportedOperationException("No se usa en estas pruebas")
    }

    override suspend fun createReview(review: ReviewRequestDto): ReviewDto {
        throw UnsupportedOperationException("No se usa en estas pruebas")
    }

    override suspend fun updateReview(id: String, review: ReviewRequestDto): ReviewDto {
        throw UnsupportedOperationException("No se usa en estas pruebas")
    }

    override suspend fun deleteReview(id: String) {
        throw UnsupportedOperationException("No se usa en estas pruebas")
    }

    override suspend fun createUser(user: UserDto): Response<UserDto> {
        return Response.success(user)
    }

    override suspend fun notifyLike(body: LikeNotificationDto): Response<Unit> {
        return Response.success(Unit)
    }

    override suspend fun notifyFollow(body: FollowNotificationDto): Response<Unit> {
        return Response.success(Unit)
    }

    override suspend fun getNotifications(userId: String): List<NotificationDto> {
        throw UnsupportedOperationException("No se usa en estas pruebas")
    }

    override suspend fun getProperties(): List<FirestorePropertyDto> {
        throw UnsupportedOperationException("No se usa en estas pruebas")
    }

    override suspend fun createProperty(body: CreatePropertyRequestDto): CreatePropertyResponseDto {
        throw UnsupportedOperationException("No se usa en estas pruebas")
    }
}