package com.airwallex.android

import androidx.annotation.UiThread
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.*

/**
 * Entry-point to the Airwallex SDK.
 */
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

    /**
     * @param token
     * @param clientSecret
     * @param baseUrl You can set different values to test on different environments
     */
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

    /**
     * Confirm a payment intent
     *
     * @param paymentIntentId the paymentIntentId that you want to confirm
     * @param paymentIntentParams [PaymentIntentParams] used to confirm the [PaymentIntent]
     * @param callback the callback of confirm [PaymentIntent]
     */
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

    /**
     * Retrieve a payment intent
     *
     * @param paymentIntentId the paymentIntentId that you want to retrieve
     * @param callback the callback of retrieve [PaymentIntent]
     */
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

    /**
     * Create a payment method
     *
     * @param paymentMethodParams [PaymentMethodParams] used to create the [PaymentMethod]
     * @param callback the callback of create [PaymentMethod]
     */
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

    /**
     * Get payment methods
     *
     * @param pageNum Page number starting from 0
     * @param pageSize Number of payment intents to be listed per page, default is 10
     * @param customerId The customerId that you want to use
     * @param callback the callback of get [PaymentMethod]
     */
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
        // The default url, that you can change in the constructor for test on different environments
        private const val BASE_URL = "https://staging-pci-api.airwallex.com"

        /**
         * Initialize some global configurations, that need call on Application
         */
        fun initialize(configuration: AirwallexConfiguration) {
            AirwallexPlugins.initialize(configuration)
        }
    }
}
