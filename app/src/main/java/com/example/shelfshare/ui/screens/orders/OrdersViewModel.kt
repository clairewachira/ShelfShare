package com.example.shelfshare.ui.screens.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelfshare.model.Order
import com.example.shelfshare.model.service.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val storageService: StorageService,
) : ViewModel() {
    private val _purchasesState = MutableStateFlow<List<Order>>(emptyList())
    val purchasesState: StateFlow<List<Order>> = _purchasesState.asStateFlow()

    private val _salesState = MutableStateFlow<List<Order>>(emptyList())
    val salesState: StateFlow<List<Order>> = _salesState.asStateFlow()


    private val _orderDetails = MutableStateFlow<Order?>(null)  // Changed to nullable
    val orderDetails: StateFlow<Order?> = _orderDetails.asStateFlow() // Make public

    init {
        fetchPurchases()
        fetchSales()
    }

    private fun fetchPurchases() {
        viewModelScope.launch {
             val purchases = storageService.getPurchase()
            _purchasesState.value = purchases
        }
    }


    private fun fetchSales() {
        viewModelScope.launch {
            val sales = storageService.getSales()
            _salesState.value = sales
        }
    }


    fun getOrderDetails(orderId: String) {
        viewModelScope.launch {
            val allOrders = storageService.getOrders()
            _orderDetails.value = allOrders.find { it.orderId == orderId }
        }
    }
}