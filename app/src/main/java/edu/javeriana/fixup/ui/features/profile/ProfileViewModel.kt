package edu.javeriana.fixup.ui.features.profile

import androidx.lifecycle.ViewModel
import edu.javeriana.fixup.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            name = authRepository.currentUser?.email?.substringBefore("@") ?: "Usuario",
            email = authRepository.currentUser?.email ?: "",
            address = "Bogotá, Colombia",
            phone = "Sin número",
            role = "Cliente",
            isLoading = false
        )
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    /** Cierra la sesión del usuario actual. */
    fun signOut() {
        authRepository.signOut()
    }
}