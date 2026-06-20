package com.ishaan.quickcart.utils


import com.ishaan.quickcart.data.local.CartEntity
import com.ishaan.quickcart.data.model.CartItem

fun CartEntity.toCartItem(): CartItem {
    return CartItem(
        productId = productId,
        productName = productName,
        productImageUrl = productImageUrl,
        price = price,
        quantity = quantity,
        unit = unit
    )
}

fun CartItem.toCartEntity(): CartEntity {
    return CartEntity(
        productId = productId,
        productName = productName,
        productImageUrl = productImageUrl,
        price = price,
        quantity = quantity,
        unit = unit
    )
}