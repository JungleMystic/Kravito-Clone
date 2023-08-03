package com.lrm.kravito.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lrm.kravito.constants.TAG
import com.lrm.kravito.data.OrderItem

class OrderViewModel: ViewModel() {

    private val _orderCartList = MutableLiveData<MutableList<OrderItem>>(mutableListOf())
    val orderCartList: LiveData<MutableList<OrderItem>> get() = _orderCartList

    private val _restaurantName = MutableLiveData<String>("")
    val restaurantName: LiveData<String> get() = _restaurantName

    fun setRestaurantName(name: String) {
        val oldRestaurantName = restaurantName.value!!
        Log.i(TAG, "setRestaurantName is called -> oldName: $oldRestaurantName and newName: $name")
        if (restaurantName.value == "" || name == oldRestaurantName || name == "") {
            _restaurantName.value = name
            Log.i(TAG, "setRestaurantName is called -> oldName: $oldRestaurantName and newName: $name")
        }
    }

    fun addToCart(orderItem: OrderItem, name: String, context: Context) {
        val oldRestaurantName = restaurantName.value!!
        if (oldRestaurantName == name) {
            _orderCartList.value?.add(orderItem)
            Log.i(TAG, "addToCart is called -> oldName: $oldRestaurantName and newName: $name")
        } else {
            Toast.makeText(context, "Item not added to cart...Wrong Restaurant", Toast.LENGTH_SHORT).show()
        }
    }

    fun increaseCartSize(position: Int) {
        Log.i(TAG, "increaseCartSize is called")
        val cartItem = _orderCartList.value!![position]
        cartItem.orderQuantity = cartItem.orderQuantity + 1
        Log.i(TAG, "increaseCartSize modified cartItem -> $cartItem")
        _orderCartList.value!![position] = cartItem
        _orderCartList.value = orderCartList.value
    }

    fun decreaseCartSize(position: Int) {
        Log.i(TAG, "decreaseCartSize is called")
        val cartItem = _orderCartList.value!![position]
        if (cartItem.orderQuantity > 1) {
            cartItem.orderQuantity = cartItem.orderQuantity - 1
            Log.i(TAG, "decreaseCartSize modified cartItem -> $cartItem")
            _orderCartList.value!![position] = cartItem
            _orderCartList.value = orderCartList.value
        } else {
            Log.i(TAG, "decreaseCartSize removed cartItem -> $cartItem")
            _orderCartList.value!!.removeAt(position)
            _orderCartList.value = orderCartList.value
        }
    }
}