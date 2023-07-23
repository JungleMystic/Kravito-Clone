package com.lrm.kravito.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.lrm.kravito.R
import com.lrm.kravito.databinding.FragmentOtpVerificationBinding
import com.lrm.kravito.viewModel.LoginViewModel
import java.util.concurrent.TimeUnit

class OtpVerificationFragment : Fragment() {

    private var _binding: FragmentOtpVerificationBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by activityViewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOtpVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        callbacks = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                this@OtpVerificationFragment.findNavController().navigate(R.id.action_otpVerificationFragment_to_homeFragment)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                p0.printStackTrace()
                Log.e("MyLogMessages", "onVerificationFailed: ${p0.message}", )
                Toast.makeText(requireContext(), "Login Failed, Try again...", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                loginViewModel.setVerificationId(verificationId)
                resendToken = token
            }
        }

        sendVerificationCode("+91${loginViewModel.phoneNumber.value}")

        binding.backIcon.setOnClickListener { this.findNavController().navigateUp() }

        binding.phoneNumber.text = resources.getString(R.string.otp_phone_number, loginViewModel.phoneNumber.value)
        binding.verifyOtp.setOnClickListener {
            verifyOTP()
        }
    }


    private fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyOTP() {
        val verificationId = loginViewModel.verificationId.value!!
        val otpEntered = binding.otpNumPinView.text.toString()

        if (otpEntered.isNotEmpty() && otpEntered.length == 6) {
            val credential = PhoneAuthProvider.getCredential(verificationId, otpEntered)
            signInWithPhoneAuthCredential(credential)
        } else {
            Toast.makeText(requireContext(), "Enter valid OTP...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()){task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "OTP Verified...", Toast.LENGTH_SHORT).show()
                    val action = OtpVerificationFragmentDirections.actionOtpVerificationFragmentToHomeFragment()
                    this@OtpVerificationFragment.findNavController().navigate(action)
                } else if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(requireContext(), "Invalid OTP...", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}