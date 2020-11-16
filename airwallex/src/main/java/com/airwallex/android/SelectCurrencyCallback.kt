package com.airwallex.android

import com.airwallex.android.model.AirwallexError
import com.airwallex.android.model.PaymentIntent

/**
 * Callback for SelectCurrency
 */
interface SelectCurrencyCallback {
    fun onSuccess(paymentIntent: PaymentIntent)

    fun onFailed(exception: AirwallexError)
}
