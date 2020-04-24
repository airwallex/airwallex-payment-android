package com.airwallex.android

import androidx.annotation.UiThread
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.*
import java.util.*

/**
 * Entry-point to the Airwallex SDK.
 */
class Airwallex internal constructor(
    private val paymentManager: PaymentManager
) {

    /**
     * Generic interface for an Airwallex API operation callback that either returns a [Response], or an [Exception]
     */
    interface PaymentListener<Response> {
        fun onFailed(exception: AirwallexException)
        fun onSuccess(response: Response)
    }

    /**
     * Constructor of [Airwallex]
     */
    constructor() : this(
        AirwallexApiRepository()
    )

    private constructor(
        repository: ApiRepository
    ) : this(
        AirwallexPaymentManager(repository)
    )

    /**
     * Confirm a [PaymentIntent] by ID
     *
     * @param params [ConfirmPaymentIntentParams] used to confirm [PaymentIntent]
     * @param listener a [PaymentListener] to receive the response or error
     */
    @UiThread
    fun confirmPaymentIntent(
        params: ConfirmPaymentIntentParams,
        listener: PaymentListener<PaymentIntent>
    ) {
        paymentManager.confirmPaymentIntent(
            AirwallexApiRepository.PaymentIntentOptions(
                clientSecret = params.clientSecret,
                paymentIntentId = params.paymentIntentId,
                paymentIntentConfirmRequest = PaymentIntentConfirmRequest.Builder(
                    requestId = UUID.randomUUID().toString()
                )
                    .setPaymentMethod(
                        PaymentMethod.Builder()
                            .setType(PaymentMethodType.WECHAT)
                            .setWeChatPayFlow(WeChatPayRequest(WeChatPayRequestFlow.IN_APP))
                            .build()
                    )
                    .setCustomerId(params.customerId)
                    .build()
            ),
            listener
        )
    }

    /**
     * Retrieve a [PaymentIntent] by ID
     *
     * @param params [RetrievePaymentIntentParams] used to receive the [PaymentIntent]
     * @param listener a [PaymentListener] to receive the response or error
     */
    @UiThread
    fun retrievePaymentIntent(
        params: RetrievePaymentIntentParams,
        listener: PaymentListener<PaymentIntent>
    ) {
        paymentManager.retrievePaymentIntent(
            AirwallexApiRepository.PaymentIntentOptions(
                clientSecret = params.clientSecret,
                paymentIntentId = params.paymentIntentId
            ),
            listener
        )
    }

    companion object {
        // The default url, that you can change in the constructor for test on different environments
        internal const val BASE_URL = "https://pci-api.airwallex.com"

        /**
         * Initialize some global configurations, better to be called on Application
         */
        fun initialize(configuration: AirwallexConfiguration) {
            AirwallexPlugins.initialize(configuration)
        }
    }
}
