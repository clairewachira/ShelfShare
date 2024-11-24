package com.example.shelfshare.model.service.impl

import com.example.shelfshare.model.MpesaPaymentRequest
import com.example.shelfshare.model.PayStatus
import com.example.shelfshare.model.STKPushRequest
import com.example.shelfshare.model.STKPushResponse
import com.example.shelfshare.model.TransactionDetails
import com.example.shelfshare.model.service.PaymentService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MPesaPaymentServiceImpl @Inject constructor() : PaymentService {

    override fun initiatePayment(phoneNumber: String, amount: Double): Flow<PayStatus> = flow {
        try {
            // Emit initial status
            emit(PayStatus.Initiated)

            // Validate phone number
            if (!validatePhoneNumber(phoneNumber)) {
                emit(PayStatus.Failed("Invalid phone number format"))
                return@flow
            }

            // Simulate API call to initiate payment
            delay(1000) // Simulate network delay

            // Emit status waiting for user confirmation
            emit(PayStatus.AwaitingConfirmation)

            // Simulate user confirming payment on their phone
            delay(3000) // Simulate user action delay

            // 90% success rate simulation
            if (Math.random() < 0.9) {
                val transactionId = UUID.randomUUID().toString()
                saveTransaction(transactionId, phoneNumber, amount)
                emit(PayStatus.Completed(transactionId))
            } else {
                emit(PayStatus.Failed("Payment was declined"))
            }

        } catch (e: Exception) {
            emit(PayStatus.Failed("An error occurred: ${e.message}"))
        }
    }

    override fun validatePhoneNumber(phoneNumber: String): Boolean {
        // Basic validation for Kenyan phone numbers
        val regex = """^(?:254|\+254|0)?([71234567]\d{8})$""".toRegex()
        return regex.matches(phoneNumber)
    }

    override suspend fun getTransactionDetails(transactionId: String): TransactionDetails? {
        return transactions[transactionId]
    }

    private fun saveTransaction(transactionId: String, phoneNumber: String, amount: Double) {
        transactions[transactionId] = TransactionDetails(
            id = transactionId,
            phoneNumber = phoneNumber,
            amount = amount,
            timestamp = System.currentTimeMillis(),
            status = "COMPLETED"
        )
    }

    companion object {
        private val transactions = mutableMapOf<String, TransactionDetails>()
    }
}