package com.airwallex.android

import android.content.Intent

internal interface PaymentController {

    fun startConfirmAndAuth(
        host: AuthActivityStarter.Host,
        confirmStripeIntentParams: ConfirmAirwallexIntentParams,
        requestOptions: ApiRequest.Options
    )

    fun shouldHandlePaymentResult(requestCode: Int, data: Intent?): Boolean

    fun handlePaymentResult(
        data: Intent,
        requestOptions: ApiRequest.Options,
        callback: ApiResultCallback<PaymentIntentResult>
    )

}