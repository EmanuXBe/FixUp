package edu.javeriana.fixup.ui.features.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.google.firebase.auth.FirebaseAuth
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
        loadUserData()
        loadUserReviews()
    }

    private fun loadUserData() {
        val user = profileRepository.currentUser ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            profileRepository.getUserData(user.uid).onSuccess { data ->
                if (data != null) {
                    _uiState.update {
                        it.copy(
                            name = data["name"] as? String ?: user.displayName ?: user.email?.substringBefore("@") ?: "Usuario",
                            phone = data["phone"] as? String ?: "Sin número",
                            address = data["address"] as? String ?: "Sin dirección",
                            role = data["role"] as? String ?: "Cliente",
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    /**
     * Actualiza la información del perfil en Auth y Firestore.
     */
    fun updateProfileInfo(name: String, email: String, phone: String, address: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            profileRepository.updateProfileData(name, email, phone, address).onSuccess {
                _uiState.update { 
                    it.copy(
                        name = name,
                        email = email,
                        phone = phone,
                        address = address,
                        isLoading = false
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    private fun loadUserReviews() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            _uiState.update { it.copy(reviews = emptyList(), isLoading = false) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            profileRepository.getReviewsByUserId(uid).onSuccess { reviews ->
                _uiState.update { it.copy(reviews = reviews, isLoading = false) }
            }.onFailure { error ->
                _uiState.update { it.copy(reviews = emptyList(), isLoading = false, errorMessage = error.message) }
            }
        }
    }

    /**
     * Guarda una reseña usando el ID dinámico del usuario autenticado.
     */
    fun saveReview(rating: Int, comment: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            _uiState.update { it.copy(errorMessage = "Usuario no autenticado") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            profileRepository.createReview(uid, rating, comment).onSuccess {
                loadUserReviews() // Recargamos la lista
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    /**
     * Borra una reseña y actualiza la lista.
     */
    fun deleteReview(reviewId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            profileRepository.deleteReview(reviewId.toString()).onSuccess {
                loadUserReviews()
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    /**
     * Actualiza una reseña y refresca la lista.
     */
    fun updateReview(reviewId: Int, rating: Int, comment: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            _uiState.update { it.copy(errorMessage = "Usuario no autenticado") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            profileRepository.updateReview(reviewId.toString(), uid, rating, comment).onSuccess {
                loadUserReviews()
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
