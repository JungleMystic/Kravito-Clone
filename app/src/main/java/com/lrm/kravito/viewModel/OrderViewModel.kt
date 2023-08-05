package com.lrm.kravito.viewModel

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lrm.kravito.R
import com.lrm.kravito.constants.TAG
import com.lrm.kravito.data.OrderItem

class OrderViewModel: ViewModel() {

    private val _orderCartList = MutableLiveData<MutableList<OrderItem>>(mutableListOf())
    val orderCartList: LiveData<MutableList<OrderItem>> get() = _orderCartList

    private val _restaurantName = MutableLiveData<String>("")
    val restaurantName: LiveData<String> get() = _restaurantName

    private val _totalCartValue = MutableLiveData<Int>()
    val totalCartValue: LiveData<Int> get() = _totalCartValue

    private val _tax = MutableLiveData<Int>()
    val tax: LiveData<Int> get() = _tax

    private val _deliveryFee = MutableLiveData<Int>(35)
    val deliveryFee: LiveData<Int> get() = _deliveryFee

    private val _deliveryTip = MutableLiveData<Int>(0)
    val deliveryTip: LiveData<Int> get() = _deliveryTip

    private val _totalWithTax = MutableLiveData<Int>()
    val totalWithTax: LiveData<Int> get() = _totalWithTax

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
            val dialog = Dialog(context)
            dialog.apply {
                setContentView(R.layout.custom_replace_cart_dialog)
                setCancelable(true)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val messageText = context.getString(R.string.replace_cart_message, oldRestaurantName, name)
                findViewById<TextView>(R.id.dialog_message).text = HtmlCompat.fromHtml(messageText, HtmlCompat.FROM_HTML_MODE_LEGACY)
                findViewById<TextView>(R.id.yes_tv).setOnClickListener {
                    _orderCartList.value?.clear()
                    setRestaurantName("")
                    setRestaurantName(name)
                    addToCart(orderItem, name, context)
                    dismiss()
                }
                findViewById<TextView>(R.id.no_tv).setOnClickListener { dismiss() }
                show()
            }
        }
        calculateTotalAndTax()
    }

    fun isSameRestaurant(name: String): Boolean {
        val oldRestaurantName = restaurantName.value!!
        return oldRestaurantName == name
    }

    fun increaseCartSize(position: Int) {
        Log.i(TAG, "increaseCartSize is called")
        val cartItem = _orderCartList.value!![position]
        cartItem.orderQuantity = cartItem.orderQuantity + 1
        Log.i(TAG, "increaseCartSize modified cartItem -> $cartItem")
        _orderCartList.value!![position] = cartItem
        _orderCartList.value = orderCartList.value
        calculateTotalAndTax()
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
        calculateTotalAndTax()
    }

    private fun calculateTotalAndTax() {
        var total = 0
        for (item in orderCartList.value!!) {
            total += (item.item.quotedPrice.toInt() * item.orderQuantity)
        }
        _totalCartValue.value = total
        val tax = (total * 0.12).toInt()
        _tax.value = tax
        _totalWithTax.value = total + tax + deliveryFee.value!! + deliveryTip.value!!
    }
}