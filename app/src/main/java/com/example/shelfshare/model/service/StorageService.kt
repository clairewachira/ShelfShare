package com.example.shelfshare.model.service

import com.example.shelfshare.model.Book
import com.example.shelfshare.model.CartItem
import com.example.shelfshare.model.Order
import com.example.shelfshare.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface StorageService {
    val books: Flow<List<Book>>
    suspend fun getBooks(category: String? = null): List<Book>
    suspend fun getBook(bookId: String) : Book?
    suspend fun searchBooks(query: String): List<Book>
    suspend fun addToCart(book: Book)
    suspend fun removeFromCart(cartItemId: String)
    suspend fun getCartItems(userId: String): List<CartItem>
    suspend fun getBooksBySeller(userId: String): List<Book>
    suspend fun addBook(newBook: Book)
    suspend fun deleteBook(id: String)


    suspend fun getUserProfile(userId: String) : UserProfile?
    suspend fun getPurchase(): List<Order>
    suspend fun getSales(): List<Order>
    suspend fun addOrder(order: Order)

    suspend fun initUserProfile(email: String, username: String, userId: String)
    suspend fun updateUserProfile(userId: String, updatedProfile: UserProfile)

    suspend fun getOrderById(orderId: String): Order?
    suspend fun getOrders(): List<Order>
}