package com.ishaan.quickcart

import android.content.Context
import com.ishaan.quickcart.ai.ClaudeRepository
import com.ishaan.quickcart.data.firebase.AuthRepository
import com.ishaan.quickcart.data.firebase.OrderRepository
import com.ishaan.quickcart.data.firebase.ProductRepository
import com.ishaan.quickcart.data.local.AppDatabase
import com.ishaan.quickcart.data.local.CartDao
import com.ishaan.quickcart.domain.usecase.AuthUseCase
import com.ishaan.quickcart.domain.usecase.GetProductsUseCase
import com.ishaan.quickcart.domain.usecase.ManageCartUseCase
import com.ishaan.quickcart.domain.usecase.PlaceOrderUseCase
import com.ishaan.quickcart.domain.usecase.SuggestCategoryUseCase

object AppModule {

    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(requireContext())
    }

    val cartDao: CartDao by lazy {
        database.cartDao()
    }

    // Repositories
    val authRepository: AuthRepository by lazy { AuthRepository() }
    val productRepository: ProductRepository by lazy { ProductRepository() }
    val orderRepository: OrderRepository by lazy { OrderRepository() }
    val claudeRepository: ClaudeRepository by lazy { ClaudeRepository() }

    // Use cases
    val authUseCase: AuthUseCase by lazy { AuthUseCase() }
    val getProductsUseCase: GetProductsUseCase by lazy { GetProductsUseCase() }
    val manageCartUseCase: ManageCartUseCase by lazy { ManageCartUseCase() }
    val placeOrderUseCase: PlaceOrderUseCase by lazy { PlaceOrderUseCase() }
    val suggestCategoryUseCase: SuggestCategoryUseCase by lazy { SuggestCategoryUseCase() }

    private fun requireContext(): Context {
        return appContext
            ?: error("AppModule not initialized. Call AppModule.init(context) in Application class.")
    }
}