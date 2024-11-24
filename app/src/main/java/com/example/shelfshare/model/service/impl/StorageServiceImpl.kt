package com.example.shelfshare.model.service.impl

import com.example.shelfshare.model.Book
import com.example.shelfshare.model.CartItem
import com.example.shelfshare.model.Order
import com.example.shelfshare.model.UserProfile
import com.example.shelfshare.model.service.AccountService
import com.example.shelfshare.model.service.StorageService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageServiceImpl
@Inject
constructor(
    private val firestore: FirebaseFirestore,
    private val auth: AccountService,
) : StorageService {

    override val books: Flow<List<Book>>
        get() = firestore.collection(BOOK_COLLECTION).dataObjects()


    override suspend fun getBooks(category: String?): List<Book> {
        val booksRef = firestore.collection(BOOK_COLLECTION).whereNotEqualTo(SELLER_ID_FIELD, auth.currentUserId)

        return if (category != null) {
            booksRef.whereEqualTo(CATEGORY_FIELD, category).get().await()
                .documents.mapNotNull { it.toObject<Book>() }
        } else {
            booksRef.get().await()
                .documents.mapNotNull { it.toObject<Book>() }
        }

    }

    override suspend fun getBook(bookId: String): Book? {
        return firestore.collection(BOOK_COLLECTION).document(bookId).get().await().toObject()
    }

    override suspend fun searchBooks(query: String): List<Book> {
        val allBooks = getBooks()
        return allBooks.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
        }
    }

    override suspend fun addToCart(book: Book) {
        val userId = auth.currentUserId
        val cartItem = CartItem(
            bookId = book.id,
            userId = userId
        )

        firestore.collection(CART_COLLECTION)
            .document(cartItem.cartItemId)
            .set(cartItem)
            .await()
    }

    override suspend fun removeFromCart(cartItemId: String) {
        firestore.collection(CART_COLLECTION)
            .document(cartItemId)
            .delete()
            .await()
    }

    override suspend fun getCartItems(userId: String): List<CartItem> {
        return firestore.collection(CART_COLLECTION)
            .whereEqualTo(USER_ID_FIELD, userId)
            .get().await()
            .documents.mapNotNull { it.toObject<CartItem>() }
    }

    override suspend fun getBooksBySeller(userId: String): List<Book> {
        return firestore.collection(BOOK_COLLECTION)
            .whereEqualTo(SELLER_ID_FIELD, userId)
            .get().await()
            .documents.mapNotNull { it.toObject<Book>() }
    }

    override suspend fun addBook(newBook: Book) {
        firestore.collection(BOOK_COLLECTION).add(newBook).await()
    }

    override suspend fun deleteBook(id: String) {
        firestore.collection(BOOK_COLLECTION).document(id).delete().await()
    }

    override suspend fun getUserProfile(userId: String): UserProfile? {
        return firestore.collection(USER_COLLECTION).document(userId).get().await().toObject()
    }

    override suspend fun getPurchase(): List<Order> {
        return firestore.collection(ORDERS_COLLECTION)
            .whereEqualTo(BUYER_ID_FIELD, auth.currentUserId)
            .get().await()
            .documents.mapNotNull { it.toObject() }
    }

    override suspend fun getSales(): List<Order> {
        return firestore.collection(ORDERS_COLLECTION)
            .whereEqualTo(SELLER_ID_FIELD, auth.currentUserId)
            .get().await()
            .documents.mapNotNull { it.toObject() }
    }

    override suspend fun addOrder(order: Order) {
        firestore.collection(ORDERS_COLLECTION).document(order.orderId).set(order).await()
    }

    override suspend fun initUserProfile(email: String, username: String, userId: String) {
        firestore.collection(USER_COLLECTION).document(userId).set(UserProfile(email, username, profilePictureUrl = "")).await()
    }

    override suspend fun updateUserProfile(userId: String, updatedProfile: UserProfile) {
        firestore.collection(USER_COLLECTION)
            .document(userId)
            .set(updatedProfile)
            .await()
    }

    override suspend fun getOrderById(orderId: String): Order? {
        return firestore.collection(ORDERS_COLLECTION)
            .document(orderId)
            .get()
            .await()
            .toObject()
    }

    override suspend fun getOrders(): List<Order> {
        return firestore.collection(ORDERS_COLLECTION)
            .get().await()
            .documents.mapNotNull { it.toObject() }
    }


    companion object {
        private const val BOOK_COLLECTION = "books"
        private const val CART_COLLECTION = "carts"
        private const val USER_COLLECTION = "users"
        private const val ORDERS_COLLECTION = "orders"
        private const val USER_ID_FIELD = "userId"
        private const val SELLER_ID_FIELD = "sellerId"
        private const val CATEGORY_FIELD = "category"
        private const val BUYER_ID_FIELD = "buyerId"
    }

}

fun removeCurlyBrackets(id: String): String {
    return id.removePrefix("{").removeSuffix("}")
}