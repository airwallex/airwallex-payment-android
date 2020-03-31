package com.airwallex.android

import androidx.annotation.UiThread
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.*
import java.util.*

/**
 * Entry-point to the Airwallex SDK.
 */
class Airwallex internal constructor(
    private val baseUrl: String,
    private val paymentController: PaymentManager
) {

    /**
     * Generic interface for an Airwallex API operation callback that either returns a [Response], or an [Exception]
     */
    interface PaymentListener<Response> {
        fun onFailed(exception: AirwallexException)
        fun onSuccess(response: Response)
    }

    /**
     * Constructor with clientSecret and customerId.
     *
     * @param enableLogging enable log in sdk, default false
     * @param baseUrl optional, you can set it to different urls and test on different environments
     */
    constructor(
        enableLogging: Boolean = false,
        baseUrl: String = BASE_URL
    ) : this(
        baseUrl,
        AirwallexApiRepository(enableLogging)
    )

    private constructor(
        baseUrl: String = BASE_URL,
        repository: ApiRepository
    ) : this(
        baseUrl,
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
        paymentController.confirmPaymentIntent(
            AirwallexApiRepository.PaymentIntentOptions(
                clientSecret = params.clientSecret,
                baseUrl = baseUrl,
                paymentIntentId = params.paymentIntentId,
                paymentIntentConfirmRequest = PaymentIntentConfirmRequest.Builder(
                    requestId = UUID.randomUUID().toString(),
                    device = DeviceUtils.device,
                    paymentMethod = PaymentMethod.Builder()
                        .setType(PaymentMethodType.WECHAT)
                        .setWechatPayFlow(WechatPayRequest(WechatPayRequestFlow.INAPP))
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
     * @param params [PaymentIntentParams] used to receive the [PaymentIntent]
     * @param listener a [PaymentListener] to receive the response or error
     */
    @UiThread
    fun retrievePaymentIntent(
        params: PaymentIntentParams,
        listener: PaymentListener<PaymentIntent>
    ) {
        paymentController.retrievePaymentIntent(
            AirwallexApiRepository.PaymentIntentOptions(
                clientSecret = params.clientSecret,
                baseUrl = baseUrl,
                paymentIntentId = params.paymentIntentId
            ),
            listener
        )
    }

    companion object {
        /**
         * The default url, that you can change it in the constructor to test on different environments
         */
        private const val BASE_URL = "https://pci-api.airwallex.com"
    }
}
