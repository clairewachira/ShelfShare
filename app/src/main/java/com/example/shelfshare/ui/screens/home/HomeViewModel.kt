package com.example.shelfshare.ui.screens.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelfshare.model.Book
import com.example.shelfshare.model.service.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storageService: StorageService
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchBooks()
    }

    fun onBookSelected(book: Book){

    }

    fun fetchBooks(category: String? = null) {
        viewModelScope.launch {
            try {
                val books = storageService.getBooks(category)
                _uiState.update {
                    it.copy(
                        books = books,
                        isLoading = false,
                        error = null,
                        selectedCategory = category
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.localizedMessage
                    )
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.isNotBlank()) {
            searchBooks(query)
        } else {
            fetchBooks()
        }
    }

    private fun searchBooks(query: String) {
        viewModelScope.launch {
            try {
                val books = storageService.searchBooks(query)
                _uiState.update {
                    it.copy(
                        books = books,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.localizedMessage
                    )
                }
            }
        }
    }

    fun addToCart(book: Book) {
        viewModelScope.launch {
            try {
                storageService.addToCart(book)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.localizedMessage)
                }
            }
        }
    }
}