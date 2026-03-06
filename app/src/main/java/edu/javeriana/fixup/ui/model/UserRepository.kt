package edu.javeriana.fixup.ui.model

object UserRepository {
    fun getUserProfile(): UserModel {
        return UserModel(
            id = "1",
            name = "Juan Pérez",
            email = "juan.perez@example.com",
            phone = "+57 300 123 4567",
            address = "Calle 100 #15-20, Bogotá",
            role = "Arrendatario"
        )
    }
}
