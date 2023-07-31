package com.lrm.kravito.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lrm.kravito.data.Restaurant
import com.lrm.kravito.databinding.RestaurantListItemBinding

class RestaurantListAdapter(
    private val restaurantList: List<Restaurant>,
    private val onItemClicked: (Restaurant) -> Unit
) : RecyclerView.Adapter<RestaurantListAdapter.RestaurantViewHolder>(){

    class RestaurantViewHolder(private val binding: RestaurantListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
            fun bind(restaurant: Restaurant){
                binding.restaurantImage.setImageResource(restaurant.image)
                binding.restaurantName.text = restaurant.name
                binding.restaurantRating.text = restaurant.rating
                binding.restaurantDistance.text = restaurant.distance
                binding.restaurantDesc.text = restaurant.desc
                binding.restaurantArea.text = restaurant.location
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        return RestaurantViewHolder(
            RestaurantListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant = restaurantList[position]
        holder.bind(restaurant)
        holder.itemView.setOnClickListener { onItemClicked(restaurant) }
    }
}