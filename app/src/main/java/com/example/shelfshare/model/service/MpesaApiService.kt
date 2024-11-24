package com.example.shelfshare.model.service

import com.example.shelfshare.model.MpesaPaymentRequest
import com.example.shelfshare.model.MpesaPaymentResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface MpesaApiService {
    @POST("api/v2/payments")
    suspend fun initiatePayment(
        @Body request: MpesaPaymentRequest
    ): MpesaPaymentResponse
}
