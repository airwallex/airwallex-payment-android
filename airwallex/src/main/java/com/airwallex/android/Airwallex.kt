package com.airwallex.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.fragment.app.Fragment

class Airwallex internal constructor(
    val context: Context,
    val config: PaymentConfiguration,
    private val paymentController: PaymentController,
    private val stripeRepository: AirwallexRepository
) {

    @UiThread
    fun confirmPayment(
        activity: Activity,
        confirmPaymentIntentParams: ConfirmPaymentIntentParams
    ) {
        paymentController.startConfirmAndAuth(
            AuthActivityStarter.Host.create(activity),
            confirmPaymentIntentParams,
            ApiRequest.Options(
                apiKey = config.apiKey,
                clientId = config.clientId,
                url = config.environment.baseUrl
            )
        )
    }

    @UiThread
    fun confirmPayment(
        fragment: Fragment,
        confirmPaymentIntentParams: ConfirmPaymentIntentParams
    ) {
        paymentController.startConfirmAndAuth(
            AuthActivityStarter.Host.create(fragment),
            confirmPaymentIntentParams,
            ApiRequest.Options(
                apiKey = config.apiKey,
                clientId = config.clientId,
                url = config.environment.baseUrl
            )
        )
    }

    @UiThread
    fun onPaymentResult(
        requestCode: Int,
        data: Intent?,
        callback: ApiResultCallback<PaymentIntentResult>
    ): Boolean {
        return if (data != null && paymentController.shouldHandlePaymentResult(requestCode, data)) {
            paymentController.handlePaymentResult(
                data,
                ApiRequest.Options(
                    apiKey = config.apiKey,
                    clientId = config.clientId,
                    url = config.environment.baseUrl
                ),
                callback
            )
            true
        } else {
            false
        }
    }

    @WorkerThread
    fun retrievePaymentIntentSynchronous(): PaymentIntent? {
        return stripeRepository.retrievePaymentIntent(
            ApiRequest.Options(
                apiKey = config.apiKey,
                clientId = config.clientId,
                url = config.environment.baseUrl
            )
        )
    }
}