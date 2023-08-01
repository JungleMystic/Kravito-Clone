package com.lrm.kravito.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lrm.kravito.constants.LOG_DATA
import com.lrm.kravito.data.Item
import com.lrm.kravito.data.Order

class OrderViewModel: ViewModel() {

    private val _orderCart = MutableLiveData<MutableList<Order>>()
    val orderCart: LiveData<MutableList<Order>> get() = _orderCart

    private val _item = MutableLiveData<Item>()
    val item: LiveData<Item> get() = _item

    private val _itemQuantity = MutableLiveData<Int>(0)
    val itemQuantity: LiveData<Int> = _itemQuantity

    fun setItem(item: Item) {
        _item.value = item
        Log.i(LOG_DATA, "OrderViewModel: setItemName is called -> $item ")
        increaseQuantity()
        addToOrderList(Order(item, 1))
    }

    fun increaseQuantity() {
        _itemQuantity.value = _itemQuantity.value?.inc()
        Log.i(LOG_DATA, "OrderViewModel: increaseQuantity is called -> ${itemQuantity.value} ")
    }

    fun decreaseQuantity() {
        if (_itemQuantity.value!! > 0) {
            _itemQuantity.value = _itemQuantity.value?.dec()
            Log.i(LOG_DATA, "OrderViewModel: decreaseQuantity is called -> ${itemQuantity.value} ")
        }
    }

    private fun addToOrderList(order: Order) {
        _orderCart.value?.add(order)
    }
}