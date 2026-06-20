package com.ishaan.quickcart.domain.usecase

import com.ishaan.quickcart.data.firebase.OrderRepository
import com.ishaan.quickcart.data.model.CartItem
import com.ishaan.quickcart.data.model.Order
import com.ishaan.quickcart.data.model.User
import com.ishaan.quickcart.utils.Resource

class PlaceOrderUseCase {

    private val orderRepository = OrderRepository()

    suspend fun placeOrder(
        user: User,
        cartItems: List<CartItem>,
        note: String = ""
    ): Resource<String> {
        if (cartItems.isEmpty()) return Resource.Error("Your cart is empty")
        if (user.address.isBlank()) return Resource.Error("Please add a delivery address")

        val totalAmount = cartItems.sumOf { it.totalPrice }

        val order = Order(
            userId = user.uid,
            userName = user.name,
            userAddress = user.address,
            items = cartItems,
            totalAmount = totalAmount,
            note = note
        )

        return orderRepository.placeOrder(order)
    }

    suspend fun getOrdersForUser(userId: String): Resource<List<Order>> {
        if (userId.isBlank()) return Resource.Error("Invalid user")
        return orderRepository.getOrdersForUser(userId)
    }

    suspend fun getAllOrders(): Resource<List<Order>> {
        return orderRepository.getAllOrders()
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String): Resource<Unit> {
        if (orderId.isBlank()) return Resource.Error("Invalid order")
        return orderRepository.updateOrderStatus(orderId, newStatus)
    }
}