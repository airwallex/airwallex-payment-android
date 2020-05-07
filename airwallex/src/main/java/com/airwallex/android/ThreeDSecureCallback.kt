package com.airwallex.android

import com.airwallex.android.model.AirwallexError

internal interface ThreeDSecureCallback {
    fun onSuccess(processorTransactionId: String)
    fun onFailed(exception: AirwallexError)
}
