package edu.javeriana.fixup.data.repository

import android.net.Uri
import edu.javeriana.fixup.data.datasource.RentDataSource
import edu.javeriana.fixup.ui.model.PropertyModel
import javax.inject.Inject

class RentRepository @Inject constructor(
    private val dataSource: RentDataSource
) {
    suspend fun getProperties(): Result<List<PropertyModel>> {
        return try {
            val properties = dataSource.getRentProperties()
            Result.success(properties)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createProperty(property: PropertyModel, imageUri: Uri): Result<PropertyModel> {
        return try {
            val created = dataSource.createProperty(property, imageUri)
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
