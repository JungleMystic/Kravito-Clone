package com.lrm.kravito.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.lrm.kravito.adapter.ParentMenuTypesAdapter
import com.lrm.kravito.constants.LOG_DATA
import com.lrm.kravito.data.Restaurant
import com.lrm.kravito.data.RestaurantData
import com.lrm.kravito.data.RestaurantMenuData
import com.lrm.kravito.databinding.FragmentRestaurantBinding

class RestaurantFragment : Fragment() {

    private var _binding: FragmentRestaurantBinding? = null
    private val binding get() = _binding!!

    private val navigationArgs: RestaurantFragmentArgs by navArgs()

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
            adapter = ParentMenuTypesAdapter(requireContext(), categoryList)
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