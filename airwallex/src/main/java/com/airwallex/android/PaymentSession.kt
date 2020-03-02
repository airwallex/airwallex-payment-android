package com.airwallex.android

import android.app.Activity
import android.content.Intent
import com.airwallex.android.view.AddPaymentMethodActivityStarter
import com.airwallex.android.view.PaymentMethodsActivityStarter
import com.airwallex.android.view.PaymentShippingActivityStarter

class PaymentSession constructor(
    private val context: Activity,
    private val paymentSessionConfig: PaymentSessionConfig? = null
) {

    interface PaymentResult<T> {
        fun onCancelled()
        fun onSuccess(result: T?)
    }

    @Throws(NullPointerException::class)
    fun presentPaymentCheckoutFlow(customerSessionConfig: CustomerSessionConfig) {
        checkNotNull(
            customerSessionConfig.paymentIntent.customerId,
            { "Customer id should not be null" })

        PaymentMethodsActivityStarter(context)
            .startForResult(
                PaymentMethodsActivityStarter.Args
                    .Builder(customerSessionConfig)
                    .build()
            )
    }

    fun presentShippingFlow() {
        PaymentShippingActivityStarter(context)
            .startForResult(
                PaymentShippingActivityStarter.Args.Builder()
                    .setShipping(paymentSessionConfig?.shipping)
                    .build()
            )
    }

    @Throws(NullPointerException::class)
    fun presentAddPaymentMethodFlow(customerSessionConfig: CustomerSessionConfig) {
        checkNotNull(
            customerSessionConfig.paymentIntent.customerId,
            { "Customer id should not be null" })
        AddPaymentMethodActivityStarter(context)
            .startForResult(
                AddPaymentMethodActivityStarter.Args.Builder(customerSessionConfig)
                    .build()
            )
    }

    fun handlePaymentMethodResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        paymentMethodCallback: PaymentResult<AddPaymentMethodActivityStarter.Result>
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
        paymentShippingCallback: PaymentResult<PaymentShippingActivityStarter.Result>
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
        paymentIntentCallback: PaymentResult<PaymentMethodsActivityStarter.Result>
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
        paymentMethodCallback: PaymentResult<AddPaymentMethodActivityStarter.Result>? = null,
        paymentShippingCallback: PaymentResult<PaymentShippingActivityStarter.Result>? = null,
        paymentIntentCallback: PaymentResult<PaymentMethodsActivityStarter.Result>? = null
    ): Boolean {
        if (!VALID_REQUEST_CODES.contains(requestCode)) {
            return false
        }

        when (resultCode) {
            Activity.RESULT_OK -> {
                return when (requestCode) {
                    AddPaymentMethodActivityStarter.REQUEST_CODE -> {
                        paymentMethodCallback?.onSuccess(
                            AddPaymentMethodActivityStarter.Result.fromIntent(
                                data
                            )
                        )
                        true
                    }
                    PaymentShippingActivityStarter.REQUEST_CODE -> {
                        paymentShippingCallback?.onSuccess(
                            PaymentShippingActivityStarter.Result.fromIntent(
                                data
                            )
                        )
                        true
                    }
                    PaymentMethodsActivityStarter.REQUEST_CODE -> {
                        paymentIntentCallback?.onSuccess(
                            PaymentMethodsActivityStarter.Result.fromIntent(
                                data
                            )
                        )
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