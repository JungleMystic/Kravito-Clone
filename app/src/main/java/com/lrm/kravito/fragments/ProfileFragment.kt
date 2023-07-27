package com.lrm.kravito.fragments

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.lrm.kravito.R
import com.lrm.kravito.constants.PLAY_STORE_LINK
import com.lrm.kravito.constants.PRIVACY_POLICY_URI
import com.lrm.kravito.constants.RETURN_AND_CANCELLATION_POLICY
import com.lrm.kravito.constants.TERMS_AND_CONDITIONS_URI
import com.lrm.kravito.databinding.FragmentProfileBinding
import com.lrm.kravito.model.User
import com.lrm.kravito.utils.PermissionCodes
import com.lrm.kravito.viewModel.ProfileViewModel
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog

class ProfileFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by activityViewModels()

    private lateinit var auth: FirebaseAuth

    private val rotateClockWise: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_clock_wise)
    }
    private val rotateAntiClockWise: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_anti_clock_wise)
    }

    private val dropDownMenuDown: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.drop_down_menu_down)
    }

    private var isTosExpanded = false
    private var isHelpSupportExpanded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backIcon.setOnClickListener { this.findNavController().navigateUp() }

        auth = FirebaseAuth.getInstance()

        val userProfile = profileViewModel.userProfile.value

        if(userProfile != null) {
            Log.i("MyLogMessages", "ProfileFragment: UserProfile $userProfile")
            bind(userProfile)
        }

        binding.tosCv.setOnClickListener { expandTos() }

        binding.helpSupportCv.setOnClickListener { expandHelpSupport() }

        binding.editProfileCv.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment()
            this.findNavController().navigate(action)
        }

        binding.tAndC.setOnClickListener { showTermsAndConditions() }
        binding.privacyPolicy.setOnClickListener { showPrivacyPolicy() }
        binding.returnPolicy.setOnClickListener { showReturnAndCancellationPolicy() }
        binding.rateAppCv.setOnClickListener { openPlayStore() }

        binding.callSupport.setOnClickListener {
            if (hasPermission()) {
                makeCall("8008308770")
            } else {
                requestPermission()
            }
        }

        binding.logOutCv.setOnClickListener { showLogOutDialog() }
    }

    private fun makeCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
    }



    private fun bind(userProfile: User){
        binding.profileName.text = userProfile.profileName
        binding.profileDob.text = userProfile.profileDob
        binding.profileMail.text = userProfile.profileMail
        binding.profileMobile.text = userProfile.phoneNumber

        if (userProfile.profilePic == "") {
            binding.profilePic.setImageResource(R.drawable.profile_user_icon)
        } else {
            Glide.with(this@ProfileFragment)
                .load(userProfile.profilePic)
                .placeholder(R.drawable.profile_user_icon)
                .into(binding.profilePic)
        }
    }

    private fun expandHelpSupport() {
        isHelpSupportExpanded = !isHelpSupportExpanded
        if (isHelpSupportExpanded) {
            binding.helpSupportExpand.startAnimation(rotateClockWise)
            binding.emailSupport.visibility = View.VISIBLE
            binding.emailSupport.startAnimation(dropDownMenuDown)
            binding.callSupport.visibility = View.VISIBLE
            binding.callSupport.startAnimation(dropDownMenuDown)
        } else {
            binding.helpSupportExpand.startAnimation(rotateAntiClockWise)
            binding.emailSupport.visibility = View.GONE
            binding.callSupport.visibility = View.GONE
        }
    }

    private fun expandTos() {
        isTosExpanded = !isTosExpanded
        if (isTosExpanded) {
            binding.tosExpand.startAnimation(rotateClockWise)
            binding.tAndC.visibility = View.VISIBLE
            binding.tAndC.startAnimation(dropDownMenuDown)
            binding.privacyPolicy.visibility = View.VISIBLE
            binding.privacyPolicy.startAnimation(dropDownMenuDown)
            binding.returnPolicy.visibility = View.VISIBLE
            binding.returnPolicy.startAnimation(dropDownMenuDown)
        } else {
            binding.tosExpand.startAnimation(rotateAntiClockWise)
            binding.tAndC.visibility = View.GONE
            binding.privacyPolicy.visibility = View.GONE
            binding.returnPolicy.visibility = View.GONE
        }
    }

    private fun showLogOutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.log_out))
            .setMessage(getString(R.string.log_out_message))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) {_, _ -> }
            .setPositiveButton(getString(R.string.yes)) {_, _ ->
                logOut()
            }
            .show()
    }

    private fun hasPermission(): Boolean {
        return EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.CALL_PHONE
        )
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.permissionPermanentlyDenied(this, perms.first())) {
            SettingsDialog.Builder(requireContext()).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
    }

    private fun requestPermission() {
        EasyPermissions.requestPermissions(
            this,
            "Permission is required to make call",
            PermissionCodes.CALL_PERMISSION_CODE,
            Manifest.permission.CALL_PHONE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //EasyPermissions handles the request result
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    private fun logOut() {
        profileViewModel.setUserProfile(null)
        Log.i("MyLogMessages", "ProfileFragment: After SignOut ${profileViewModel.userProfile.value}")
        auth.signOut()
        val action = ProfileFragmentDirections.actionProfileFragmentToLoginFragment()
        this.findNavController().navigate(action)
    }

    private fun showTermsAndConditions() {
        val uri: Uri = Uri.parse(TERMS_AND_CONDITIONS_URI)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        requireContext().startActivity(intent)
    }

    private fun openPlayStore() {
        val uri: Uri = Uri.parse(PLAY_STORE_LINK)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        requireContext().startActivity(intent)
    }

    private fun showPrivacyPolicy() {
        val uri: Uri = Uri.parse(PRIVACY_POLICY_URI)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        requireContext().startActivity(intent)
    }

    private fun showReturnAndCancellationPolicy() {
        val uri: Uri = Uri.parse(RETURN_AND_CANCELLATION_POLICY)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        requireContext().startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}