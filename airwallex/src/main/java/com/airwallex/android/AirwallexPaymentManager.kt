package com.airwallex.android

import android.app.Activity
import com.airwallex.android.Airwallex.PaymentListener
import com.airwallex.android.PaymentManager.Companion.buildCardPaymentIntentOptions
import com.airwallex.android.PaymentManager.Companion.buildWeChatPaymentIntentOptions
import com.airwallex.android.exception.APIException
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.exception.ThreeDSException
import com.airwallex.android.model.*
import com.airwallex.android.view.SelectCurrencyActivityLaunch
import com.airwallex.android.view.ThreeDSecureActivityLaunch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**
 * The implementation of [PaymentManager] to request the payment.
 */
internal class AirwallexPaymentManager(
    private val repository: ApiRepository
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
        CoroutineScope(Dispatchers.IO).launch {
            val result = runCatching {
                requireNotNull(repository.continuePaymentIntent(options))
            }
            withContext(Dispatchers.Main) {
                result.fold(
                    onSuccess = { listener.onSuccess(it) },
                    onFailure = { listener.onFailed(handleError(it)) }
                )
            }
        }
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
        CoroutineScope(Dispatchers.IO).launch {
            val result = runCatching {
                requireNotNull(repository.confirmPaymentIntent(options))
            }
            withContext(Dispatchers.Main) {
                result.fold(
                    onSuccess = { listener.onSuccess(it) },
                    onFailure = { listener.onFailed(handleError(it)) }
                )
            }
        }
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
        CoroutineScope(Dispatchers.IO).launch {
            val result = runCatching {
                requireNotNull(repository.retrievePaymentIntent(options))
            }
            withContext(Dispatchers.Main) {
                result.fold(
                    onSuccess = { listener.onSuccess(it) },
                    onFailure = { listener.onFailed(handleError(it)) }
                )
            }
        }
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
        CoroutineScope(Dispatchers.IO).launch {
            val result = runCatching {
                requireNotNull(repository.createPaymentMethod(options))
            }
            withContext(Dispatchers.Main) {
                result.fold(
                    onSuccess = { listener.onSuccess(it) },
                    onFailure = { listener.onFailed(handleError(it)) }
                )
            }
        }
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
        CoroutineScope(Dispatchers.IO).launch {
            val result = runCatching {
                requireNotNull(repository.retrievePaymentMethods(options))
            }
            withContext(Dispatchers.Main) {
                result.fold(
                    onSuccess = { listener.onSuccess(it) },
                    onFailure = { listener.onFailed(handleError(it)) }
                )
            }
        }
    }

    /**
     * Retrieve paRes with id
     */
    override fun retrieveParesWithId(options: ApiRepository.Options, listener: PaymentListener<ThreeDSecurePares>) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = runCatching {
                requireNotNull(repository.retrieveParesWithId(options))
            }
            withContext(Dispatchers.Main) {
                result.fold(
                    onSuccess = { listener.onSuccess(it) },
                    onFailure = { listener.onFailed(handleError(it)) }
                )
            }
        }
    }

    private fun handleError(throwable: Throwable): AirwallexException {
        return if (throwable is AirwallexException) {
            throwable
        } else {
            APIException(AirwallexError(message = throwable.message))
        }
    }

    /**
     * Confirm [PaymentIntent] with device id
     */
    override fun confirmPaymentIntent(
        activity: Activity,
        deviceId: String,
        params: ConfirmPaymentIntentParams,
        selectCurrencyActivityLaunch: SelectCurrencyActivityLaunch,
        threeDSecureActivityLaunch: ThreeDSecureActivityLaunch,
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
                    ThreeDSecure.Builder()
                        .setReturnUrl(ThreeDSecureManager.THREE_DS_RETURN_URL)
                        .build()
                )
            }
        }

        val paymentListener = when (params.paymentMethodType) {
            PaymentMethodType.CARD -> {
                object : PaymentListener<PaymentIntent> {
                    override fun onFailed(exception: AirwallexException) {
                        // Payment failed
                        listener.onFailed(exception)
                    }

                    override fun onSuccess(response: PaymentIntent) {
                        if (response.nextAction?.type == PaymentIntent.NextActionType.DCC && response.nextAction.dcc != null) {
                            SelectCurrencyManager.selectCurrencyCallback = object : SelectCurrencyCallback {
                                override fun onSuccess(paymentIntent: PaymentIntent) {
                                    SelectCurrencyManager.selectCurrencyCallback = null
                                    listener.onSuccess(paymentIntent)
                                }

                                override fun onFailed(exception: AirwallexError) {
                                    SelectCurrencyManager.selectCurrencyCallback = null
                                    listener.onFailed(APIException(error = exception))
                                }
                            }
                            // DCC flow, please select your currency
                            selectCurrencyActivityLaunch.startForResult(
                                SelectCurrencyActivityLaunch.Args(response.nextAction.dcc, response, params.clientSecret, device)
                            )
                        } else {
                            // Handle next action
                            handleNextAction(activity, threeDSecureActivityLaunch, response, params.clientSecret, device, listener)
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

    override fun continueDccPaymentIntent(activity: Activity, threeDSecureActivityLaunch: ThreeDSecureActivityLaunch, options: ApiRepository.Options, listener: PaymentListener<PaymentIntent>) {
        val paymentListener = object : PaymentListener<PaymentIntent> {
            override fun onFailed(exception: AirwallexException) {
                // Payment failed
                listener.onFailed(exception)
            }

            override fun onSuccess(response: PaymentIntent) {
                // Handle next action
                handleNextAction(activity, threeDSecureActivityLaunch, response, options.clientSecret, (options as AirwallexApiRepository.ContinuePaymentIntentOptions).request.device, listener)
            }
        }
        continuePaymentIntent(options, paymentListener)
    }

    /**
     * Handle 3DS flow - Check jwt if existed
     */
    private fun handleNextAction(activity: Activity, threeDSecureActivityLaunch: ThreeDSecureActivityLaunch, response: PaymentIntent, clientSecret: String, device: Device?, listener: PaymentListener<PaymentIntent>) {
        val serverJwt = response.nextAction?.data?.get("jwt") as? String

        if (serverJwt != null) {
            Logger.debug("Prepare 3DS Flow, serverJwt: $serverJwt")
            // 3D Secure Flow
            handle3DSFlow(activity, threeDSecureActivityLaunch, response.id, clientSecret, serverJwt, device, listener)
        } else {
            Logger.debug("Don't need the 3DS Flow")
            listener.onSuccess(response)
        }
    }

    /**
     * Handle next action for 3ds
     *
     * Step 1: Request `referenceId` with `serverJwt` by Cardinal SDK
     * Step 2: 3DS Enrollment with `referenceId`
     * Step 3: Use `ThreeDSecureActivity` to show 3DS UI, then wait user input. After user input, will receive `processorTransactionId`.
     * Step 4: 3DS Validate with `processorTransactionId`
     *
     * @param activity the `Activity` that is to start 3ds screen
     * @param threeDSecureActivityLaunch instance of [ThreeDSecureActivityLaunch]
     * @param paymentIntentId the ID of the [PaymentIntent], required.
     * @param clientSecret the clientSecret of [PaymentIntent], required.
     * @param serverJwt for perform 3ds flow
     * @param device device info
     * @param listener a [PaymentListener] to receive the response or error
     */
    override fun handle3DSFlow(activity: Activity, threeDSecureActivityLaunch: ThreeDSecureActivityLaunch, paymentIntentId: String, clientSecret: String, serverJwt: String, device: Device?, listener: PaymentListener<PaymentIntent>) {
        Logger.debug("Step 1: Request `referenceId` with `serverJwt` by Cardinal SDK")
        val applicationContext = activity.applicationContext
        ThreeDSecureManager.performCardinalInitialize(
            applicationContext,
            serverJwt
        ) { referenceId, validateResponse ->
            if (validateResponse != null) {
                activity.runOnUiThread {
                    listener.onFailed(ThreeDSException(AirwallexError(message = validateResponse.errorDescription)))
                }
            } else {
                Logger.debug("Step2: 3DS Enrollment with `referenceId`")
                continuePaymentIntent(
                    build3DSContinuePaymentIntentOptions(device, paymentIntentId, clientSecret, PaymentIntentContinueType.ENROLLMENT,
                        ThreeDSecure.Builder()
                            .setDeviceDataCollectionRes(referenceId)
                            .build()
                    ),
                    object : PaymentListener<PaymentIntent> {
                        override fun onFailed(exception: AirwallexException) {
                            listener.onFailed(exception)
                        }

                        override fun onSuccess(response: PaymentIntent) {
                            if (response.status == PaymentIntentStatus.REQUIRES_CAPTURE || response.nextAction == null) {
                                Logger.debug("3DS Enrollment finished, doesn't need challenge. Status: ${response.status}, NextAction: ${response.nextAction}")
                                listener.onSuccess(response)
                                return
                            }
                            val transactionId = response.nextAction.data?.get("xid") as? String
                            val req = response.nextAction.data?.get("req") as? String
                            val acs = response.nextAction.data?.get("acs") as? String
                            val version = response.latestPaymentAttempt?.authenticationData?.dsData?.version
                                ?: "2.0"

                            Logger.debug("Step 3: Use `ThreeDSecureActivity` to show 3DS UI, then wait user input. After user input, will receive `processorTransactionId`.")
                            ThreeDSecureManager.threeDSecureCallback = object : ThreeDSecureCallback {
                                private fun continuePaymentIntent(transactionId: String) {
                                    Logger.debug("Step 4: 3DS Validate with `processorTransactionId`")
                                    continuePaymentIntent(
                                        build3DSContinuePaymentIntentOptions(device, paymentIntentId, clientSecret, PaymentIntentContinueType.VALIDATE,
                                            ThreeDSecure.Builder()
                                                .setTransactionId(transactionId)
                                                .build()
                                        ),
                                        listener
                                    )
                                }

                                override fun onThreeDS1Success(payload: String) {
                                    Logger.debug("3DS1 Success, Retrieve pares with paResId start...")
                                    ThreeDSecureManager.threeDSecureCallback = null
                                    retrieveParesWithId(AirwallexApiRepository.RetrievePaResOptions(clientSecret, payload), object : PaymentListener<ThreeDSecurePares> {
                                        override fun onFailed(exception: AirwallexException) {
                                            Logger.debug("Retrieve pares with paResId failed", exception)
                                            listener.onFailed(exception)
                                        }

                                        override fun onSuccess(response: ThreeDSecurePares) {
                                            Logger.debug("Retrieve pares with paResId success. Rares ${response.pares}")
                                            continuePaymentIntent(response.pares)
                                        }
                                    })
                                }

                                override fun onThreeDS2Success(transactionId: String) {
                                    Logger.debug("3DS2 Success, Continue PaymentIntent start...")
                                    ThreeDSecureManager.threeDSecureCallback = null
                                    continuePaymentIntent(transactionId)
                                }

                                override fun onFailed(exception: AirwallexError) {
                                    Logger.debug("3DS Failed, Reason ${exception.message}")
                                    ThreeDSecureManager.threeDSecureCallback = null
                                    listener.onFailed(ThreeDSException(exception))
                                }
                            }

                            val threeDSecureLookup = ThreeDSecureLookup(transactionId, req, acs, version)
                            threeDSecureActivityLaunch.startForResult(ThreeDSecureActivityLaunch.Args(threeDSecureLookup))
                        }
                    }
                )
            }
        }
    }

    private fun build3DSContinuePaymentIntentOptions(
        device: Device?,
        paymentIntentId: String,
        clientSecret: String,
        type: PaymentIntentContinueType,
        threeDSecure: ThreeDSecure
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
}
