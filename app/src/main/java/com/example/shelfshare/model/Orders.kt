package com.example.shelfshare.model

import com.google.firebase.firestore.DocumentId
import java.util.UUID

data class Order(
    val orderId: String = UUID.randomUUID().toString(),
    val bookId: String = "",
    val bookTitle: String = "",
    val sellerId: String = "",
    val buyerId: String = "",
    val price: Double = 0.0,
    val orderDate: Long = System.currentTimeMillis(),
    val status: OrderStatus = OrderStatus.PENDING,
    val paymentDetails: PaymentDetails = PaymentDetails()
)

data class UserReference(
    val userId: String,
    val username: String,
    val email: String
)

enum class OrderStatus {
    PENDING,           // Order created, awaiting payment
    PAID,              // Payment confirmed
    SHIPPED,           // Book sent by seller
    DELIVERED,         // Book received by buyer
    CANCELLED,         // Order cancelled
    REFUNDED           // Payment returned
}

data class PaymentDetails(
    val transactionId: String? = null,
    val mpesaCode: String? = null,
    val paymentMethod: PaymentMethod = PaymentMethod.MPESA,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val amount: Double = 0.0
)

enum class PaymentMethod {
    MPESA,
    CARD,
    BANK_TRANSFER
}

enum class PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED,
    REFUNDED,
}