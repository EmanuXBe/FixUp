package edu.javeriana.fixup.ui.features.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.javeriana.fixup.data.repository.AuthRepository
import edu.javeriana.fixup.data.repository.UserRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val isUserLoggedIn: Boolean
        get() = authRepository.isUserLoggedIn

    init {
        refreshFcmToken()
    }

    // Refresca el token FCM en cada arranque, no solo al hacer login.
    // Cubre el caso donde el usuario ya estaba autenticado y nunca pasa por LogInViewModel.
    private fun refreshFcmToken() {
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val token = FirebaseMessaging.getInstance().token.await()
                userRepository.updateFcmToken(uid, token)
            } catch (_: Exception) { }
        }
    }
}
