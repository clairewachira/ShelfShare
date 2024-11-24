package com.example.shelfshare.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelfshare.model.MpesaPaymentRequest
import com.example.shelfshare.model.PaymentState
import com.example.shelfshare.model.service.MpesaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MpesaViewModel @Inject constructor(
    private val repository: MpesaRepository
) : ViewModel() {

    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState

    fun initiatePayment(phoneNumber: String, amount: Double) {
        viewModelScope.launch {
            _paymentState.value = PaymentState.Processing
            try {
                val request = MpesaPaymentRequest(
                    username = "jerrylegend254",
                    networkCode = "63902",
                    amount = amount.toInt(),
                    phoneNumber = phoneNumber,
                    narration = "Payment for goods",
                    currency = "KES",
                    callbackUrl = "https://example.com/callback"
                )

                val response = repository.initiateSTKPush(request)
                if (response.success) {
                    _paymentState.value = PaymentState.Success(response.transactionReference)
                } else {
                    _paymentState.value = PaymentState.Error(response.message)
                }
            } catch (e: Exception) {
                _paymentState.value = PaymentState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}
