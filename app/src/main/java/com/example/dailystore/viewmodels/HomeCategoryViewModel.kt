package com.example.dailystore.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailystore.data.Product
import com.example.dailystore.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    // asStateFlow() and StateFlow<> both are same just type casting

    private val _specialProduct = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProduct = _specialProduct.asStateFlow()

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProduct: StateFlow<Resource<List<Product>>> = _bestProducts

    init {
        fetchSpecialProducts()
        fetchBestProduct()
    }

    private fun fetchSpecialProducts() {
        viewModelScope.launch {
            _specialProduct.emit(Resource.Loading())
        }

        firestore.collection("products")
            .whereEqualTo("category", "Special Product").get().addOnSuccessListener { result ->
                val specialProductList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _specialProduct.emit(Resource.Success(specialProductList))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _specialProduct.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    private fun fetchBestProduct() {
        viewModelScope.launch {
            _bestProducts.emit(Resource.Loading())
        }
        firestore.collection("products")
            .whereEqualTo("category", "Best Product").get().addOnSuccessListener { result ->
                val bestProductList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Success(bestProductList))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}