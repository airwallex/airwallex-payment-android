package com.airwallex.android.card

import com.airwallex.android.core.model.PaymentIntent

/**
 * Callback for Dcc
 */
interface DccCallback {
    fun onSuccess(paymentIntent: PaymentIntent)

    fun onFailed(exception: Exception)
}
