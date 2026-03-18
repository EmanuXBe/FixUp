package edu.javeriana.fixup.data.repository

import edu.javeriana.fixup.data.datasource.RentDataSource
import edu.javeriana.fixup.ui.model.PropertyModel

class RentRepository(
    private val dataSource: RentDataSource = RentDataSource()
) {
    fun getProperties(): Result<List<PropertyModel>> {
        return try {
            Result.success(dataSource.getRentProperties())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
