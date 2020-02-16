package com.airwallex.paymentacceptance.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.airwallex.android.model.Shipping
import com.airwallex.paymentacceptance.PaymentBaseActivity
import com.airwallex.paymentacceptance.PaymentEditShippingActivity
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
                PaymentEditShippingActivity.startActivityForResult(
                    context as Activity,
                    it,
                    PaymentBaseActivity.REQUEST_EDIT_SHIPPING_CODE
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
            PaymentBaseActivity.REQUEST_EDIT_SHIPPING_CODE -> {
                val shipping =
                    data.getParcelableExtra<Parcelable>(PaymentBaseActivity.PAYMENT_SHIPPING) as Shipping
                renewalShipping(shipping)
                completion(shipping)
            }
        }
    }
}