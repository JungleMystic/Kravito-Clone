package com.lrm.kravito.fragments

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.lrm.kravito.R
import com.lrm.kravito.constants.LOG_DATA
import com.lrm.kravito.databinding.FragmentOtpVerificationBinding
import com.lrm.kravito.viewModel.LoginViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class OtpVerificationFragment : Fragment() {

    private var _binding: FragmentOtpVerificationBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by activityViewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var resendTimer: CountDownTimer

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

        resendTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.resendTimeCount.text = resources.getString(
                    R.string.resend_time_count_text,
                    (millisUntilFinished / 1000)
                )
            }

            override fun onFinish() {
                binding.resendTimeCount.visibility = View.GONE
                binding.resendOtpButton.visibility = View.VISIBLE
                hideSoftKeyboard()
            }
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {

            }

            override fun onVerificationFailed(p0: FirebaseException) {
                p0.printStackTrace()
                Log.e(LOG_DATA, "onVerificationFailed: ${p0.message}")
                Toast.makeText(requireContext(), "Login Failed, Try again...", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                loginViewModel.setVerificationId(verificationId)
                resendToken = token
                binding.resendTimeCount.visibility = View.VISIBLE
                resendTimer.start()
                Toast.makeText(requireContext(), "OTP Sent", Toast.LENGTH_SHORT).show()
                binding.otpNumPinView.isEnabled = true
            }
        }

        sendVerificationCode("+91${loginViewModel.phoneNumber.value}")

        binding.backIcon.setOnClickListener { this.findNavController().navigateUp() }

        binding.phoneNumber.text =
            resources.getString(R.string.otp_phone_number, loginViewModel.phoneNumber.value)
        binding.verifyOtpButton.setOnClickListener {
            verifyOTP()
        }

        binding.resendOtpButton.setOnClickListener {
            binding.resendOtpButton.visibility = View.INVISIBLE
            resendVerificationCode("+91${loginViewModel.phoneNumber.value}")
        }
    }

    private fun checkUserInFirestore() {

        val currentUserPhone = auth.currentUser?.phoneNumber
        val currentUserUid = auth.currentUser?.uid

        Log.i(LOG_DATA, "checkUser: $currentUserPhone")
        Log.i(LOG_DATA, "checkUser: $currentUserUid")

        lifecycleScope.launch {
            if (currentUserPhone != null) {
                if (currentUserUid != null) {
                    loginViewModel.checkUserInFirestore(requireContext(), currentUserPhone, currentUserUid)
                }
            }
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

    private fun resendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .setForceResendingToken(resendToken)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyOTP() {
        hideSoftKeyboard()
        val verificationId = loginViewModel.verificationId.value!!
        val otpEntered = binding.otpNumPinView.text.toString()

        if (otpEntered.isNotEmpty() && otpEntered.length == 6) {
            val credential = PhoneAuthProvider.getCredential(verificationId, otpEntered)
            signInWithPhoneAuthCredential(credential)
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
            Toast.makeText(requireContext(), "Enter valid OTP...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    resendTimer.cancel()
                    val action =
                        OtpVerificationFragmentDirections.actionOtpVerificationFragmentToHomeFragment()
                    this@OtpVerificationFragment.findNavController().navigate(action)

                    checkUserInFirestore()

                } else if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    binding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(requireContext(), "Invalid OTP...", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun hideSoftKeyboard() {
        val inputManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        resendTimer.cancel()
        _binding = null
    }
}