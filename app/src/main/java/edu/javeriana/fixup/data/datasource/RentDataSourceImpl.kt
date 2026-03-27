package edu.javeriana.fixup.data.datasource

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import edu.javeriana.fixup.ui.model.PropertyModel
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

/**
 * Implementación de RentDataSource que usa Retrofit para datos y Firebase Storage para imágenes.
 */
class RentDataSourceImpl @Inject constructor(
    private val apiService: FixUpApiService,
    private val storage: FirebaseStorage
) : RentDataSource {

    override suspend fun getRentProperties(): List<PropertyModel> {
        return apiService.getServices()
    }

    override suspend fun getPropertyById(id: Int): PropertyModel {
        return apiService.getServiceById(id)
    }

    override suspend fun createProperty(property: PropertyModel, imageUri: Uri): PropertyModel {
        // 1. Subir imagen a Firebase Storage
        val filename = UUID.randomUUID().toString()
        val ref = storage.getReference("properties/$filename.jpg")
        
        ref.putFile(imageUri).await()
        
        // 2. Obtener la URL de descarga
        val downloadUrl = ref.downloadUrl.await().toString()
        
        // 3. Crear el objeto con la URL de la imagen y enviarlo a la API
        val propertyWithImage = property.copy(imageUrl = downloadUrl)
        
        return apiService.createService(propertyWithImage)
    }
}
