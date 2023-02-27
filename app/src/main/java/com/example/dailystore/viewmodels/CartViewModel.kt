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
import kotlinx.coroutines.flow.*
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

    val productPrice = cartProduct.map {
        when(it) {
            is Resource.Success -> {
                calculatePrice(it.data!!)
            }
            else -> null
        }
    }

    private val _deleteDialog = MutableSharedFlow<CartProduct>()
    val deleteDialog = _deleteDialog.asSharedFlow()

    private fun calculatePrice(data: List<CartProduct>): Float? {
        return data.sumByDouble { cartProduct ->
            (cartProduct.product.price * cartProduct.quantity).toDouble()
        }.toFloat()
    }

    private var cartProductDocuments = emptyList<DocumentSnapshot>()

    init {
        getCartProduct()
    }

    fun deleteCartProduct(cartProducts: CartProduct) {
        val index = cartProduct.value.data?.indexOf(cartProducts)
        if(index != null && index != -1) {
            val documentId = cartProductDocuments[index].id
            firestore.collection("user").document(auth.uid!!).collection("cart").document(documentId).delete()
        }
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
                    viewModelScope.launch { _cartProduct.emit(Resource.Loading()) }
                    increaseQuantity(documentId)
                }
                FirebaseCommon.QuantityChanging.DECREASE -> {
                    if(cartProducts.quantity == 1){
                        viewModelScope.launch { _deleteDialog.emit(cartProducts) }
                        return
                    }
                    viewModelScope.launch { _cartProduct.emit(Resource.Loading()) }
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