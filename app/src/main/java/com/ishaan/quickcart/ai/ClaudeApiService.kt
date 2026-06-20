package com.ishaan.quickcart.ai

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ClaudeApiService {

    @Headers(
        "anthropic-version: 2023-06-01",
        "content-type: application/json"
    )
    @POST("v1/messages")
    suspend fun getCompletion(
        @Body request: ClaudeRequest
    ): ClaudeResponse
}