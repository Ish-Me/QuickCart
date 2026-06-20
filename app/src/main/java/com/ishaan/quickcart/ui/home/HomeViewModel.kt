package com.ishaan.quickcart.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishaan.quickcart.AppModule
import com.ishaan.quickcart.data.model.Product
import com.ishaan.quickcart.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val getProductsUseCase = AppModule.getProductsUseCase
    private val manageCartUseCase = AppModule.manageCartUseCase

    private val _productsState = MutableStateFlow<Resource<List<Product>>>(Resource.Loading)
    val productsState: StateFlow<Resource<List<Product>>> = _productsState

    private val _addToCartState = MutableStateFlow<Resource<Unit>?>(null)
    val addToCartState: StateFlow<Resource<Unit>?> = _addToCartState

    val cartItemCount = manageCartUseCase.getCartItemCount()

    private var selectedCategory: String = ""
    private var allProducts: List<Product> = emptyList()

    init {
        fetchProducts()
    }

    fun fetchProducts() {
        viewModelScope.launch {
            _productsState.value = Resource.Loading
            when (val result = getProductsUseCase.getAllProducts()) {
                is Resource.Success -> {
                    allProducts = result.data
                    _productsState.value = Resource.Success(allProducts)
                }
                is Resource.Error -> _productsState.value = Resource.Error(result.message)
                Resource.Loading -> {}
            }
        }
    }

    fun filterByCategory(category: String) {
        selectedCategory = category
        viewModelScope.launch {
            _productsState.value = Resource.Loading
            if (category.isBlank()) {
                _productsState.value = Resource.Success(allProducts)
            } else {
                val filtered = allProducts.filter { it.category == category }
                _productsState.value = Resource.Success(filtered)
            }
        }
    }

    fun searchProducts(query: String) {
        viewModelScope.launch {
            _productsState.value = Resource.Loading
            val result = getProductsUseCase.searchProducts(query)
            _productsState.value = result
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            _addToCartState.value = Resource.Loading
            _addToCartState.value = manageCartUseCase.addToCart(product)
        }
    }

    fun resetAddToCartState() {
        _addToCartState.value = null
    }
}