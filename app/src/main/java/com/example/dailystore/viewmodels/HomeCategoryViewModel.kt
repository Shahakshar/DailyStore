package com.example.dailystore.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailystore.data.Product
import com.example.dailystore.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _specialProduct = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProduct = _specialProduct.asStateFlow()

    init {
        fetchSpecialProducts()
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
}