package com.ishaan.quickcart.data.model

data class CartItem(
    val productId: String = "",
    val productName: String = "",
    val productImageUrl: String = "",
    val price: Double = 0.0,
    val quantity: Int = 1,
    val unit: String = ""
) {
    val totalPrice: Double
        get() = price * quantity
}