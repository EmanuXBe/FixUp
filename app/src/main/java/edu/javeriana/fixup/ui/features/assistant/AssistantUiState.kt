package edu.javeriana.fixup.ui.features.assistant

data class FixerModel(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val category: String = "",
    val availability: String = "",
    val profileImageUrl: String? = null
)

data class AssistantUiState(
    val isLoading: Boolean = false,
    val selectedCategory: String? = null,
    val selectedUrgency: String? = null,
    val fixers: List<FixerModel> = emptyList(),
    val error: String? = null
)
