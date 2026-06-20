package com.ishaan.quickcart.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishaan.quickcart.AppModule
import com.ishaan.quickcart.data.model.Product
import com.ishaan.quickcart.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel : ViewModel() {

    private val getProductsUseCase = AppModule.getProductsUseCase
    private val manageCartUseCase = AppModule.manageCartUseCase

    private val _productState = MutableStateFlow<Resource<Product>>(Resource.Loading)
    val productState: StateFlow<Resource<Product>> = _productState

    private val _addToCartState = MutableStateFlow<Resource<Unit>?>(null)
    val addToCartState: StateFlow<Resource<Unit>?> = _addToCartState

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _productState.value = Resource.Loading
            _productState.value = getProductsUseCase.getProductById(productId)
        }
    }

    fun incrementQuantity() {
        _quantity.value = _quantity.value + 1
    }

    fun decrementQuantity() {
        if (_quantity.value > 1) {
            _quantity.value = _quantity.value - 1
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            _addToCartState.value = Resource.Loading
            _addToCartState.value = manageCartUseCase.addToCart(product, _quantity.value)
        }
    }

    fun resetAddToCartState() {
        _addToCartState.value = null
    }
}