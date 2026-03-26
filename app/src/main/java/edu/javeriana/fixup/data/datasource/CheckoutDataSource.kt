package edu.javeriana.fixup.data.datasource

import edu.javeriana.fixup.ui.features.checkout.CheckoutItemUiModel

/**
 * Contrato del Data Source para Checkout.
 */
interface CheckoutDataSource {
    fun getCheckoutItems(): List<CheckoutItemUiModel>
}
