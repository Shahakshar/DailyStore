package com.example.dailystore.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.dailystore.adapter.ViewPager2Images
import com.example.dailystore.databinding.FragmentProductDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailsFragment: Fragment() {

    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var binding: FragmentProductDetailsBinding
    private val viewPagerAdapter by lazy { ViewPager2Images() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            findNavController().popBackStack()
        }
    }

    private fun setUpViewPager() {
        binding.apply {
            viewPagerProductImages.adapter = viewPagerAdapter
        }
    }
}