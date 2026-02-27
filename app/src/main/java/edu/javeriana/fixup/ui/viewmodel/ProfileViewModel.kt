package edu.javeriana.fixup.ui.viewmodel

import androidx.lifecycle.ViewModel
import edu.javeriana.fixup.ui.model.ProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState(
        name = "Gabo peñuela",
        address = "Calle 1 # 1-99 conjunto Alegre",
        phone = "3002001010",
        email = "jhondoe@siemprealegre.com",
        role = "Cliente estrella"
    ))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun updateName(newName: String) {
        _uiState.value = _uiState.value.copy(name = newName)
    }

    fun updateAddress(newAddress: String) {
        _uiState.value = _uiState.value.copy(address = newAddress)
    }

    fun updatePhone(newPhone: String) {
        _uiState.value = _uiState.value.copy(phone = newPhone)
    }

    fun updateEmail(newEmail: String) {
        _uiState.value = _uiState.value.copy(email = newEmail)
    }
}
