package com.lrm.kravito.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lrm.kravito.data.FoodItem
import com.lrm.kravito.databinding.ShortMenuListItemBinding

class ShortMenuAdapter(private val categoryList: List<FoodItem>
): RecyclerView.Adapter<ShortMenuAdapter.MenuItemViewHolder>() {

    inner class MenuItemViewHolder(private val binding: ShortMenuListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val categoryTv = binding.categoryName
        val categoryCount = binding.categoryCount
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        return MenuItemViewHolder(
            ShortMenuListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        val category = categoryList[position]
        holder.categoryTv.text = category.itemCategory
        holder.categoryCount.text = category.itemList.size.toString()
    }
}