package com.airwallex.android

import android.content.Intent

internal interface PaymentController {

    fun startConfirm(token: String, paymentIntentId: String)

    fun shouldHandlePaymentResult(requestCode: Int, data: Intent?): Boolean

    fun handlePaymentResult(
        data: Intent,
        callback: ApiResultCallback<PaymentIntentResult>
    )
}
