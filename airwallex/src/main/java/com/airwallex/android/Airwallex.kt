package com.airwallex.android

import android.app.Activity
import androidx.annotation.UiThread
import com.airwallex.android.exception.APIConnectionException
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

    interface PaymentCallback<Response> {
        fun onFailed(exception: AirwallexException)
        fun onSuccess(response: Response)
    }

    /**
     * @param token The token that should be removed on SDK later
     * @param clientSecret All API requests need to take this parameter
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
        activity: Activity,
        paymentMethodType: PaymentMethodType,
        paymentIntentId: String,
        paymentIntentParams: PaymentIntentParams,
        callback: PaymentCallback<PaymentIntent>
    ) {
        val options = AirwallexApiRepository.Options(
            token = token,
            clientSecret = clientSecret,
            baseUrl = baseUrl,
            paymentIntentOptions = AirwallexApiRepository.PaymentIntentOptions(
                paymentIntentId = paymentIntentId
            )
        )

        val paymentCallback = when (paymentMethodType) {
            PaymentMethodType.CARD -> {
                object : PaymentCallback<PaymentIntent> {
                    override fun onFailed(exception: AirwallexException) {
                        callback.onFailed(exception)
                    }

                    override fun onSuccess(response: PaymentIntent) {
                        val jwt = response.nextAction?.data?.jwt

                        var threeDs: PaymentMethodOptions.CardOptions.ThreeDs
                        if (jwt != null) {
                            // Need 3ds
                            ThreeDSecure.performVerification(activity, jwt) { referenceId ->
                                if (referenceId == null) {
                                    callback.onFailed(APIConnectionException(message = "Setup 3ds failed"))
                                    return@performVerification
                                }

                                // Step 2: Fetch pareq & transactionId from server
                                threeDs = PaymentMethodOptions.CardOptions.ThreeDs.Builder()
                                    .setAttemptId(response.latestPaymentAttempt?.id)
                                    .setDeviceDataCollectionRes(referenceId)
                                    .setReturnUrl("http://requestbin.net/r/1il2qkm1")
                                    .build()

                                paymentIntentParams.copy(
                                    paymentMethodOptions = PaymentMethodOptions.Builder()
                                        .setCardOptions(
                                            PaymentMethodOptions.CardOptions.Builder()
                                                .setAutoCapture(true)
                                                .setThreeDs(threeDs).build()
                                        )
                                        .build()
                                )
                                paymentController.confirmPaymentIntent(
                                    options,
                                    paymentIntentParams,
                                    object : PaymentCallback<PaymentIntent> {
                                        override fun onFailed(exception: AirwallexException) {
                                            callback.onFailed(exception)
                                        }

                                        override fun onSuccess(response: PaymentIntent) {
                                            // TODO transaction id & pareq
                                            ThreeDSecure.performCardinalAuthentication(
                                                activity,
                                                ThreeDSecureLookup("", "")
                                            ) { threeDSecureLookup, validateResponse, jwt ->

                                                if (!validateResponse.isValidated) {
                                                    callback.onFailed(APIConnectionException(message = "Valid 3ds failed"))
                                                    return@performCardinalAuthentication
                                                }

                                                // Step 3: Perform 3ds page of sdk
                                                threeDs =
                                                    PaymentMethodOptions.CardOptions.ThreeDs.Builder()
                                                        .setAttemptId(response.latestPaymentAttempt?.id)
                                                        .setDsTransactionId(validateResponse.payment.processorTransactionId)
                                                        .setReturnUrl("http://requestbin.net/r/1il2qkm1")
                                                        .build()
                                                paymentIntentParams.copy(
                                                    paymentMethodOptions = PaymentMethodOptions.Builder()
                                                        .setCardOptions(
                                                            PaymentMethodOptions.CardOptions.Builder()
                                                                .setAutoCapture(true)
                                                                .setThreeDs(threeDs).build()
                                                        )
                                                        .build()
                                                )
                                                paymentController.confirmPaymentIntent(
                                                    options,
                                                    paymentIntentParams,
                                                    object : PaymentCallback<PaymentIntent> {
                                                        override fun onFailed(exception: AirwallexException) {
                                                            callback.onFailed(exception)
                                                        }

                                                        override fun onSuccess(response: PaymentIntent) {
                                                            callback.onSuccess(response)
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        } else {
                            // Not not need 3ds
                            callback.onSuccess(response)
                        }
                    }

                }
            }
            PaymentMethodType.WECHAT -> {
                callback
            }
        }

        // Step 1: Check JWT, to see if need 3ds
        paymentController.confirmPaymentIntent(
            options,
            paymentIntentParams,
            paymentCallback
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
