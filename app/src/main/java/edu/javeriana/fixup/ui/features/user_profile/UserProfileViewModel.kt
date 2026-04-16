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
            
            profileRepository.getUserData(userId).onSuccess { data ->
                if (data != null) {
                    val user = UserModel(
                        id = userId,
                        name = data["name"] as? String ?: "Usuario sin nombre",
                        email = data["email"] as? String ?: "Sin correo",
                        phone = data["phone"] as? String ?: "Sin teléfono",
                        address = data["address"] as? String ?: "Sin dirección",
                        role = data["role"] as? String ?: "Miembro",
                        profileImageUrl = data["profileImageUrl"] as? String ?: "https://firebasestorage.googleapis.com/v0/b/fixup-f2128.firebasestorage.app/o/WhatsApp%20Image%202026-03-18%20at%205.27.50%20PM.jpeg?alt=media&token=7d9a7e23-31b0-4f0a-b705-c7c9d71abe64"
                    )

                    profileRepository.getReviewsByUserId(userId).onSuccess { reviews ->
                        _uiState.update { 
                            it.copy(
                                user = user,
                                reviews = reviews,
                                isLoading = false,
                                error = null
                            )
                        }
                    }.onFailure { error ->
                        _uiState.update { 
                            it.copy(
                                user = user,
                                isLoading = false,
                                error = "Error al cargar reseñas: ${error.message}"
                            )
                        }
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Usuario no encontrado") }
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }
}
