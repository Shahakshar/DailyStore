package com.example.dailystore.fragments.shopping

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailystore.R
import com.example.dailystore.adapter.BestProductsAdapter
import com.example.dailystore.adapter.SpecialProductsAdapter
import com.example.dailystore.databinding.FragmentHomeBinding
import com.example.dailystore.utils.Resource
import com.example.dailystore.viewmodels.HomeCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class HomeFragment: Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
    private lateinit var bestProductsAdapter: BestProductsAdapter

    private val viewModel by viewModels<HomeCategoryViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpSpecialProductsRv()
        setUpBestProductsRv()

        lifecycleScope.launchWhenStarted {
            viewModel.specialProduct.collectLatest {
                when(it) {
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Log.e("TAG", it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Success -> {
                        specialProductsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestProduct.collectLatest {
                when(it) {
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Log.e("TAG", it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Success -> {
                        bestProductsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setUpBestProductsRv() {
        bestProductsAdapter = BestProductsAdapter()
        binding.rvBestProduct.apply {
            layoutManager = GridLayoutManager(requireContext(),2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductsAdapter
        }
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun setUpSpecialProductsRv() {
        specialProductsAdapter = SpecialProductsAdapter()
        binding.rvSpecialProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = specialProductsAdapter
        }
    }
}