package com.example.shelfshare.model

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class CartItem(
    val bookId: String = "",
    val userId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val cartItemId: String = UUID.randomUUID().toString()
)

data class CartItemWithBook(
    val cartItem: CartItem,
    val book: Book?,
    val isSelected: Boolean = true
)

data class MpesaPaymentRequest(
    val username: String,
    @SerializedName("network_code") val networkCode: String = "63902",
    val amount: Int,
    @SerializedName("phone_number") val phoneNumber: String,
    val narration: String = "Payment for goods",
    val currency: String = "KES",
    @SerializedName("callback_url") val callbackUrl: String = "https://example.com/callback"
)

data class MpesaPaymentResponse(
    val channel: String,
    val success: Boolean,
    val message: String,
    @SerializedName("transaction_reference") val transactionReference: String
)



sealed class CartUiState {
    data object Loading : CartUiState()
    data class Success(
        val items: List<CartItemWithBook> = emptyList(),
        val totalAmount: Double = 0.0,
        val selectedItems: Int = 0
    ) : CartUiState()
    data class Error(val message: String) : CartUiState()
}

sealed class PaymentState {
    data object Idle : PaymentState()
    data object Processing : PaymentState()
    data class Success(val transactionId: String) : PaymentState()
    data class Error(val message: String) : PaymentState()
}

sealed class PayStatus {
    data object Initiated : PayStatus()
    data object AwaitingConfirmation : PayStatus()
    data class Completed(val transactionId: String) : PayStatus()
    data class Failed(val reason: String) : PayStatus()
}

data class TransactionDetails(
    val id: String,
    val phoneNumber: String,
    val amount: Double,
    val timestamp: Long,
    val status: String
)