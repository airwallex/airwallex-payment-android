package com.airwallex.paymentacceptance

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.airwallex.android.AirwallexStarter
import com.airwallex.android.model.Address
import com.airwallex.android.model.Shipping
import java.util.*
import kotlinx.android.synthetic.main.shipping_item.view.*

class ShippingItemView constructor(
    context: Context,
    attrs: AttributeSet
) : RelativeLayout(context, attrs) {

    var shipping: Shipping = Shipping.Builder()
        .setFirstName("John")
        .setLastName("Doe")
        .setPhone("13800000000")
        .setAddress(
            Address.Builder()
                .setCountryCode("CN")
                .setState("Shanghai")
                .setCity("Shanghai")
                .setStreet("Pudong District")
                .setPostcode("100000")
                .build()
        )
        .build()

    private val airwallexStarter: AirwallexStarter by lazy {
        AirwallexStarter(context as Activity)
    }

    init {
        View.inflate(getContext(), R.layout.shipping_item, this)

        rlBilling.setOnClickListener {
            airwallexStarter.presentShippingFlow(shipping)
        }
        renewalShipping(shipping)
    }

    fun renewalShipping(shipping: Shipping) {
        this.shipping = shipping
        val countryName = shipping.address?.countryCode?.let {
            val loc = Locale("", it)
            loc.displayCountry
        }

        tvShippingAddress.text = String.format(
            "%s %s\n%s\n%s, %s, %s",
            shipping.lastName,
            shipping.firstName,
            shipping.address?.street,
            shipping.address?.city,
            shipping.address?.state,
            countryName
        )

        tvShippingAddress.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.airwallex_color_dark_deep
            )
        )
    }

    fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        airwallexStarter.handlePaymentShippingResult(
            requestCode,
            resultCode,
            data,
            object :
                AirwallexStarter.PaymentShippingResult {
                override fun onSuccess(shipping: Shipping) {
                    Log.d(TAG, "Save the shipping success")
                    renewalShipping(shipping)
                }

                override fun onCancelled() {
                    Log.d(TAG, "User cancel edit shipping...")
                }
            })
    }

    companion object {
        private const val TAG = "ShippingItemView"
    }
}
