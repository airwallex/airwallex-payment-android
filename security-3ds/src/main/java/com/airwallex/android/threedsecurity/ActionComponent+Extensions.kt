package com.airwallex.android.threedsecurity

import android.content.Intent
import com.airwallex.android.core.ActionComponent
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentStatus

fun ActionComponent.handleThreeDSActivityResult(
    paymentConsentId: String?,
    data: Intent?,
    listener: Airwallex.PaymentResultListener
) {
    val result = ThreeDSecurityActivityLaunch.Result.fromIntent(data)
    result?.paymentIntentId?.let { intentId ->
        listener.onCompleted(
            AirwallexPaymentStatus.Success(
                paymentIntentId = intentId,
                consentId = paymentConsentId
            )
        )
    }
    result?.exception?.let { exception ->
        listener.onCompleted(AirwallexPaymentStatus.Failure(exception))
    }
}