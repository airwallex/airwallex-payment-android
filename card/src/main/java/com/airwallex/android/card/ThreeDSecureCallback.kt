package com.airwallex.android.card

import com.airwallex.android.core.exception.AirwallexException

/**
 * Callback for 3DS
 */
interface ThreeDSecureCallback {
    /**
     * 3DS1 success, need to fetch `transactionId` via `/paresCache?paResId=%s`
     */
    fun onThreeDS1Success(payload: String)

    /**
     * 3DS2 success
     */
    fun onThreeDS2Success(transactionId: String)

    /**
     * 3DS failed with [AirwallexException]
     */
    fun onFailed(exception: AirwallexException)
}
