package com.lrm.kravito.fragments

import android.Manifest
import android.app.DatePickerDialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.lrm.kravito.R
import com.lrm.kravito.constants.LOG_DATA
import com.lrm.kravito.constants.USERS_COLLECTION
import com.lrm.kravito.databinding.FragmentEditProfileBinding
import com.lrm.kravito.model.User
import com.lrm.kravito.utils.PermissionCodes
import com.lrm.kravito.viewModel.ProfileViewModel
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditProfileFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by activityViewModels()

    private var filePath: Uri = Uri.EMPTY
    private lateinit var storageRef: StorageReference
    private val contract = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        if (it != null) {
            filePath = it
            Log.i(LOG_DATA, "EditProfileFragment: File path-> $filePath")
            binding.profileImage.setImageURI(filePath)
        } else {
            filePath = Uri.EMPTY
            binding.profileImage.setImageResource(R.drawable.profile_user_icon)
        }
    }

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

        profileViewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            Log.i(LOG_DATA, "EditProfileFragment: LiveData Observer $userProfile")
            if (userProfile != null) {
                bind(userProfile)
            }
        }

        binding.profileImage.setOnClickListener {
            if (hasPermission()) {
                pickImageFromGallery()
            } else {
                requestPermission()
            }
        }

        binding.datePicker.setOnClickListener { showDatePicker() }

        binding.updateButton.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            updateDataToFirestore()
        }
    }

    private fun pickImageFromGallery() {
        contract.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val datePickerDialog = DatePickerDialog(requireContext(),
            { _, dobYear, dobMonth, dobDay ->
                calendar.set(dobYear, dobMonth, dobDay)
                binding.dobDate.text = sdf.format(calendar.timeInMillis)
            }, currentYear, currentMonth, currentDay
        )

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun updateDataToFirestore() {
        val profileName = binding.profileName.text.toString()
        val profileMail = binding.profileMail.text.toString()
        val profileDob = binding.dobDate.text.toString()

        val phoneNumber = profileViewModel.userProfile.value?.phoneNumber
        var profilePic = ""

        storageRef = FirebaseStorage.getInstance().reference

        if (filePath != Uri.EMPTY) {
            val reference = storageRef.child("profilePics/$phoneNumber")

            reference.putFile(filePath)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        reference.downloadUrl.addOnSuccessListener { task ->
                            profilePic = task.toString()
                            uploadDataToFirestore(profileName, profileMail, profileDob, profilePic)
                        }
                    }
                }
                .addOnFailureListener{
                    Toast.makeText(requireContext(), "Image Upload failed", Toast.LENGTH_SHORT).show()
                    profilePic = ""
                    uploadDataToFirestore(profileName, profileMail, profileDob, profilePic)
                }
        } else if (filePath == Uri.EMPTY) {
            profilePic = ""
            uploadDataToFirestore(profileName, profileMail, profileDob, profilePic)
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
        profileMail: String,
        profileDob: String,
        profilePicUrl: String
    ) {
        val phoneNumber = profileViewModel.userProfile.value?.phoneNumber
        val db = Firebase.firestore

        if (profileName.isEmpty() && profileMail.isEmpty() && profileDob.isEmpty()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
        } else {
            val userData = User(
                profileName,
                profileMail,
                phoneNumber!!,
                profilePicUrl,
                profileViewModel.currentUserUid,
                profileDob
            )

            db.collection(USERS_COLLECTION).document(phoneNumber).set(userData)
                .addOnSuccessListener {
                    binding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(
                        requireContext(),
                        "Profile updated successfully...",
                        Toast.LENGTH_SHORT
                    ).show()
                    profileViewModel.getUserProfile()
                    this@EditProfileFragment.findNavController().navigateUp()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        "An error occurred while updating profile",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun hasPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            EasyPermissions.hasPermissions(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            EasyPermissions.hasPermissions(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            EasyPermissions.requestPermissions(
                this,
                "Permission is required to upload profile photo",
                PermissionCodes.READ_EXTERNAL_STORAGE_PERMISSION_CODE,
                Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "Permission is required to upload profile photo",
                PermissionCodes.READ_EXTERNAL_STORAGE_PERMISSION_CODE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.permissionPermanentlyDenied(this, perms.first())) {
            SettingsDialog.Builder(requireContext()).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}