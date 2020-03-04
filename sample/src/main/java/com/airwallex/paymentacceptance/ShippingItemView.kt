package com.airwallex.paymentacceptance

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.airwallex.android.PaymentSession
import com.airwallex.android.PaymentSessionConfig
import com.airwallex.android.model.Shipping
import com.airwallex.android.view.PaymentShippingActivityStarter
import kotlinx.android.synthetic.main.shipping_item.view.*
import java.util.*

class ShippingItemView constructor(
    context: Context,
    attrs: AttributeSet
) : RelativeLayout(context, attrs) {

    private var shipping: Shipping? = null

    private var paymentSession: PaymentSession? = null

    init {
        View.inflate(getContext(), R.layout.shipping_item, this)

        rlBilling.setOnClickListener {
            shipping?.let {
                paymentSession = PaymentSession(
                    context as Activity,
                    PaymentSessionConfig.Builder().setShipping(shipping).build()
                )
                paymentSession?.presentShippingFlow()

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
        completion: (shipping: Shipping?) -> Unit
    ) {
        paymentSession?.handlePaymentShippingResult(
            requestCode,
            resultCode,
            data,
            paymentShippingCallback = object :
                PaymentSession.PaymentResult<PaymentShippingActivityStarter.Result> {
                override fun onCancelled() {
                    Log.d(TAG, "User cancel edit shipping...")
                    completion.invoke(null)
                }

                override fun onSuccess(result: PaymentShippingActivityStarter.Result) {
                    Log.d(TAG, "Save the shipping success")
                    renewalShipping(result.shipping)
                    completion(result.shipping)
                }
            })
    }

    companion object {
        private const val TAG = "ShippingItemView"
    }
}