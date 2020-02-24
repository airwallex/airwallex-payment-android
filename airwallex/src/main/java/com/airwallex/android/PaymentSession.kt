package com.airwallex.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.view.ActivityStarter
import com.airwallex.android.view.PaymentMethodsActivity
import com.airwallex.android.view.PaymentMethodsActivityStarter

class PaymentSession internal constructor(
    private val context: Context,
    private val paymentMethodsActivityStarter:
    ActivityStarter<PaymentMethodsActivity, PaymentMethodsActivityStarter.PaymentMethodsArgs>,
    val paymentSessionData: PaymentSessionData
) {

    constructor(activity: Activity, paymentSessionData: PaymentSessionData) : this(
        activity.applicationContext,
        PaymentMethodsActivityStarter(activity),
        paymentSessionData
    )

    constructor(fragment: Fragment, paymentSessionData: PaymentSessionData) : this(
        fragment.requireContext().applicationContext,
        PaymentMethodsActivityStarter(fragment.requireActivity()),
        paymentSessionData
    )

    interface PaymentMethodResult {
        fun onCancelled()
        fun onSuccess(paymentMethod: PaymentMethod?)
    }

    fun presentPaymentMethodSelection() {
        paymentSessionData.config
        paymentMethodsActivityStarter.startForResult(
            PaymentMethodsActivityStarter.PaymentMethodsArgs
                .Builder(
                    paymentSessionData.clientSecret,
                    paymentSessionData.token,
                    paymentSessionData.customerId
                )
                .setPaymentMethod(paymentSessionData.paymentMethod)
                .setShouldShowWechatPay(paymentSessionData.config?.shouldShowWechatPay ?: false)
                .build()
        )
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
            PaymentMethodsActivityStarter.REQUEST_CODE
        )
    }
}