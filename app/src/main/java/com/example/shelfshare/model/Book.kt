package com.example.shelfshare.model

import com.google.firebase.firestore.DocumentId

data class Book(
    @DocumentId val id: String = "",
    val title: String = "",
    val description: String = "",
    val price: Int = 0,
    val category: String = "",
    val imageUrl: String? = null,
    val sellerId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
