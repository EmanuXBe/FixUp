package edu.javeriana.fixup.ui.features.review_map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.javeriana.fixup.data.repository.ArticleMapRepository
import edu.javeriana.fixup.ui.model.ArticleMapModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewMapViewModel @Inject constructor(
    private val repository: ArticleMapRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewMapUiState())
    val uiState: StateFlow<ReviewMapUiState> = _uiState.asStateFlow()

    init {
        loadArticles()
    }

    fun loadArticles() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getArticlesWithLocation()
                .onSuccess { articles ->
                    _uiState.update { it.copy(isLoading = false, articles = articles) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun onMarkerClick(article: ArticleMapModel) {
        _uiState.update { it.copy(selectedArticle = article) }
    }

    fun onDismissInfo() {
        _uiState.update { it.copy(selectedArticle = null) }
    }
}
