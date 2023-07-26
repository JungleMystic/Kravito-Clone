package com.lrm.kravito.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lrm.kravito.R
import com.lrm.kravito.constants.USERS_COLLECTION
import com.lrm.kravito.databinding.FragmentEditProfileBinding
import com.lrm.kravito.model.User
import com.lrm.kravito.viewModel.ProfileViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by activityViewModels()

    private lateinit var userProfile: User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backIcon.setOnClickListener { this.findNavController().navigateUp() }

        userProfile = profileViewModel.userProfile.value!!
        Log.i("MyLogMessages", "EditProfileFragment: UserProfile $userProfile")
        bind(userProfile)

        binding.datePicker.setOnClickListener { showDatePicker()}

        binding.updateButton.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            updateDataToFirestore()
        }
    }

    private fun showDatePicker() {

        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val datePickerDialog = DatePickerDialog(requireContext(),
            { _, dob_year, dob_month, dob_day ->
                calendar.set(dob_year, dob_month, dob_day)
                binding.dobDate.text = sdf.format(calendar.timeInMillis)
            }
            , currentYear, currentMonth, currentDay)

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun updateDataToFirestore() {
        val profileName = binding.profileName.text.toString()
        val profileMail = binding.profileMail.text.toString()
        val profileDob = binding.dobDate.text.toString()

        lifecycleScope.launch {
            uploadDataToFirestore(
                profileName, profileMail, profileDob
            )

            profileViewModel.getUserProfile()
        }
    }

    private fun bind(userProfile: User) {
        binding.profileName.setText(userProfile.profileName, TextView.BufferType.SPANNABLE)
        binding.profileMail.setText(userProfile.profileMail, TextView.BufferType.SPANNABLE)
        binding.dobDate.setText(userProfile.profileDob, TextView.BufferType.SPANNABLE)
        binding.profileNumber.setText(userProfile.phoneNumber, TextView.BufferType.SPANNABLE)

        if (userProfile.profilePic == "") {
            binding.profileImage.setImageResource(R.drawable.profile_user_icon)
        } else {
            Glide.with(this@EditProfileFragment)
                .load(userProfile.profilePic)
                .placeholder(R.drawable.profile_user_icon)
                .into(binding.profileImage)
        }
    }

    private fun uploadDataToFirestore(
        profileName: String,
        profileMail:String,
        profileDob:String
    ) {
        val db = Firebase.firestore

        if (profileName.isEmpty() && profileMail.isEmpty() && profileDob.isEmpty()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
        } else {
            val userData = User(
                profileName,
                profileMail,
                userProfile.phoneNumber,
                "",
                profileViewModel.currentUserUid,
                profileDob
            )

            db.collection(USERS_COLLECTION).document(userProfile.phoneNumber).set(userData)
                .addOnSuccessListener {
                    binding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(requireContext(), "Profile updated successfully...", Toast.LENGTH_SHORT).show()
                    this@EditProfileFragment.findNavController().navigateUp()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "An error occurred while updating profile", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}