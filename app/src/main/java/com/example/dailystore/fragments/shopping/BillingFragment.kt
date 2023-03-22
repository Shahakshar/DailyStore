package com.example.dailystore.fragments.shopping

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailystore.R
import com.example.dailystore.adapter.AddressAdapter
import com.example.dailystore.adapter.BillingProductAdapter
import com.example.dailystore.data.Address
import com.example.dailystore.data.CartProduct
import com.example.dailystore.data.order.Order
import com.example.dailystore.data.order.OrderStatus
import com.example.dailystore.databinding.FragmentBillingBinding
import com.example.dailystore.utils.HorizontalItemDecoration
import com.example.dailystore.utils.Resource
import com.example.dailystore.utils.hideBottomNavigationView
import com.example.dailystore.viewmodels.BillingViewModel
import com.example.dailystore.viewmodels.OrderViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class BillingFragment : Fragment() {

    private lateinit var binding: FragmentBillingBinding
    private val addressAdapter by lazy { AddressAdapter() }
    private val billingAdapter by lazy { BillingProductAdapter() }
    private val billingViewModel by viewModels<BillingViewModel>()

    private val args by navArgs<BillingFragmentArgs>()
    private var product = emptyList<CartProduct>()
    private var totalPrice = 0f

    private var selectedAddress: Address? = null
    private val orderViewModel by viewModels<OrderViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        product = args.products.toList()
        totalPrice = args.totalPrice
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBillingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideBottomNavigationView()
        onHomeClick()
        setUpBillingProductRv()
        setUpAddressRv()

        binding.imgCloseBilling.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.imgAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
        }

        lifecycleScope.launchWhenStarted {
            billingViewModel.address.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarAddresses.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        addressAdapter.differ.submitList(it.data)
                        binding.progressbarAddresses.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Error ${it.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            orderViewModel.order.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        Snackbar.make(requireView(), "Loading...", Snackbar.LENGTH_SHORT).show()
                    }
                    is Resource.Success -> {
                        Snackbar.make(requireView(), "Your Order Is Placed", Snackbar.LENGTH_LONG).show()
                        // order placed then go to the previous fragment
                        findNavController().navigateUp()
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(), "${it.message}", Snackbar.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        billingAdapter.differ.submitList(product)
        binding.tvTotalprice.text = buildString {
            append("₹ ")
            append(totalPrice)
        }


        addressAdapter.onClick = {
            selectedAddress = it
        }

        binding.btnPlaceOlder.setOnClickListener {
            if (selectedAddress == null) {
                Toast.makeText(requireContext(), "Please select an address", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            showOrderConfirmationDialog()
        }
    }

    private fun showOrderConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle("Order Item")
            setMessage("Do you want to order your cart items?")
            setNegativeButton("Cancel") { dialog,_ ->
                dialog.dismiss()
            }
            setPositiveButton("Yes") { dialog,_ ->
                val order = Order(
                    OrderStatus.Ordered.status,
                    totalPrice,
                    product,
                    selectedAddress!!
                )
                dialog.dismiss()
                orderViewModel.placeOrder(order)
            }
        }
        alertDialog.create()
        alertDialog.show()
    }

    private fun setUpAddressRv() {
        binding.rvAdresses.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = addressAdapter
        }
    }

    private fun setUpBillingProductRv() {
        binding.rvProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = billingAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }

    private fun onHomeClick() {
        val btm = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)
        btm?.menu?.getItem(0)?.setOnMenuItemClickListener {
            activity?.onBackPressed()
            true
        }
    }


}