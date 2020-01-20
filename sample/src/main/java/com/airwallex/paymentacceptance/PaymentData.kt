package com.airwallex.paymentacceptance

import android.content.Context
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.PaymentMethodType
import com.airwallex.android.model.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object PaymentData {

    private const val SHARED_PREFERENCE_NAME = "sample"
    private const val SHIPPING_OBJECT = "shipping"

    var paymentMethodType: PaymentMethodType? = PaymentMethodType.WECHAT

    var shipping: PaymentMethod.Billing?
        get() {
            val sharedPref = SampleApplication.instance.getSharedPreferences(
                SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE
            )
            if (sharedPref.contains(SHIPPING_OBJECT)) {
                val shippingStr = sharedPref.getString(SHIPPING_OBJECT, null)
                if (shippingStr != null) {
                    return Gson().fromJson(shippingStr, PaymentMethod.Billing::class.java)
                }
            }

            return Gson().fromJson(SampleApplication.instance.assets.open("shipping.json").bufferedReader().use {
                it.readText()
            }, PaymentMethod.Billing::class.java)
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