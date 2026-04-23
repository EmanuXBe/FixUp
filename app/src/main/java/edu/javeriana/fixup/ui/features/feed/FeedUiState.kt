package edu.javeriana.fixup.ui.features.feed

data class FeedUiState(
    val searchQuery: String = "",
    val categories: List<CategoryItemModel> = emptyList(),
    val publications: List<PublicationCardModel> = emptyList(),
    val isLoading: Boolean = false,
    val isConnected: Boolean = true
)

data class CategoryItemModel(val imageRes: Int, val title: String)
data class PublicationCardModel(
    val id: String,
    val imageUrl: Any, // Puede ser un Int (recurso) o String (URL)
    val title: String,
    val price: String,
    val description: String? = null,
    val location: String? = null,
    val authorId: String? = null
)
