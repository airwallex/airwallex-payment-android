package com.airwallex.android

import com.airwallex.android.model.AirwallexError

/**
 * Callback for 3DS
 */
internal interface ThreeDSecureCallback {
    /**
     * 3DS success with `transactionId`
     */
    fun onSuccess(paResId: String)

    /**
     * 3DS failed with [AirwallexError]
     */
    fun onFailed(exception: AirwallexError)
}
