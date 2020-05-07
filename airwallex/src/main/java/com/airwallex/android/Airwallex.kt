package com.airwallex.android

import androidx.annotation.UiThread
import androidx.fragment.app.FragmentActivity
import com.airwallex.android.ThreeDSecure.THREE_DS_RETURN_URL
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.exception.ThreeDSException
import com.airwallex.android.model.*
import com.cardinalcommerce.cardinalmobilesdk.models.ValidateResponse
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
        activity: FragmentActivity,
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
                        .setCookiesAccepted("true")
                        .setHostName("www.airwallex.com")
                        .setHttpBrowserType("chrome")
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
                                    val jwt = response.nextAction?.data?.get("jwt") as? String

                                    if (jwt != null) {
                                        Logger.debug("Prepare 3DS Flow, jwt: $jwt")
                                        // 3D Secure Flow
                                        prepareThreeDSecureFlow(
                                            activity = activity,
                                            params = params,
                                            jwt = jwt,
                                            listener = listener
                                        )
                                    } else {
                                        Logger.debug("Don't need the 3DS")
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
     * Prepare 3DS Flow
     */
    private fun prepareThreeDSecureFlow(
        activity: FragmentActivity,
        params: ConfirmPaymentIntentParams,
        jwt: String,
        listener: PaymentListener<PaymentIntent>
    ) {
        ThreeDSecure.performCardinalInitialize(
            activity.applicationContext,
            jwt
        ) { referenceId, validateResponse ->
            if (validateResponse != null) {
                activity.runOnUiThread {
                    listener.onFailed(ThreeDSException(AirwallexError(message = validateResponse.errorDescription)))
                }
            } else {
                // Request 3DS lookup response
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
                            if (response.nextAction == null) {
                                // No need to step-up authentication
                                listener.onSuccess(response)
                                return
                            }
                            val transactionId = response.nextAction.data?.get("xid") as? String
                            val req = response.nextAction.data?.get("req") as? String
                            val acs = response.nextAction.data?.get("acs") as? String
                            val dsData =
                                requireNotNull(response.latestPaymentAttempt?.authenticationData?.dsData)

                            val threeDSecureLookup =
                                ThreeDSecureLookup(transactionId, req, acs, dsData)

                            val fragment = ThreeDSecureFragment.newInstance(activity)
                            fragment.threeDSecureCallback = object : ThreeDSecureCallback {
                                override fun onSuccess(validateResponse: ValidateResponse) {
                                    // Authorization transactionId
                                    paymentManager.confirmPaymentIntent(
                                        buildCardPaymentIntentOptions(
                                            params = params,
                                            threeDSecure = PaymentMethodOptions.CardOptions.ThreeDSecure.Builder()
                                                .setTransactionId(validateResponse.payment?.processorTransactionId)
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

                            ThreeDSecure.performCardinalAuthentication(
                                fragment,
                                threeDSecureLookup
                            )
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

        /**
         * Initialize some global configurations, better to be called on Application
         */
        fun initialize(configuration: AirwallexConfiguration) {
            AirwallexPlugins.initialize(configuration)
        }
    }
}
