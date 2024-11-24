package com.example.shelfshare.ui.screens.sell

import android.net.Uri
import com.example.shelfshare.model.Book

data class SellUiState(
    val bookTitle: String = "",
    val bookDescription: String = "",
    val bookImageUrl: String = "",
    val bookPrice: String = "",
    val bookCategory: String = "",
    val bookImageUri: Uri? = null,
    val userListings: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
