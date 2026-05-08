package edu.javeriana.fixup.data.datasource.interfaces

import android.net.Uri
import edu.javeriana.fixup.ui.model.PropertyModel

interface RentDataSource {

    /** Retorna la lista de inmuebles (API real con fallback a mocks). */
    suspend fun getRentProperties(): List<PropertyModel>

    /** Retorna un inmueble por su ID de string (Firestore o mock). */
    suspend fun getPropertyById(id: String): PropertyModel

    /**
     * Publica un nuevo inmueble:
     *   1. Sube imágenes a Firebase Storage → obtiene URLs
     *   2. Llama a POST /api/properties con los datos y las URLs
     * @return propertyId generado por Firestore
     */
    suspend fun createProperty(
        userId: String,
        titulo: String,
        ubicacion: String,
        descripcion: String,
        precio: Double,
        tipo: String,
        imageUris: List<Uri>
    ): String
}
