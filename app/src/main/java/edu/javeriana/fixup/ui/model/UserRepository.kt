package edu.javeriana.fixup.ui.model

import kotlinx.coroutines.delay

/**
 * Repositorio para gestionar los datos del usuario.
 */
object UserRepository {
    
    /**
     * Simula la obtención de los datos del perfil del usuario con un retraso de red.
     */
    suspend fun getUserProfile(): ProfileUiState {
        delay(1000) // Simular latencia de red
        return ProfileUiState(
            name = "Gabo Peñuela",
            address = "Calle 1 # 1-99 Conjunto Alegre",
            phone = "3002001010",
            email = "gabo.penuela@javeriana.edu.co",
            role = "Cliente Estrella",
            isLoading = false
        )
    }
}
