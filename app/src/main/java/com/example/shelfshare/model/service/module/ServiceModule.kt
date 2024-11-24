package com.example.shelfshare.model.service.module

import com.example.shelfshare.model.service.AccountService
import com.example.shelfshare.model.service.LogService
import com.example.shelfshare.model.service.PaymentService
import com.example.shelfshare.model.service.StorageService
import com.example.shelfshare.model.service.impl.AccountServiceImpl
import com.example.shelfshare.model.service.impl.LogServiceImpl
import com.example.shelfshare.model.service.impl.MPesaPaymentServiceImpl
import com.example.shelfshare.model.service.impl.StorageServiceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds abstract fun provideAccountService(impl: AccountServiceImpl): AccountService

    @Binds abstract fun provideStorageService(impl: StorageServiceImpl): StorageService

    @Binds abstract fun provideLogService(impl: LogServiceImpl): LogService

    @Binds abstract fun providePaymentService(impl: MPesaPaymentServiceImpl): PaymentService

}