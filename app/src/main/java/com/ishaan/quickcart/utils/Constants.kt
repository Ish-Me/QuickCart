package com.ishaan.quickcart.utils

object Constants {

    // Firestore collection names
    const val COLLECTION_USERS = "Users"
    const val COLLECTION_PRODUCTS = "Products"
    const val COLLECTION_ORDERS = "Orders"

    // Product categories — matches what Claude will suggest from
    val CATEGORIES = listOf(
        "Fruits",
        "Vegetables",
        "Dairy",
        "Bakery",
        "Beverages",
        "Snacks",
        "Household"
    )

    // Claude API
    const val CLAUDE_BASE_URL = "https://api.anthropic.com/"
    const val CLAUDE_MODEL = "claude-sonnet-4-20250514"
    const val CLAUDE_API_VERSION = "2023-06-01"
    const val CLAUDE_MAX_TOKENS = 20         // category name is never more than a few words

    // Room
    const val CART_DATABASE_NAME = "quickcart_db"
}