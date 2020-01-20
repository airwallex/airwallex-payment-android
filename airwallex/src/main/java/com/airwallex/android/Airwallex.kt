package com.airwallex.android

import android.content.Context
import android.content.Intent
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread

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
        baseUrl: String = "https://staging-pci-api.airwallex.com"
    ) {
        paymentController.startConfirm(
            AirwallexApiRepository.Options(
                baseUrl = baseUrl,
                token = token,
                paymentIntentId = paymentIntentId
            )
        )
    }

    @WorkerThread
    fun retrievePaymentIntent(
        paymentIntentId: String,
        baseUrl: String = "https://staging-pci-api.airwallex.com"
    ) {
        airwallexRepository.retrievePaymentIntent(
            AirwallexApiRepository.Options(
                baseUrl = baseUrl,
                token = token,
                paymentIntentId = paymentIntentId
            )
        )
    }

    @UiThread
    fun onPaymentResult(
        requestCode: Int,
        data: Intent?,
        callback: ApiResultCallback<PaymentIntentResult>
    ) {

    }

}