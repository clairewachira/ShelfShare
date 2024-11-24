package com.example.shelfshare.model.service

import com.example.shelfshare.model.MpesaPaymentRequest
import com.example.shelfshare.model.PayStatus
import com.example.shelfshare.model.STKPushRequest
import com.example.shelfshare.model.STKPushResponse
import com.example.shelfshare.model.TransactionDetails
import kotlinx.coroutines.flow.Flow

interface PaymentService {
    /**
     * Initiates a payment transaction
     * @param phoneNumber The M-Pesa phone number
     * @param amount The amount to be paid
     * @return Flow of PaymentStatus updates
     */
    fun initiatePayment(phoneNumber: String, amount: Double): Flow<PayStatus>

    /**
     * Validates a phone number format
     * @param phoneNumber The phone number to validate
     * @return true if the phone number is valid
     */
    fun validatePhoneNumber(phoneNumber: String): Boolean

    /**
     * Gets the transaction details
     * @param transactionId The ID of the transaction
     * @return Transaction details or null if not found
     */
    suspend fun getTransactionDetails(transactionId: String): TransactionDetails?

}
