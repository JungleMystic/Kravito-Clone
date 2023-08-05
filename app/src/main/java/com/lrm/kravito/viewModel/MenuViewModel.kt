package com.lrm.kravito.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lrm.kravito.data.FoodItem
import com.lrm.kravito.data.Item

class MenuViewModel: ViewModel() {

    private val _menuCategoryList = MutableLiveData<List<FoodItem>>()
    val menuCategoryList: LiveData<List<FoodItem>> get() = _menuCategoryList

    private val _itemsList = MutableLiveData<List<Item>>()
    val itemsList: LiveData<List<Item>> get() = _itemsList

    private val _vegFilter = MutableLiveData<Boolean>(false)
    val vegFilter: LiveData<Boolean> get() = _vegFilter

    private val _nonVegFilter = MutableLiveData<Boolean>(false)
    val nonVegFilter: LiveData<Boolean> get() = _nonVegFilter

    fun setMenuCategoryList(menuCategory: List<FoodItem>){
        _menuCategoryList.value = menuCategory
    }

    fun setVegFilter(value: Boolean) {
        _vegFilter.value = value
    }

    fun setNonVegFilter(value: Boolean) {
        _nonVegFilter.value = value
    }

    fun getCategoryList(): List<FoodItem> {
        return _menuCategoryList.value!!.toList()
    }

    fun getItemsList(foodItem: FoodItem) {
        if (_vegFilter.value == true) {
            _itemsList.value = foodItem.itemList.filter { it.type == 0 }
        } else if (_nonVegFilter.value == true) {
            _itemsList.value = foodItem.itemList.filter { it.type == 1 }
        } else {
            _itemsList.value = foodItem.itemList
        }
    }
}