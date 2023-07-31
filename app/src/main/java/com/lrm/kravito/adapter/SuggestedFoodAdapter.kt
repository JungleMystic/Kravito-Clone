package com.lrm.kravito.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lrm.kravito.data.SuggestedFood
import com.lrm.kravito.databinding.FoodIconItemViewBinding

class SuggestedFoodAdapter(private val suggestList: List<SuggestedFood>)
    : RecyclerView.Adapter<SuggestedFoodAdapter.SFoodViewHolder>(){

        class SFoodViewHolder(private val binding: FoodIconItemViewBinding)
            : RecyclerView.ViewHolder(binding.root) {
            fun bind(item: SuggestedFood) {
                binding.foodItemImage.setImageResource(item.foodImage)
                binding.foodItemName.text = item.foodName
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SFoodViewHolder {
        return SFoodViewHolder(
            FoodIconItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return suggestList.size
    }

    override fun onBindViewHolder(holder: SFoodViewHolder, position: Int) {
        val item = suggestList[position]
        holder.bind(item)
    }
}