package com.example.shelfshare.model.service

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.Credentials

class AuthInterceptor(private val username: String, private val password: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val credentials = Credentials.basic(username, password)
        val authenticatedRequest = originalRequest.newBuilder()
            .addHeader("Authorization", credentials)
            .build()
        return chain.proceed(authenticatedRequest)
    }
}
