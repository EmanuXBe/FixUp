package edu.javeriana.fixup.data.repository

import edu.javeriana.fixup.data.datasource.CheckoutDataSource
import edu.javeriana.fixup.ui.features.checkout.CheckoutItemUiModel

class CheckoutRepository(
    private val dataSource: CheckoutDataSource = CheckoutDataSource()
) {
    fun getCheckoutItems(): Result<List<CheckoutItemUiModel>> {
        return try {
            Result.success(dataSource.getCheckoutItems())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
