package com.example.shelfshare.ui.screens.home

import com.example.shelfshare.model.Book

data class HomeUiState(
    val books: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCategory: String? = null,
    val searchQuery: String = ""
)