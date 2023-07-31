package com.lrm.kravito.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lrm.kravito.R
import com.lrm.kravito.constants.LOG_DATA
import com.lrm.kravito.data.Item
import com.lrm.kravito.databinding.FoodItemListItemBinding

class ChildFoodItemsListAdapter(
    private val itemsList: List<Item>
): RecyclerView.Adapter<ChildFoodItemsListAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(
        private val binding: FoodItemListItemBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(foodItem: Item) {
            Log.i(LOG_DATA, "ChildFoodItemsListAdpater: Food Items List -> $itemsList")

            binding.itemName.text = foodItem.name
            binding.itemPrice.text = foodItem.quotedPrice
            if (foodItem.type == 0) {
                binding.vNvIcon.setImageResource(R.drawable.veg_icon)
            } else if (foodItem.type == 1) {
                binding.vNvIcon.setImageResource(R.drawable.non_veg_icon)
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