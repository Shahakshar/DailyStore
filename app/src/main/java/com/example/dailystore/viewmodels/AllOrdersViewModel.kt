package com.example.dailystore.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailystore.data.CartProduct
import com.example.dailystore.data.order.Order
import com.example.dailystore.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllOrdersViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _allOrder = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    val allOrders = _allOrder.asStateFlow()

    private var ordersDocuments = emptyList<DocumentSnapshot>()

    init {
        getAllOrders()
    }

    private fun getAllOrders() {
        viewModelScope.launch { _allOrder.emit(Resource.Loading()) }

        // SnapShot
        firestore.collection("user").document(auth.uid!!).collection("orders")
            .addSnapshotListener { value, error ->
                if(error != null || value == null) {
                    viewModelScope.launch {
                        _allOrder.emit(Resource.Error(error?.message.toString()))
                    }
                } else {
                    ordersDocuments = value.documents
                    val orders = value.toObjects(Order::class.java)
                    viewModelScope.launch {
                        _allOrder.emit(Resource.Success(orders))
                    }
                }
            }
    }

    fun deleteCartProduct(order: Order) {
        val index = allOrders.value.data?.indexOf(order)
        if(index != null && index != -1) {
            val documentId = ordersDocuments[index].id
            firestore.collection("user").document(auth.uid!!).collection("orders")
                .document(documentId).delete()
        }
    }

}


/**
 * this is simple way of getting data it is static way
 */
//        firestore.collection("user").document(auth.uid!!).collection("orders").get()
//            .addOnSuccessListener {
//                val orders = it.toObjects(Order::class.java)
//                viewModelScope.launch {
//                    _allOrder.emit(Resource.Success(orders))
//                }
//            }
//            .addOnFailureListener {
//                viewModelScope.launch {
//                    _allOrder.emit(Resource.Error(it.message.toString()))
//                }
//            }