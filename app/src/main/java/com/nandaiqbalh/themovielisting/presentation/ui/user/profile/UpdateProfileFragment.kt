package com.nandaiqbalh.themovielisting.presentation.ui.user.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.nandaiqbalh.themovielisting.R
import com.nandaiqbalh.themovielisting.data.local.model.user.UserEntity
import com.nandaiqbalh.themovielisting.data.local.preference.UserPreferences
import com.nandaiqbalh.themovielisting.databinding.FragmentUpdateProfileBinding
import com.nandaiqbalh.themovielisting.di.UserServiceLocator
import com.nandaiqbalh.themovielisting.util.viewModelFactory
import com.nandaiqbalh.themovielisting.wrapper.Resource

class UpdateProfileFragment : Fragment() {

    private var _binding: FragmentUpdateProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModelFactory {
        ProfileViewModel(UserServiceLocator.provideUserRepository(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClickListener()
        observeData()
    }

    private fun observeData() {
        viewModel.getUser().observe(viewLifecycleOwner) {
            bindDataToForm(it)
        }
    }

    private fun setOnClickListener() {
        binding.btnUpdate.setOnClickListener {
            if (validateInput()) {
                viewModel.updateUser(parseFormIntoData())
                findNavController().navigate(R.id.action_updateProfileFragment_to_profileFragment)
                Toast.makeText(requireContext(), "Update profile success!", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun parseFormIntoData(): UserPreferences {
        return UserPreferences(
            username = binding.etUsername.text.toString().trim(),
            email = binding.etUsername.text.toString().trim(),
            fullName = binding.etFullName.text.toString().trim(),
            dateOfBirth = binding.etDateOfBirth.text.toString().trim(),
            address = binding.etAddress.text.toString().trim()
        ).apply {
            viewModel.getUser().observe(viewLifecycleOwner) {
                this.id = it.id
            }
        }
    }

    private fun bindDataToForm(user: UserPreferences?) {
        user?.let {
            binding.apply {
                etUsername.setText(user.username)
                etEmail.setText(user.email)
                etFullName.setText(user.fullName)
                etDateOfBirth.setText(user.dateOfBirth)
                etAddress.setText(user.address)
            }
        }
    }

    private fun validateInput(): Boolean {
        var isValid = true
        val username = binding.etUsername.text.toString()
        val email = binding.etEmail.text.toString()
        val fullName = binding.etFullName.text.toString()
        val dateOfBirth = binding.etDateOfBirth.text.toString()
        val address = binding.etAddress.text.toString()

        if (username.isEmpty()) {
            isValid = false
            binding.etUsername.error = "Username or password must not be empty"
        }
        if (email.isEmpty()) {
            isValid = false
            binding.etEmail.error = "Email must not be empty"
        }
        if (fullName.isEmpty()) {
            isValid = false
            binding.etFullName.error = "Full name must not be empty"
        }
        if (dateOfBirth.isEmpty()) {
            isValid = false
            binding.etDateOfBirth.error = "Date of birth must not be empty"
        }
        if (address.isEmpty()) {
            isValid = false
            binding.etAddress.error = "Address must not be empty"
        }
        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}