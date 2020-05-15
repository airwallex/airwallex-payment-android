package com.airwallex.android

import android.app.Activity
import android.os.Build
import androidx.annotation.UiThread
import com.airwallex.android.ThreeDSecure.THREE_DS_RETURN_URL
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.exception.ThreeDSException
import com.airwallex.android.model.*
import java.util.*

/**
 * Entry-point to the Airwallex SDK.
 */
class Airwallex internal constructor(
    private val paymentManager: PaymentManager
) {
    private lateinit var device: Device

    private val securityConnector: SecurityConnector by lazy {
        AirwallexSecurityConnector()
    }

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
     * @param activity the `Activity` that is start confirm the payment intent
     * @param params [ConfirmPaymentIntentParams] used to confirm [PaymentIntent]
     * @param listener a [PaymentListener] to receive the response or error
     */
    @UiThread
    fun confirmPaymentIntent(
        activity: Activity,
        params: ConfirmPaymentIntentParams,
        listener: PaymentListener<PaymentIntent>
    ) {
        securityConnector.retrieveSecurityToken(
            paymentIntentId = params.paymentIntentId,
            applicationContext = activity.applicationContext,
            securityTokenListener = object :
                AirwallexSecurityConnector.SecurityTokenListener {
                override fun onResponse(sessionId: String) {
                    device = Device.Builder()
                        .setDeviceId(sessionId)
                        .setDeviceModel(Build.MODEL)
                        .setSdkVersion(AirwallexPlugins.getSdkVersion(activity))
                        .setPlatformType(PLATFORM)
                        .setDeviceOS(Build.VERSION.RELEASE)
                        .build()

                    val options = when (params.paymentMethodType) {
                        PaymentMethodType.WECHAT -> {
                            buildWeChatPaymentIntentOptions(params)
                        }
                        PaymentMethodType.CARD -> {
                            buildCardPaymentIntentOptions(
                                params = params,
                                threeDSecure = PaymentMethodOptions.CardOptions.ThreeDSecure.Builder()
                                    .setReturnUrl(THREE_DS_RETURN_URL)
                                    .build()
                            )
                        }
                    }

                    val paymentListener = when (params.paymentMethodType) {
                        PaymentMethodType.CARD -> {
                            object : PaymentListener<PaymentIntent> {
                                override fun onFailed(exception: AirwallexException) {
                                    listener.onFailed(exception)
                                }

                                override fun onSuccess(response: PaymentIntent) {
                                    val serverJwt = response.nextAction?.data?.get("jwt") as? String

                                    if (serverJwt != null) {
                                        Logger.debug("Prepare 3DS Flow, serverJwt: $serverJwt")
                                        // 3D Secure Flow
                                        prepareThreeDSecureFlow(
                                            activity = activity,
                                            params = params,
                                            serverJwt = serverJwt,
                                            listener = listener
                                        )
                                    } else {
                                        Logger.debug("Don't need the 3DS Flow")
                                        listener.onSuccess(response)
                                    }
                                }
                            }
                        }
                        PaymentMethodType.WECHAT -> {
                            listener
                        }
                    }

                    paymentManager.confirmPaymentIntent(options, paymentListener)
                }
            })
    }

    private fun buildWeChatPaymentIntentOptions(
        params: ConfirmPaymentIntentParams
    ): AirwallexApiRepository.ConfirmPaymentIntentOptions {
        return AirwallexApiRepository.ConfirmPaymentIntentOptions(
            clientSecret = params.clientSecret,
            paymentIntentId = params.paymentIntentId,
            request = PaymentIntentConfirmRequest.Builder(
                requestId = UUID.randomUUID().toString()
            )
                .setPaymentMethod(
                    PaymentMethod.Builder()
                        .setType(PaymentMethodType.WECHAT)
                        .setWeChatPayFlow(WeChatPayRequest(WeChatPayRequestFlow.IN_APP))
                        .build()
                )
                .setCustomerId(params.customerId)
                .setDevice(device)
                .build()
        )
    }

    private fun buildCardPaymentIntentOptions(
        params: ConfirmPaymentIntentParams,
        threeDSecure: PaymentMethodOptions.CardOptions.ThreeDSecure
    ): AirwallexApiRepository.ConfirmPaymentIntentOptions {
        return AirwallexApiRepository.ConfirmPaymentIntentOptions(
            clientSecret = params.clientSecret,
            paymentIntentId = params.paymentIntentId,
            request = PaymentIntentConfirmRequest.Builder(
                requestId = UUID.randomUUID().toString()
            )
                .setPaymentMethodOptions(
                    PaymentMethodOptions.Builder()
                        .setCardOptions(
                            PaymentMethodOptions.CardOptions.Builder()
                                .setAutoCapture(true)
                                .setThreeDSecure(threeDSecure).build()
                        )
                        .build()
                )
                .setPaymentMethodReference(requireNotNull(params.paymentMethodReference))
                .setCustomerId(params.customerId)
                .setDevice(device)
                .build()
        )
    }

    /**
     * 3DS Flow.
     *
     * Step 1: Request `referenceId` with `serverJwt` by Cardinal SDK
     * Step 2: Request 3DS lookup response by `confirmPaymentIntent` with `referenceId`
     * Step 3: Use `ThreeDSecureActivity` to show 3DS UI, then wait user input. After user input, will receive `processorTransactionId`.
     * Step 4: Finally call `confirmPaymentIntent` method to send `processorTransactionId` to server to validate
     */
    private fun prepareThreeDSecureFlow(
        activity: Activity,
        params: ConfirmPaymentIntentParams,
        serverJwt: String,
        listener: PaymentListener<PaymentIntent>
    ) {
        Logger.debug("Step 1: Request `referenceId` with `serverJwt` by Cardinal SDK")
        ThreeDSecure.performCardinalInitialize(
            activity.applicationContext,
            serverJwt
        ) { referenceId, validateResponse ->
            if (validateResponse != null) {
                activity.runOnUiThread {
                    listener.onFailed(ThreeDSException(AirwallexError(message = validateResponse.errorDescription)))
                }
            } else {
                Logger.debug("Request 3DS lookup response by `confirmPaymentIntent` with `referenceId`")
                paymentManager.confirmPaymentIntent(
                    buildCardPaymentIntentOptions(
                        params = params,
                        threeDSecure = PaymentMethodOptions.CardOptions.ThreeDSecure.Builder()
                            .setDeviceDataCollectionRes(referenceId)
                            .setReturnUrl(THREE_DS_RETURN_URL)
                            .build()
                    ),
                    object : PaymentListener<PaymentIntent> {
                        override fun onFailed(exception: AirwallexException) {
                            listener.onFailed(exception)
                        }

                        override fun onSuccess(response: PaymentIntent) {
                            if (response.status == PaymentIntentStatus.REQUIRES_CAPTURE || response.nextAction == null) {
                                Logger.debug("Request 3DS Lookup response, doesn't need challenge")
                                listener.onSuccess(response)
                                return
                            }
                            val transactionId = response.nextAction.data?.get("xid") as? String
                            val req = response.nextAction.data?.get("req") as? String
                            val acs = response.nextAction.data?.get("acs") as? String
                            val version = response.latestPaymentAttempt?.authenticationData?.dsData?.version
                                ?: "2.0"

                            Logger.debug("Step 3: Use `ThreeDSecureActivity` to show 3DS UI, then wait user input. After user input, will receive `processorTransactionId`.")
                            val threeDSecureLookup = ThreeDSecureLookup(transactionId, req, acs, version)
                            val fragment = ThreeDSecureFragment.newInstance(activity.fragmentManager)
                            ThreeDSecure.performCardinalAuthentication(fragment, threeDSecureLookup)

                            fragment.threeDSecureCallback = object : ThreeDSecureCallback {
                                override fun onSuccess(processorTransactionId: String) {
                                    Logger.debug("Step 4: Finally call `confirmPaymentIntent` method to send `processorTransactionId` to server to validate")
                                    paymentManager.confirmPaymentIntent(
                                        buildCardPaymentIntentOptions(
                                            params = params,
                                            threeDSecure = PaymentMethodOptions.CardOptions.ThreeDSecure.Builder()
                                                .setTransactionId(processorTransactionId)
                                                .setReturnUrl(THREE_DS_RETURN_URL)
                                                .build()
                                        ),
                                        listener
                                    )
                                }

                                override fun onFailed(exception: AirwallexError) {
                                    listener.onFailed(ThreeDSException(exception))
                                }
                            }
                        }
                    }
                )
            }
        }
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
            AirwallexApiRepository.RetrievePaymentIntentOptions(
                clientSecret = params.clientSecret,
                paymentIntentId = params.paymentIntentId
            ),
            listener
        )
    }

    /**
     * Create a payment method
     *
     * @param params [CreatePaymentMethodParams] used to create the [PaymentMethod]
     * @param listener the callback of create [PaymentMethod]
     */
    @UiThread
    internal fun createPaymentMethod(
        params: CreatePaymentMethodParams,
        listener: PaymentListener<PaymentMethod>
    ) {
        paymentManager.createPaymentMethod(
            AirwallexApiRepository.CreatePaymentMethodOptions(
                clientSecret = params.clientSecret,
                customerId = params.customerId,
                request = PaymentMethodCreateRequest.Builder()
                    .setCustomerId(params.customerId)
                    .setRequestId(UUID.randomUUID().toString())
                    .setType(PaymentMethodType.CARD)
                    .setCard(params.card)
                    .setBilling(params.billing)
                    .build()
            ),
            listener
        )
    }

    /**
     * Retrieve payment methods
     *
     * @param params [RetrievePaymentMethodParams] used to retrieve the [PaymentMethod]
     * @param listener the callback of get [PaymentMethod]
     */
    @UiThread
    internal fun retrievePaymentMethods(
        params: RetrievePaymentMethodParams,
        listener: PaymentListener<PaymentMethodResponse>
    ) {
        paymentManager.retrievePaymentMethods(
            AirwallexApiRepository.RetrievePaymentMethodOptions(
                clientSecret = params.clientSecret,
                customerId = params.customerId,
                pageNum = params.pageNum,
                pageSize = params.pageSize,
                fromCreatedAt = params.fromCreatedAt,
                toCreatedAt = params.toCreatedAt,
                type = params.type
            ),
            listener
        )
    }

    companion object {
        // The default url, that you can change in the constructor for test on different environments
        internal const val BASE_URL = "https://pci-api.airwallex.com"

        private const val PLATFORM = "Android"

        /**
         * Initialize some global configurations, better to be called on Application
         */
        fun initialize(configuration: AirwallexConfiguration) {
            AirwallexPlugins.initialize(configuration)
        }
    }
}
