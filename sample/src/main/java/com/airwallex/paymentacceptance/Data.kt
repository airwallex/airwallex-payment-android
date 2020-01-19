package com.airwallex.paymentacceptance

import android.content.Context
import com.airwallex.android.model.Product
import com.airwallex.android.model.Shipping
import com.airwallex.paymentacceptance.model.PaymentMethodType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Data {

    private const val SHARED_PREFERENCE_NAME = "sample"
    private const val SHIPPING_OBJECT = "shipping"

    var paymentMethodType: PaymentMethodType? = null

    var shipping: Shipping?
        get() {
            val sharedPref = SampleApplication.instance.getSharedPreferences(
                SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE
            )
            if (sharedPref.contains(SHIPPING_OBJECT)) {
                val shippingStr = sharedPref.getString(SHIPPING_OBJECT, null)
                if (shippingStr != null) {
                    return Gson().fromJson(shippingStr, Shipping::class.java)
                }
            }

            return Gson().fromJson(SampleApplication.instance.assets.open("shipping.json").bufferedReader().use {
                it.readText()
            }, Shipping::class.java)
        }
        set(value) {
            val sharedPref = SampleApplication.instance.getSharedPreferences(
                SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE
            )
            with(sharedPref.edit()) {
                putString(SHIPPING_OBJECT, Gson().toJson(value))
                apply()
            }
        }

    val products: ArrayList<Product>
        get() = Gson().fromJson(SampleApplication.instance.assets.open("products.json").bufferedReader().use {
            it.readText()
        }, object : TypeToken<ArrayList<Product>>() {}.type)
}