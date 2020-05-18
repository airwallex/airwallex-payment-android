package com.airwallex.android

import com.airwallex.android.model.AirwallexError

internal interface ThreeDSecureCallback {
    fun onSuccess(transactionId: String)
    fun onFailed(exception: AirwallexError)
}
