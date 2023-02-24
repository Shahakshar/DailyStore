package com.example.dailystore.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailystore.R
import com.example.dailystore.adapter.CartProductAdapter
import com.example.dailystore.databinding.FragmentCartBinding
import com.example.dailystore.utils.Resource
import com.example.dailystore.utils.VerticalItemDecoration
import com.example.dailystore.viewmodels.CartViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

class CartFragment: Fragment(R.layout.fragment_cart) {

    private lateinit var binding: FragmentCartBinding
    private val cartAdapter by lazy {
        CartProductAdapter()
    }
    private val viewModel by activityViewModels<CartViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpCartRv()

        lifecycleScope.launchWhenCreated {
            viewModel.cartProduct.collectLatest {
                when(it) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        if(it.data!!.isEmpty()) {
                            showEmptyCart()
                            hideOtherView()
                        } else {
                            hideEmptyCart()
                            showOtherView()
                            cartAdapter.differ.submitList(it.data)
                        }
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun showOtherView() {
        binding.apply {
            rvCart.visibility = View.VISIBLE
            linear.visibility = View.VISIBLE
            btnCheckout.visibility = View.VISIBLE
        }
    }

    private fun hideOtherView() {
        binding.apply {
            rvCart.visibility = View.GONE
            linear.visibility = View.GONE
            btnCheckout.visibility = View.GONE
        }
    }

    private fun hideEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility = View.GONE
        }
    }

    private fun showEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility = View.VISIBLE
        }
    }

    private fun setUpCartRv() {
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = cartAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }

}