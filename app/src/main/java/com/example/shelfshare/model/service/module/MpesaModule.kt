package com.example.shelfshare.model.service.module

import com.example.shelfshare.model.service.AuthInterceptor
import com.example.shelfshare.model.service.MpesaApiService
import com.example.shelfshare.model.service.MpesaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object MpesaModule {

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val username = "uFG3YvPd0vWKxxiEHDRl"
        val password = "TgbpuomOQOqKlyoLm9TBIP0jkn0nTA8az0GxSYL0"

        // Create a logging interceptor to log HTTP request and response details
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(username, password))
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    fun provideMpesaApiService(client: OkHttpClient): MpesaApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.mypayd.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(MpesaApiService::class.java)
    }

    @Provides
    fun provideMpesaRepository(apiService: MpesaApiService): MpesaRepository {
        return MpesaRepository(apiService)
    }
}

