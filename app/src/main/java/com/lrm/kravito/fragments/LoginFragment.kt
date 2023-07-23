package com.lrm.kravito.fragments

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.lrm.kravito.constants.PRIVACY_POLICY_URI
import com.lrm.kravito.constants.TERMS_AND_CONDITIONS_URI
import com.lrm.kravito.databinding.FragmentLoginBinding
import com.lrm.kravito.viewModel.LoginViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.termsAndConditionsTv.setOnClickListener {
            showTermsAndConditions()
        }

        binding.privacyPolicyTv.setOnClickListener {
            showPrivacyPolicy()
        }

        binding.continueButton.setOnClickListener {
           onLogin()
        }
    }

    private fun onLogin() {

        hideSoftKeyboard()

        val phoneNumber = binding.mobileNumber.text.toString()

        if (loginViewModel.isLoginValid(
                requireContext(),
                phoneNumber,
                binding.termsCheckbox.isChecked
            )
        ) {
            loginViewModel.setPhoneNumber(phoneNumber)
            val action = LoginFragmentDirections.actionLoginFragmentToOtpVerificationFragment()
            this.findNavController().navigate(action)
        }
    }

    private fun hideSoftKeyboard() {
        val inputManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
    }

    private fun showTermsAndConditions() {
        val uri: Uri = Uri.parse(TERMS_AND_CONDITIONS_URI)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        requireContext().startActivity(intent)
    }

    private fun showPrivacyPolicy() {
        val uri: Uri = Uri.parse(PRIVACY_POLICY_URI)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        requireContext().startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}