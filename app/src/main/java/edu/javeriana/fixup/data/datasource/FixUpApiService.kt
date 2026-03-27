package edu.javeriana.fixup.data.datasource

import edu.javeriana.fixup.ui.model.PropertyModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FixUpApiService {
    @GET("api/services")
    suspend fun getServices(): List<PropertyModel>

    @GET("api/services/{id}")
    suspend fun getServiceById(@Path("id") id: Int): PropertyModel

    @POST("api/services")
    suspend fun createService(@Body service: PropertyModel): PropertyModel

    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") id: String): Any

    @POST("api/reviews")
    suspend fun createReview(@Body review: Any): Any
}
