package com.airwallex.android

import androidx.annotation.UiThread
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentIntentParams

class Airwallex internal constructor(
    private val token: String,
    private val paymentController: PaymentController
) {
    companion object {
        fun initialize(configuration: AirwallexConfiguration) {
            AirwallexPlugins.initialize(configuration)
        }
    }

    interface PaymentIntentCallback {
        fun onSuccess(paymentIntent: PaymentIntent)

        fun onFailed()
    }

    @JvmOverloads
    constructor(
        token: String
    ) : this(
        token,
        AirwallexApiRepository()
    )

    private constructor(
        token: String,
        repository: ApiRepository
    ) : this(
        token,
        AirwallexPaymentController(repository)
    )

    @UiThread
    fun confirmPaymentIntent(
        paymentIntentId: String,
        paymentIntentParams: PaymentIntentParams,
        callback: PaymentIntentCallback
    ) {
        paymentController.startConfirm(
            AirwallexApiRepository.Options(
                token = token,
                paymentIntentId = paymentIntentId
            ),
            paymentIntentParams,
            callback
        )
    }

    @UiThread
    fun retrievePaymentIntent(
        paymentIntentId: String,
        callback: PaymentIntentCallback
    ) {
        paymentController.retrievePaymentIntent(
            AirwallexApiRepository.Options(
                token = token,
                paymentIntentId = paymentIntentId
            ),
            callback
        )
    }
}