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
import androidx.navigation.fragment.findNavController
import com.example.dailystore.R
import com.example.dailystore.activities.ShoppingActivity
import com.example.dailystore.data.User
import com.example.dailystore.databinding.FragmentLoginBinding
import com.example.dailystore.databinding.FragmentRegisterBinding
import com.example.dailystore.dialog.setupBottomSheetDialog
import com.example.dailystore.utils.RegisterValidation
import com.example.dailystore.utils.Resource
import com.example.dailystore.viewmodels.LoginViewModel
import com.example.dailystore.viewmodels.RegisterViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext

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

        binding.tvDontHaveAnAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.tvForgotPassword.setOnClickListener {
            setupBottomSheetDialog { email ->
                viewmodel.resetPassword(email)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewmodel.resetPassword.collect{
                when(it) {
                    is Resource.Loading -> {
                        Toast.makeText(requireContext(), "loading..", Toast.LENGTH_SHORT).show()
                        Log.d("TAG", "onViewCreated: loading")
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(), "error ${it.message}", Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Success -> {
                        Snackbar.make(requireView(), "reset link was sent to your email", Snackbar.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
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
                        val intent = Intent(activity,ShoppingActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                    else -> Unit
                }
            }
        }


        lifecycleScope.launchWhenStarted {
            viewmodel.validation.collect{validation ->
                if(validation.email is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edEmailLogin.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }

                if(validation.password is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edPasswordLogin.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }
            }
        }
    }



}