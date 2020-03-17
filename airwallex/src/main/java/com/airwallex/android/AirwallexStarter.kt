package com.airwallex.android

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.airwallex.android.model.*
import com.airwallex.android.view.*

/**
 *  Create a AirwallexStarter attached to the given host Activity.
 *
 *  @param activity This Activity will receive results in `Activity#onActivityResult(int, int, Intent)`
 *  that should be passed back to this session.
 */
class AirwallexStarter constructor(
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

    /**
     * Launch the [PaymentShippingActivity] to allow the user to fill the shipping information
     *
     * [handlePaymentShippingResult] to handle the Shipping
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
     * Handle [presentShippingFlow] results. Pass data here from your onActivityResult(int, int, Intent)` function.
     */
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
                    PaymentShippingActivityStarter.REQUEST_CODE -> {
                        val result = PaymentShippingActivityStarter.Result.fromIntent(data)
                        (callback as? PaymentShippingResult)?.onSuccess(requireNotNull(result?.shipping))
                        true
                    }
                    else -> false
                }
            }
            Activity.RESULT_CANCELED -> {
                return when (requestCode) {
                    PaymentShippingActivityStarter.REQUEST_CODE -> {
                        (callback as? PaymentShippingResult)?.onCancelled()
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
            PaymentShippingActivityStarter.REQUEST_CODE
        )
    }
}
