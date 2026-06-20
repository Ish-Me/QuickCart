package com.ishaan.quickcart.domain.usecase

import com.ishaan.quickcart.ai.ClaudeRepository
import com.ishaan.quickcart.utils.Resource

class SuggestCategoryUseCase {

    private val claudeRepository = ClaudeRepository()

    suspend fun execute(
        productName: String,
        productDescription: String
    ): Resource<String> {
        if (productName.isBlank()) return Resource.Error("Product name is required")
        if (productDescription.isBlank()) return Resource.Error("Product description is required")
        return claudeRepository.suggestCategory(productName, productDescription)
    }
}