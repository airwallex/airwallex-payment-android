package com.airwallex.android

import android.content.Context
import android.content.Intent
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentIntentParams

class Airwallex internal constructor(
    private val context: Context,
    private val token: String,
    private val paymentController: PaymentController,
    private val airwallexRepository: ApiRepository
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
        context: Context,
        token: String
    ) : this(
        context.applicationContext,
        token,
        AirwallexApiRepository()
    )

    private constructor(
        context: Context,
        token: String,
        airwallexRepository: ApiRepository
    ) : this(
        context.applicationContext,
        token,
        AirwallexPaymentController(airwallexRepository),
        airwallexRepository
    )

    @UiThread
    fun confirmPaymentIntent(
        paymentIntentId: String,
        paymentIntentParams: PaymentIntentParams,
        callback: PaymentIntentCallback,
        baseUrl: String = "https://staging-pci-api.airwallex.com"
    ) {
        paymentController.startConfirm(
            AirwallexApiRepository.Options(
                baseUrl = baseUrl,
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
        baseUrl: String = "https://staging-pci-api.airwallex.com",
        callback: PaymentIntentCallback
    ) {
        paymentController.retrievePaymentIntent(
            AirwallexApiRepository.Options(
                baseUrl = baseUrl,
                token = token,
                paymentIntentId = paymentIntentId
            ),
            callback
        )
    }

}