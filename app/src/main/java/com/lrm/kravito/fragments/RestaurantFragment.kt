package com.lrm.kravito.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.lrm.kravito.R
import com.lrm.kravito.adapter.ParentMenuTypesAdapter
import com.lrm.kravito.adapter.ShortMenuAdapter
import com.lrm.kravito.constants.LOG_DATA
import com.lrm.kravito.data.Restaurant
import com.lrm.kravito.data.RestaurantData
import com.lrm.kravito.data.RestaurantMenuData
import com.lrm.kravito.databinding.FragmentRestaurantBinding
import com.lrm.kravito.viewModel.OrderViewModel

class RestaurantFragment : Fragment() {

    private var _binding: FragmentRestaurantBinding? = null
    private val binding get() = _binding!!

    private val navigationArgs: RestaurantFragmentArgs by navArgs()
    private val orderViewModel: OrderViewModel by activityViewModels()

    private val rotateClockWise: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_clock_wise)
    }
    private val rotateAntiClockWise: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_anti_clock_wise)
    }
    private val alphaUp: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.alpha_up)
    }
    private val alphaDown2: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.alpha_down2)
    }
    private val alphaDown3: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.alpha_down3)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRestaurantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val restaurantId = navigationArgs.restaurantId
        Log.i(LOG_DATA, "onViewCreated: restaurantId = $restaurantId")
        val restaurant = RestaurantData.restaurantList.single { it.id == restaurantId }
        Log.i(LOG_DATA, "onViewCreated: restaurantId = ${restaurant.name}")
        val menuId = restaurant.menuId
        binding.fragmentLabel.text = restaurant.name

        bind(restaurant)

        val restaurantMenu = RestaurantMenuData.menuList.single {it.menuId == menuId}
        val categoryList = restaurantMenu.categoryList

        binding.menuCategoryRv.apply {
            adapter = ParentMenuTypesAdapter(requireContext(), categoryList, orderViewModel, restaurant.name) {
                binding.viewCartCard.visibility = View.VISIBLE
            }
            ViewCompat.setNestedScrollingEnabled(binding.menuCategoryRv, false)
        }

        orderViewModel.orderCartList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.viewCartCard.visibility = View.VISIBLE
            }
        }

        binding.shortMenuRv.apply {
            adapter = ShortMenuAdapter(categoryList)
        }

        binding.addToFav.setOnClickListener {
            binding.addToFav.isSelected = !binding.addToFav.isSelected
        }

        binding.viewCartButton.setOnClickListener {
            val action = RestaurantFragmentDirections.actionRestaurantFragmentToItemCartFragment()
            this.findNavController().navigate(action)
        }

        var isMenuExpanded = false

        binding.menuFab.setOnClickListener {
            isMenuExpanded = !isMenuExpanded
           if (isMenuExpanded) {
               binding.nestedSv.startAnimation(alphaDown3)
               binding.shortMenuCl.visibility = View.VISIBLE
               binding.shortMenuCl.startAnimation(alphaUp)
               binding.menuFab.startAnimation(rotateClockWise)
               binding.menuFab.setImageResource(R.drawable.ic_close_icon)
           } else {
               binding.nestedSv.startAnimation(alphaUp)
               binding.shortMenuCl.startAnimation(alphaDown2)
               binding.shortMenuCl.visibility = View.GONE
               binding.menuFab.startAnimation(rotateAntiClockWise)
               binding.menuFab.setImageResource(R.drawable.ic_menu_icon)
           }
        }
    }

    private fun bind(restaurant: Restaurant) {
        binding.restaurantName.text = restaurant.name
        binding.restaurantRating.text = restaurant.rating
        binding.restaurantDistance.text = restaurant.distance
        binding.restaurantDesc.text = restaurant.desc
        binding.restaurantArea.text = restaurant.location
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}