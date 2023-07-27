package com.lrm.kravito.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lrm.kravito.R
import com.lrm.kravito.databinding.FragmentHomeBinding
import com.lrm.kravito.utils.PermissionCodes
import com.lrm.kravito.viewModel.ProfileViewModel
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import java.util.Locale

class HomeFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var isExpanded = false
    private val fabUp: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.fab_up)
    }
    private val fabDown: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.fab_down)
    }
    private val rotateClockWise: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_clock_wise)
    }
    private val rotateAntiClockWise: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_anti_clock_wise)
    }

    private val profileViewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (profileViewModel.userProfile.value == null) {
            Log.i(
                "MyLogMessages",
                "HomeFragment: userProfile ${profileViewModel.userProfile.value}"
            )
            Log.i("MyLogMessages", "HomeFragment: getUserProfile is called")
            profileViewModel.getUserProfile()
        }

        profileViewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            if (userProfile?.profilePic == "") {
                binding.profileIcon.setImageResource(R.drawable.profile_user_icon)
            } else {
                Glide.with(this@HomeFragment)
                    .load(userProfile?.profilePic)
                    .placeholder(R.drawable.profile_user_icon)
                    .into(binding.profileIcon)
            }
        }

        if (!hasPermissions()) {
            requestNotificationAndLocationPermissions()
        }

        if (hasPermissions()) {
            if (profileViewModel.userLocation.value == "Loading Location...") {
                getLocation()
            }
        }

        binding.locationText.setOnClickListener {
            if (hasPermissions()) {
                getLocation()
            } else {
                requestNotificationAndLocationPermissions()
            }
        }

        profileViewModel.userLocation.observe(viewLifecycleOwner) { newLocation ->
            binding.locationText.text = newLocation
        }

        binding.menuFab.setOnClickListener {
            if (isExpanded) {
                shrinkFabMenu()
            } else {
                expandFabMenu()
            }
        }

        binding.profileIcon.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToProfileFragment()
            this.findNavController().navigate(action)
        }

        binding.favouritesFab.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToFavouritesFragment()
            this.findNavController().navigate(action)
        }

        binding.notificationsFab.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToNotificationsFragment()
            this.findNavController().navigate(action)
        }

        binding.trackOrderFab.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToTrackOrderFragment()
            this.findNavController().navigate(action)
        }
    }

    private fun getLocation() {
        Log.i("MyLogMessages", "HomeFragment: getLocation is called")

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        if (isLocationEnabled()) {
            fusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                val location: Location? = task.result
                if (location != null) {
                    val geoCoder = Geocoder(requireContext(), Locale.getDefault())
                    val list: List<Address>? =
                        geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                    val address = list!![0]
                    val userLocation = "${address.getAddressLine(0)} \n${address.locality}"
                    profileViewModel.setUserLocation(userLocation)
                    Log.i("MyLogMessages", "HomeFragment: Location Update $userLocation")
                }
            }
        } else {
            Toast.makeText(requireContext(), "Please turn on location", Toast.LENGTH_LONG).show()
            showTurnOnLocationDialog()
        }
    }

    private fun showTurnOnLocationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Turn on Location")
            .setCancelable(true)
            .setPositiveButton("Go to Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            .show()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun expandFabMenu() {
        isExpanded = !isExpanded
        binding.menuFab.startAnimation(rotateClockWise)
        binding.notificationLl.startAnimation(fabUp)
        binding.favouritesLl.startAnimation(fabUp)
        binding.trackOrderLl.startAnimation(fabUp)
    }

    private fun shrinkFabMenu() {
        isExpanded = !isExpanded
        binding.menuFab.startAnimation(rotateAntiClockWise)
        binding.notificationLl.startAnimation(fabDown)
        binding.favouritesLl.startAnimation(fabDown)
        binding.trackOrderLl.startAnimation(fabDown)
    }

    private fun hasPermissions(): Boolean {
        return EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    private fun requestNotificationAndLocationPermissions() {
        EasyPermissions.requestPermissions(
            this,
            "Permission is required to get notifications",
            PermissionCodes.NOTIFICATION_LOCATION_PERMISSION_CODE,
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.permissionPermanentlyDenied(this, perms.first())) {
            SettingsDialog.Builder(requireContext()).build().show()
        }

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(requireContext()).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if (hasPermissions()) {
            getLocation()
        }
        Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        isExpanded = false
        _binding = null
    }
}