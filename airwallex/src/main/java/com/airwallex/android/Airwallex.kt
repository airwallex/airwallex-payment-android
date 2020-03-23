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

    interface PaymentListener<Response> {
        fun onFailed(exception: AirwallexException)
        fun onSuccess(response: Response)
    }

    /**
     * @param clientSecret The client secret of the PaymentIntent.
     * @param customerId The id of a Customer, it's optional.
     * @param baseUrl You can set to different value to test on different environment
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
     * Confirm a [PaymentIntent] using the paymentIntentId
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
     * Retrieve a [PaymentIntent] using the paymentIntentId
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
            AirwallexApiRepository.PaymentIntentOptions(
                clientSecret = clientSecret,
                baseUrl = baseUrl,
                paymentIntentId = paymentIntentId
            ),
            listener
        )
    }

    companion object {
        // The default url, that you can change in the constructor for test on different environments
        private const val BASE_URL = "https://api.airwallex.com"
    }
}
