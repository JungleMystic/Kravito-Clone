package com.lrm.kravito.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lrm.kravito.R
import com.lrm.kravito.data.OrderItem
import com.lrm.kravito.databinding.ItemCartListItemBinding
import com.lrm.kravito.viewModel.OrderViewModel

class ItemCartAdapter(
    val viewModel: OrderViewModel
): ListAdapter<OrderItem, ItemCartAdapter.ItemViewHolder>(DiffCallback) {

    inner class ItemViewHolder(private val binding: ItemCartListItemBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(orderItem: OrderItem, position: Int){
            binding.itemName.text = orderItem.item.name
            binding.itemPrice.text = orderItem.item.quotedPrice
            if (orderItem.item.type == 0) {
                binding.vNvIcon.setImageResource(R.drawable.veg_icon)
            } else if (orderItem.item.type == 1) {
                binding.vNvIcon.setImageResource(R.drawable.non_veg_icon)
            }

            binding.itemQuantity.text = orderItem.orderQuantity.toString()
            binding.itemQty.text = orderItem.orderQuantity.toString()
            binding.itemTotalPrice.text = (orderItem.item.quotedPrice.toInt() * orderItem.orderQuantity).toString()

            binding.qtyIncrease.setOnClickListener {
                viewModel.increaseCartSize(position)
                notifyDataSetChanged()
            }

            binding.qtyDecrease.setOnClickListener {
                viewModel.decreaseCartSize(position)
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemCartListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val orderItem = getItem(position)
        holder.bind(orderItem, position)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<OrderItem>() {
        override fun areItemsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
            return oldItem.item == newItem.item
        }

    }
}