package com.example.shelfshare.model.service

import com.example.shelfshare.model.MpesaPaymentRequest
import com.example.shelfshare.model.MpesaPaymentResponse
import javax.inject.Inject

class MpesaRepository @Inject constructor(
    private val apiService: MpesaApiService
) {
    suspend fun initiateSTKPush(request: MpesaPaymentRequest): MpesaPaymentResponse {
        return apiService.initiatePayment(request)
    }
}
