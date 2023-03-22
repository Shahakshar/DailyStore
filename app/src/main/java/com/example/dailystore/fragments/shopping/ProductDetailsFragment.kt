package com.example.dailystore.fragments.shopping

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.dailystore.R
import com.example.dailystore.adapter.ViewPager2Images
import com.example.dailystore.data.CartProduct
import com.example.dailystore.databinding.FragmentProductDetailsBinding
import com.example.dailystore.utils.Resource
import com.example.dailystore.utils.hideBottomNavigationView
import com.example.dailystore.viewmodels.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {

    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var binding: FragmentProductDetailsBinding
    private val viewPagerAdapter by lazy { ViewPager2Images() }

    private val viewModel by viewModels<DetailViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // hide bottom view
        hideBottomNavigationView()
        binding = FragmentProductDetailsBinding.inflate(inflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product

        setUpViewPager()
        binding.apply {
            tvProductName.text = product.name
            tvProductPrice.text = "₹${product.price}"
            productDescription.text = product.description
        }

        viewPagerAdapter.differ.submitList(product.image)

        binding.imageClose.setOnClickListener {
            // here just i can pop my current fragment to go on previous screen
            // because here stack is maintain for all fragment and one by one visited fragment push in to stack
            // we just going to pop them
//            findNavController().popBackStack()
            findNavController().navigateUp()
        }

        binding.btnAddToCard.setOnClickListener {
            showProgress()
            viewModel.updateProductInCart(
                CartProduct(
                    product,
                    1,
                    selectedColor = null,
                    selectedSize = null
                )
            )
        }

        lifecycleScope.launchWhenStarted {
            viewModel.addCart.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Success -> {
                        Toast.makeText(requireContext(), "Successfully added", Toast.LENGTH_SHORT).show()
                        hideProgress()
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun hideProgress() {
        binding.progressDetails.visibility = View.GONE
    }

    private fun showProgress() {
        binding.progressDetails.visibility = View.VISIBLE
    }

    private fun setUpViewPager() {
        binding.apply {
            viewPagerProductImages.adapter = viewPagerAdapter
        }
    }
}