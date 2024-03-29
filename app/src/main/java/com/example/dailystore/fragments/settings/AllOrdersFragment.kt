package com.example.dailystore.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailystore.adapter.AllOrderAdapter
import com.example.dailystore.databinding.FragmentOrderBinding
import com.example.dailystore.utils.Resource
import com.example.dailystore.utils.hideBottomNavigationView
import com.example.dailystore.viewmodels.AllOrdersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllOrdersFragment: Fragment() {

    private lateinit var binding: FragmentOrderBinding
    private val viewModel by viewModels<AllOrdersViewModel>()
    private val orderAdapter by lazy { AllOrderAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideBottomNavigationView()
        setUpOrdersRv()

        binding.imgCloseOrders.setOnClickListener {
            findNavController().navigateUp()
        }

        // when view created first time then use launch when started

        lifecycleScope.launch {
            viewModel.allOrders.collect {
                when(it) {
                    is Resource.Loading -> {
                        binding.progressbarAllOrders.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarAllOrders.visibility = View.GONE
                        orderAdapter.differ.submitList(it.data)

                        if(it.data.isNullOrEmpty()) {
                            binding.tvEmptyOrders.visibility = View.VISIBLE
                            binding.imgEmptyBox.visibility = View.VISIBLE
                            binding.imgEmptyBoxTexture.visibility = View.VISIBLE

                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        binding.progressbarAllOrders.visibility = View.GONE
                    }
                    else -> Unit
                }
            }
        }

        orderAdapter.onClick = {
            val action = AllOrdersFragmentDirections.actionOrdersFragmentToOrderDetails(it)
            findNavController().navigate(action)
        }

    }

    private fun setUpOrdersRv() {
        binding.rvAllOrders.apply {
            adapter = orderAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun onResume() {
        super.onResume()

//        lifecycleScope.launch {
//            viewModel.getAllOrders()
//        }

    }

}