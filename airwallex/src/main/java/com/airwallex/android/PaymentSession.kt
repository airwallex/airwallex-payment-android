package com.airwallex.android

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.Shipping
import com.airwallex.android.view.*

class PaymentSession internal constructor(
    private val paymentMethodsActivityStarter: ActivityStarter<PaymentMethodsActivity, PaymentMethodsActivityStarter.Args>,
    private val paymentShippingActivityStarter: ActivityStarter<PaymentShippingActivity, PaymentShippingActivityStarter.Args>,
    private val addPaymentMethodActivityStarter: ActivityStarter<AddPaymentMethodActivity, AddPaymentMethodActivityStarter.Args>,
    private val paymentSessionConfig: PaymentSessionConfig?
) {

    constructor(activity: Activity, paymentSessionConfig: PaymentSessionConfig? = null) : this(
        PaymentMethodsActivityStarter(activity),
        PaymentShippingActivityStarter(activity),
        AddPaymentMethodActivityStarter(activity),
        paymentSessionConfig
    )

    constructor(fragment: Fragment, paymentSessionConfig: PaymentSessionConfig? = null) : this(
        PaymentMethodsActivityStarter(fragment.requireActivity()),
        PaymentShippingActivityStarter(fragment.requireActivity()),
        AddPaymentMethodActivityStarter(fragment.requireActivity()),
        paymentSessionConfig
    )

    interface PaymentResult<T> {
        fun onCancelled()
        fun onSuccess(model: T?)
    }

    @Throws(NullPointerException::class)
    fun presentPaymentCheckoutFlow(customerSessionConfig: CustomerSessionConfig) {
        checkNotNull(
            customerSessionConfig.paymentIntent.customerId,
            { "Customer id should not be null" })

        paymentMethodsActivityStarter
            .startForResult(
                PaymentMethodsActivityStarter.Args
                    .Builder(customerSessionConfig)
                    .build()
            )
    }

    fun presentShippingFlow() {
        paymentShippingActivityStarter
            .startForResult(
                PaymentShippingActivityStarter.Args.Builder()
                    .setShipping(paymentSessionConfig?.shipping)
                    .build()
            )
    }

    fun presentAddPaymentMethodFlow(customerSessionConfig: CustomerSessionConfig) {
        addPaymentMethodActivityStarter
            .startForResult(
                AddPaymentMethodActivityStarter.Args.Builder(customerSessionConfig)
                    .build()
            )
    }

    fun handlePaymentMethodResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        paymentMethodCallback: PaymentResult<PaymentMethod>
    ) {
        handlePaymentResult(
            requestCode,
            resultCode,
            data,
            paymentMethodCallback = paymentMethodCallback
        )
    }

    fun handlePaymentShippingResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        paymentShippingCallback: PaymentResult<Shipping>
    ) {
        handlePaymentResult(
            requestCode,
            resultCode,
            data,
            paymentShippingCallback = paymentShippingCallback
        )
    }

    fun handlePaymentIntentResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        paymentIntentCallback: PaymentResult<PaymentIntent>
    ) {
        handlePaymentResult(
            requestCode,
            resultCode,
            data,
            paymentIntentCallback = paymentIntentCallback
        )
    }

    private fun handlePaymentResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        paymentMethodCallback: PaymentResult<PaymentMethod>? = null,
        paymentShippingCallback: PaymentResult<Shipping>? = null,
        paymentIntentCallback: PaymentResult<PaymentIntent>? = null
    ): Boolean {
        if (!VALID_REQUEST_CODES.contains(requestCode)) {
            return false
        }

        when (resultCode) {
            Activity.RESULT_OK -> {
                return when (requestCode) {
                    AddPaymentMethodActivityStarter.REQUEST_CODE -> {
                        val result = AddPaymentMethodActivityStarter.Result.fromIntent(data)
                        paymentMethodCallback?.onSuccess(result?.paymentMethod)
                        true
                    }
                    PaymentShippingActivityStarter.REQUEST_CODE -> {
                        val result = PaymentShippingActivityStarter.Result.fromIntent(data)
                        paymentShippingCallback?.onSuccess(result?.shipping)
                        true
                    }
                    PaymentMethodsActivityStarter.REQUEST_CODE -> {
                        val result = PaymentMethodsActivityStarter.Result.fromIntent(data)
                        paymentIntentCallback?.onSuccess(result?.paymentIntent)
                        true
                    }
                    else -> false
                }
            }
            Activity.RESULT_CANCELED -> {
                return when (requestCode) {
                    AddPaymentMethodActivityStarter.REQUEST_CODE -> {
                        paymentMethodCallback?.onCancelled()
                        true
                    }
                    PaymentShippingActivityStarter.REQUEST_CODE -> {
                        paymentShippingCallback?.onCancelled()
                        true
                    }
                    PaymentMethodsActivityStarter.REQUEST_CODE -> {
                        paymentIntentCallback?.onCancelled()
                        true
                    }
                    else -> false
                }
            }
            else -> return false
        }
    }

    internal companion object {

        private val VALID_REQUEST_CODES = setOf(
            PaymentMethodsActivityStarter.REQUEST_CODE,
            PaymentShippingActivityStarter.REQUEST_CODE,
            AddPaymentMethodActivityStarter.REQUEST_CODE
        )
    }
}