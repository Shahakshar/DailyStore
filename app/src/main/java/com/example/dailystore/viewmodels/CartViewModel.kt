package com.example.dailystore.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailystore.data.CartProduct
import com.example.dailystore.firebase.FirebaseCommon
import com.example.dailystore.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
): ViewModel() {

    private val _cartProduct = MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())
    val cartProduct = _cartProduct.asStateFlow()

    private var cartProductDocuments = emptyList<DocumentSnapshot>()

    init {
        getCartProduct()
    }

    private fun getCartProduct() {
        viewModelScope.launch {
            _cartProduct.emit(Resource.Loading())
        }
        firestore.collection("user").document(auth.uid!!).collection("cart")
            .addSnapshotListener { value, error ->
                if(error != null || value == null) {
                    viewModelScope.launch {
                        _cartProduct.emit(Resource.Error(error?.message.toString()))
                    }
                } else {
                    cartProductDocuments = value.documents
                    val cartProducts = value.toObjects(CartProduct::class.java)
                    viewModelScope.launch {
                        _cartProduct.emit(Resource.Success(cartProducts))
                    }
                }
            }
    }

    fun changeQuantity(cartProducts: CartProduct, quantityChanging: FirebaseCommon.QuantityChanging) {
        val index = cartProduct.value.data?.indexOf(cartProducts)

        /**
         *
         */

        if(index != null && index != -1) {
            val documentId = cartProductDocuments[index].id

            when(quantityChanging) {
                FirebaseCommon.QuantityChanging.INCREASE -> {
                    increaseQuantity(documentId)
                }
                FirebaseCommon.QuantityChanging.DECREASE -> {
                    decreaseQuantity(documentId)
                }
            }
        }
    }

    private fun decreaseQuantity(documentId: String) {
        firebaseCommon.decreaseQuantity(documentId) { result, exception ->
            if(exception != null) {
                viewModelScope.launch {
                    _cartProduct.emit(Resource.Error(exception.message.toString()))
                }
            }
        }
    }

    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId) { result, exception ->
            if(exception != null) {
                viewModelScope.launch {
                    _cartProduct.emit(Resource.Error(exception.message.toString()))
                }
            }
        }
    }

}