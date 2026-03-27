package edu.javeriana.fixup.data.datasource

import edu.javeriana.fixup.ui.model.PropertyModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FixUpApiService {
    @GET("api/servicios")
    suspend fun getServicios(): List<PropertyModel>

    @POST("api/servicios")
    suspend fun createServicio(@Body servicio: PropertyModel): PropertyModel
}
