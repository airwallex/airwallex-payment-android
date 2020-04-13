package com.airwallex.android

import android.app.Activity
import android.content.Intent
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

    private var activity: Activity? = null
    private var confirmPaymentIntentParams: ConfirmPaymentIntentParams? = null
    private var listener: PaymentListener<PaymentIntent>? = null

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
        this.activity = activity
        this.confirmPaymentIntentParams = params
        this.listener = listener
        val options = when (params.paymentMethodType) {
            PaymentMethodType.WECHAT -> {
                AirwallexApiRepository.PaymentIntentOptions(
                    clientSecret = params.clientSecret,
                    paymentIntentId = params.paymentIntentId,
                    paymentIntentConfirmRequest = PaymentIntentConfirmRequest.Builder(
                        requestId = UUID.randomUUID().toString()
                    )
                        .setPaymentMethod(
                            PaymentMethod.Builder()
                                .setType(PaymentMethodType.WECHAT)
                                .setWechatPayFlow(WechatPayRequest(WechatPayRequestFlow.INAPP))
                                .build()
                        )
                        .setCustomerId(params.customerId)
                        .build()
                )
            }
            PaymentMethodType.CARD -> {
                buildPaymentIntentOptions(
                    params = params,
                    threeDs = PaymentMethodOptions.CardOptions.ThreeDs.Builder()
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
                            // 3DS Flow
                            prepareCardinalLookup(
                                activity = activity,
                                jwt = jwt
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

        paymentManager.confirmPaymentIntent(options, paymentListener)
    }

    /**
     * Perform 3ds flow
     */
    private fun prepareCardinalLookup(
        activity: Activity,
        jwt: String
    ) {
        ThreeDSecure.performVerification(activity, jwt) { referenceId, validateResponse ->
            if (validateResponse != null) {
                activity.runOnUiThread {
                    listener?.onFailed(ThreeDSException(AirwallexError(message = validateResponse.errorDescription)))
                }
            } else {
                paymentManager.confirmPaymentIntent(
                    buildPaymentIntentOptions(
                        params = requireNotNull(confirmPaymentIntentParams),
                        threeDs = PaymentMethodOptions.CardOptions.ThreeDs.Builder()
                            .setDeviceDataCollectionRes(referenceId)
                            .setReturnUrl(THREE_DS_RETURN_URL)
                            .build()
                    ),
                    object : PaymentListener<PaymentIntent> {
                        override fun onFailed(exception: AirwallexException) {
                            listener?.onFailed(exception)
                        }

                        override fun onSuccess(response: PaymentIntent) {
                            if (response.nextAction == null) {
                                listener?.onFailed(ThreeDSException(AirwallexError(message = "Frictionless card")))
                                return
                            }
                            val transactionId = response.nextAction.data?.get("xid") as? String
                            val req = response.nextAction.data?.get("req") as? String
                            val acs = response.nextAction.data?.get("acs") as? String
                            val dsData = response.latestPaymentAttempt?.authData?.dsData

                            val threeDSecureLookup =
                                ThreeDSecureLookup(
                                    requireNotNull(transactionId),
                                    requireNotNull(req),
                                    requireNotNull(acs),
                                    requireNotNull(dsData)
                                )
                            ThreeDSecure.performCardinalAuthentication(
                                activity,
                                threeDSecureLookup
                            )
                        }
                    }
                )
            }
        }
    }

    fun handleConfirmPaymentIntentResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode != Activity.RESULT_OK || requestCode != ThreeDSecureActivity.THREE_D_SECURE) {
            return
        }
        ThreeDSecure.onActivityResult(data) { validateResponse, exception ->
            if (exception != null) {
                activity?.runOnUiThread {
                    listener?.onFailed(exception)
                }
            } else {
                // Confirm transactionId
                paymentManager.confirmPaymentIntent(
                    buildPaymentIntentOptions(
                        params = requireNotNull(confirmPaymentIntentParams),
                        threeDs = PaymentMethodOptions.CardOptions.ThreeDs.Builder()
                            .setDsTransactionId(validateResponse.payment.processorTransactionId)
                            .setReturnUrl(THREE_DS_RETURN_URL)
                            .build()
                    ),
                    object : PaymentListener<PaymentIntent> {
                        override fun onFailed(exception: AirwallexException) {
                            listener?.onFailed(exception)
                        }

                        override fun onSuccess(response: PaymentIntent) {
                            listener?.onSuccess(response)
                        }
                    }
                )
            }
        }
    }

    private fun buildPaymentIntentOptions(
        params: ConfirmPaymentIntentParams,
        threeDs: PaymentMethodOptions.CardOptions.ThreeDs
    ): AirwallexApiRepository.PaymentIntentOptions {
        return AirwallexApiRepository.PaymentIntentOptions(
            clientSecret = params.clientSecret,
            paymentIntentId = params.paymentIntentId,
            paymentIntentConfirmRequest = PaymentIntentConfirmRequest.Builder(
                requestId = UUID.randomUUID().toString()
            )
                .setPaymentMethodOptions(
                    PaymentMethodOptions.Builder()
                        .setCardOptions(
                            PaymentMethodOptions.CardOptions.Builder()
                                .setAutoCapture(true)
                                .setThreeDs(threeDs).build()
                        )
                        .build()
                )
                .setPaymentMethodReference(requireNotNull(params.paymentMethodReference))
                .setCustomerId(params.customerId)
                .build()
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
                paymentMethodParams = PaymentMethodParams.Builder()
                    .setCustomerId(params.customerId)
                    .setRequestId(UUID.randomUUID().toString())
                    .setType(PaymentMethodType.CARD.value)
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
                pageSize = params.pageSize
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
