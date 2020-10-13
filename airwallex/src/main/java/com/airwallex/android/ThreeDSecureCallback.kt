package com.airwallex.android

import com.airwallex.android.model.AirwallexError

/**
 * Callback for 3DS
 */
internal interface ThreeDSecureCallback {
    /**
     * 3DS success with `transactionId`
     */
    fun onSuccess(paResId: String, threeDSecureType: ThreeDSecure.ThreeDSecureType)

    /**
     * 3DS failed with [AirwallexError]
     */
    fun onFailed(exception: AirwallexError)
}
