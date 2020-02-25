package com.airwallex.paymentacceptance.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.airwallex.android.model.Shipping
import com.airwallex.android.view.AddPaymentShippingActivityStarter
import com.airwallex.paymentacceptance.R
import kotlinx.android.synthetic.main.shipping_item.view.*
import java.util.*

class ShippingItemView constructor(
    context: Context,
    attrs: AttributeSet
) : RelativeLayout(context, attrs) {

    private var shipping: Shipping? = null

    init {
        View.inflate(getContext(), R.layout.shipping_item, this)

        rlBilling.setOnClickListener {
            shipping?.let {
                AddPaymentShippingActivityStarter(context as Activity)
                    .startForResult(
                        AddPaymentShippingActivityStarter.Args.Builder()
                            .setShipping(it)
                            .build()
                    )
            }
        }
    }

    fun renewalShipping(shipping: Shipping?) {
        this.shipping = shipping
        if (shipping == null) {
            tvShippingAddress.text =
                context.getString(R.string.enter_shipping)
            tvShippingAddress.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.airwallex_color_dark_light
                )
            )
            return
        }

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
        data: Intent?,
        completion: (shipping: Shipping) -> Unit
    ) {
        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }

        when (requestCode) {
            AddPaymentShippingActivityStarter.REQUEST_CODE -> {
                val result = AddPaymentShippingActivityStarter.Result.fromIntent(data)

                result?.let {
                    renewalShipping(result.shipping)
                    completion(result.shipping)
                }
            }
        }
    }
}