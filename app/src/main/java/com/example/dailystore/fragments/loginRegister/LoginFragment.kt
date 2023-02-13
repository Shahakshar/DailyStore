package com.example.dailystore.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.dailystore.R
import com.example.dailystore.activities.ShoppingActivity
import com.example.dailystore.data.User
import com.example.dailystore.databinding.FragmentLoginBinding
import com.example.dailystore.databinding.FragmentRegisterBinding
import com.example.dailystore.utils.Resource
import com.example.dailystore.viewmodels.LoginViewModel
import com.example.dailystore.viewmodels.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewmodel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnLoginFragment.setOnClickListener {
                val email = edEmailLogin.text.toString().trim()
                val password = edPasswordLogin.text.toString()


                viewmodel.login(email, password)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewmodel.login.collect {
                when(it) {
                    is Resource.Loading -> {
                        Toast.makeText(requireContext(), "loading..", Toast.LENGTH_SHORT).show()
                        Log.d("TAG", "onViewCreated: loading")
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "not logged in", Toast.LENGTH_SHORT).show()
                        Log.d("TAG", "onViewCreated: loading")
                    }
                    is Resource.Success -> {
                        Intent(requireContext(), ShoppingActivity::class.java).also {intent ->

                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                        }
                    }
                    else -> Unit
                }
            }
        }

    }



}