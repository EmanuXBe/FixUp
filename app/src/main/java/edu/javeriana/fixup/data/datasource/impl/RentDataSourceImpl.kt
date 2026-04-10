package edu.javeriana.fixup.data.datasource.impl

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import edu.javeriana.fixup.data.datasource.interfaces.RentDataSource
import edu.javeriana.fixup.data.network.api.FixUpApiService
import edu.javeriana.fixup.ui.model.PropertyModel
import javax.inject.Inject

/**
 * Implementación de RentDataSource con datos quemados (Mock) para Alquiler.
 * Se han migrado los servicios reales a FeedDataSource.
 */
class RentDataSourceImpl @Inject constructor() : RentDataSource {

    private val mockProperties = listOf(
        PropertyModel(
            id = 101,
            title = "Apartamento en Chapinero",
            description = "Hermoso apartamento amoblado, cerca a universidades y zonas comerciales. Excelente iluminación natural.",
            price = 2500000.0,
            location = "Chapinero Alto, Bogotá",
            imageUrl = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=60"
        ),
        PropertyModel(
            id = 102,
            title = "Casa Campestre en Chía",
            description = "Amplia casa con zonas verdes, 3 habitaciones, 4 baños y estudio. Ideal para familias que buscan tranquilidad.",
            price = 4800000.0,
            location = "Vía Guaymaral, Chía",
            imageUrl = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=60"
        ),
        PropertyModel(
            id = 103,
            title = "Estudio Loft en El Retiro",
            description = "Moderno loft con acabados industriales. Edificio con gimnasio y terraza comunitaria.",
            price = 3200000.0,
            location = "Barrio El Retiro, Bogotá",
            imageUrl = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=60"
        ),
        PropertyModel(
            id = 104,
            title = "Local Comercial en Usaquén",
            description = "Excelente ubicación para negocio gastronómico. Amplio espacio y zona de alto tráfico peatonal.",
            price = 6000000.0,
            location = "Plaza de Usaquén, Bogotá",
            imageUrl = "https://images.unsplash.com/photo-1497366216548-37526070297c?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=60"
        )
    )

    override suspend fun getRentProperties(): List<PropertyModel> {
        // Retornamos datos locales en lugar de llamar a la API
        return mockProperties
    }

    override suspend fun getPropertyById(id: Int): PropertyModel {
        return mockProperties.find { it.id == id } 
            ?: throw Exception("Propiedad no encontrada en los datos locales")
    }

    override suspend fun createProperty(property: PropertyModel, imageUri: Uri): PropertyModel {
        // Simulamos la creación retornando el mismo objeto con un ID generado
        return property.copy(id = (100..999).random())
    }
}
