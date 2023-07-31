package com.lrm.kravito.fragments

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.constants.ActionTypes
import com.denzcoskun.imageslider.interfaces.TouchListener
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lrm.kravito.R
import com.lrm.kravito.adapter.RestaurantListAdapter
import com.lrm.kravito.adapter.SuggestedFoodAdapter
import com.lrm.kravito.constants.LOG_DATA
import com.lrm.kravito.data.OffersData
import com.lrm.kravito.data.RestaurantData
import com.lrm.kravito.data.SuggestedFoodData
import com.lrm.kravito.databinding.FragmentHomeBinding
import com.lrm.kravito.utils.PermissionCodes
import com.lrm.kravito.viewModel.ProfileViewModel
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.coroutines.launch
import java.io.IOException
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

    private lateinit var networkDialog: Dialog

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

        checkInternet()

        Handler(Looper.myLooper()!!).postDelayed({
            if (profileViewModel.userLocation.value == "Loading Location...") {
                if (hasPermissions()) {
                    getLocation()
                }
            }
        }, 2000)

        if (profileViewModel.userProfile.value == null) {
            Log.i(
                LOG_DATA,
                "HomeFragment: userProfile ${profileViewModel.userProfile.value}"
            )
            Log.i(LOG_DATA, "HomeFragment: getUserProfile is called")
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

        networkDialog = Dialog(requireContext())
        networkDialog.apply {
            setCancelable(false)
            setContentView(R.layout.custom_dialog_network)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            findViewById<TextView>(R.id.go_settings_tv).setOnClickListener {
                val intent = Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS)
                startActivity(intent)
            }
        }

        profileViewModel.internet.observe(viewLifecycleOwner) { status ->
            if (status == false) {
                networkDialog.show()
                Log.i(LOG_DATA, "showCustomNoNetworkDialog: show is called")
            } else {
                networkDialog.cancel()
                Log.i(LOG_DATA, "showCustomNoNetworkDialog: cancel is called")
            }
        }

        if (!hasPermissions()) {
            requestNotificationAndLocationPermissions()
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

        binding.offersImageSlider.apply {
            setImageList(OffersData.imageList)
            setTouchListener(object : TouchListener {
                override fun onTouched(touched: ActionTypes, position: Int) {
                    if (touched == ActionTypes.DOWN) {
                        stopSliding()
                    } else if (touched == ActionTypes.UP) {
                        startSliding()
                    }
                }
            })
        }

        binding.suggestedFoodRv.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = SuggestedFoodAdapter(SuggestedFoodData.suggestedList)
        }

        binding.restaurantsRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = RestaurantListAdapter(RestaurantData.restaurantList) {
                val action = HomeFragmentDirections.actionHomeFragmentToRestaurantFragment(it.id)
                this@HomeFragment.findNavController().navigate(action)
            }
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

    private fun checkInternet() {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        connectivityManager.registerDefaultNetworkCallback(object :
            ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                profileViewModel.setInternetConnectionStatus(true)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                profileViewModel.setInternetConnectionStatus(false)
                Toast.makeText(requireContext(), "Disconnected", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getLocation() {
        Log.i(LOG_DATA, "HomeFragment: getLocation is called")

        lifecycleScope.launch {
            val fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(requireContext())

            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@launch
            }
            if (isLocationEnabled()) {
                Toast.makeText(requireContext(), "Getting Location...", Toast.LENGTH_SHORT).show()
                fusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        try {
                            val geoCoder = Geocoder(requireContext(), Locale.getDefault())
                            val list: List<Address>? =
                                geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                            val address = list!![0]
                            val userLocation = "${address.getAddressLine(0)} \n${address.locality}"
                            profileViewModel.setUserLocation(userLocation)
                            Log.i(LOG_DATA, "HomeFragment: Location Update $userLocation")
                            Toast.makeText(requireContext(), "Location Updated", Toast.LENGTH_SHORT)
                                .show()
                        } catch (e: IOException) {
                            Toast.makeText(requireContext(), "Turn on Internet", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please turn on location", Toast.LENGTH_LONG)
                    .show()
                showTurnOnLocationDialog()
            }
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
        binding.nestedSv.alpha = 0.1F
        binding.menuFab.startAnimation(rotateClockWise)
        binding.notificationLl.startAnimation(fabUp)
        binding.favouritesLl.startAnimation(fabUp)
        binding.trackOrderLl.startAnimation(fabUp)
    }

    private fun shrinkFabMenu() {
        isExpanded = !isExpanded
        binding.nestedSv.alpha = 1.0F
        binding.menuFab.startAnimation(rotateAntiClockWise)
        binding.notificationLl.startAnimation(fabDown)
        binding.favouritesLl.startAnimation(fabDown)
        binding.trackOrderLl.startAnimation(fabDown)
    }

    private fun hasPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            EasyPermissions.hasPermissions(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.hasPermissions(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }

    private fun requestNotificationAndLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            EasyPermissions.requestPermissions(
                this,
                "Permission is required to get notifications and location",
                PermissionCodes.NOTIFICATION_LOCATION_PERMISSION_CODE,
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "Permission is required to get location",
                PermissionCodes.NOTIFICATION_LOCATION_PERMISSION_CODE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
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