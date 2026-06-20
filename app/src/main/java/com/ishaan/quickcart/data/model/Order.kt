package com.ishaan.quickcart.data.model

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userAddress: String = "",
    val items: List<CartItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val status: String = OrderStatus.PENDING.name,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
) {
    val itemCount: Int
        get() = items.sumOf { it.quantity }
}