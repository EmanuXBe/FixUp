package edu.javeriana.fixup.ui.features.user_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.javeriana.fixup.data.repository.ProfileRepository
import edu.javeriana.fixup.ui.model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    fun loadUserProfile(userId: String?) {
        if (userId == null) {
            _uiState.update { it.copy(error = "ID de usuario no proporcionado") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Simulamos la obtención de datos del usuario. 
            // En una app real, el repositorio tendría un método getUserById(userId)
            val mockUser = UserModel(
                id = userId,
                name = "Usuario $userId",
                email = "user$userId@example.com",
                phone = "+57 300 000 0000",
                address = "Bogotá, Colombia",
                role = "Miembro de FixUp",
                profileImageUrl = "https://firebasestorage.googleapis.com/v0/b/fixup-f2128.firebasestorage.app/o/WhatsApp%20Image%202026-03-18%20at%205.27.50%20PM.jpeg?alt=media&token=7d9a7e23-31b0-4f0a-b705-c7c9d71abe64"
            )

            profileRepository.getReviewsByUserId(userId).onSuccess { reviews ->
                _uiState.update { 
                    it.copy(
                        user = mockUser,
                        reviews = reviews,
                        isLoading = false,
                        error = null
                    )
                }
            }.onFailure { error ->
                _uiState.update { 
                    it.copy(
                        user = mockUser, // Aún mostramos el usuario si las reseñas fallan
                        isLoading = false,
                        error = "Error al cargar reseñas: ${error.message}"
                    )
                }
            }
        }
    }
}
