package edu.javeriana.fixup.ui.features.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.javeriana.fixup.data.repository.AuthRepository
import edu.javeriana.fixup.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
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

    init {
        loadUserReviews()
    }

    private fun loadUserReviews() {
        // No dependemos del UID de Firebase para la carga, ya que el Repositorio usará el ID fijo "1"
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            profileRepository.getReviewsByUserId("1").onSuccess { reviews ->
                _uiState.update { it.copy(reviews = reviews, isLoading = false) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    /**
     * Guarda una reseña usando el ID fijo "1" configurado en el Repositorio.
     */
    fun saveReview(rating: Int, comment: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            profileRepository.createReview(rating, comment).onSuccess {
                loadUserReviews() // Recargamos la lista
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
    }

    /**
     * Sube una imagen y fuerza el refresco de la UI usando un cache-buster.
     */
    fun uploadProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            val result = profileRepository.updateProfileImage(imageUri)
            
            result.onSuccess { newUrl ->
                // Agregamos un timestamp a la URL para forzar a Coil a refrescar la imagen
                val freshUrl = if (newUrl.contains("?")) {
                    "$newUrl&t=${System.currentTimeMillis()}"
                } else {
                    "$newUrl?t=${System.currentTimeMillis()}"
                }

                _uiState.update { 
                    it.copy(
                        profileImageUrl = freshUrl,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = error.message 
                    )
                }
            }
        }
    }
    
    fun resetErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
