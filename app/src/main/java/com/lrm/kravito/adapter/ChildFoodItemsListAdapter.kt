package com.lrm.kravito.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lrm.kravito.R
import com.lrm.kravito.constants.LOG_DATA
import com.lrm.kravito.data.Item
import com.lrm.kravito.data.OrderItem
import com.lrm.kravito.databinding.FoodItemListItemBinding
import com.lrm.kravito.viewModel.OrderViewModel

class ChildFoodItemsListAdapter(
    private val itemsList: List<Item>,
    private val viewModel: OrderViewModel,
    private val restaurantName: String,
    val context: Context,
    private val onItemClicked: () -> Unit
): RecyclerView.Adapter<ChildFoodItemsListAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(
        private val binding: FoodItemListItemBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(foodItem: Item) {
            //Log.i(LOG_DATA, "ChildFoodItemsListAdapter: Food Items List -> $itemsList")
            binding.itemName.text = foodItem.name
            binding.itemPrice.text = foodItem.quotedPrice
            if (foodItem.type == 0) {
                binding.vNvIcon.setImageResource(R.drawable.veg_icon)
            } else if (foodItem.type == 1) {
                binding.vNvIcon.setImageResource(R.drawable.non_veg_icon)
            }

            binding.addItemButton.setOnClickListener{
                binding.addItemButton.visibility = View.GONE
                binding.addedItemButton.visibility = View.VISIBLE
                viewModel.setRestaurantName(restaurantName)
                viewModel.addToCart(OrderItem(foodItem, 1), restaurantName, context)
                Log.i(LOG_DATA, "ChildFoodItemAdapter: setting order item ${OrderItem(foodItem, 1)} ")
                onItemClicked()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            FoodItemListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemsList[position]
        holder.bind(item)
    }
}