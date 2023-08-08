package com.lrm.kravito.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.lrm.kravito.data.RestaurantData
import com.lrm.kravito.databinding.FragmentOrderSummaryBinding
import com.lrm.kravito.viewModel.OrderViewModel
import com.lrm.kravito.viewModel.ProfileViewModel

class OrderSummaryFragment : Fragment() {

    private var _binding: FragmentOrderSummaryBinding? = null
    private val binding get() = _binding!!

    private val orderViewModel: OrderViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderSummaryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderViewModel.totalWithTax.observe(viewLifecycleOwner) {newTotal->
            binding.grandTotalTv.text = newTotal.toString()
        }

        orderViewModel.restaurantName.observe(viewLifecycleOwner) {name->
            binding.restaurantName.text = name
            val restaurant = RestaurantData.restaurantList.single{it.name == name}
            binding.restaurantAddress.text = restaurant.location
        }

        binding.yourName.text = profileViewModel.userProfile.value?.profileName
        binding.yourAddress.text = profileViewModel.userLocation.value

        binding.placeOrderButton.setOnClickListener {
            if (binding.cod.isChecked || binding.phonepe.isChecked) {
                Toast.makeText(requireContext(), "Order Placed", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please select mode of payment", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cod.setOnClickListener{
            orderViewModel.setModeOfPayment("cod")
        }
        binding.phonepe.setOnClickListener {
            orderViewModel.setModeOfPayment("phonepe")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}