package com.example.dailystore.data.order

import com.example.dailystore.data.Address
import com.example.dailystore.data.CartProduct

data class Order(
    val orderStatus: String,
    val totalPrice: Float,
    val product: List<CartProduct>,
    val address: Address
)
