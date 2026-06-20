package com.ishaan.quickcart.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ishaan.quickcart.data.model.Order
import com.ishaan.quickcart.utils.Constants
import com.ishaan.quickcart.utils.Resource
import kotlinx.coroutines.tasks.await

class OrderRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val ordersCollection = firestore.collection(Constants.COLLECTION_ORDERS)

    suspend fun placeOrder(order: Order): Resource<String> {
        return try {
            val docRef = ordersCollection.add(order).await()
            Resource.Success(docRef.id)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to place order")
        }
    }

    suspend fun getOrdersForUser(userId: String): Resource<List<Order>> {
        return try {
            val snapshot = ordersCollection
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
            val orders = snapshot.documents.map { doc ->
                doc.toObject(Order::class.java)!!.copy(orderId = doc.id)
            }
            Resource.Success(orders)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch orders")
        }
    }

    // Admin only
    suspend fun getAllOrders(): Resource<List<Order>> {
        return try {
            val snapshot = ordersCollection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
            val orders = snapshot.documents.map { doc ->
                doc.toObject(Order::class.java)!!.copy(orderId = doc.id)
            }
            Resource.Success(orders)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch all orders")
        }
    }

    // Admin only
    suspend fun updateOrderStatus(orderId: String, newStatus: String): Resource<Unit> {
        return try {
            ordersCollection.document(orderId)
                .update("status", newStatus)
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update order status")
        }
    }
}