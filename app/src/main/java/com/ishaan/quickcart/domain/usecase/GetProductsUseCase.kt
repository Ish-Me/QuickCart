package com.ishaan.quickcart.domain.usecase

import com.ishaan.quickcart.data.firebase.ProductRepository
import com.ishaan.quickcart.data.model.Product
import com.ishaan.quickcart.utils.Resource

class GetProductsUseCase {

    private val repository = ProductRepository()

    suspend fun getAllProducts(): Resource<List<Product>> {
        return repository.getAllProducts()
    }

    suspend fun getProductsByCategory(category: String): Resource<List<Product>> {
        if (category.isBlank()) return getAllProducts()
        return repository.getProductsByCategory(category)
    }

    suspend fun getProductById(productId: String): Resource<Product> {
        if (productId.isBlank()) return Resource.Error("Invalid product")
        return repository.getProductById(productId)
    }

    suspend fun searchProducts(query: String): Resource<List<Product>> {
        if (query.isBlank()) return getAllProducts()
        return when (val result = getAllProducts()) {
            is Resource.Success -> {
                val filtered = result.data.filter { product ->
                    product.name.contains(query, ignoreCase = true) ||
                            product.category.contains(query, ignoreCase = true) ||
                            product.description.contains(query, ignoreCase = true)
                }
                Resource.Success(filtered)
            }
            is Resource.Error -> result
            Resource.Loading -> Resource.Loading
        }
    }
}