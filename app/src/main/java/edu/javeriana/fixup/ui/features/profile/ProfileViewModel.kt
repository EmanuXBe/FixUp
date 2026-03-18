package edu.javeriana.fixup.ui.features.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import edu.javeriana.fixup.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
            profileImageUrl = authRepository.currentUser?.photoUrl?.toString() 
                ?: "https://firebasestorage.googleapis.com/v0/b/fixup-f2128.firebasestorage.app/o/WhatsApp%20Image%202026-03-18%20at%205.27.50%20PM.jpeg?alt=media&token=7d9a7e23-31b0-4f0a-b705-c7c9d71abe64",
            isLoading = false
        )
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val storage = FirebaseStorage.getInstance()

    /** Cierra la sesión del usuario actual. */
    fun signOut() {
        authRepository.signOut()
    }

    /** Sube una imagen a Firebase Storage y actualiza el perfil del usuario. */
    fun uploadProfileImage(imageUri: Uri) {
        val user = authRepository.currentUser ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                // Usamos el bucket configurado en el google-services.json
                val storageRef = storage.reference.child("profile_images/${user.uid}.jpg")
                
                // Subir archivo
                storageRef.putFile(imageUri).await()
                
                // Obtener URL de descarga
                val downloadUrl = storageRef.downloadUrl.await()
                
                // Actualizar perfil de Firebase Auth para que persista la URL
                val profileUpdates = userProfileChangeRequest {
                    photoUri = downloadUrl
                }
                user.updateProfile(profileUpdates).await()
                
                // Actualizar UI State
                _uiState.update { 
                    it.copy(
                        profileImageUrl = downloadUrl.toString(),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = "Error al subir la imagen: ${e.message}"
                    )
                }
            }
        }
    }
}
