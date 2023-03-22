package com.example.dailystore.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailystore.adapter.BillingProductAdapter
import com.example.dailystore.data.order.Order
import com.example.dailystore.data.order.OrderStatus
import com.example.dailystore.data.order.getOrderStatus
import com.example.dailystore.databinding.FragmentOrderDetailsBinding
import com.example.dailystore.utils.VerticalItemDecoration
import com.example.dailystore.utils.hideBottomNavigationView
import com.example.dailystore.viewmodels.AllOrdersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OrderDetails @Inject constructor() : Fragment() {

    private lateinit var binding: FragmentOrderDetailsBinding
    private val billingProductAdapter by lazy { BillingProductAdapter() }
    private val args by navArgs<OrderDetailsArgs>()
//    private val viewModel by activityViewModels<AllOrdersViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val order = args.order

        hideBottomNavigationView()
        setUpOrderRv()
        hideProgressBar()
//        binding.cancelBtn.setOnClickListener {
//            deleteItemFromOrders(order)
//        }

        binding.apply {
            tvOrderId.text = "Order #${order.orderId}"

            stepView.setSteps(
                mutableListOf(
                    OrderStatus.Ordered.status,
                    OrderStatus.Confirmed.status,
                    OrderStatus.Shipped.status,
                    OrderStatus.Delivered.status
                )
            )

            val currentOrderState = when (getOrderStatus(order.orderStatus)) {
                is OrderStatus.Ordered -> 0
                is OrderStatus.Confirmed -> 1
                is OrderStatus.Shipped -> 2
                is OrderStatus.Delivered -> 3
                else -> 0
            }

            stepView.go(currentOrderState, false)
            if (currentOrderState == 3) {
                stepView.done(true)
            }

            tvFullName.text = "Name: ${order.address.fullName}"
            tvAddress.text = "Address: ${order.address.street}  ${order.address.city}"
            tvPhoneNumber.text = order.address.Phone

            tvTotalprice.text = "₹" + order.totalPrice.toString()

            imgCloseOrder.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        billingProductAdapter.differ.submitList(order.product)
    }

    private fun hideProgressBar() {
        lifecycleScope.launch {
            Job.apply {
                delay(500L)
                binding.progressbarOrder.visibility = View.GONE
            }
        }
    }


//    private fun deleteItemFromOrders(order: Order) {
//        viewModel.deleteCartProduct(order)
//        lifecycleScope.launch {
//            Job.apply {
//                delay(1000L)
//                findNavController().navigateUp()
//            }
//        }
//
//    }

    private fun setUpOrderRv() {
        binding.rvProducts.apply {
            adapter = billingProductAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(VerticalItemDecoration())
        }
    }

}