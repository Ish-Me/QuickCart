package com.ishaan.quickcart.ui.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishaan.quickcart.AppModule
import com.ishaan.quickcart.data.model.CartItem
import com.ishaan.quickcart.data.model.Order
import com.ishaan.quickcart.data.model.User
import com.ishaan.quickcart.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {

    private val placeOrderUseCase = AppModule.placeOrderUseCase
    private val manageCartUseCase = AppModule.manageCartUseCase

    private val _ordersState = MutableStateFlow<Resource<List<Order>>>(Resource.Loading)
    val ordersState: StateFlow<Resource<List<Order>>> = _ordersState

    private val _placeOrderState = MutableStateFlow<Resource<String>?>(null)
    val placeOrderState: StateFlow<Resource<String>?> = _placeOrderState

    fun fetchOrders(userId: String) {
        viewModelScope.launch {
            _ordersState.value = Resource.Loading
            _ordersState.value = placeOrderUseCase.getOrdersForUser(userId)
        }
    }

    fun placeOrder(user: User, cartItems: List<CartItem>, note: String = "") {
        viewModelScope.launch {
            _placeOrderState.value = Resource.Loading
            val result = placeOrderUseCase.placeOrder(user, cartItems, note)
            if (result is Resource.Success) {
                manageCartUseCase.clearCart()
            }
            _placeOrderState.value = result
        }
    }

    fun resetPlaceOrderState() {
        _placeOrderState.value = null
    }
}