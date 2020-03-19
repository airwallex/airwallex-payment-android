package com.airwallex.android

import androidx.annotation.UiThread
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.*
import java.util.*

/**
 * Entry-point to the Airwallex SDK.
 */
class Airwallex internal constructor(
    private val token: String,
    private val clientSecret: String,
    private val customerId: String?,
    private val baseUrl: String,
    private val paymentController: PaymentController
) {

    interface PaymentListener<Response> {
        fun onFailed(exception: AirwallexException)
        fun onSuccess(response: Response)
    }

    /**
     * @param token The token that should be removed on SDK later
     * @param clientSecret All API requests need to take this parameter
     * @param customerId The customer id of user
     * @param baseUrl You can set different values to test on different environments
     */
    // TODO token need to be removed after API changed
    constructor(
        token: String,
        clientSecret: String,
        customerId: String? = null,
        baseUrl: String = BASE_URL
    ) : this(
        token,
        clientSecret,
        customerId,
        baseUrl,
        AirwallexApiRepository()
    )

    private constructor(
        token: String,
        clientSecret: String,
        customerId: String? = null,
        baseUrl: String = BASE_URL,
        repository: ApiRepository
    ) : this(
        token,
        clientSecret,
        customerId,
        baseUrl,
        AirwallexPaymentController(repository)
    )

    /**
     * Confirm a payment intent
     *
     * @param paymentIntentId the paymentIntentId that you want to confirm
     * @param listener the callback of confirm [PaymentIntent]
     */
    @UiThread
    fun confirmPaymentIntent(
        paymentIntentId: String,
        listener: PaymentListener<PaymentIntent>
    ) {
        paymentController.confirmPaymentIntent(
            AirwallexApiRepository.Options(
                token = token,
                clientSecret = clientSecret,
                baseUrl = baseUrl,
                paymentIntentOptions = AirwallexApiRepository.PaymentIntentOptions(
                    paymentIntentId = paymentIntentId,
                    paymentIntentConfirmRequest = PaymentIntentConfirmRequest.Builder()
                        .setRequestId(UUID.randomUUID().toString())
                        .setCustomerId(customerId)
                        .setDevice(DeviceUtils.device)
                        .setPaymentMethod(
                            PaymentMethod.Builder()
                                .setType(PaymentMethodType.WECHAT)
                                .setWechatPayFlow(WechatPayRequest(WechatPayRequestFlow.INAPP))
                                .build()
                        )
                        .build()
                )
            ),
            listener
        )
    }

    /**
     * Retrieve a payment intent
     *
     * @param paymentIntentId the paymentIntentId that you want to retrieve
     * @param listener the callback of retrieve [PaymentIntent]
     */
    @UiThread
    fun retrievePaymentIntent(
        paymentIntentId: String,
        listener: PaymentListener<PaymentIntent>
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
            listener
        )
    }

    companion object {
        // The default url, that you can change in the constructor for test on different environments
        private const val BASE_URL = "https://api.airwallex.com"
    }
}
