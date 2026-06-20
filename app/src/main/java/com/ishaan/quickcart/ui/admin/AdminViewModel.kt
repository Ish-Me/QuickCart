package com.ishaan.quickcart.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishaan.quickcart.AppModule
import com.ishaan.quickcart.data.model.Order
import com.ishaan.quickcart.data.model.Product
import com.ishaan.quickcart.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    private val getProductsUseCase = AppModule.getProductsUseCase
    private val placeOrderUseCase = AppModule.placeOrderUseCase
    private val suggestCategoryUseCase = AppModule.suggestCategoryUseCase

    private val _productsState = MutableStateFlow<Resource<List<Product>>>(Resource.Loading)
    val productsState: StateFlow<Resource<List<Product>>> = _productsState

    private val _ordersState = MutableStateFlow<Resource<List<Order>>>(Resource.Loading)
    val ordersState: StateFlow<Resource<List<Order>>> = _ordersState

    private val _addProductState = MutableStateFlow<Resource<String>?>(null)
    val addProductState: StateFlow<Resource<String>?> = _addProductState

    private val _updateOrderState = MutableStateFlow<Resource<Unit>?>(null)
    val updateOrderState: StateFlow<Resource<Unit>?> = _updateOrderState

    private val _suggestedCategory = MutableStateFlow<Resource<String>?>(null)
    val suggestedCategory: StateFlow<Resource<String>?> = _suggestedCategory

    fun fetchAllProducts() {
        viewModelScope.launch {
            _productsState.value = Resource.Loading
            _productsState.value = getProductsUseCase.getAllProducts()
        }
    }

    fun fetchAllOrders() {
        viewModelScope.launch {
            _ordersState.value = Resource.Loading
            _ordersState.value = placeOrderUseCase.getAllOrders()
        }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            _addProductState.value = Resource.Loading
            _addProductState.value = AppModule.productRepository.addProduct(product)
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            _updateOrderState.value = Resource.Loading
            _updateOrderState.value = placeOrderUseCase.updateOrderStatus(orderId, newStatus)
            if (_updateOrderState.value is Resource.Success) {
                fetchAllOrders()
            }
        }
    }

    // AI feature
    fun suggestCategory(productName: String, productDescription: String) {
        viewModelScope.launch {
            _suggestedCategory.value = Resource.Loading
            _suggestedCategory.value = suggestCategoryUseCase.execute(
                productName,
                productDescription
            )
        }
    }

    fun resetSuggestedCategory() {
        _suggestedCategory.value = null
    }

    fun resetAddProductState() {
        _addProductState.value = null
    }

    fun resetUpdateOrderState() {
        _updateOrderState.value = null
    }
}