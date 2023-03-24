package com.example.dailystore.firebase

import android.util.Log
import com.example.dailystore.data.CartProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseCommon(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    fun addProductToCart(cartProduct: CartProduct, onResult: (CartProduct?, Exception?) -> Unit) {
        Log.d("dummmp", "addProductToCart: ${auth.currentUser?.uid}")
        firestore.collection("user").document(auth.currentUser?.uid!!).collection("cart").document().set(cartProduct)
            .addOnSuccessListener {
                onResult(cartProduct, null)
            }
            .addOnFailureListener {
                onResult(null, it)
            }
    }

    fun increaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit) {
        firestore.runTransaction { transition ->
            val documentRef = firestore.collection("user").document(auth.currentUser?.uid!!).collection("cart").document(documentId)
            val document = transition.get(documentRef)
            val productObj = document.toObject(CartProduct::class.java)
            productObj?.let {
                val newQuantity = it.quantity + 1
                val newProductObject = it.copy(quantity = newQuantity)
                transition.set(documentRef, newProductObject)
            }
        }
            .addOnSuccessListener {
                onResult(documentId, null)
            }
            .addOnFailureListener {
                onResult(null, it)
            }
    }

    fun decreaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit) {
        firestore.runTransaction { transition ->
            val documentRef = firestore.collection("user").document(auth.currentUser?.uid!!).collection("cart").document(documentId)
            val document = transition.get(documentRef)
            val productObj = document.toObject(CartProduct::class.java)
            productObj?.let {
                val newQuantity = it.quantity - 1
                val newProductObject = it.copy(quantity = newQuantity)
                transition.set(documentRef, newProductObject)
            }
        }
            .addOnSuccessListener {
                onResult(documentId, null)
            }
            .addOnFailureListener {
                onResult(null, it)
            }
    }

    enum class QuantityChanging {
        INCREASE, DECREASE
    }
}