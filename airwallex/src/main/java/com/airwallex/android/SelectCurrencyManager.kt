package com.airwallex.android

import android.app.Activity
import android.content.Intent
import com.airwallex.android.model.AirwallexError
import com.airwallex.android.view.SelectCurrencyActivityLaunch

object SelectCurrencyManager {

    var selectCurrencyCallback: SelectCurrencyCallback? = null

    internal fun handleOnActivityResult(data: Intent?, resultCode: Int) {
        selectCurrencyCallback?.let {
            try {
                onActivityResult(data, resultCode, it)
            } catch (e: Exception) {
                it.onFailed(AirwallexError(message = e.localizedMessage))
            }
        }
    }

    private fun onActivityResult(
        data: Intent?,
        resultCode: Int,
        callback: SelectCurrencyCallback
    ) {
        if (data == null) {
            callback.onFailed(AirwallexError(message = "Confirm PaymentIntent Failed. Reason: Intent data is null"))
            return
        }
        when (resultCode) {
            Activity.RESULT_OK -> {
                val result = SelectCurrencyActivityLaunch.Result.fromIntent(data)
                val paymentIntent = result?.paymentIntent
                if (paymentIntent != null) {
                    callback.onSuccess(paymentIntent)
                } else {
                    callback.onFailed(result?.error
                        ?: AirwallexError("Confirm PaymentIntent Failed."))
                }
            }
            Activity.RESULT_CANCELED -> {
                callback.onFailed(AirwallexError(message = "Please select your currency."))
            }
        }
    }
}
