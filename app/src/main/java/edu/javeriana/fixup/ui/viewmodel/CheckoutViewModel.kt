package edu.javeriana.fixup.ui.viewmodel

import androidx.lifecycle.ViewModel
import edu.javeriana.fixup.R
import edu.javeriana.fixup.ui.CheckoutItemUiModel
import edu.javeriana.fixup.ui.model.CheckoutUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CheckoutViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    init {
        loadCheckoutData()
    }

    private fun loadCheckoutData() {
        val items = listOf(
            CheckoutItemUiModel(
                imageRes = R.drawable.cocina,
                category = "Iluminacion",
                title = "Luces para entrada",
                description = "Instalacion incluida",
                price = "$350.000"
            ),
            CheckoutItemUiModel(
                imageRes = R.drawable.comedor,
                category = "Lavanderia",
                title = "Crea tu zona de lavado",
                description = "Materiales cotizados",
                price = "$1.550.000"
            )
        )
        _uiState.update { 
            it.copy(
                items = items,
                subtotal = "$1.900.000"
            )
        }
    }
}
