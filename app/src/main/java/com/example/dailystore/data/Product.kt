package com.example.dailystore.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id : String,
    val name: String,
    val adminIdEmail: String,
    val category: String,
    val price: Float,
    val description: String? = null,
    val image: List<String>
): Parcelable {
    constructor(): this("0", "", "", "",0f, image = emptyList())
}