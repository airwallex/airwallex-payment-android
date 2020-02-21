package com.airwallex.android

import androidx.annotation.UiThread
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.*

class Airwallex internal constructor(
    private val token: String,
    private val clientSecret: String,
    private val paymentController: PaymentController
) {
    companion object {
        fun initialize(configuration: AirwallexConfiguration) {
            AirwallexPlugins.initialize(configuration)
        }
    }

    interface PaymentIntentCallback {
        fun onSuccess(paymentIntent: PaymentIntent)

        fun onFailed(exception: AirwallexException)
    }

    interface PaymentMethodCallback {
        fun onSuccess(paymentMethod: PaymentMethod)

        fun onFailed(exception: AirwallexException)
    }

    interface GetPaymentMethodsCallback {
        fun onSuccess(response: PaymentMethodResponse)

        fun onFailed(exception: AirwallexException)
    }

    // TODO token need to be removed after API changed
    constructor(
        token: String,
        clientSecret: String
    ) : this(
        token,
        clientSecret,
        AirwallexApiRepository()
    )

    private constructor(
        token: String,
        clientSecret: String,
        repository: ApiRepository
    ) : this(
        token,
        clientSecret,
        AirwallexPaymentController(repository)
    )

    @UiThread
    fun confirmPaymentIntent(
        paymentIntentId: String,
        paymentIntentParams: PaymentIntentParams,
        callback: PaymentIntentCallback
    ) {
        paymentController.confirmPaymentIntent(
            AirwallexApiRepository.Options(
                token = token,
                clientSecret = clientSecret,
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
                clientSecret = clientSecret,
                paymentIntentId = paymentIntentId
            ),
            callback
        )
    }

    @UiThread
    internal fun createPaymentMethod(
        paymentMethodParams: PaymentMethodParams,
        callback: PaymentMethodCallback
    ) {
        paymentController.createPaymentMethod(
            AirwallexApiRepository.Options(
                token = token,
                clientSecret = clientSecret
            ),
            paymentMethodParams,
            callback
        )
    }


    @UiThread
    internal fun getPaymentMethods(
        pageNum: Int = 0,
        pageSize: Int = 10,
        customerId: String,
        callback: GetPaymentMethodsCallback
    ) {
        paymentController.getPaymentMethods(
            AirwallexApiRepository.Options(
                token = token,
                clientSecret = clientSecret,
                pageNum = pageNum,
                pageSize = pageSize,
                customerId = customerId
            ),
            callback
        )
    }
}