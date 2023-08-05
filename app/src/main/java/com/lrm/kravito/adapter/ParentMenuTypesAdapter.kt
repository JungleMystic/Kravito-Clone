package com.lrm.kravito.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.lrm.kravito.R
import com.lrm.kravito.data.FoodItem
import com.lrm.kravito.databinding.MenuCategoryListItemBinding
import com.lrm.kravito.viewModel.MenuViewModel
import com.lrm.kravito.viewModel.OrderViewModel

class ParentMenuTypesAdapter(
    val context: Context,
    private val categoryList: List<FoodItem>,
    private val viewModel: OrderViewModel,
    private val menuViewModel: MenuViewModel,
    private val restaurantName: String,
    private val onItemClicked: () -> Unit
) : RecyclerView.Adapter<ParentMenuTypesAdapter.CategoryViewHolder>() {

    val itemContext = context

    inner class CategoryViewHolder(
        private val binding: MenuCategoryListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val rotateClockWise: Animation by lazy {
            AnimationUtils.loadAnimation(itemContext, R.anim.rotate_clock_wise)
        }
        private val rotateAntiClockWise: Animation by lazy {
            AnimationUtils.loadAnimation(itemContext, R.anim.rotate_anti_clock_wise)
        }

        private val dropDownMenuDown: Animation by lazy {
            AnimationUtils.loadAnimation(itemContext, R.anim.drop_down_menu_down2)
        }

        private val dropDownMenuUp: Animation by lazy {
            AnimationUtils.loadAnimation(itemContext, R.anim.drop_down_menu_up2)
        }

        fun bind(menuCategory: FoodItem) {
            binding.categoryName.text = menuCategory.itemCategory

            menuViewModel.getItemsList(menuCategory)

            binding.itemsRv.apply {
                adapter = ChildFoodItemsListAdapter(menuViewModel.itemsList.value!!.toList(), viewModel, restaurantName, context) {
                    onItemClicked()
                }
                ViewCompat.setNestedScrollingEnabled(binding.itemsRv, false)
            }
            //Log.i(LOG_DATA, "ParentMenuTypesAdapter: Food Items List-> ${menuCategory.itemList} ")

            var isExpanded = false
            binding.categoryCard.setOnClickListener {
                isExpanded = !isExpanded
                if (isExpanded) {
                    binding.itemsRv.visibility = View.GONE
                    binding.expandMenu.startAnimation(rotateClockWise)
                    binding.itemsRv.startAnimation(dropDownMenuUp)
                } else {
                    binding.itemsRv.visibility = View.VISIBLE
                    binding.expandMenu.startAnimation(rotateAntiClockWise)
                    binding.itemsRv.startAnimation(dropDownMenuDown)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            MenuCategoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.bind(category)
    }
}