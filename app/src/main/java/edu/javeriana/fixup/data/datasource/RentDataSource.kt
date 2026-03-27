package edu.javeriana.fixup.data.datasource

import android.net.Uri
import edu.javeriana.fixup.ui.model.PropertyModel

/**
 * Contrato del Data Source para Rent.
 */
interface RentDataSource {
    suspend fun getRentProperties(): List<PropertyModel>
    suspend fun getPropertyById(id: Int): PropertyModel
    suspend fun createProperty(property: PropertyModel, imageUri: Uri): PropertyModel
}
