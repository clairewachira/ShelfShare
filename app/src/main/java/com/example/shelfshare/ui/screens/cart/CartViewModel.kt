package com.example.shelfshare.ui.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelfshare.model.*
import com.example.shelfshare.model.service.AccountService
import com.example.shelfshare.model.service.PaymentService
import com.example.shelfshare.model.service.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val storageService: StorageService,
    private val accountService: AccountService,
) : ViewModel() {

    private val _cartState = MutableStateFlow<CartUiState>(CartUiState.Loading)
    val cartState: StateFlow<CartUiState> = _cartState.asStateFlow()

    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()

    init {
        fetchCartItems()
    }


    private fun fetchCartItems() {
        viewModelScope.launch {
            _cartState.value = CartUiState.Loading
            try {
                val userId = accountService.currentUserId
                val cartItems = storageService.getCartItems(userId)
                val cartItemsWithBooks = cartItems.map { cartItem ->
                        val book = storageService.getBook(cartItem.bookId)
                        CartItemWithBook(cartItem, book)
                }
                updateCartState(cartItemsWithBooks)
            } catch (e: Exception) {
                _cartState.value = CartUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    private fun updateCartState(items: List<CartItemWithBook>) {
        val selectedItems = items.count { it.isSelected }
        val totalAmount = items
           .filter { it.isSelected }
            .sumOf { it.book?.price ?: 0 }

        _cartState.value = CartUiState.Success(
            items = items,
            totalAmount = totalAmount.toDouble(),
            selectedItems = selectedItems
        )
    }

    fun removeFromCart(cartItemId: String) {
        viewModelScope.launch {
            try {
                storageService.removeFromCart(cartItemId)
                // State will be automatically updated via Flow collection
                fetchCartItems()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun toggleItemSelection(cartItemId: String) {
        val currentState = _cartState.value
        if (currentState is CartUiState.Success) {
            val updatedItems = currentState.items.map { item ->
                if (item.cartItem.cartItemId == cartItemId) {
                    item.copy(isSelected = !item.isSelected)
                } else item
            }
            updateCartState(updatedItems)
        }
    }


}