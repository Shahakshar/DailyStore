package com.example.dailystore.fragments.loginRegister

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.dailystore.data.User
import com.example.dailystore.databinding.FragmentRegisterBinding
import com.example.dailystore.utils.Resource
import com.example.dailystore.viewmodels.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

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

        lifecycleScope.launchWhenStarted {
            viewmodel.register.collect {
                when(it) {
                    is Resource.Loading -> {
                        Log.d("TAG", "onViewCreated: loading")
                    }
                    is Resource.Error -> {
                        Log.d("TAG", "onViewCreated: loading")
                    }
                    is Resource.Success -> {
                        Log.d("TAG", "onViewCreated: loading")
                    }
                    else -> Unit
                }
            }
        }
    }

}