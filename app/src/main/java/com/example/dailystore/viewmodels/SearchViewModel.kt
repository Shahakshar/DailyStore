package com.example.dailystore.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailystore.data.Product
import com.example.dailystore.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _searchedProduct = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val searchedProduct = _searchedProduct.asStateFlow()


    fun searchProduct(searchQuery: String) {

        firestore.collection("products").orderBy("name").startAt(searchQuery)
            .endAt("\u03A9+$searchQuery").limit(5).get()
            .addOnSuccessListener {
                val productList = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _searchedProduct.emit(Resource.Success(productList))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _searchedProduct.emit(Resource.Error(it.message.toString()))
                }
            }


    }

}