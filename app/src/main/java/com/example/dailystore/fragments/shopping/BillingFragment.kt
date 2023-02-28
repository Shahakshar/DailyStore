package com.example.dailystore.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailystore.R
import com.example.dailystore.adapter.AddressAdapter
import com.example.dailystore.adapter.BillingProductAdapter
import com.example.dailystore.data.CartProduct
import com.example.dailystore.databinding.FragmentBillingBinding
import com.example.dailystore.utils.HorizontalItemDecoration
import com.example.dailystore.utils.Resource
import com.example.dailystore.viewmodels.BillingViewModel
import com.example.dailystore.viewmodels.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class BillingFragment: Fragment() {

    private lateinit var binding: FragmentBillingBinding
    private val addressAdapter by lazy { AddressAdapter() }
    private val billingAdapter by lazy { BillingProductAdapter() }
    private val viewModel by viewModels<BillingViewModel>()

    private val args by navArgs<BillingFragmentArgs>()
    private var product = emptyList<CartProduct>()
    private var totalPrice = 0f

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

        setUpBillingProductRv()
        setUpAddressRv()

        binding.imgAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.address.collectLatest {
                when(it) {
                    is Resource.Loading -> {
                        binding.progressbarAddresses.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        addressAdapter.differ.submitList(it.data)
                        binding.progressbarAddresses.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Error ${it.message}", Toast.LENGTH_SHORT).show()
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
    }

    private fun setUpAddressRv() {
        binding.rvAdresses.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = addressAdapter
        }
    }

    private fun setUpBillingProductRv() {
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = billingAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }

}