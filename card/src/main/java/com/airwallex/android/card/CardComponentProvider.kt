package com.airwallex.android.card

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.airwallex.android.card.exception.DccException
import com.airwallex.android.card.exception.ThreeDSException
import com.airwallex.android.card.view.DccActivityLaunch
import com.airwallex.android.card.view.ThreeDSecureActivity
import com.airwallex.android.card.view.ThreeDSecureActivityLaunch
import com.airwallex.android.core.*
import com.airwallex.android.core.Airwallex.PaymentListener
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.log.Logger
import com.airwallex.android.core.model.*
import com.cardinalcommerce.cardinalmobilesdk.models.CardinalActionCode
import com.cardinalcommerce.cardinalmobilesdk.models.ValidateResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class CardComponentProvider : ActionComponentProvider<CardComponent> {

    private var dccCallback: DccCallback? = null
    private var threeDSecureCallback: ThreeDSecureCallback? = null

    override fun handlePaymentIntentResponse(
        nextAction: NextAction?,
        cardNextActionModel: ComponentProvider.CardNextActionModel?,
        listener: PaymentListener<PaymentIntent>
    ) {
        if (cardNextActionModel == null) {
            listener.onFailed(AirwallexCheckoutException(message = "No required card model"))
            return
        }

        val fragment = cardNextActionModel.fragment
        val activity = cardNextActionModel.activity

        val dccActivityLaunch: DccActivityLaunch
        val threeDSecureActivityLaunch: ThreeDSecureActivityLaunch
        val applicationContext: Context
        if (fragment != null) {
            dccActivityLaunch = DccActivityLaunch(fragment)
            threeDSecureActivityLaunch = ThreeDSecureActivityLaunch(fragment)
            applicationContext = fragment.requireContext().applicationContext
        } else {
            dccActivityLaunch = DccActivityLaunch(activity)
            threeDSecureActivityLaunch = ThreeDSecureActivityLaunch(activity)
            applicationContext = activity.applicationContext
        }

        when {
            // DCC flow
            nextAction?.type == NextAction.NextActionType.DCC && nextAction.dcc != null -> {
                dccCallback = object : DccCallback {
                    override fun onSuccess(paymentIntent: PaymentIntent) {
                        dccCallback = null
                        listener.onSuccess(paymentIntent)
                    }

                    override fun onFailed(exception: Exception) {
                        dccCallback = null
                        listener.onFailed(AirwallexCheckoutException(e = exception))
                    }
                }

                // DCC flow, please select your currency
                dccActivityLaunch.startForResult(
                    DccActivityLaunch.Args(
                        dcc = requireNotNull(nextAction.dcc),
                        paymentIntentId = cardNextActionModel.paymentIntentId,
                        currency = cardNextActionModel.currency,
                        amount = cardNextActionModel.amount,
                        clientSecret = cardNextActionModel.clientSecret
                    )
                )
            }
            // Handle 3DS flow - Check jwt if existed
            nextAction?.data?.get("jwt") != null -> {
                val serverJwt = nextAction.data?.get("jwt") as String
                Logger.debug("Prepare 3DS Flow, serverJwt: $serverJwt")
                // 3D Secure Flow
                Tracker.track(
                    TrackerRequest.Builder()
                        .setCode(TrackerRequest.TrackerCode.ON_CHALLENGE)
                        .setNextActionType(nextAction.type?.value)
                        .setNextActionUrl(nextAction.url)
                        .build()
                )
                handle3DSFlow(
                    applicationContext = applicationContext,
                    threeDSecureActivityLaunch = threeDSecureActivityLaunch,
                    paymentIntentId = cardNextActionModel.paymentIntentId,
                    clientSecret = cardNextActionModel.clientSecret,
                    serverJwt = serverJwt,
                    device = cardNextActionModel.device,
                    paymentManager = cardNextActionModel.paymentManager,
                    object : PaymentListener<PaymentIntent> {
                        override fun onFailed(exception: AirwallexException) {
                            Tracker.track(
                                TrackerRequest.Builder()
                                    .setCode(TrackerRequest.TrackerCode.ON_CHALLENGE_ERROR)
                                    .setError(exception.localizedMessage)
                                    .build()
                            )
                            listener.onFailed(exception)
                        }

                        override fun onSuccess(response: PaymentIntent) {
                            Tracker.track(
                                TrackerRequest.Builder()
                                    .setCode(TrackerRequest.TrackerCode.ON_CHALLENGE_SUCCESS)
                                    .build()
                            )
                            listener.onSuccess(response)
                        }
                    }
                )
            }
            else -> {
                Logger.debug("Don't need the 3DS Flow")
                cardNextActionModel.paymentManager.startOperation(
                    AirwallexApiRepository.RetrievePaymentIntentOptions(
                        clientSecret = cardNextActionModel.clientSecret,
                        paymentIntentId = cardNextActionModel.paymentIntentId
                    ),
                    listener
                )
            }
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
     * @param applicationContext the Application Context that is to start 3ds screen
     * @param threeDSecureActivityLaunch instance of [ThreeDSecureActivityLaunch]
     * @param paymentIntentId the ID of the [PaymentIntent], required.
     * @param clientSecret the clientSecret of [PaymentIntent], required.
     * @param serverJwt for perform 3ds flow
     * @param device device info
     * @param paymentManager instance of [PaymentManager]
     * @param listener a [PaymentListener] to receive the response or error
     */
    private fun handle3DSFlow(
        applicationContext: Context,
        threeDSecureActivityLaunch: ThreeDSecureActivityLaunch,
        paymentIntentId: String,
        clientSecret: String,
        serverJwt: String,
        device: Device?,
        paymentManager: PaymentManager,
        listener: PaymentListener<PaymentIntent>
    ) {
        Logger.debug("Step 1: Request `referenceId` with `serverJwt` by Cardinal SDK")
        ThreeDSecureManager.performCardinalInitialize(
            applicationContext,
            serverJwt
        ) { referenceId, validateResponse ->
            if (validateResponse != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    listener.onFailed(ThreeDSException(message = validateResponse.errorDescription))
                }
            } else {
                Logger.debug("Step2: 3DS Enrollment with `referenceId`")
                paymentManager.startOperation(
                    build3DSContinuePaymentIntentOptions(
                        device, paymentIntentId, clientSecret, PaymentIntentContinueType.ENROLLMENT,
                        ThreeDSecure.Builder()
                            .setDeviceDataCollectionRes(referenceId)
                            .build()
                    ),
                    object : PaymentListener<PaymentIntent> {
                        override fun onFailed(exception: AirwallexException) {
                            listener.onFailed(exception)
                        }

                        override fun onSuccess(response: PaymentIntent) {
                            val nextAction = response.nextAction
                            if (response.status == PaymentIntentStatus.REQUIRES_CAPTURE || nextAction == null) {
                                Logger.debug("3DS Enrollment finished, doesn't need challenge. Status: ${response.status}, NextAction: ${response.nextAction}")
                                listener.onSuccess(response)
                                return
                            }

                            Logger.debug("Handle nextAction ${response.nextAction}")

                            val transactionId = nextAction.data?.get("xid") as? String
                            val req = nextAction.data?.get("req") as? String
                            val acs = nextAction.data?.get("acs") as? String
                            val version =
                                response.latestPaymentAttempt?.authenticationData?.dsData?.version
                                    ?: "2.0"

                            Logger.debug("Step 3: Use `ThreeDSecureActivity` to show 3DS UI, then wait user input. After user input, will receive `processorTransactionId`.")
                            threeDSecureCallback = object : ThreeDSecureCallback {
                                private fun continuePaymentIntent(transactionId: String) {
                                    Logger.debug("Step 4: 3DS Validate with `processorTransactionId`")
                                    paymentManager.startOperation(
                                        build3DSContinuePaymentIntentOptions(
                                            device,
                                            paymentIntentId,
                                            clientSecret,
                                            PaymentIntentContinueType.VALIDATE,
                                            ThreeDSecure.Builder()
                                                .setTransactionId(transactionId)
                                                .build()
                                        ),
                                        listener
                                    )
                                }

                                override fun onThreeDS1Success(payload: String) {
                                    Logger.debug("3DS1 Success, Retrieve pares with paResId start...")
                                    threeDSecureCallback = null
                                    paymentManager.startOperation(
                                        AirwallexApiRepository.RetrievePaResOptions(
                                            clientSecret,
                                            payload
                                        ),
                                        object : PaymentListener<ThreeDSecurePares> {
                                            override fun onFailed(exception: AirwallexException) {
                                                Logger.debug(
                                                    "Retrieve pares with paResId failed",
                                                    exception
                                                )
                                                listener.onFailed(exception)
                                            }

                                            override fun onSuccess(response: ThreeDSecurePares) {
                                                Logger.debug("Retrieve pares with paResId success. Rares ${response.pares}")
                                                continuePaymentIntent(response.pares)
                                            }
                                        }
                                    )
                                }

                                override fun onThreeDS2Success(transactionId: String) {
                                    Logger.debug("3DS2 Success, Continue PaymentIntent start...")
                                    threeDSecureCallback = null
                                    continuePaymentIntent(transactionId)
                                }

                                override fun onFailed(exception: Exception) {
                                    Logger.debug("3DS Failed, Reason ${exception.message}")
                                    threeDSecureCallback = null
                                    listener.onFailed(AirwallexCheckoutException(e = exception))
                                }
                            }

                            val threeDSecureLookup =
                                ThreeDSecureLookup(transactionId, req, acs, version)
                            Logger.debug("Handle threeDSecureLookup $threeDSecureLookup")
                            threeDSecureActivityLaunch.startForResult(
                                ThreeDSecureActivityLaunch.Args(
                                    threeDSecureLookup
                                )
                            )
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode == DccActivityLaunch.REQUEST_CODE) {
            dccCallback?.let {
                try {
                    handleDccData(data, resultCode, it)
                } catch (e: Exception) {
                    it.onFailed(DccException(message = e.localizedMessage ?: "Dcc failed."))
                }
            }
            return true
        } else if (requestCode == ThreeDSecureActivityLaunch.REQUEST_CODE) {
            threeDSecureCallback?.let {
                try {
                    handleThreeDSecureData(data, it)
                } catch (e: Exception) {
                    it.onFailed(ThreeDSException(message = e.localizedMessage ?: "3DS failed."))
                }
            }
            return true
        }
        return false
    }

    private fun handleDccData(
        data: Intent?,
        resultCode: Int,
        callback: DccCallback
    ) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val result = DccActivityLaunch.Result.fromIntent(data)
                val paymentIntent = result?.paymentIntent
                if (paymentIntent != null) {
                    callback.onSuccess(paymentIntent)
                } else {
                    callback.onFailed(result?.exception ?: DccException(message = "Dcc failed."))
                }
            }
            Activity.RESULT_CANCELED -> {
                callback.onFailed(DccException(message = "Dcc failed. Reason: User cancel the Dcc"))
            }
        }
    }

    private fun handleThreeDSecureData(
        data: Intent?,
        callback: ThreeDSecureCallback
    ) {
        when (data?.getSerializableExtra(ThreeDSecureActivity.EXTRA_THREE_D_SECURE_TYPE) as? ThreeDSecureManager.ThreeDSecureType) {
            ThreeDSecureManager.ThreeDSecureType.THREE_D_SECURE_1 -> {
                // 1.0 Flow
                val payload = data.getStringExtra(ThreeDSecureActivity.EXTRA_THREE_PAYLOAD)
                if (payload != null) {
                    Logger.debug("3DS 1.0 success. Response payload: $payload")
                    callback.onThreeDS1Success(payload)
                } else {
                    val cancel =
                        data.getBooleanExtra(ThreeDSecureActivity.EXTRA_THREE_CANCEL, false)
                    if (cancel) {
                        Logger.debug("3DS 1.0 canceled")
                        callback.onFailed(ThreeDSException(message = "3DS 1.0 failed. Reason: User cancel the 3DS 1.0"))
                    } else {
                        val reason =
                            data.getStringExtra(ThreeDSecureActivity.EXTRA_THREE_FAILED_REASON)
                        Logger.debug("3DS 1.0 failed. Reason: $reason")
                        callback.onFailed(
                            ThreeDSException(
                                message = reason
                                    ?: "3DS 1.0 verification failed"
                            )
                        )
                    }
                }
            }
            ThreeDSecureManager.ThreeDSecureType.THREE_D_SECURE_2 -> {
                // 2.0 Flow
                val validateResponse =
                    data.getSerializableExtra(ThreeDSecureActivity.EXTRA_VALIDATION_RESPONSE) as ValidateResponse
                if (validateResponse.actionCode != null && validateResponse.actionCode == CardinalActionCode.CANCEL) {
                    Logger.debug("3DS 2.0 canceled")
                    callback.onFailed(ThreeDSException(message = "3DS 2.0 failed. Reason: User cancel the 3DS 2.0"))
                } else {
                    if (validateResponse.errorDescription.lowercase(Locale.ROOT) == "success") {
                        Logger.debug("3DS 2.0 success. Response payload: ${validateResponse.payment.processorTransactionId}")
                        callback.onThreeDS2Success(validateResponse.payment.processorTransactionId)
                    } else {
                        Logger.debug("3DS 2.0 failed. Reason: ${validateResponse.errorDescription}")
                        callback.onFailed(ThreeDSException(message = validateResponse.errorDescription))
                    }
                }
            }
        }
    }

    override fun canHandleAction(paymentMethodType: PaymentMethodType): Boolean {
        return paymentMethodType == PaymentMethodType.CARD
    }

    override fun retrieveSecurityToken(
        paymentIntentId: String,
        applicationContext: Context,
        securityTokenListener: SecurityTokenListener
    ) {
        AirwallexSecurityConnector().retrieveSecurityToken(
            paymentIntentId,
            applicationContext,
            securityTokenListener
        )
    }
}
