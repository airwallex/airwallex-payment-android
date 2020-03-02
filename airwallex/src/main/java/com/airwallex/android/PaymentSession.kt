package com.airwallex.android

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.Shipping
import com.airwallex.android.view.ActivityStarter
import com.airwallex.android.view.AddPaymentShippingActivityStarter
import com.airwallex.android.view.PaymentMethodsActivity
import com.airwallex.android.view.PaymentMethodsActivityStarter

class PaymentSession internal constructor(
    private val context: Activity,
    private val paymentMethodsActivityStarter:
    ActivityStarter<PaymentMethodsActivity, PaymentMethodsActivityStarter.Args>,
    private val paymentSessionConfig: PaymentSessionConfig?
) {

    constructor(activity: Activity, paymentSessionConfig: PaymentSessionConfig? = null) : this(
        activity,
        PaymentMethodsActivityStarter(activity),
        paymentSessionConfig
    )

    constructor(fragment: Fragment, paymentSessionConfig: PaymentSessionConfig? = null) : this(
        fragment.requireActivity(),
        PaymentMethodsActivityStarter(fragment.requireActivity()),
        paymentSessionConfig
    )

    interface PaymentCheckoutResult {
        fun onCancelled()
        fun onSuccess(paymentIntent: PaymentIntent?)
    }

    interface PaymentShippingResult {
        fun onCancelled()
        fun onSuccess(shipping: Shipping?)
    }

    @Throws(NullPointerException::class)
    fun presentPaymentFlow(customerSessionConfig: CustomerSessionConfig) {
        checkNotNull(
            customerSessionConfig.paymentIntent.customerId,
            { "Customer id should not be null" })

        paymentMethodsActivityStarter.startForResult(
            PaymentMethodsActivityStarter.Args
                .Builder(customerSessionConfig)
                .build()
        )
    }

    fun presentShippingFlow() {
        AddPaymentShippingActivityStarter(context)
            .startForResult(
                AddPaymentShippingActivityStarter.Args.Builder()
                    .setShipping(paymentSessionConfig?.shipping)
                    .build()
            )
    }

    fun handlePaymentShipping(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        callback: PaymentShippingResult
    ): Boolean {
        if (!VALID_REQUEST_CODES.contains(requestCode)) {
            return false
        }

        when (requestCode) {
            AddPaymentShippingActivityStarter.REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val result = AddPaymentShippingActivityStarter.Result.fromIntent(data)
                        callback.onSuccess(result?.shipping)
                    }
                    Activity.RESULT_CANCELED -> callback.onCancelled()
                }
                return true
            }

            else -> return false
        }
    }

    fun handlePaymentCheckoutResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        callback: PaymentCheckoutResult
    ): Boolean {
        if (!VALID_REQUEST_CODES.contains(requestCode)) {
            return false
        }

        when (requestCode) {
            PaymentMethodsActivityStarter.REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val result = PaymentMethodsActivityStarter.Result.fromIntent(data)
                        callback.onSuccess(result?.paymentIntent)
                    }
                    Activity.RESULT_CANCELED -> callback.onCancelled()
                }
                return true
            }
            else -> return false
        }
    }

    internal companion object {

        private val VALID_REQUEST_CODES = setOf(
            PaymentMethodsActivityStarter.REQUEST_CODE,
            AddPaymentShippingActivityStarter.REQUEST_CODE
        )
    }
}