package com.ishaan.quickcart.domain.usecase

import com.ishaan.quickcart.AppModule
import com.ishaan.quickcart.data.model.CartItem
import com.ishaan.quickcart.data.model.Product
import com.ishaan.quickcart.utils.Resource
import com.ishaan.quickcart.utils.toCartEntity
import com.ishaan.quickcart.utils.toCartItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ManageCartUseCase {

    private val cartDao = AppModule.cartDao

    fun getCartItems(): Flow<List<CartItem>> {
        return cartDao.getAllCartItems().map { entities ->
            entities.map { it.toCartItem() }
        }
    }

    fun getCartItemCount(): Flow<Int> {
        return cartDao.getCartItemCount()
    }

    fun getCartTotal(): Flow<Double> {
        return cartDao.getCartTotal()
    }

    suspend fun addToCart(product: Product, quantity: Int = 1): Resource<Unit> {
        return try {
            val existingItem = cartDao.getItemById(product.id)
            if (existingItem != null) {
                cartDao.updateQuantity(product.id, existingItem.quantity + quantity)
            } else {
                val cartItem = CartItem(
                    productId = product.id,
                    productName = product.name,
                    productImageUrl = product.imageUrl,
                    price = product.price,
                    quantity = quantity,
                    unit = product.unit
                )
                cartDao.insertOrUpdate(cartItem.toCartEntity())
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add to cart")
        }
    }

    suspend fun updateQuantity(productId: String, quantity: Int): Resource<Unit> {
        return try {
            if (quantity <= 0) {
                cartDao.removeItem(productId)
            } else {
                cartDao.updateQuantity(productId, quantity)
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update quantity")
        }
    }

    suspend fun removeFromCart(productId: String): Resource<Unit> {
        return try {
            cartDao.removeItem(productId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to remove item")
        }
    }

    suspend fun clearCart(): Resource<Unit> {
        return try {
            cartDao.clearCart()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to clear cart")
        }
    }
}