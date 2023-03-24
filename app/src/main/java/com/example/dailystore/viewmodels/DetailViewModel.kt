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
class DetailViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {

    private val _updateList = MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())
    val updateList = _updateList.asStateFlow()

    private val _addToCart = MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())
    val addCart = _addToCart.asStateFlow()

    private var productAvailable = emptyList<DocumentSnapshot>()

    init {
        updateProductList()
    }
    private fun updateProductList() {
        firestore.collection("user").document(auth.uid!!).collection("cart")
            .addSnapshotListener { value, error ->
                if(error != null || value == null) {
                    viewModelScope.launch {
                        _updateList.emit(Resource.Error(error?.message.toString()))
                    }
                } else {
                    productAvailable = value.documents
                    val cartProducts = value.toObjects(CartProduct::class.java)
                    viewModelScope.launch {
                        _updateList.emit(Resource.Success(cartProducts))
                    }
                }
            }
    }

    fun updateProductInCart(cartProduct: CartProduct) {

        /**
         * small logic code for increment in quantity in cart if already available
         */
        var index = -1
        var count = 0
        updateList.value.data.let {
            it?.forEach {cartProducts->
               if(cartProducts.product == cartProduct.product) {
                   index = count
               } else {
                   count++
                   index = -1
               }
            }
        }

        // this means that index on which product present if it is not null
        if(index != null && index != -1) {
            val documentId = productAvailable[index].id
            // increment in Quantity
            increaseQuantity(documentId, cartProduct)
        } else {
            // add new product
            addNewProduct(cartProduct)
        }
    }

    private fun addNewProduct(cartProduct: CartProduct) {
        firebaseCommon.addProductToCart(cartProduct) { addProduct, e ->
            viewModelScope.launch {
                if (e == null) {
                    _addToCart.emit(Resource.Success(addProduct!!))
                } else {
                    _addToCart.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }

    private fun increaseQuantity(documentId: String, cartProduct: CartProduct) {
        firebaseCommon.increaseQuantity(documentId) { _, e ->
            viewModelScope.launch {
                if (e == null) {
                    _addToCart.emit(Resource.Success(cartProduct))
                } else {
                    _addToCart.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }
}