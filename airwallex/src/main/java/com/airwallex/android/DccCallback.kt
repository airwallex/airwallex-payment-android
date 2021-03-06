package com.airwallex.android

import com.airwallex.android.model.PaymentIntent
import java.lang.Exception

/**
 * Callback for Dcc
 */
internal interface DccCallback {
    fun onSuccess(paymentIntent: PaymentIntent)

    fun onFailed(exception: Exception)
}
