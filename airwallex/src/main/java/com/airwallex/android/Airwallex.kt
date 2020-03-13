package com.airwallex.android

import android.app.Activity
import androidx.annotation.UiThread
import com.airwallex.android.exception.APIConnectionException
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.*
import java.util.*

/**
 * Entry-point to the Airwallex SDK.
 */
class Airwallex internal constructor(
    private val token: String,
    private val clientSecret: String,
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
     * @param activity
     * @param paymentIntentId the paymentIntentId that you want to confirm
     * @param paymentMethod [PaymentMethod] used to confirm the [PaymentIntent]
     * @param customerId
     * @param cvc
     * @param listener the callback of confirm [PaymentIntent]
     */
    @UiThread
    fun confirmPaymentIntent(
        activity: Activity,
        paymentIntentId: String,
        customerId: String,
        paymentMethod: PaymentMethod,
        cvc: String,
        listener: PaymentListener<PaymentIntent>
    ) {
        val paymentIntentParams = buildPaymentIntentParams(
            paymentMethod = paymentMethod,
            customerId = customerId,
            cvc = cvc
        )

        val options = AirwallexApiRepository.Options(
            token = token,
            clientSecret = clientSecret,
            baseUrl = baseUrl,
            paymentIntentOptions = AirwallexApiRepository.PaymentIntentOptions(
                paymentIntentId = paymentIntentId
            )
        )

        val paymentCallback = when (paymentMethod.type) {
            PaymentMethodType.CARD -> {
                object : PaymentListener<PaymentIntent> {
                    override fun onFailed(exception: AirwallexException) {
                        listener.onFailed(exception)
                    }

                    override fun onSuccess(response: PaymentIntent) {
                        val jwt = response.nextAction?.data?.jwt

                        if (jwt != null) {
                            // 3DS Flow
                            performThreeDsFlow(
                                activity = activity,
                                jwt = jwt,
                                paymentIntent = response,
                                paymentIntentParams = paymentIntentParams,
                                options = options,
                                listener = listener
                            )
                        } else {
                            listener.onSuccess(response)
                        }
                    }
                }
            }
            PaymentMethodType.WECHAT -> {
                listener
            }
        }

        paymentController.confirmPaymentIntent(
            options,
            paymentIntentParams,
            paymentCallback
        )
    }

    private fun performThreeDsFlow(
        activity: Activity,
        jwt: String,
        paymentIntent: PaymentIntent,
        paymentIntentParams: PaymentIntentParams,
        options: AirwallexApiRepository.Options,
        listener: PaymentListener<PaymentIntent>
    ) {
        var threeDs: PaymentMethodOptions.CardOptions.ThreeDs
        ThreeDSecure.performVerification(activity, jwt) { referenceId ->
            if (referenceId == null) {
                listener.onFailed(APIConnectionException(message = "Setup 3ds failed"))
                return@performVerification
            }

            // Fetch pareq & transactionId from server
            threeDs = PaymentMethodOptions.CardOptions.ThreeDs.Builder()
                .setAttemptId(paymentIntent.latestPaymentAttempt?.id)
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
                object : PaymentListener<PaymentIntent> {
                    override fun onFailed(exception: AirwallexException) {
                        listener.onFailed(exception)
                    }

                    override fun onSuccess(response: PaymentIntent) {
                        // TODO transaction id & pareq
                        // Perform 3ds auth page
                        ThreeDSecure.performCardinalAuthentication(
                            activity,
                            ThreeDSecureLookup("", "")
                        ) { threeDSecureLookup, validateResponse, jwt ->

                            if (!validateResponse.isValidated) {
                                listener.onFailed(APIConnectionException(message = "Valid 3ds failed"))
                                return@performCardinalAuthentication
                            }

                            // Confirm transactionId
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
                                object : PaymentListener<PaymentIntent> {
                                    override fun onFailed(exception: AirwallexException) {
                                        listener.onFailed(exception)
                                    }

                                    override fun onSuccess(response: PaymentIntent) {
                                        listener.onSuccess(response)
                                    }
                                }
                            )
                        }
                    }
                }
            )
        }
    }

    private fun buildPaymentIntentParams(
        paymentMethod: PaymentMethod,
        customerId: String,
        cvc: String
    ): PaymentIntentParams {
        return when (paymentMethod.type) {
            PaymentMethodType.CARD -> {
                PaymentIntentParams.Builder()
                    .setRequestId(UUID.randomUUID().toString())
                    .setCustomerId(customerId)
                    .setDevice(DeviceUtils.device)
                    .setPaymentMethodReference(
                        PaymentMethodReference.Builder()
                            .setId(paymentMethod.id)
                            .setCvc(cvc)
                            .build()
                    )
                    .setPaymentMethodOptions(
                        PaymentMethodOptions.Builder()
                            .setCardOptions(
                                PaymentMethodOptions.CardOptions.Builder()
                                    .setAutoCapture(true)
                                    .setThreeDs(
                                        PaymentMethodOptions.CardOptions.ThreeDs.Builder()
                                            .build()
                                    ).build()
                            )
                            .build()
                    )
                    .build()
            }
            PaymentMethodType.WECHAT -> {
                PaymentIntentParams.Builder()
                    .setRequestId(UUID.randomUUID().toString())
                    .setCustomerId(customerId)
                    .setDevice(DeviceUtils.device)
                    .setPaymentMethod(paymentMethod)
                    .build()
            }
        }
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

    /**
     * Create a payment method
     *
     * @param paymentMethodParams [PaymentMethodParams] used to create the [PaymentMethod]
     * @param listener the callback of create [PaymentMethod]
     */
    @UiThread
    internal fun createPaymentMethod(
        paymentMethodParams: PaymentMethodParams,
        listener: PaymentListener<PaymentMethod>
    ) {
        paymentController.createPaymentMethod(
            AirwallexApiRepository.Options(
                token = token,
                clientSecret = clientSecret,
                baseUrl = baseUrl
            ),
            paymentMethodParams,
            listener
        )
    }

    /**
     * Get payment methods
     *
     * @param pageNum Page number starting from 0
     * @param pageSize Number of payment intents to be listed per page, default is 10
     * @param customerId The customerId that you want to use
     * @param listener the callback of get [PaymentMethod]
     */
    @UiThread
    internal fun getPaymentMethods(
        pageNum: Int = 0,
        pageSize: Int = 10,
        customerId: String,
        listener: PaymentListener<PaymentMethodResponse>
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
            listener
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
