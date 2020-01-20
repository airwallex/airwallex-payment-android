package com.airwallex.android

import android.content.Intent

internal interface PaymentController {

    fun startConfirm(options: AirwallexApiRepository.Options)

    fun shouldHandlePaymentResult(requestCode: Int, data: Intent?): Boolean

    fun handlePaymentResult(
        data: Intent,
        callback: ApiResultCallback<PaymentIntentResult>
    )
}
