package com.example.shelfshare.ui.screens.order_details

import androidx.lifecycle.viewModelScope
import com.example.shelfshare.model.Order
import com.example.shelfshare.model.service.LogService
import com.example.shelfshare.model.service.StorageService
import com.example.shelfshare.ui.screens.ShelfShareViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val storageService: StorageService,
    logService: LogService
) : ShelfShareViewModel(logService){

    private val _orderDetails = MutableStateFlow<Order>(Order())
    private val orderDetails: StateFlow<Order> = _orderDetails.asStateFlow()


    fun getOrderDetails(orderId: String): Order {
        // Combine purchases and sales to search for the order
        viewModelScope.launch {
            val allOrd = storageService.getOrders()
            _orderDetails.value = allOrd.find { it.orderId == orderId }!!
        }

        return orderDetails.value

    }
}