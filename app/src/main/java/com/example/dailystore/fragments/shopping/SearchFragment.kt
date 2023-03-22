package com.example.dailystore.fragments.shopping

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.dailystore.R
import com.example.dailystore.adapter.SearchRecyclerAdapter
import com.example.dailystore.databinding.FragmentSearchBinding
import com.example.dailystore.utils.Resource
import com.example.dailystore.viewmodels.SearchViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SearchFragment: Fragment(R.layout.fragment_search) {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel by viewModels<SearchViewModel>()
    private lateinit var searchAdapter: SearchRecyclerAdapter
    private lateinit var inputMethodManger: InputMethodManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearchRecyclerView()
        showKeyboardAutomatically()
        onHomeClick()
        getSearchedProduct()
        searchProducts()
        onSearchTextClick()
    }

    private fun onSearchTextClick() {
        searchAdapter.onItemClick = { product ->
            val bundle = Bundle()
            bundle.putParcelable("product", product)

            /**
             * Hide the keyboard
             */

            val imm =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(requireView().windowToken, 0)

            findNavController().navigate(
                R.id.action_searchFragment_to_productDetailsFragment,
                bundle
            )

        }
    }

    private fun setupSearchRecyclerView() {
        searchAdapter = SearchRecyclerAdapter()
        binding.rvSearch.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun getSearchedProduct() {
        lifecycleScope.launchWhenStarted {
            viewModel.searchedProduct.collectLatest { response ->
                when(response) {
                    is Resource.Loading -> {
                        Log.d("test", "Loading...")
                        return@collectLatest
                    }
                    is Resource.Success -> {
                        val products = response.data
                        searchAdapter.differ.submitList(products)
                        binding.tvCancel.visibility = View.VISIBLE
                        return@collectLatest
                    }
                    is Resource.Error -> {
                        Log.e("TAG", response.message.toString())
                        return@collectLatest
                    }
                    else -> Unit
                }
            }
        }

    }

    private fun onHomeClick() {
        val btm = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)
        btm?.menu?.getItem(0)?.setOnMenuItemClickListener {
            activity?.onBackPressed()
            true
        }
    }

    private var job: Job? = null
    private fun searchProducts() {
        binding.edSearch.addTextChangedListener {query ->
            val queryTrim = query.toString().trim()
            if (queryTrim.isNotEmpty()) {
                val searchQuery = query.toString().substring(0, 1)
                    .plus(query.toString().substring(1))
                job?.cancel()
                job = CoroutineScope(Dispatchers.IO).launch {
                    delay(500L)
                    viewModel.searchProduct(searchQuery)
                }
            } else {
                searchAdapter.differ.submitList(emptyList())
            }
        }

        onclickCancel()
    }

    private fun onclickCancel() {
        binding.tvCancel.setOnClickListener {
            hideCancelText()
            searchAdapter.differ.submitList(emptyList())
        }
    }

    private fun hideCancelText() {
        binding.tvCancel.visibility = View.GONE
        binding.edSearch.setText("")
    }


    private fun showKeyboardAutomatically() {
        inputMethodManger =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManger.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )

        binding.edSearch.requestFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.edSearch.clearFocus()
    }

    override fun onResume() {
        super.onResume()
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav?.visibility = View.VISIBLE
    }


}

