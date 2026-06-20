package com.ishaan.quickcart.ai

import com.ishaan.quickcart.BuildConfig
import com.ishaan.quickcart.utils.Constants
import com.ishaan.quickcart.utils.Resource
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ClaudeRepository {

    private val api: ClaudeApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("x-api-key", BuildConfig.CLAUDE_API_KEY)
                    .build()
                chain.proceed(request)
            }
            .build()

        Retrofit.Builder()
            .baseUrl(Constants.CLAUDE_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ClaudeApiService::class.java)
    }

    suspend fun suggestCategory(
        productName: String,
        productDescription: String
    ): Resource<String> {
        return try {
            val prompt = """
                You are a grocery store product categorizer.
                Given a product name and description, respond with exactly one category from this list:
                Fruits, Vegetables, Dairy, Bakery, Beverages, Snacks, Household.
                Respond with only the category name, nothing else. No punctuation, no explanation.
                
                Product name: $productName
                Product description: $productDescription
            """.trimIndent()

            val request = ClaudeRequest(
                model = Constants.CLAUDE_MODEL,
                max_tokens = Constants.CLAUDE_MAX_TOKENS,
                messages = listOf(
                    ClaudeMessage(role = "user", content = prompt)
                )
            )

            val response = api.getCompletion(request)
            val category = response.content.firstOrNull()?.text?.trim()
                ?: return Resource.Error("No response from AI")

            // Validate it returned one of our known categories
            if (category !in Constants.CATEGORIES) {
                return Resource.Error("Unexpected category: $category")
            }

            Resource.Success(category)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "AI suggestion failed")
        }
    }
}