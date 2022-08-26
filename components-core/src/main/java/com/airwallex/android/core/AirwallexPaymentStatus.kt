package com.airwallex.android.core

import com.airwallex.android.core.exception.AirwallexException

sealed class AirwallexPaymentStatus {
    // payment success
    data class Success(
        val paymentIntentId: String,
        val additionalInfo: Map<String, Any>? = null
    ) : AirwallexPaymentStatus()

    // payment redirecting
    data class InProgress(val paymentIntentId: String) : AirwallexPaymentStatus()

    // payment failure
    data class Failure(val exception: AirwallexException) : AirwallexPaymentStatus()

    // Payment cancel
    object Cancel : AirwallexPaymentStatus()
}
