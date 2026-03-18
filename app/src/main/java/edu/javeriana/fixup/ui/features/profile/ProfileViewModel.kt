package edu.javeriana.fixup.ui.features.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.javeriana.fixup.data.repository.AuthRepository
import edu.javeriana.fixup.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val profileRepository: ProfileRepository = ProfileRepository()
) : ViewModel() {

    private val defaultImageUrl = "https://firebasestorage.googleapis.com/v0/b/fixup-f2128.firebasestorage.app/o/WhatsApp%20Image%202026-03-18%20at%205.27.50%20PM.jpeg?alt=media&token=7d9a7e23-31b0-4f0a-b705-c7c9d71abe64"

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            name = profileRepository.currentUser?.email?.substringBefore("@") ?: "Usuario",
            email = profileRepository.currentUser?.email ?: "",
            address = "Bogotá, Colombia",
            phone = "Sin número",
            role = "Cliente",
            profileImageUrl = profileRepository.currentUser?.photoUrl?.toString() ?: defaultImageUrl,
            isLoading = false
        )
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    /** Cierra la sesión del usuario actual. */
    fun signOut() {
        authRepository.signOut()
    }

    /** Sube una imagen a través del repositorio y reacciona al Result. */
    fun uploadProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            // 1. Mostrar estado de carga en la UI
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            // 2. Consumir el Result del repositorio
            val result = profileRepository.updateProfileImage(imageUri)
            
            result.onSuccess { newUrl ->
                // 3. Caso Éxito: Actualizar la imagen en la UI
                _uiState.update { 
                    it.copy(
                        profileImageUrl = newUrl,
                        isLoading = false
                    )
                }
            }.onFailure { error ->
                // 4. Caso Error: Mostrar mensaje en la UI
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = "Error al subir: ${error.message}"
                    )
                }
            }
        }
    }
}
