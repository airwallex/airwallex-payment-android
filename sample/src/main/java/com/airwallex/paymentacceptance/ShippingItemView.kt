package com.airwallex.paymentacceptance

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.airwallex.android.core.extension.setOnSingleClickListener
import com.airwallex.android.core.model.Shipping
import com.airwallex.paymentacceptance.databinding.ShippingItemBinding
import java.util.*

class ShippingItemView constructor(
    context: Context,
    attrs: AttributeSet
) : RelativeLayout(context, attrs) {

    private val viewBinding = ShippingItemBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    var onClickAction: (() -> Unit)? = null

    init {
        viewBinding.rlBilling.setOnSingleClickListener {
            onClickAction?.invoke()
        }
    }

    fun renewalShipping(shipping: Shipping?) {
        if (shipping == null) {
            viewBinding.tvShippingAddress.text =
                context.getString(R.string.enter_shipping)
            viewBinding.tvShippingAddress.setTextColor(
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

        viewBinding.tvShippingAddress.text = String.format(
            "%s %s\n%s\n%s, %s, %s",
            shipping.lastName,
            shipping.firstName,
            shipping.address?.street,
            shipping.address?.city,
            shipping.address?.state,
            countryName
        )

        viewBinding.tvShippingAddress.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.airwallex_color_dark_deep
            )
        )
    }
}
