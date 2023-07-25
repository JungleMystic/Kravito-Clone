package com.lrm.kravito.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lrm.kravito.R
import com.lrm.kravito.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

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

        binding.menuFab.setOnClickListener {
            if (isExpanded) {
                shrinkFabMenu()
            } else {
                expandFabMenu()
            }
        }

        binding.profileIcon.setOnClickListener{
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

    override fun onDestroyView() {
        super.onDestroyView()
        isExpanded = false
        _binding = null
    }
}