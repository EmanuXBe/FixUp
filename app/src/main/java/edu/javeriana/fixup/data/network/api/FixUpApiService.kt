package edu.javeriana.fixup.data.network.api

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
}
