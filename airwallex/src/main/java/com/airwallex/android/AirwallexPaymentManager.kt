package com.airwallex.android

import androidx.fragment.app.FragmentActivity
import com.airwallex.android.Airwallex.PaymentListener
import com.airwallex.android.PaymentManager.Companion.buildCardPaymentIntentOptions
import com.airwallex.android.PaymentManager.Companion.buildWeChatPaymentIntentOptions
import com.airwallex.android.exception.APIException
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.exception.ThreeDSException
import com.airwallex.android.model.*
import com.airwallex.android.view.SelectCurrencyActivityLaunch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.*

/**
 * The implementation of [PaymentManager] to request the payment.
 */
internal class AirwallexPaymentManager(
    private val repository: ApiRepository,
    private val workScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : PaymentManager {

    /**
     * Continue the [PaymentIntent] using [ApiRepository.Options], used for 3DS
     *
     * @param options contains the confirm [PaymentIntent] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    override fun continuePaymentIntent(
        options: ApiRepository.Options,
        listener: PaymentListener<PaymentIntent>
    ) {
        executeApiOperation(ApiOperationType.CONFIRM_CONTINUE_PAYMENT_INTENT, options, listener)
    }

    /**
     * Confirm the [PaymentIntent] using [ApiRepository.Options]
     *
     * @param options contains the confirm [PaymentIntent] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    override fun confirmPaymentIntent(
        options: ApiRepository.Options,
        listener: PaymentListener<PaymentIntent>
    ) {
        executeApiOperation(ApiOperationType.CONFIRM_PAYMENT_INTENT, options, listener)
    }

    /**
     * Retrieve the [PaymentIntent] using [ApiRepository.Options]
     *
     * @param options contains the retrieve [PaymentIntent] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    override fun retrievePaymentIntent(
        options: ApiRepository.Options,
        listener: PaymentListener<PaymentIntent>
    ) {
        executeApiOperation(ApiOperationType.RETRIEVE_PAYMENT_INTENT, options, listener)
    }

    /**
     * Create a Airwallex [PaymentMethod] using [ApiRepository.Options]
     *
     * @param options contains the create [PaymentMethod] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    override fun createPaymentMethod(
        options: ApiRepository.Options,
        listener: PaymentListener<PaymentMethod>
    ) {
        executeApiOperation(ApiOperationType.CREATE_PAYMENT_METHOD, options, listener)
    }

    /**
     * Retrieve all of the customer's [PaymentMethod] using [ApiRepository.Options]
     *
     * @param options contains the retrieve [PaymentMethod] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    override fun retrievePaymentMethods(
        options: ApiRepository.Options,
        listener: PaymentListener<PaymentMethodResponse>
    ) {
        executeApiOperation(ApiOperationType.RETRIEVE_PAYMENT_METHOD, options, listener)
    }

    override fun confirmPaymentIntentWithDeviceId(
        activity: FragmentActivity,
        deviceId: String,
        params: ConfirmPaymentIntentParams,
        listener: PaymentListener<PaymentIntent>
    ) {
        val applicationContext = activity.applicationContext
        val device = PaymentManager.buildDeviceInfo(deviceId, applicationContext)
        val options = when (params.paymentMethodType) {
            PaymentMethodType.WECHAT -> {
                buildWeChatPaymentIntentOptions(params, device)
            }
            PaymentMethodType.CARD -> {
                buildCardPaymentIntentOptions(device, params,
                    PaymentMethodOptions.CardOptions.ThreeDSecure.Builder()
                        .setReturnUrl(ThreeDSecure.THREE_DS_RETURN_URL)
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
                        if (response.nextAction?.type == PaymentIntent.NextActionType.DCC && response.nextAction.dcc != null) {
                            SelectCurrencyActivityLaunch(activity).startForResult(
                                SelectCurrencyActivityLaunch.Args(response.nextAction.dcc, response, params.clientSecret, device)
                            )
                        } else {
                            handle3DSFlow(activity, response, params.clientSecret, device, listener)
                        }
                    }
                }
            }
            PaymentMethodType.WECHAT -> {
                listener
            }
        }

        confirmPaymentIntent(options, paymentListener)
    }

    override fun continueDccPaymentIntent(activity: FragmentActivity, options: ApiRepository.Options, listener: PaymentListener<PaymentIntent>) {
        val paymentListener = object : PaymentListener<PaymentIntent> {
            override fun onFailed(exception: AirwallexException) {
                listener.onFailed(exception)
            }

            override fun onSuccess(response: PaymentIntent) {
                handle3DSFlow(activity, response, options.clientSecret, (options as AirwallexApiRepository.ContinuePaymentIntentOptions).request.device, listener)
            }
        }
        continuePaymentIntent(options, paymentListener)
    }

    private fun handle3DSFlow(activity: FragmentActivity, response: PaymentIntent, clientSecret: String, device: Device, listener: PaymentListener<PaymentIntent>) {
        val serverJwt = response.nextAction?.data?.get("jwt") as? String

        if (serverJwt != null) {
            Logger.debug("Prepare 3DS Flow, serverJwt: $serverJwt")
            // 3D Secure Flow
            handleNextAction(activity, response.id, clientSecret, serverJwt, device, listener)
        } else {
            Logger.debug("Don't need the 3DS Flow")
            listener.onSuccess(response)
        }
    }

    /**
     * Handle next action for 3ds
     *
     * Step 1: Request `referenceId` with `serverJwt` by Cardinal SDK
     * Step 2: Request 3DS lookup response by `confirmPaymentIntent` with `referenceId`
     * Step 3: Use `ThreeDSecureActivity` to show 3DS UI, then wait user input. After user input, will receive `processorTransactionId`.
     * Step 4: Finally call `confirmPaymentIntent` method to send `processorTransactionId` to server to validate
     *
     * @param activity the `Activity` that is to start 3ds screen
     * @param paymentIntentId the ID of the [PaymentIntent], required.
     * @param clientSecret the clientSecret of [PaymentIntent], required.
     * @param serverJwt for perform 3ds flow
     * @param device device info
     * @param listener a [PaymentListener] to receive the response or error
     */
    override fun handleNextAction(activity: FragmentActivity, paymentIntentId: String, clientSecret: String, serverJwt: String, device: Device, listener: PaymentListener<PaymentIntent>) {
        Logger.debug("Step 1: Request `referenceId` with `serverJwt` by Cardinal SDK")
        val applicationContext = activity.applicationContext
        ThreeDSecure.performCardinalInitialize(
            applicationContext,
            serverJwt
        ) { referenceId, validateResponse ->
            if (validateResponse != null) {
                activity.runOnUiThread {
                    listener.onFailed(ThreeDSException(AirwallexError(message = validateResponse.errorDescription)))
                }
            } else {
                Logger.debug("Request 3DS lookup response by `confirmPaymentIntent` with `referenceId`")
                continuePaymentIntent(
                    build3DSContinuePaymentIntentOptions(device, paymentIntentId, clientSecret, PaymentIntentContinueType.ENROLLMENT,
                        PaymentMethodOptions.CardOptions.ThreeDSecure.Builder()
                            .setDeviceDataCollectionRes(referenceId)
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
                            val fragment = ThreeDSecureFragment.newInstance(activity.supportFragmentManager)
                            ThreeDSecure.performCardinalAuthentication(fragment, threeDSecureLookup)

                            fragment.threeDSecureCallback = object : ThreeDSecureCallback {
                                override fun onSuccess(transactionId: String) {
                                    Logger.debug("Step 4: Finally call `confirmPaymentIntent` method to send `processorTransactionId` to server to validate")
                                    continuePaymentIntent(
                                        build3DSContinuePaymentIntentOptions(device, paymentIntentId, clientSecret, PaymentIntentContinueType.VALIDATE,
                                            PaymentMethodOptions.CardOptions.ThreeDSecure.Builder()
                                                .setTransactionId(transactionId)
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

    private fun build3DSContinuePaymentIntentOptions(
        device: Device,
        paymentIntentId: String,
        clientSecret: String,
        type: PaymentIntentContinueType,
        threeDSecure: PaymentMethodOptions.CardOptions.ThreeDSecure
    ): AirwallexApiRepository.ContinuePaymentIntentOptions {
        val request = PaymentIntentContinueRequest(
            requestId = UUID.randomUUID().toString(),
            type = type,
            threeDSecure = threeDSecure,
            device = device
        )
        return AirwallexApiRepository.ContinuePaymentIntentOptions(
            clientSecret = clientSecret,
            paymentIntentId = paymentIntentId,
            request = request
        )
    }

    private fun <T> executeApiOperation(
        apiOperationType: ApiOperationType,
        options: ApiRepository.Options,
        listener: PaymentListener<T>
    ) {
        AirwallexApiOperation(
            options,
            repository,
            workScope,
            apiOperationType,
            object : ApiExecutor.ApiResponseListener<AirwallexHttpResponse> {
                override fun onSuccess(response: AirwallexHttpResponse) {
                    if (response.isSuccessful && response.body != null) {
                        val result: T = AirwallexPlugins.gson.fromJson(
                            response.body.string(),
                            classType(apiOperationType)
                        )
                        listener.onSuccess(result)
                    } else {
                        val error = if (response.body != null) AirwallexPlugins.gson.fromJson(
                            response.body.string(),
                            AirwallexError::class.java
                        ) else AirwallexError(message = "Unknown error")
                        listener.onFailed(
                            APIException(
                                error = error,
                                traceId = response.allHeaders["x-awx-traceid"],
                                statusCode = response.statusCode,
                                message = response.message
                            )
                        )
                    }
                }

                override fun onError(e: AirwallexException) {
                    listener.onFailed(e)
                }
            }
        ).execute()
    }

    private class AirwallexApiOperation(
        private val options: ApiRepository.Options,
        private val repository: ApiRepository,
        workScope: CoroutineScope,
        private val apiOperationType: ApiOperationType,
        listener: ApiResponseListener<AirwallexHttpResponse>
    ) : ApiExecutor<AirwallexHttpResponse>(workScope, listener) {

        override suspend fun getResponse(): AirwallexHttpResponse? {
            return when (apiOperationType) {
                ApiOperationType.CONFIRM_CONTINUE_PAYMENT_INTENT -> {
                    repository.continuePaymentIntent(options)
                }
                ApiOperationType.CONFIRM_PAYMENT_INTENT -> {
                    repository.confirmPaymentIntent(options)
                }
                ApiOperationType.RETRIEVE_PAYMENT_INTENT -> {
                    repository.retrievePaymentIntent(options)
                }
                ApiOperationType.CREATE_PAYMENT_METHOD -> {
                    repository.createPaymentMethod(options)
                }
                ApiOperationType.RETRIEVE_PAYMENT_METHOD -> {
                    repository.retrievePaymentMethods(options)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> classType(type: ApiOperationType): Class<T> {
        return when (type) {
            ApiOperationType.CONFIRM_CONTINUE_PAYMENT_INTENT,
            ApiOperationType.CONFIRM_PAYMENT_INTENT,
            ApiOperationType.RETRIEVE_PAYMENT_INTENT -> {
                PaymentIntent::class.java as Class<T>
            }
            ApiOperationType.CREATE_PAYMENT_METHOD -> {
                PaymentMethod::class.java as Class<T>
            }
            ApiOperationType.RETRIEVE_PAYMENT_METHOD -> {
                PaymentMethodResponse::class.java as Class<T>
            }
        }
    }

    internal enum class ApiOperationType {
        CONFIRM_CONTINUE_PAYMENT_INTENT,
        CONFIRM_PAYMENT_INTENT,
        RETRIEVE_PAYMENT_INTENT,
        CREATE_PAYMENT_METHOD,
        RETRIEVE_PAYMENT_METHOD
    }
}
