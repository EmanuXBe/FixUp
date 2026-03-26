package edu.javeriana.fixup.data.datasource

import edu.javeriana.fixup.R
import edu.javeriana.fixup.ui.features.checkout.CheckoutItemUiModel
import javax.inject.Inject

/**
 * Implementación concreta de CheckoutDataSource.
 */
class CheckoutDataSourceImpl @Inject constructor() : CheckoutDataSource {
    override fun getCheckoutItems(): List<CheckoutItemUiModel> {
        return listOf(
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
    }
}
