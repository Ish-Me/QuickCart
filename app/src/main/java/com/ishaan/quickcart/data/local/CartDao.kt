package com.ishaan.quickcart.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Query("SELECT * FROM cart")
    fun getAllCartItems(): Flow<List<CartEntity>>

    @Query("SELECT COUNT(*) FROM cart")
    fun getCartItemCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(price * quantity), 0.0) FROM cart")
    fun getCartTotal(): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: CartEntity)

    @Query("UPDATE cart SET quantity = :quantity WHERE productId = :productId")
    suspend fun updateQuantity(productId: String, quantity: Int)

    @Query("DELETE FROM cart WHERE productId = :productId")
    suspend fun removeItem(productId: String)

    @Query("DELETE FROM cart")
    suspend fun clearCart()

    @Query("SELECT * FROM cart WHERE productId = :productId")
    suspend fun getItemById(productId: String): CartEntity?
}