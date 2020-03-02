package com.airwallex.android

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.Shipping
import com.airwallex.android.view.*

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

    interface PaymentResult<T> {
        fun onCancelled()
        fun onSuccess(t: T?)
    }

    @Throws(NullPointerException::class)
    fun presentPaymentCheckoutFlow(customerSessionConfig: CustomerSessionConfig) {
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

    fun presentAddPaymentMethodFlow(customerSessionConfig: CustomerSessionConfig) {
        AddPaymentMethodActivityStarter(context)
            .startForResult(
                AddPaymentMethodActivityStarter.Args.Builder(customerSessionConfig)
                    .build()
            )
    }

    fun handlePaymentResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        callback: PaymentResult<PaymentMethod>
    ): Boolean {
        if (!VALID_REQUEST_CODES.contains(requestCode)) {
            return false
        }

        when (requestCode) {
            AddPaymentMethodActivityStarter.REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val result = AddPaymentMethodActivityStarter.Result.fromIntent(data)
                        callback.onSuccess(result?.paymentMethod)
                    }
                    Activity.RESULT_CANCELED -> callback.onCancelled()
                }
                return true
            }

            else -> return false
        }
    }

    fun handlePaymentShipping(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        callback: PaymentResult<Shipping>
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
        callback: PaymentResult<PaymentIntent>
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
            AddPaymentShippingActivityStarter.REQUEST_CODE,
            AddPaymentMethodActivityStarter.REQUEST_CODE
        )
    }
}