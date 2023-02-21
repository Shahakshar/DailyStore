package com.example.dailystore.data

data class Product(
    val id : String,
    val name: String,
    val category: String,
    val price: Float,
    val offerPercentage: Float? = null,
    val description: String? = null,
    val colors: List<Int>? = null,
    val sizes: List<String>? = null,
    val image: List<String>
) {
    constructor(): this("0", "", "",0f, image = emptyList())
}