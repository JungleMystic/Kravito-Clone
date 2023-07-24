package com.lrm.kravito.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lrm.kravito.databinding.FragmentEditProfileBinding

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}