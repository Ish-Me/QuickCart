package com.ishaan.quickcart.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.ishaan.quickcart.data.model.Product
import com.ishaan.quickcart.utils.Constants
import com.ishaan.quickcart.utils.Resource
import kotlinx.coroutines.tasks.await

class ProductRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val productsCollection = firestore.collection(Constants.COLLECTION_PRODUCTS)

    suspend fun getAllProducts(): Resource<List<Product>> {
        return try {
            android.util.Log.d("ProductRepo", "Fetching products from collection: ${Constants.COLLECTION_PRODUCTS}")
            val snapshot = productsCollection
                .whereEqualTo("isAvailable", true)
                .get()
                .await()
            android.util.Log.d("ProductRepo", "Documents found: ${snapshot.documents.size}")
            snapshot.documents.forEach { doc ->
                android.util.Log.d("ProductRepo", "Doc: ${doc.id} -> ${doc.data}")
            }
            val products = snapshot.documents.map { doc ->
                doc.toObject(Product::class.java)!!.copy(id = doc.id)
            }
            Resource.Success(products)
        } catch (e: Exception) {
            android.util.Log.e("ProductRepo", "Error: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to fetch products")
        }
    }

    suspend fun getProductsByCategory(category: String): Resource<List<Product>> {
        return try {
            val snapshot = productsCollection
                .whereEqualTo("category", category)
                .whereEqualTo("isAvailable", true)
                .get()
                .await()
            val products = snapshot.documents.map { doc ->
                doc.toObject(Product::class.java)!!.copy(id = doc.id)
            }
            Resource.Success(products)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch products")
        }
    }

    suspend fun getProductById(productId: String): Resource<Product> {
        return try {
            val doc = productsCollection.document(productId).get().await()
            val product = doc.toObject(Product::class.java)
                ?: return Resource.Error("Product not found")
            Resource.Success(product.copy(id = doc.id))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch product")
        }
    }

    // Admin only
    suspend fun addProduct(product: Product): Resource<String> {
        return try {
            android.util.Log.d("ProductDebug", "Saving product: $product")
            val docRef = productsCollection.add(product).await()
            android.util.Log.d("ProductDebug", "Saved with id: ${docRef.id}")

            // Read it back immediately to see what Firestore actually stored
            val savedDoc = docRef.get().await()
            android.util.Log.d("ProductDebug", "Raw data after save: ${savedDoc.data}")

            Resource.Success(docRef.id)
        } catch (e: Exception) {
            android.util.Log.e("ProductDebug", "Save failed: ${e.message}")
            Resource.Error(e.message ?: "Failed to add product")
        }
    }

    // Admin only
    suspend fun updateProduct(product: Product): Resource<Unit> {
        return try {
            productsCollection.document(product.id).set(product).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update product")
        }
    }
}