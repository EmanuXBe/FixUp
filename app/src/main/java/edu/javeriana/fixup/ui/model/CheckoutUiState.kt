package edu.javeriana.fixup.ui.model

import edu.javeriana.fixup.ui.CheckoutItemUiModel

data class CheckoutUiState(
    val selectedAddress: String = "Direcciones guardadas",
    val selectedDate: String = "5 de octubre 2025",
    val selectedPayment: String = "Visa *1234",
    val items: List<CheckoutItemUiModel> = emptyList(),
    val subtotal: String = "$0",
    val isLoading: Boolean = false
)
