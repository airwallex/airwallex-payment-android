package com.airwallex.android

import com.airwallex.android.model.AirwallexError

/**
 * Callback for 3DS
 */
internal interface ThreeDSecureCallback {
    /**
     * 3DS1 success, need to fetch `transactionId` via `/paresCache?paResId=%s`
     */
    fun onThreeDS1Success(payload: String)

    /**
     * 3DS2 success
     */
    fun onThreeDS2Success(transactionId: String)

    /**
     * 3DS failed with [AirwallexError]
     */
    fun onFailed(exception: AirwallexError)
}
