package com.nandaiqbalh.themovielisting.presentation.ui.user.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nandaiqbalh.themovielisting.R
import com.nandaiqbalh.themovielisting.data.network.firebase.model.User
import com.nandaiqbalh.themovielisting.databinding.FragmentLoginBinding
import com.nandaiqbalh.themovielisting.presentation.ui.movie.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isUserLoggedIn()

        binding.btnLogin.setOnClickListener {
            signIn()
            checkLogin()
        }

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

    }

    private fun checkLogin() {
        val isLoginSuccess = viewModel.isLoginSuccess()
        if (isLoginSuccess) {
            navigateToHome()
            viewModel.setUserLogin(true)
        }
    }

    private fun signIn() {
        if (validateInput()) {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()

            viewModel.signInFirebase(username, password)
        }
    }

    private fun isUserLoggedIn() {
        viewModel.getUserLogin().observe(viewLifecycleOwner) {
            if (it) {
                navigateToHome()
            }
        }
    }

    private fun validateInput(): Boolean {
        var isValid = true
        val username = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()
        if (username.isEmpty()) {
            isValid = false
            binding.etUsername.error = "This field must not be empty!"
        }
        if (password.isEmpty()) {
            isValid = false
            Toast.makeText(requireContext(), "This field must not be empty!", Toast.LENGTH_SHORT)
                .show()
        }
        return isValid
    }

    fun navigateToHome(user: User? = null) {
        viewModel.setUserLogin(true)

        val intent = Intent(requireContext(), HomeActivity::class.java).apply {
            putExtra(EXTRA_USERNAME, user?.username)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        activity?.finish()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val EXTRA_USERNAME = "extra_username"
    }
}