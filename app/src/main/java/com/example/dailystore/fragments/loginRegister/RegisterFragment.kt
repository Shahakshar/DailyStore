package com.example.dailystore.fragments.loginRegister

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
import com.example.dailystore.data.User
import com.example.dailystore.databinding.FragmentRegisterBinding
import com.example.dailystore.utils.RegisterValidation
import com.example.dailystore.utils.Resource
import com.example.dailystore.viewmodels.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewmodel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnRegister.setOnClickListener {
                val user = User(
                    edFirstName.text.toString().trim(),
                    edLastName.text.toString().trim(),
                    edEmail.text.toString().trim()
                )
                val password = edPassword.text.toString()
                viewmodel.crateAccountWithEmailAndPassword(user, password)
            }
        }

        binding.tvAlreadyHaveAccount.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }


        lifecycleScope.launchWhenStarted {
            viewmodel.register.collect {
                when(it) {
                    is Resource.Loading -> {
                        Toast.makeText(requireContext(), "loading..", Toast.LENGTH_SHORT).show()
                        Log.d("TAG", "onViewCreated: loading")
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "account already exist", Toast.LENGTH_SHORT).show()
                        Log.d("TAG", "onViewCreated: loading")
                    }
                    is Resource.Success -> {
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        Toast.makeText(requireContext(), "account created successfully", Toast.LENGTH_SHORT).show()
                        Log.d("TAG", "onViewCreated: loading")
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewmodel.validation.collect { validation ->
                if(validation.email is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edEmail.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }

                if (validation.password is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edPassword.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }
            }
        }
    }
}