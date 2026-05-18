package edu.javeriana.fixup.ui.features.assistant

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.javeriana.fixup.data.util.FirebaseSeeder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AssistantViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val seeder: FirebaseSeeder
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssistantUiState(isLoading = false))
    val uiState: StateFlow<AssistantUiState> = _uiState.asStateFlow()

    fun selectCategory(category: String) {
        val newCategory = if (_uiState.value.selectedCategory == category) null else category
        _uiState.value = _uiState.value.copy(selectedCategory = newCategory)
        searchFixers()
    }

    fun selectUrgency(urgency: String) {
        val newUrgency = if (_uiState.value.selectedUrgency == urgency) null else urgency
        _uiState.value = _uiState.value.copy(selectedUrgency = newUrgency)
        searchFixers()
    }

    fun searchFixers() {
        val category = _uiState.value.selectedCategory
        val urgency = _uiState.value.selectedUrgency

        if (category == null && urgency == null) {
            _uiState.value = _uiState.value.copy(fixers = emptyList(), error = null)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            // Lectura sin filtros compuestos para evitar índices compuestos. En producción,
            // si la regla bloquea la lectura, la sembrada o el doc faltante, lo registramos
            // y caemos a vista vacía SIN mostrar error rojo (el usuario no puede actuar sobre él).
            val readResult = runCatching {
                var snapshot = firestore.collection("fixers").get().await()
                Log.d(TAG, "Primera lectura /fixers: ${snapshot.size()} docs")
                if (snapshot.isEmpty) {
                    Log.d(TAG, "Colección /fixers vacía → invocando seedFixers()")
                    val seedResult = seeder.seedFixers()
                    if (seedResult.isFailure) {
                        // seedFixers devuelve Result<Unit>; un runCatching externo no
                        // ve esta falla sin desempacar el Result manualmente.
                        Log.e(TAG, "seedFixers() retornó Failure", seedResult.exceptionOrNull())
                    } else {
                        Log.d(TAG, "seedFixers() retornó Success")
                    }
                    snapshot = firestore.collection("fixers").get().await()
                    Log.d(TAG, "Segunda lectura /fixers: ${snapshot.size()} docs")
                }
                snapshot.documents.map { doc ->
                    FixerModel(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        phone = doc.getString("phone") ?: "",
                        address = doc.getString("address") ?: "",
                        category = doc.getString("category") ?: "",
                        availability = doc.getString("availability") ?: "",
                        profileImageUrl = doc.getString("profileImageUrl")
                    )
                }
            }

            readResult.onSuccess { all ->
                val filtered = all.filter { fixer ->
                    (category == null || fixer.category.equals(category, ignoreCase = true)) &&
                    (urgency == null || fixer.availability.equals(urgency, ignoreCase = true))
                }
                Log.d(TAG, "Filtrado en memoria: ${all.size} totales → ${filtered.size} match (category=$category, urgency=$urgency)")
                _uiState.value = _uiState.value.copy(
                    fixers = filtered,
                    isLoading = false,
                    error = null
                )
            }.onFailure { e ->
                Log.e(TAG, "Error consultando /fixers: ${e.javaClass.simpleName} — ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    fixers = emptyList(),
                    isLoading = false,
                    error = null
                )
            }
        }
    }

    companion object {
        private const val TAG = "AssistantViewModel"
    }
}
