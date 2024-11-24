package com.example.shelfshare.model

import com.google.gson.annotations.SerializedName

data class STKPushRequest(
    val username: String,
    val networkCode: String = "63902",
    val amount: Int,
    val phoneNumber: String,
    val narration: String,
    val currency: String = "KES",
    val callbackUrl: String
)

data class STKPushResponse(
    val channel: String,
    val success: Boolean,
    val message: String,
    val transactionReference: String
)
