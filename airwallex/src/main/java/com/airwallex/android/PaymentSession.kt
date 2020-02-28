package com.airwallex.android

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.Shipping
import com.airwallex.android.view.ActivityStarter
import com.airwallex.android.view.AddPaymentShippingActivityStarter
import com.airwallex.android.view.PaymentMethodsActivity
import com.airwallex.android.view.PaymentMethodsActivityStarter

class PaymentSession internal constructor(
    private val context: Activity,
    private val paymentMethodsActivityStarter:
    ActivityStarter<PaymentMethodsActivity, PaymentMethodsActivityStarter.Args>,
    private val paymentSessionData: PaymentSessionData
) {

    constructor(activity: Activity, paymentSessionData: PaymentSessionData) : this(
        activity,
        PaymentMethodsActivityStarter(activity),
        paymentSessionData
    )

    constructor(fragment: Fragment, paymentSessionData: PaymentSessionData) : this(
        fragment.requireActivity(),
        PaymentMethodsActivityStarter(fragment.requireActivity()),
        paymentSessionData
    )

    interface PaymentMethodResult {
        fun onCancelled()
        fun onSuccess(paymentMethod: PaymentMethod?)
    }

    interface PaymentShippingResult {
        fun onCancelled()
        fun onSuccess(shipping: Shipping?)
    }

    fun presentPaymentFlow() {
        paymentMethodsActivityStarter.startForResult(
            PaymentMethodsActivityStarter.Args
                .Builder()
                .setPaymentIntent(paymentSessionData.paymentIntent)
                .setToken(paymentSessionData.token)
                .build()
        )
    }

    fun presentShippingFlow() {
        AddPaymentShippingActivityStarter(context)
            .startForResult(
                AddPaymentShippingActivityStarter.Args.Builder()
                    .setShipping(paymentSessionData.shipping)
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

    fun handlePaymentMethod(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        callback: PaymentMethodResult
    ): Boolean {
        if (!VALID_REQUEST_CODES.contains(requestCode)) {
            return false
        }

        when (requestCode) {
            PaymentMethodsActivityStarter.REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val result = PaymentMethodsActivityStarter.Result.fromIntent(data)
                        callback.onSuccess(result?.paymentMethod)
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