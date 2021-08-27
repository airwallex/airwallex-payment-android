package com.airwallex.android.card

/**
 * Callback for Dcc
 */
interface DccCallback {
    fun onSuccess(paymentIntentId: String)

    fun onFailed(exception: Exception)
}
