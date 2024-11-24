package com.example.shelfshare.ui.screens.sell

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelfshare.model.Book
import com.example.shelfshare.model.service.AccountService
import com.example.shelfshare.model.service.LogService
import com.example.shelfshare.model.service.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SellViewModel @Inject constructor(
    private val storageService: StorageService,
    private val authService: AccountService
) : ViewModel() {
    private val _uiState = MutableStateFlow(SellUiState())
    val uiState: StateFlow<SellUiState> = _uiState.asStateFlow()

    init {
        fetchUserListings()
    }

    private fun fetchUserListings() {
        viewModelScope.launch {
            try {
                val userId = authService.currentUserId
                val userBooks = storageService.getBooksBySeller(userId)
                _uiState.update {
                    it.copy(
                        userListings = userBooks,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.localizedMessage,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun updateBookField(field: BookField, value: String) {
        _uiState.update {
            when (field) {
                BookField.TITLE -> it.copy(bookTitle = value)
                BookField.DESCRIPTION -> it.copy(bookDescription = value)
                BookField.PRICE -> it.copy(bookPrice = value)
                BookField.CATEGORY -> it.copy(bookCategory = value)
                BookField.IMAGE -> it.copy(bookImageUrl = value)
            }
        }
    }

    fun addBook() {
        viewModelScope.launch {
            try {
                // Validate inputs
                val price = _uiState.value.bookPrice.toIntOrNull()
                    ?: throw IllegalArgumentException("Invalid price")

                val newBook = Book(
                    title = _uiState.value.bookTitle,
                    description = _uiState.value.bookDescription,
                    price = price,
                    category = _uiState.value.bookCategory,
                    sellerId = authService.currentUserId,
                    imageUrl = _uiState.value.bookImageUrl
                )

                // Add book to storage
                storageService.addBook(newBook)

                // Reset form and refresh listings
                _uiState.update {
                    it.copy(
                        bookTitle = "",
                        bookDescription = "",
                        bookPrice = "",
                        bookCategory = "",
                        bookImageUri = null,
                        bookImageUrl = ""
                    )
                }
                fetchUserListings()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.localizedMessage)
                }
            }
        }
    }

    fun deleteBook(book: Book) {
        viewModelScope.launch {
            try {
                storageService.deleteBook(book.id)
                fetchUserListings()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.localizedMessage)
                }
            }
        }
    }
}

// Enum for book input fields
enum class BookField {
    TITLE, DESCRIPTION, PRICE, CATEGORY, IMAGE
}