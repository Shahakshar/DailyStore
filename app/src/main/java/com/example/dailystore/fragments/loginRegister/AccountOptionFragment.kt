package com.example.dailystore.fragments.loginRegister

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavGraph
import androidx.navigation.fragment.findNavController
import com.example.dailystore.R
import com.example.dailystore.activities.LoginRegisterActivity
import com.example.dailystore.databinding.FragmentAccountOptionBinding
import com.example.dailystore.viewmodels.IntroductionViewModel

class AccountOptionFragment : Fragment(R.layout.fragment_account_option) {

    private lateinit var binding: FragmentAccountOptionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountOptionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegisterFragment.setOnClickListener {
            findNavController().navigate(R.id.action_accountOptionFragment_to_registerFragment)
        }

        binding.btnLoginFragment.setOnClickListener {
            findNavController().navigate(R.id.action_accountOptionFragment_to_loginFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!findNavController().popBackStack()) findNavController().popBackStack()
    }
}