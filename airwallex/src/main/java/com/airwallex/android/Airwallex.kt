package com.airwallex.android

import androidx.annotation.UiThread
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.*

class Airwallex internal constructor(
    private val token: String,
    private val clientSecret: String,
    private val baseUrl: String,
    private val paymentController: PaymentController
) {

    interface PaymentCallback<T> {
        fun onFailed(exception: AirwallexException)
        fun onSuccess(response: T)
    }

    // TODO token need to be removed after API changed
    constructor(
        token: String,
        clientSecret: String,
        baseUrl: String = BASE_URL
    ) : this(
        token,
        clientSecret,
        baseUrl,
        AirwallexApiRepository()
    )

    private constructor(
        token: String,
        clientSecret: String,
        baseUrl: String = BASE_URL,
        repository: ApiRepository
    ) : this(
        token,
        clientSecret,
        baseUrl,
        AirwallexPaymentController(repository)
    )

    @UiThread
    fun confirmPaymentIntent(
        paymentIntentId: String,
        paymentIntentParams: PaymentIntentParams,
        callback: PaymentCallback<PaymentIntent>
    ) {
        paymentController.confirmPaymentIntent(
            AirwallexApiRepository.Options(
                token = token,
                clientSecret = clientSecret,
                baseUrl = baseUrl,
                paymentIntentOptions = AirwallexApiRepository.PaymentIntentOptions(
                    paymentIntentId = paymentIntentId
                )
            ),
            paymentIntentParams,
            callback
        )
    }

    @UiThread
    fun retrievePaymentIntent(
        paymentIntentId: String,
        callback: PaymentCallback<PaymentIntent>
    ) {
        paymentController.retrievePaymentIntent(
            AirwallexApiRepository.Options(
                token = token,
                clientSecret = clientSecret,
                baseUrl = baseUrl,
                paymentIntentOptions = AirwallexApiRepository.PaymentIntentOptions(
                    paymentIntentId = paymentIntentId
                )
            ),
            callback
        )
    }

    @UiThread
    internal fun createPaymentMethod(
        paymentMethodParams: PaymentMethodParams,
        callback: PaymentCallback<PaymentMethod>
    ) {
        paymentController.createPaymentMethod(
            AirwallexApiRepository.Options(
                token = token,
                clientSecret = clientSecret,
                baseUrl = baseUrl
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
        callback: PaymentCallback<PaymentMethodResponse>
    ) {
        paymentController.getPaymentMethods(
            AirwallexApiRepository.Options(
                token = token,
                clientSecret = clientSecret,
                baseUrl = baseUrl,
                paymentMethodOptions = AirwallexApiRepository.PaymentMethodOptions(
                    pageNum = pageNum,
                    pageSize = pageSize,
                    customerId = customerId
                )
            ),
            callback
        )
    }

    companion object {
        private const val BASE_URL = "https://staging-pci-api.airwallex.com"

        fun initialize(configuration: AirwallexConfiguration) {
            AirwallexPlugins.initialize(configuration)
        }
    }
}
