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
                    com.airwallex.android.model.ThreeDSecure.Builder()
                        .setReturnUrl(ThreeDSecure.THREE_DS_RETURN_URL)
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
                            // DCC flow, please select your currency
                            SelectCurrencyActivityLaunch(activity).startForResult(
                                SelectCurrencyActivityLaunch.Args(response.nextAction.dcc, response, params.clientSecret, device)
                            )
                        } else {
                            // Handle next action
                            handleNextAction(activity, response, params.clientSecret, device, listener)
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
                // Payment failed
                listener.onFailed(exception)
            }

            override fun onSuccess(response: PaymentIntent) {
                // Handle next action
                handleNextAction(activity, response, options.clientSecret, (options as AirwallexApiRepository.ContinuePaymentIntentOptions).request.device, listener)
            }
        }
        continuePaymentIntent(options, paymentListener)
    }

    /**
     * Handle 3DS flow - Check jwt if existed
     */
    private fun handleNextAction(activity: FragmentActivity, response: PaymentIntent, clientSecret: String, device: Device?, listener: PaymentListener<PaymentIntent>) {
        val serverJwt = response.nextAction?.data?.get("jwt") as? String

        if (serverJwt != null) {
            Logger.debug("Prepare 3DS Flow, serverJwt: $serverJwt")
            // 3D Secure Flow
            handle3DSFlow(activity, response.id, clientSecret, serverJwt, device, listener)
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
     * @param paymentIntentId the ID of the [PaymentIntent], required.
     * @param clientSecret the clientSecret of [PaymentIntent], required.
     * @param serverJwt for perform 3ds flow
     * @param device device info
     * @param listener a [PaymentListener] to receive the response or error
     */
    override fun handle3DSFlow(activity: FragmentActivity, paymentIntentId: String, clientSecret: String, serverJwt: String, device: Device?, listener: PaymentListener<PaymentIntent>) {
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
                Logger.debug("Step2: 3DS Enrollment with `referenceId`")
                continuePaymentIntent(
                    build3DSContinuePaymentIntentOptions(device, paymentIntentId, clientSecret, PaymentIntentContinueType.ENROLLMENT,
                        com.airwallex.android.model.ThreeDSecure.Builder()
                            .setDeviceDataCollectionRes(referenceId)
                            .build()
                    ),
                    object : PaymentListener<PaymentIntent> {
                        override fun onFailed(exception: AirwallexException) {
                            listener.onFailed(exception)
                        }

                        override fun onSuccess(response: PaymentIntent) {
                            if (response.status == PaymentIntentStatus.REQUIRES_CAPTURE || response.nextAction == null) {
                                Logger.debug("3DS Enrollment finished, doesn't need challenge")
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
                                override fun onSuccess(paResId: String, threeDSecureType: ThreeDSecure.ThreeDSecureType) {
                                    fun continuePaymentIntent(transactionId: String) {
                                        Logger.debug("Step 4: 3DS Validate with `processorTransactionId`")
                                        continuePaymentIntent(
                                            build3DSContinuePaymentIntentOptions(device, paymentIntentId, clientSecret, PaymentIntentContinueType.VALIDATE,
                                                com.airwallex.android.model.ThreeDSecure.Builder()
                                                    .setTransactionId(transactionId)
                                                    .build()
                                            ),
                                            listener
                                        )
                                    }

                                    if (threeDSecureType == ThreeDSecure.ThreeDSecureType.THREE_D_SECURE_1) {
                                        Logger.debug("Start retrieve pares with paResId")
                                        retrieveParesWithId(AirwallexApiRepository.RetrievePaResOptions(clientSecret, paResId), object : PaymentListener<ThreeDSecurePares> {
                                            override fun onFailed(exception: AirwallexException) {
                                                listener.onFailed(exception)
                                            }

                                            override fun onSuccess(response: ThreeDSecurePares) {
                                                continuePaymentIntent(response.pares)
                                            }
                                        })
                                    } else {
                                        continuePaymentIntent(paResId)
                                    }
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
        device: Device?,
        paymentIntentId: String,
        clientSecret: String,
        type: PaymentIntentContinueType,
        threeDSecure: com.airwallex.android.model.ThreeDSecure
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
