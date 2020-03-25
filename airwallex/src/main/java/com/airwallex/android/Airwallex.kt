package com.airwallex.android

import androidx.annotation.UiThread
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.*
import java.util.*

/**
 * Entry-point to the Airwallex SDK.
 */
class Airwallex internal constructor(
    private val clientSecret: String,
    private val customerId: String?,
    private val baseUrl: String,
    private val paymentController: PaymentController
) {

    /**
     * Generic interface for an API operation callback that either returns a [Response], or an [Exception]
     */
    interface PaymentListener<Response> {
        fun onFailed(exception: AirwallexException)
        fun onSuccess(response: Response)
    }

    /**
     * Constructor with clientSecret and customerId.
     *
     * @param clientSecret The client secret of [PaymentIntent].
     * @param customerId The ID of a Customer, it's optional.
     * @param baseUrl You can set it to different urls and test on different environments
     */
    constructor(
        clientSecret: String,
        customerId: String? = null,
        baseUrl: String = BASE_URL
    ) : this(
        clientSecret,
        customerId,
        baseUrl,
        AirwallexApiRepository()
    )

    private constructor(
        clientSecret: String,
        customerId: String? = null,
        baseUrl: String = BASE_URL,
        repository: ApiRepository
    ) : this(
        clientSecret,
        customerId,
        baseUrl,
        AirwallexPaymentController(repository)
    )

    /**
     * Confirm a [PaymentIntent] by ID
     *
     * @param paymentIntentId ID of [PaymentIntent]
     * @param listener a [PaymentListener] to receive the result or error
     */
    @UiThread
    fun confirmPaymentIntent(
        paymentIntentId: String,
        listener: PaymentListener<PaymentIntent>
    ) {
        paymentController.confirmPaymentIntent(
            AirwallexApiRepository.PaymentIntentOptions(
                clientSecret = clientSecret,
                baseUrl = baseUrl,
                paymentIntentId = paymentIntentId,
                paymentIntentConfirmRequest = PaymentIntentConfirmRequest.Builder(
                    requestId = UUID.randomUUID().toString(),
                    device = DeviceUtils.device,
                    paymentMethod = PaymentMethod.Builder()
                        .setType(PaymentMethodType.WECHAT)
                        .setWechatPayFlow(WechatPayRequest(WechatPayRequestFlow.INAPP))
                        .build()
                )
                    .setCustomerId(customerId)
                    .build()
            ),
            listener
        )
    }

    /**
     * Retrieve a [PaymentIntent] by ID
     *
     * @param paymentIntentId ID of [PaymentIntent]
     * @param listener a [PaymentListener] to receive the result or error
     */
    @UiThread
    fun retrievePaymentIntent(
        paymentIntentId: String,
        listener: PaymentListener<PaymentIntent>
    ) {
        paymentController.retrievePaymentIntent(
            AirwallexApiRepository.PaymentIntentOptions(
                clientSecret = clientSecret,
                baseUrl = baseUrl,
                paymentIntentId = paymentIntentId
            ),
            listener
        )
    }

    companion object {
        // The default url, that you can change it in the constructor to test on different environments
        private const val BASE_URL = "https://api.airwallex.com"
    }
}
