package com.ishaan.quickcart.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartEntity(
    @PrimaryKey
    val productId: String,
    val productName: String,
    val productImageUrl: String,
    val price: Double,
    val quantity: Int,
    val unit: String
) {
    val totalPrice: Double
        get() = price * quantity
}