package com.airwallex.android

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.airwallex.android.model.*
import com.airwallex.android.view.*

class PaymentSession constructor(
    private val activity: Activity
) {

    constructor(
        fragment: Fragment
    ) : this(fragment.requireActivity())

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
        fun onSuccess(paymentMethod: PaymentMethod, cvc: String?)
    }

    /**
     * Launch the [PaymentMethodsActivity] to allow the user to finish the payment checkout flow
     */
    fun presentPaymentFlow(paymentIntent: PaymentIntent, token: String) {
        requireNotNull(paymentIntent.customerId, {
            "Customer id must be provided"
        })
        PaymentMethodsActivityStarter(activity)
            .startForResult(
                PaymentMethodsActivityStarter.Args.Builder()
                    .setPaymentIntent(paymentIntent)
                    .setToken(token)
                    .setIncludeCheckoutFlow(true)
                    .build()
            )
    }

    /**
     * Launch the [PaymentShippingActivity] to allow the user to fill the shipping information
     */
    fun presentShippingFlow(shipping: Shipping? = null) {
        PaymentShippingActivityStarter(activity)
            .startForResult(
                PaymentShippingActivityStarter.Args.Builder()
                    .setShipping(shipping)
                    .build()
            )
    }

    /**
     * Launch the [AddPaymentMethodActivity] to allow the user to add a payment method
     *
     */
    fun presentAddPaymentMethodFlow(paymentIntent: PaymentIntent, token: String) {
        requireNotNull(paymentIntent.customerId, {
            "Customer id must be provided"
        })
        AddPaymentMethodActivityStarter(activity)
            .startForResult(
                AddPaymentMethodActivityStarter.Args.Builder()
                    .setPaymentIntent(paymentIntent)
                    .setToken(token)
                    .build()
            )
    }

    /**
     * Launch the [PaymentMethodsActivity] to allow the user to select or create a payment method,
     * or to add a new one.
     */
    @Throws(NullPointerException::class)
    fun presentSelectPaymentMethodFlow(paymentIntent: PaymentIntent, token: String) {
        requireNotNull(paymentIntent.customerId, {
            "Customer id must be provided"
        })
        PaymentMethodsActivityStarter(activity)
            .startForResult(
                PaymentMethodsActivityStarter.Args.Builder()
                    .setPaymentIntent(paymentIntent)
                    .setToken(token)
                    .setIncludeCheckoutFlow(false)
                    .build()
            )
    }

    /**
     * Launch the [PaymentCheckoutActivity] to allow the user to confirm payment intent
     */
    @Throws(NullPointerException::class)
    fun presentPaymentCheckoutFlow(
        paymentIntent: PaymentIntent,
        paymentMethod: PaymentMethod,
        token: String
    ) {
        PaymentCheckoutActivityStarter(activity)
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
        handleResult(
            requestCode,
            resultCode,
            data,
            callback
        )
    }

    fun handleAddPaymentMethodResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        callback: PaymentMethodResult
    ) {
        handleResult(
            requestCode,
            resultCode,
            data,
            callback
        )
    }

    fun handleSelectPaymentMethodResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        callback: PaymentMethodResult
    ) {
        handleResult(
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
        handleResult(
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
        handleResult(
            requestCode,
            resultCode,
            data,
            callback
        )
    }

    private fun handleResult(
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
                        (callback as? PaymentMethodResult)?.onSuccess(
                            requireNotNull(result?.paymentMethod),
                            result?.cvc
                        )
                        true
                    }
                    PaymentShippingActivityStarter.REQUEST_CODE -> {
                        val result = PaymentShippingActivityStarter.Result.fromIntent(data)
                        (callback as? PaymentShippingResult)?.onSuccess(requireNotNull(result?.shipping))
                        true
                    }
                    PaymentMethodsActivityStarter.REQUEST_CODE -> {
                        val result = PaymentMethodsActivityStarter.Result.fromIntent(data)
                        if (result?.error != null) {
                            (callback as? PaymentIntentResult)?.onFailed(result.error)
                        } else {
                            if (result?.paymentMethod != null) {
                                (callback as? PaymentMethodResult)?.onSuccess(
                                    requireNotNull(result.paymentMethod),
                                    result.cvc
                                )
                            } else {
                                (callback as? PaymentIntentResult)?.onSuccess(
                                    requireNotNull(result?.paymentIntent),
                                    requireNotNull(result?.paymentMethodType)
                                )
                            }
                        }
                        true
                    }
                    PaymentCheckoutActivityStarter.REQUEST_CODE -> {
                        val result = PaymentCheckoutActivityStarter.Result.fromIntent(data)
                        if (result?.error != null) {
                            (callback as? PaymentIntentResult)?.onFailed(result.error)
                        } else {
                            (callback as? PaymentIntentResult)?.onSuccess(
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
                        (callback as? PaymentMethodResult)?.onCancelled()
                        true
                    }
                    PaymentShippingActivityStarter.REQUEST_CODE -> {
                        (callback as? PaymentShippingResult)?.onCancelled()
                        true
                    }
                    PaymentMethodsActivityStarter.REQUEST_CODE -> {
                        (callback as? PaymentIntentResult)?.onCancelled()
                        true
                    }
                    PaymentCheckoutActivityStarter.REQUEST_CODE -> {
                        (callback as? PaymentIntentResult)?.onCancelled()
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
