package com.ishaan.quickcart.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishaan.quickcart.AppModule
import com.ishaan.quickcart.data.model.CartItem
import com.ishaan.quickcart.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {

    private val manageCartUseCase = AppModule.manageCartUseCase

    val cartItems = manageCartUseCase.getCartItems()
    val cartTotal = manageCartUseCase.getCartTotal()
    val cartItemCount = manageCartUseCase.getCartItemCount()

    private val _actionState = MutableStateFlow<Resource<Unit>?>(null)
    val actionState: StateFlow<Resource<Unit>?> = _actionState

    fun increaseQuantity(cartItem: CartItem) {
        viewModelScope.launch {
            manageCartUseCase.updateQuantity(cartItem.productId, cartItem.quantity + 1)
        }
    }

    fun decreaseQuantity(cartItem: CartItem) {
        viewModelScope.launch {
            if (cartItem.quantity <= 1) {
                manageCartUseCase.removeFromCart(cartItem.productId)
            } else {
                manageCartUseCase.updateQuantity(cartItem.productId, cartItem.quantity - 1)
            }
        }
    }

    fun removeItem(cartItem: CartItem) {
        viewModelScope.launch {
            manageCartUseCase.removeFromCart(cartItem.productId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            manageCartUseCase.clearCart()
        }
    }
}