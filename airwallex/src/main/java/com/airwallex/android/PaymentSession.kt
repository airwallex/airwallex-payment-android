package com.airwallex.android

import android.app.Activity
import android.content.Intent
import com.airwallex.android.model.*
import com.airwallex.android.view.AddPaymentMethodActivityStarter
import com.airwallex.android.view.PaymentCheckoutActivityStarter
import com.airwallex.android.view.PaymentMethodsActivityStarter
import com.airwallex.android.view.PaymentShippingActivityStarter

class PaymentSession constructor(
    private val context: Activity,
    private val paymentSessionConfig: PaymentSessionConfig
) {

    interface PaymentResult {
        fun onCancelled()
    }

    interface PaymentShippingResult : PaymentResult {
        fun onSuccess(shipping: Shipping)
    }

    interface PaymentIntentResult : PaymentResult {
        fun onSuccess(paymentIntent: PaymentIntent, paymentMethodType: PaymentMethodType)
        fun onFailed(error: AirwallexError)
    }

    interface PaymentMethodResult : PaymentResult {
        fun onSuccess(paymentMethod: PaymentMethod)
    }

    @Throws(NullPointerException::class)
    fun presentPaymentFlow() {
        val paymentIntent = requireNotNull(paymentSessionConfig.paymentIntent)
        val token = requireNotNull(paymentSessionConfig.token)
        PaymentMethodsActivityStarter(context)
            .startForResult(
                PaymentMethodsActivityStarter.Args.Builder()
                    .setPaymentIntent(paymentIntent)
                    .setToken(token)
                    .build()
            )
    }

    fun presentShippingFlow() {
        PaymentShippingActivityStarter(context)
            .startForResult(
                PaymentShippingActivityStarter.Args.Builder()
                    .setShipping(paymentSessionConfig.shipping)
                    .build()
            )
    }

    @Throws(NullPointerException::class)
    fun presentAddPaymentMethodFlow() {
        val paymentIntent = requireNotNull(paymentSessionConfig.paymentIntent)
        val token = requireNotNull(paymentSessionConfig.token)
        AddPaymentMethodActivityStarter(context)
            .startForResult(
                AddPaymentMethodActivityStarter.Args.Builder()
                    .setPaymentIntent(paymentIntent)
                    .setToken(token)
                    .build()
            )
    }

    fun presentPaymentCheckoutFlow() {
        val paymentIntent = requireNotNull(paymentSessionConfig.paymentIntent)
        val token = requireNotNull(paymentSessionConfig.token)
        val paymentMethod = requireNotNull(paymentSessionConfig.paymentMethod)
        PaymentCheckoutActivityStarter(context)
            .startForResult(
                PaymentCheckoutActivityStarter.Args.Builder()
                    .setPaymentIntent(paymentIntent)
                    .setToken(token)
                    .setPaymentMethod(paymentMethod)
                    .build()
            )
    }

    fun handlePaymentCheckoutResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        callback: PaymentIntentResult
    ) {
        handlePaymentResult(
            requestCode,
            resultCode,
            data,
            callback
        )
    }

    fun handlePaymentMethodResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        callback: PaymentMethodResult
    ) {
        handlePaymentResult(
            requestCode,
            resultCode,
            data,
            callback
        )
    }

    fun handlePaymentShippingResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        callback: PaymentShippingResult
    ) {
        handlePaymentResult(
            requestCode,
            resultCode,
            data,
            callback
        )
    }

    fun handlePaymentIntentResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        callback: PaymentIntentResult
    ) {
        handlePaymentResult(
            requestCode,
            resultCode,
            data,
            callback
        )
    }

    private fun handlePaymentResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        callback: PaymentResult? = null
    ): Boolean {
        if (!VALID_REQUEST_CODES.contains(requestCode)) {
            return false
        }

        when (resultCode) {
            Activity.RESULT_OK -> {
                return when (requestCode) {
                    AddPaymentMethodActivityStarter.REQUEST_CODE -> {
                        val result = AddPaymentMethodActivityStarter.Result.fromIntent(data)
                        (callback as PaymentMethodResult).onSuccess(requireNotNull(result?.paymentMethod))
                        true
                    }
                    PaymentShippingActivityStarter.REQUEST_CODE -> {
                        val result = PaymentShippingActivityStarter.Result.fromIntent(data)
                        (callback as PaymentShippingResult).onSuccess(requireNotNull(result?.shipping))
                        true
                    }
                    PaymentMethodsActivityStarter.REQUEST_CODE -> {
                        val result = PaymentMethodsActivityStarter.Result.fromIntent(data)
                        if (result?.error != null) {
                            (callback as PaymentIntentResult).onFailed(result.error)
                        } else {
                            (callback as PaymentIntentResult).onSuccess(
                                requireNotNull(result?.paymentIntent),
                                requireNotNull(result?.paymentMethodType)
                            )
                        }
                        true
                    }
                    PaymentCheckoutActivityStarter.REQUEST_CODE -> {
                        val result = PaymentCheckoutActivityStarter.Result.fromIntent(data)
                        if (result?.error != null) {
                            (callback as PaymentIntentResult).onFailed(result.error)
                        } else {
                            (callback as PaymentIntentResult).onSuccess(
                                requireNotNull(result?.paymentIntent),
                                requireNotNull(result?.paymentMethodType)
                            )
                        }
                        true
                    }
                    else -> false
                }
            }
            Activity.RESULT_CANCELED -> {
                return when (requestCode) {
                    AddPaymentMethodActivityStarter.REQUEST_CODE -> {
                        (callback as PaymentMethodResult).onCancelled()
                        true
                    }
                    PaymentShippingActivityStarter.REQUEST_CODE -> {
                        (callback as PaymentShippingResult).onCancelled()
                        true
                    }
                    PaymentMethodsActivityStarter.REQUEST_CODE -> {
                        (callback as PaymentIntentResult).onCancelled()
                        true
                    }
                    PaymentCheckoutActivityStarter.REQUEST_CODE -> {
                        (callback as PaymentIntentResult).onCancelled()
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
            AddPaymentMethodActivityStarter.REQUEST_CODE,
            PaymentCheckoutActivityStarter.REQUEST_CODE
        )
    }
}