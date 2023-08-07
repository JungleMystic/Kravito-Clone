package com.lrm.kravito.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.lrm.kravito.R
import com.lrm.kravito.adapter.ItemCartAdapter
import com.lrm.kravito.constants.LOG_DATA
import com.lrm.kravito.data.OrderItem
import com.lrm.kravito.databinding.FragmentItemCartBinding
import com.lrm.kravito.viewModel.OrderViewModel

class ItemCartFragment : Fragment() {

    private var _binding: FragmentItemCartBinding? = null
    private val binding get() = _binding!!

    private val orderViewModel: OrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemCartBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backIcon.setOnClickListener { this.findNavController().navigateUp() }

        val adapter = ItemCartAdapter(orderViewModel)
        binding.itemCartRv.adapter = adapter
        orderViewModel.orderCartList.observe(viewLifecycleOwner) {itemsList ->
            itemsList.let {
                Log.i(LOG_DATA, "List is observing")
                adapter.submitList(it)
                checkIfCartIsEmpty(it)
            }
        }

        orderViewModel.totalCartValue.observe(viewLifecycleOwner) {newTotal ->
            binding.totalTv.text = newTotal.toString()
        }
        orderViewModel.tax.observe(viewLifecycleOwner){newTax ->
            binding.gstTv.text = newTax.toString()
        }
        orderViewModel.deliveryFee.observe(viewLifecycleOwner) {newDeliveryFee ->
            binding.deliveryFeeTv.text = newDeliveryFee.toString()
        }
        orderViewModel.deliveryTip.observe(viewLifecycleOwner) {newTip ->
            binding.deliveryTipTv.text = newTip.toString()
        }
        orderViewModel.totalWithTax.observe(viewLifecycleOwner) {newGrandTotal->
            binding.grandTotalTv.text = newGrandTotal.toString()
            binding.summaryTotal.text = newGrandTotal.toString()
        }

        binding.chip20.setOnClickListener {
            if (binding.chip20.isChecked) {
                orderViewModel.setDeliveryTip(20)
            } else {
                orderViewModel.setDeliveryTip(0)
            }
        }

        binding.chip30.setOnClickListener {
            if (binding.chip30.isChecked) {
                orderViewModel.setDeliveryTip(30)
            } else {
                orderViewModel.setDeliveryTip(0)
            }
        }

        binding.chip40.setOnClickListener {
            if (binding.chip40.isChecked) {
                orderViewModel.setDeliveryTip(40)
            } else {
                orderViewModel.setDeliveryTip(0)
            }
        }

        binding.chip50.setOnClickListener {
            if (binding.chip50.isChecked) {
                orderViewModel.setDeliveryTip(50)
            } else {
                orderViewModel.setDeliveryTip(0)
            }
        }

        binding.avoidRing.setOnClickListener {
            binding.avoidRing.isChecked = !binding.avoidRing.isChecked
            orderViewModel.setDeliveryInstruction(getString(R.string.avoid_ringing_bell))
        }
        binding.leaveAtDoor.setOnClickListener {
            binding.leaveAtDoor.isChecked = !binding.leaveAtDoor.isChecked
            orderViewModel.setDeliveryInstruction(getString(R.string.leave_at_the_door))
        }
        binding.directionsToReach.setOnClickListener {
            binding.directionsToReach.isChecked = !binding.directionsToReach.isChecked
            orderViewModel.setDeliveryInstruction(getString(R.string.directions_to_reach))
        }
        binding.avoidCalling.setOnClickListener {
            binding.avoidCalling.isChecked = !binding.avoidCalling.isChecked
            orderViewModel.setDeliveryInstruction(getString(R.string.avoid_calling))
        }
        binding.leaveWithSecurity.setOnClickListener {
            binding.leaveWithSecurity.isChecked = !binding.leaveWithSecurity.isChecked
            orderViewModel.setDeliveryInstruction(getString(R.string.leave_with_security))
        }

        binding.viewDetailBill.setOnClickListener {
            binding.nestedSv.scrollTo(0, binding.billDetailsTitle.bottom)
        }

    }

    private fun checkIfCartIsEmpty(orderList: MutableList<OrderItem>) {
        Log.i(LOG_DATA, "ItemCartFragment: LiveData OrderItemList $orderList and its size: ${orderList.size}")
        if (orderList.isEmpty()) {
            binding.nestedSv.visibility = View.GONE
            binding.noItemsInCartTv.visibility = View.VISIBLE
            binding.placeOrderCard.visibility = View.GONE
            binding.proceedToPayButton.visibility = View.GONE
            binding.viewDetailBill.visibility = View.GONE
            orderViewModel.setRestaurantName("")
        } else {
            binding.nestedSv.visibility = View.VISIBLE
            binding.noItemsInCartTv.visibility = View.GONE
            binding.placeOrderCard.visibility = View.VISIBLE
            binding.proceedToPayButton.visibility = View.VISIBLE
            binding.viewDetailBill.visibility = View.VISIBLE
            binding.restaurantName.text = orderViewModel.restaurantName.value
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}