package com.airwallex.android.core

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.annotation.UiThread
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.airwallex.android.core.data.AirwallexCheckoutParam
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.exception.AirwallexComponentDependencyException
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.exception.InvalidParamsException
import com.airwallex.android.core.extension.confirmGooglePayIntent
import com.airwallex.android.core.extension.createCardPaymentMethod
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.AirwallexPaymentRequest
import com.airwallex.android.core.model.AirwallexPaymentRequestFlow
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.BankResponse
import com.airwallex.android.core.model.Billing
import com.airwallex.android.core.model.CardScheme
import com.airwallex.android.core.model.ConfirmPaymentIntentParams
import com.airwallex.android.core.model.CreatePaymentConsentParams
import com.airwallex.android.core.model.CreatePaymentMethodParams
import com.airwallex.android.core.model.Dependency
import com.airwallex.android.core.model.Device
import com.airwallex.android.core.model.DisablePaymentConsentParams
import com.airwallex.android.core.model.Options
import com.airwallex.android.core.model.Page
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentConsentCreateRequest
import com.airwallex.android.core.model.PaymentConsentDisableRequest
import com.airwallex.android.core.model.PaymentConsentReference
import com.airwallex.android.core.model.PaymentConsentVerifyRequest
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PaymentIntentConfirmRequest
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodCreateRequest
import com.airwallex.android.core.model.PaymentMethodOptions
import com.airwallex.android.core.model.PaymentMethodRequest
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.core.model.PaymentMethodTypeInfo
import com.airwallex.android.core.model.RetrieveAvailablePaymentConsentsParams
import com.airwallex.android.core.model.RetrieveAvailablePaymentMethodParams
import com.airwallex.android.core.model.RetrieveBankParams
import com.airwallex.android.core.model.RetrievePaymentIntentParams
import com.airwallex.android.core.model.RetrievePaymentMethodTypeInfoParams
import com.airwallex.android.core.model.ThreeDSecure
import com.airwallex.android.core.model.TransactionMode
import com.airwallex.android.core.model.VerifyPaymentConsentParams
import com.airwallex.android.core.util.BuildConfigHelper
import com.airwallex.android.core.util.SessionUtils.getIntentId
import com.airwallex.risk.AirwallexRisk
import com.airwallex.risk.RiskConfiguration
import com.airwallex.risk.Tenant
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.UUID

@Suppress("LongMethod")
class Airwallex internal constructor(
    private val fragment: Fragment?,
    private var activity: ComponentActivity,
    private val paymentManager: PaymentManager,
    private val applicationContext: Context,
) {

    interface PaymentListener<T> {
        fun onSuccess(response: T)
        fun onFailed(exception: AirwallexException)
    }

    interface ShippingResultListener {

        /**
         * This method is called when the user has completed the shipping flow.
         *
         * @param status The status of shipping result.
         */
        fun onCompleted(status: AirwallexShippingStatus)
    }

    interface PaymentResultListener {
        /**
         * This method is called when the user has completed the checkout.
         *
         * @param status The status of checkout result.
         */
        fun onCompleted(status: AirwallexPaymentStatus)
    }

    init {
        AnalyticsLogger.initialize(applicationContext)
    }

    /**
     * Update the activity reference. This should be called when the activity is recreated
     * due to configuration changes to ensure the Airwallex instance always has a valid
     * activity reference.
     *
     * @param newActivity The new activity instance after recreation
     */
    fun updateActivity(newActivity: ComponentActivity) {
        this.activity = newActivity
    }

    /**
     * Constructor of [Airwallex]
     */
    constructor(fragment: Fragment) : this(
        fragment,
        fragment.requireActivity(),
        AirwallexPaymentManager(AirwallexApiRepository()),
        fragment.requireContext().applicationContext
    )

    constructor(activity: ComponentActivity) : this(
        null,
        activity,
        AirwallexPaymentManager(AirwallexApiRepository()),
        activity.applicationContext
    )

    @VisibleForTesting
    constructor(activity: ComponentActivity, applicationContext: Context) : this(
        null,
        activity,
        AirwallexPaymentManager(AirwallexApiRepository()),
        applicationContext
    )

    private fun setupAnalyticsLogger(session: AirwallexSession) {
        when (session) {
            is AirwallexPaymentSession -> {
                AnalyticsLogger.setSessionInformation(
                    transactionMode = TransactionMode.ONE_OFF.value,
                    paymentIntentId = session.paymentIntent?.id,
                )
            }
            is AirwallexRecurringSession -> {
                AnalyticsLogger.setSessionInformation(
                    transactionMode = TransactionMode.RECURRING.value,
                )
            }
            is AirwallexRecurringWithIntentSession -> {
                AnalyticsLogger.setSessionInformation(
                    transactionMode = TransactionMode.RECURRING.value,
                    paymentIntentId = session.paymentIntent?.id,
                )
            }
        }
    }

    /**
     * Method to handle Activity results from Airwallex activities. Pass data here from your
     * host's `#onActivityResult(int, int, Intent)` function.
     *
     * @param requestCode the request code used to open the resulting activity
     * @param resultCode a result code representing the success of the intended action
     * @param data an [Intent] with the resulting data from the Activity
     *
     * @return `true` if the activity result was handled by this function,
     * otherwise `false`
     */
    @Deprecated("This method will be removed in future versions")
    fun handlePaymentData(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        val providers = listOf(
            AirwallexPlugins.getProvider(ActionComponentProviderType.CARD),
            AirwallexPlugins.getProvider(ActionComponentProviderType.GOOGLEPAY)
        )

        for (provider in providers) {
            if (provider?.get()?.handleActivityResult(requestCode, resultCode, data) == true) {
                return true
            }
        }
        return false
    }

    /**
     * Confirm a payment intent with card and billing details
     *
     * @param session a [AirwallexSession] used to start the payment flow
     * @param card the card information
     * @param billing the billing information, it's optional
     * @param saveCard whether card will be saved as a payment consent,
     * if set as true, [AirwallexSession.customerId] must be provided for the [session]
     * @param listener The callback of the payment flow
     */
    @UiThread
    fun confirmPaymentIntent(
        session: AirwallexSession,
        card: PaymentMethod.Card,
        billing: Billing?,
        saveCard: Boolean = false,
        listener: PaymentResultListener
    ) {
        // Bind session's PaymentIntentProvider to this Activity's lifecycle
        session.bindToActivity(activity)

        setupAnalyticsLogger(session)
        createCardPaymentMethod(
            session = session,
            card = card,
            billing = billing,
            saveCard = saveCard,
            listener = object : PaymentListener<PaymentMethod> {
                override fun onSuccess(response: PaymentMethod) {
                    checkout(session, response, card.cvc, saveCard, listener)
                }

                override fun onFailed(exception: AirwallexException) {
                    listener.onCompleted(AirwallexPaymentStatus.Failure(exception))
                }
            }
        )
    }

    /**
     * Confirm a payment intent with payment consent ID
     *
     * @param session a [AirwallexPaymentSession] used to start the payment flow
     * @param paymentConsent a [PaymentConsent] used to start the payment flow
     * @param listener The callback of the payment flow
     */
    @UiThread
    fun confirmPaymentIntent(
        session: AirwallexPaymentSession,
        paymentConsent: PaymentConsent,
        listener: PaymentResultListener
    ) {
        // Bind session's PaymentIntentProvider to this Activity's lifecycle
        session.bindToActivity(activity)

        setupAnalyticsLogger(session)
        val paymentMethod = paymentConsent.paymentMethod
        val paymentConsentId = paymentConsent.id
        if (paymentMethod == null) {
            AirwallexLogger.info("confirmPaymentIntent, paymentMethod == null")
            listener.onCompleted(AirwallexPaymentStatus.Failure(AirwallexCheckoutException(message = "paymentMethod is required")))
            return
        }
        if (paymentConsentId.isNullOrEmpty()) {
            AirwallexLogger.info("confirmPaymentIntent, paymentConsentId isNullOrEmpty")
            listener.onCompleted(AirwallexPaymentStatus.Failure(AirwallexCheckoutException(message = "paymentConsentId is required")))
            return
        }
        if (paymentMethod.card?.numberType == PaymentMethod.Card.NumberType.PAN) {
            AirwallexLogger.info("confirmPaymentIntent, need cvc")
            val provider = AirwallexPlugins.getProvider(ActionComponentProviderType.CARD)
            provider?.get()?.let { paymentProvider ->
                paymentProvider.handlePaymentData(
                    AirwallexCheckoutParam(
                        activity,
                        paymentMethod,
                        session,
                        paymentConsentId
                    )
                ) { status: AirwallexPaymentStatus? ->
                    listener.onCompleted(
                        status ?: AirwallexPaymentStatus.Failure(
                            AirwallexCheckoutException(message = "cvc unknown error")
                        )
                    )
                }
            } ?: run {
                AirwallexLogger.error("confirmPaymentIntent, Provider is null, unable to handle payment data")
                listener.onCompleted(
                    AirwallexPaymentStatus.Failure(
                        AirwallexComponentDependencyException(dependency = Dependency.CARD)
                    )
                )
            }
        } else {
            AirwallexLogger.info("confirmPaymentIntent, skip cvc")
            confirmPaymentIntent(session, paymentConsentId, listener)
        }
    }

    /**
     * Confirm a payment intent with payment consent ID
     *
     * @param session a [AirwallexPaymentSession] used to start the payment flow
     * @param paymentConsentId the ID of the [PaymentConsent]
     * @param listener The callback of the payment flow
     */
    fun confirmPaymentIntent(
        session: AirwallexPaymentSession,
        paymentConsentId: String,
        listener: PaymentResultListener
    ) {
        // Bind session's PaymentIntentProvider to this Activity's lifecycle
        session.bindToActivity(activity)

        setupAnalyticsLogger(session)
        session.resolvePaymentIntent(object : PaymentIntentProvider.PaymentIntentCallback {
            override fun onSuccess(paymentIntent: PaymentIntent) {
                val params = ConfirmPaymentIntentParams.createCardParams(
                    paymentIntentId = paymentIntent.id,
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    paymentMethod = null,
                    cvc = null,
                    customerId = session.customerId,
                    paymentConsentId = paymentConsentId,
                    returnUrl = AirwallexPlugins.environment.threeDsReturnUrl(),
                    autoCapture = session.autoCapture
                )
                confirmPaymentIntent(params, listener)
            }

            override fun onError(error: Throwable) {
                listener.onCompleted(AirwallexPaymentStatus.Failure(AirwallexCheckoutException(message = error.message, e = error)))
            }
        })
    }

    /**
     * Confirm a payment intent with google pay options
     *
     * @param session a [AirwallexPaymentSession] used to start the payment flow
     * @param listener The callback of the payment flow
     */
    @UiThread
    fun startGooglePay(
        session: AirwallexSession,
        listener: PaymentResultListener
    ) {
        // Bind session's PaymentIntentProvider to this Activity's lifecycle
        session.bindToActivity(activity)

        setupAnalyticsLogger(session)
        // Only log payment_launched for API integration (when activity is NOT an Airwallex UI activity)
        if (!isAirwallexUIActivity) {
            AnalyticsLogger.logAction(
                actionName = "payment_launched",
                additionalInfo = mapOf(
                    "subtype" to "api",
                    "paymentMethod" to PaymentMethodType.GOOGLEPAY.value,
                    "expressCheckout" to session.isExpressCheckout
                )
            )
        }
        val googlePayProvider = AirwallexPlugins.getProvider(ActionComponentProviderType.GOOGLEPAY)
        if (googlePayProvider != null) {
            val coroutineScope = fragment?.lifecycleScope
                ?: if (activity is AppCompatActivity) {
                    activity.lifecycleScope
                } else {
                    MainScope()
                }

            coroutineScope.launch {
                val cardSchemes = (
                        session.googlePayOptions?.allowedCardNetworks.takeIf { !it.isNullOrEmpty() }
                            ?: googlePaySupportedNetworks()
                        )
                    .map {
                        CardScheme(it.uppercase())
                    }
                val canMakePayment = googlePayProvider.canHandleSessionAndPaymentMethod(
                    session,
                    AvailablePaymentMethodType("googlepay", cardSchemes = cardSchemes),
                    activity
                )
                if (canMakePayment) {
                    checkoutGooglePay(
                        session = session,
                        listener = object : PaymentResultListener {
                            override fun onCompleted(status: AirwallexPaymentStatus) {
                                if (status is AirwallexPaymentStatus.Success) {
                                    AnalyticsLogger.logAction(
                                        "payment_success",
                                        mapOf(
                                            "payment_method" to PaymentMethodType.GOOGLEPAY.value,
                                            "skipReadinessCheck" to (session.googlePayOptions?.skipReadinessCheck == true)
                                        )
                                    )
                                }
                                listener.onCompleted(status)
                            }
                        }
                    )
                } else {
                    listener.onCompleted(
                        AirwallexPaymentStatus.Failure(AirwallexCheckoutException(message = "Payment not supported via Google Pay."))
                    )
                }
            }
        } else {
            listener.onCompleted(
                AirwallexPaymentStatus.Failure(
                    AirwallexComponentDependencyException(dependency = Dependency.GOOGLEPAY)
                )
            )
        }
    }

    /**
     * Create a payment method
     *
     * @param params [CreatePaymentMethodParams] used to create the [PaymentMethod]
     * @param listener the callback of create [PaymentMethod]
     */
    @UiThread
    fun createPaymentMethod(
        params: CreatePaymentMethodParams,
        listener: PaymentListener<PaymentMethod>
    ) {
        paymentManager.startOperation(
            buildCreatePaymentMethodOptions(params),
            listener
        )
    }

    suspend fun createPaymentMethod(params: CreatePaymentMethodParams): PaymentMethod {
        return paymentManager.createPaymentMethod(buildCreatePaymentMethodOptions(params))
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
        paymentManager.startOperation(
            Options.RetrievePaymentIntentOptions(
                clientSecret = params.clientSecret,
                paymentIntentId = params.paymentIntentId
            ),
            listener
        )
    }

    /**
     * Retrieve available payment consents
     *
     * @param params [RetrieveAvailablePaymentConsentsParams] used to retrieve all [PaymentConsent]
     */
    suspend fun retrieveAvailablePaymentConsents(
        params: RetrieveAvailablePaymentConsentsParams
    ): Page<PaymentConsent> {
        return paymentManager.retrieveAvailablePaymentConsents(
            Options.RetrieveAvailablePaymentConsentsOptions(
                clientSecret = params.clientSecret,
                customerId = params.customerId,
                merchantTriggerReason = params.merchantTriggerReason,
                nextTriggeredBy = params.nextTriggeredBy,
                status = params.status,
                pageNum = params.pageNum,
                pageSize = params.pageSize
            )
        )
    }

    /**
     * Retrieve available payment consents
     *
     * @param params [RetrieveAvailablePaymentConsentsParams] used to retrieve all [PaymentConsent]
     * @param callback [AirwallexCallback] A callback interface to handle the success or failure of the network request.
     */
    fun retrieveAvailablePaymentConsents(
        params: RetrieveAvailablePaymentConsentsParams,
        callback: AirwallexCallback<Page<PaymentConsent>>
    ) {
        activity.lifecycleScope.launch {
            try {
                val result = retrieveAvailablePaymentConsents(params)
                callback.onSuccess(result)
            } catch (e: Exception) {
                callback.onFailure(AirwallexCheckoutException(e = e))
            }
        }
    }

    /**
     * Retrieve available payment methods
     *
     * @param params [RetrieveAvailablePaymentMethodParams] used to retrieve all [AvailablePaymentMethodType]
     */
    suspend fun retrieveAvailablePaymentMethods(
        session: AirwallexSession,
        params: RetrieveAvailablePaymentMethodParams
    ): Page<AvailablePaymentMethodType> {
        setupAnalyticsLogger(session)
        val transactionMode = when (session) {
            is AirwallexRecurringSession, is AirwallexRecurringWithIntentSession -> TransactionMode.RECURRING
            is AirwallexPaymentSession -> TransactionMode.ONE_OFF
            else -> throw AirwallexCheckoutException(message = "Not support session $session")
        }
        AirwallexLogger.info("Airwallex retrieveAvailablePaymentMethods[${(session as? AirwallexPaymentSession)?.paymentIntent?.id}]: transactionMode = $transactionMode ")
        val response = paymentManager.retrieveAvailablePaymentMethods(
            Options.RetrieveAvailablePaymentMethodsOptions(
                clientSecret = params.clientSecret,
                pageNum = params.pageNum,
                pageSize = params.pageSize,
                active = params.active,
                transactionCurrency = params.transactionCurrency,
                transactionMode = transactionMode,
                countryCode = params.countryCode
            )
        )
        response.items = response.items.filter { paymentMethod ->
            paymentMethod.transactionMode == transactionMode &&
                    AirwallexPlugins.getProvider(paymentMethod)?.canHandleSessionAndPaymentMethod(
                        session,
                        paymentMethod,
                        activity
                    ) ?: false
        }
        AirwallexLogger.info("Airwallex retrieveAvailablePaymentMethods[${(session as? AirwallexPaymentSession)?.paymentIntent?.id}]: response.items.size = ${response.items.size}")
        return response
    }

    /**
     * Retrieve available payment methods
     *
     * @param params [RetrieveAvailablePaymentMethodParams] used to retrieve all [AvailablePaymentMethodType]
     * @param session The [AirwallexSession] which contains session information for retrieving payment methods.
     * @param params [RetrieveAvailablePaymentMethodParams] Parameters used to retrieve all [AvailablePaymentMethodType].
     */
    fun retrieveAvailablePaymentMethods(
        session: AirwallexSession,
        params: RetrieveAvailablePaymentMethodParams,
        callback: AirwallexCallback<Page<AvailablePaymentMethodType>>
    ) {
        activity.lifecycleScope.launch {
            try {
                val result = retrieveAvailablePaymentMethods(session, params)
                callback.onSuccess(result)
            } catch (e: Exception) {
                callback.onFailure(AirwallexCheckoutException(e = e))
            }
        }
    }

    /**
     * Verify a [PaymentConsent]
     *
     * @param params [VerifyPaymentConsentParams] used to verify the [PaymentConsent]
     * @param listener a [PaymentListener] to receive the response or error
     */
    @UiThread
    fun verifyPaymentConsent(
        device: Device,
        params: VerifyPaymentConsentParams,
        listener: PaymentResultListener
    ) {
        AirwallexLogger.info("Airwallex verifyPaymentConsent: type = ${params.paymentMethodType}")
        val paymentMethodType = params.paymentMethodType
        val verificationOptions = when (paymentMethodType) {
            // The backend requires passing parameters using the card field in verification_options, even if the payment type is Google Pay.
            PaymentMethodType.CARD.value, PaymentMethodType.GOOGLEPAY.value -> PaymentConsentVerifyRequest.VerificationOptions(
                type = PaymentMethodType.CARD.value,
                cardOptions = PaymentConsentVerifyRequest.CardVerificationOptions(
                    amount = params.amount,
                    currency = params.currency,
                    cvc = params.cvc,
                )
            )

            else -> {
                PaymentConsentVerifyRequest.VerificationOptions(
                    type = paymentMethodType,
                    thirdPartOptions = PaymentConsentVerifyRequest.ThirdPartVerificationOptions()
                )
            }
        }

        paymentManager.startOperation(
            Options.VerifyPaymentConsentOptions(
                clientSecret = params.clientSecret,
                paymentConsentId = params.paymentConsentId,
                request = PaymentConsentVerifyRequest.Builder()
                    .setRequestId(UUID.randomUUID().toString())
                    .setVerificationOptions(verificationOptions)
                    .setDevice(device)
                    .setReturnUrl(params.returnUrl)
                    .build()
            ),
            object : PaymentListener<PaymentConsent> {
                override fun onFailed(exception: AirwallexException) {
                    listener.onCompleted(AirwallexPaymentStatus.Failure(exception))
                }

                override fun onSuccess(response: PaymentConsent) {
                    // for redirect, initialPaymentIntentId is empty now. so we don support recurring in redirect flow
                    val paymentIntentId = response.initialPaymentIntentId
                    if (response.nextAction == null) {
                        listener.onCompleted(
                            AirwallexPaymentStatus.Success(
                                paymentIntentId,
                                response.id
                            )
                        )
                        return
                    }
                    val provider = AirwallexPlugins.getProvider(
                        ActionComponentProviderType.fromValue(paymentMethodType)
                    )
                    if (provider == null) {
                        listener.onCompleted(
                            AirwallexPaymentStatus.Failure(
                                AirwallexCheckoutException(message = "Missing dependency!")
                            )
                        )
                        return
                    }
                    when (paymentMethodType) {
                        PaymentMethodType.CARD.value, PaymentMethodType.GOOGLEPAY.value -> {
                            if (paymentIntentId.isNullOrEmpty()) {
                                AnalyticsLogger.logError(
                                    "initialPaymentIntentId_null_or_empty",
                                    mapOf("type" to paymentMethodType)
                                )
                                AirwallexLogger.error("Airwallex verifyPaymentConsent: type = $paymentMethodType, paymentIntentId isNullOrEmpty")
                                listener.onCompleted(
                                    AirwallexPaymentStatus.Failure(
                                        AirwallexCheckoutException(message = "Unsupported payment method")
                                    )
                                )
                                return
                            }

                            val nextActionModel = CardNextActionModel(
                                paymentManager = paymentManager,
                                clientSecret = params.clientSecret,
                                device = null,
                                paymentIntentId = paymentIntentId,
                                currency = requireNotNull(params.currency),
                                amount = requireNotNull(params.amount),
                                activityProvider = { activity }
                            )

                            provider.get().handlePaymentIntentResponse(
                                paymentIntentId,
                                response.nextAction,
                                fragment,
                                activity,
                                applicationContext,
                                nextActionModel,
                                listener,
                                response.id
                            )
                        }

                        else -> {
                            provider.get().handlePaymentIntentResponse(
                                null,
                                response.nextAction,
                                fragment,
                                activity,
                                applicationContext,
                                null,
                                listener,
                                response.id
                            )
                        }
                    }
                }
            }
        )
    }

    /**
     * Disable a [PaymentConsent] by ID
     * @param params [DisablePaymentConsentParams] used to disable the [PaymentConsent]
     * @param listener a [PaymentListener] to receive the response or error
     */
    @UiThread
    fun disablePaymentConsent(
        params: DisablePaymentConsentParams,
        listener: PaymentListener<PaymentConsent>
    ) {
        paymentManager.startOperation(
            Options.DisablePaymentConsentOptions(
                clientSecret = params.clientSecret,
                paymentConsentId = params.paymentConsentId,
                request = PaymentConsentDisableRequest.Builder()
                    .setRequestId(UUID.randomUUID().toString())
                    .build()
            ),
            listener
        )
    }

    @UiThread
    fun retrieveBanks(
        params: RetrieveBankParams,
        listener: PaymentListener<BankResponse>
    ) {
        paymentManager.startOperation(
            Options.RetrieveBankOptions(
                clientSecret = params.clientSecret,
                paymentMethodType = params.paymentMethodType,
                flow = params.flow,
                transactionMode = params.transactionMode,
                countryCode = params.countryCode,
                openId = params.openId
            ),
            listener
        )
    }

    @UiThread
    fun retrievePaymentMethodTypeInfo(
        params: RetrievePaymentMethodTypeInfoParams,
        listener: PaymentListener<PaymentMethodTypeInfo>
    ) {
        paymentManager.startOperation(
            Options.RetrievePaymentMethodTypeInfoOptions(
                clientSecret = params.clientSecret,
                paymentMethodType = params.paymentMethodType,
                flow = params.flow,
                transactionMode = params.transactionMode,
                countryCode = params.countryCode,
                openId = params.openId
            ),
            listener
        )
    }

    /**
     * Checkout the payment by paymentType and session
     *
     * @param session a [AirwallexSession] used to present the Checkout flow, required.
     * @param paymentMethodName a [String] representing one of the redirect payment type names, required. check all methods by API reference: https://www.airwallex.com/docs/api#/Payment_Acceptance/Config/_api_v1_pa_config_payment_method_types/get JSON Object field: items.name
     * @param additionalInfo a [Map] containing extra information needed for certain payment types, such as phone number, email, bank details, etc., optional.
     * @param flow an [AirwallexPaymentRequestFlow], currently only supporting [AirwallexPaymentRequestFlow.IN_APP], optional.
     * @param listener the callback for the checkout result.
     */
    @UiThread
    fun startRedirectPay(
        session: AirwallexSession,
        paymentMethodName: String,
        additionalInfo: Map<String, String>? = null,
        flow: AirwallexPaymentRequestFlow? = AirwallexPaymentRequestFlow.IN_APP,
        listener: PaymentResultListener,
    ) {
        // Bind session's PaymentIntentProvider to this Activity's lifecycle
        session.bindToActivity(activity)

        setupAnalyticsLogger(session)
        if (AirwallexPlugins.getProvider(ActionComponentProviderType.REDIRECT) == null) {
            listener.onCompleted(
                AirwallexPaymentStatus.Failure(
                    AirwallexComponentDependencyException(dependency = Dependency.REDIRECT)
                )
            )
            return
        }
        val paymentMethod = PaymentMethod.Builder()
            .setType(paymentMethodName)
            .build()
        checkout(
            session = session,
            paymentMethod = paymentMethod,
            additionalInfo = additionalInfo,
            flow = flow,
            listener = listener
        )
    }

    /**
     * Checkout the one-off payment
     *
     * @param session a [AirwallexSession] used to present the Checkout flow, required.
     * @param paymentMethod a [PaymentMethod] used to present the Checkout flow, required.
     * @param listener The callback of checkout
     */
    @UiThread
    internal fun checkout(
        session: AirwallexSession,
        paymentMethod: PaymentMethod,
        cvc: String? = null,
        saveCard: Boolean = false,
        listener: PaymentResultListener
    ) {
        this.checkout(session, paymentMethod, null, cvc, null, null, listener, saveCard)
    }

    /**
     * Checkout the payment, include one-off & recurring
     *
     * @param session a [AirwallexSession] used to present the Checkout flow, required.
     * @param paymentMethod a [PaymentMethod] used to present the Checkout flow, required.
     * @param paymentConsentId the ID of the [PaymentConsent], optional. (CIT & Card will need this field for subsequent payments, paymentConsentId is not empty indicating a subsequent payment, empty indicating a recurring )
     * @param cvc the CVC of the Credit Card, optional.
     * @param additionalInfo used by LPMs
     * @param listener The callback of checkout
     */
    @UiThread
    fun checkout(
        session: AirwallexSession,
        paymentMethod: PaymentMethod,
        paymentConsentId: String? = null,
        cvc: String? = null,
        additionalInfo: Map<String, String>? = null,
        flow: AirwallexPaymentRequestFlow? = null,
        listener: PaymentResultListener,
        saveCard: Boolean = false,
    ) {
        setupAnalyticsLogger(session)
        // Only log payment_launched for API integration (when activity is NOT an Airwallex UI activity)
        if (!isAirwallexUIActivity) {
            AnalyticsLogger.logAction(
                actionName = "payment_launched",
                additionalInfo = mutableMapOf<String, Any>(
                    "subtype" to "api",
                    "expressCheckout" to session.isExpressCheckout
                ).apply {
                    paymentMethod.type?.let { put("paymentMethod", it) }
                }
            )
        }
        AirwallexLogger.info("Airwallex checkout: saveCard = $saveCard, paymentMethod.type = ${paymentMethod.type} session type = ${session.javaClass}")
        when (session) {
            is AirwallexPaymentSession -> {
                if (paymentMethod.type == PaymentMethodType.GOOGLEPAY.value) {
                    checkoutGooglePay(session, listener)
                } else if (saveCard) {
                    createPaymentConsentAndConfirmIntent(session, paymentMethod, cvc, listener)
                } else {
                    session.resolvePaymentIntent(object : PaymentIntentProvider.PaymentIntentCallback {
                        override fun onSuccess(paymentIntent: PaymentIntent) {
                            confirmPaymentIntent(
                                paymentIntentId = paymentIntent.id,
                                clientSecret = requireNotNull(paymentIntent.clientSecret),
                                paymentMethod = paymentMethod,
                                cvc = cvc,
                                currency = session.currency,
                                customerId = session.customerId,
                                paymentConsentId = paymentConsentId,
                                additionalInfo = additionalInfo,
                                returnUrl = if (paymentMethod.type == PaymentMethodType.CARD.value) {
                                    AirwallexPlugins.environment.threeDsReturnUrl()
                                } else session.returnUrl,
                                autoCapture = session.autoCapture,
                                flow = flow,
                                listener = listener
                            )
                        }

                        override fun onError(error: Throwable) {
                            listener.onCompleted(AirwallexPaymentStatus.Failure(AirwallexCheckoutException(message = error.message, e = error)))
                        }
                    })
                }
            }

            else -> createPaymentConsentAndConfirmIntent(session, paymentMethod, cvc, listener)
        }
    }

    @UiThread
    private fun createPaymentConsentAndConfirmIntent(
        session: AirwallexSession,
        paymentMethod: PaymentMethod,
        cvc: String? = null,
        listener: PaymentResultListener
    ) {
        setupAnalyticsLogger(session)
        fun confirmPaymentIntent(
            session: AirwallexPaymentSession,
            consent: PaymentConsent? = null
        ) {
            session.resolvePaymentIntent(object : PaymentIntentProvider.PaymentIntentCallback {
                override fun onSuccess(paymentIntent: PaymentIntent) {
                    confirmPaymentIntent(
                        paymentIntentId = paymentIntent.id,
                        clientSecret = requireNotNull(paymentIntent.clientSecret),
                        paymentMethod = paymentMethod,
                        cvc = cvc,
                        customerId = session.customerId,
                        paymentConsentId = consent?.id,
                        returnUrl = if (paymentMethod.type
                            == PaymentMethodType.CARD.value
                        ) {
                            AirwallexPlugins.environment.threeDsReturnUrl()
                        } else session.returnUrl,
                        autoCapture = session.autoCapture,
                        listener = listener
                    )
                }

                override fun onError(error: Throwable) {
                    listener.onCompleted(AirwallexPaymentStatus.Failure(AirwallexCheckoutException(message = error.message, e = error)))
                }
            })
        }

        when (session) {
            is AirwallexPaymentSession -> {
                session.resolvePaymentIntent(object : PaymentIntentProvider.PaymentIntentCallback {
                    override fun onSuccess(paymentIntent: PaymentIntent) {
                        createPaymentConsent(
                            clientSecret = requireNotNull(paymentIntent.clientSecret),
                    customerId = requireNotNull(session.customerId),
                    paymentMethod = paymentMethod,
                    nextTriggeredBy = PaymentConsent.NextTriggeredBy.CUSTOMER,
                    requiresCvc = true,
                    merchantTriggerReason = null,
                    listener = object : PaymentListener<PaymentConsent> {
                        override fun onFailed(exception: AirwallexException) {
                            confirmPaymentIntent(session)
                        }

                        override fun onSuccess(response: PaymentConsent) {
                            confirmPaymentIntent(session, response)
                        }
                    }
                        )
                    }

                    override fun onError(error: Throwable) {
                        listener.onCompleted(AirwallexPaymentStatus.Failure(AirwallexCheckoutException(message = error.message, e = error)))
                    }
                })
            }

            is AirwallexRecurringSession -> {
                val customerId = session.customerId
                val clientSecret = session.clientSecret
                createPaymentConsent(
                    clientSecret = clientSecret,
                    customerId = customerId,
                    paymentMethod = paymentMethod,
                    nextTriggeredBy = session.nextTriggerBy,
                    requiresCvc = session.requiresCVC,
                    merchantTriggerReason = session.merchantTriggerReason,
                    listener = object : PaymentListener<PaymentConsent> {
                        override fun onFailed(exception: AirwallexException) {
                            listener.onCompleted(
                                AirwallexPaymentStatus.Failure(exception)
                            )
                        }

                        override fun onSuccess(response: PaymentConsent) {
                            verifyPaymentConsent(
                                paymentConsent = response,
                                currency = session.currency,
                                amount = session.amount,
                                cvc = cvc,
                                returnUrl = if (paymentMethod.type == PaymentMethodType.CARD.value) AirwallexPlugins.environment.threeDsReturnUrl() else session.returnUrl,
                                listener = listener
                            )
                        }
                    }
                )
            }

            is AirwallexRecurringWithIntentSession -> {
                session.resolvePaymentIntent(object : PaymentIntentProvider.PaymentIntentCallback {
                    override fun onSuccess(paymentIntent: PaymentIntent) {
                        createPaymentConsent(
                            clientSecret = requireNotNull(paymentIntent.clientSecret),
                    customerId = session.customerId,
                    paymentMethod = paymentMethod,
                    nextTriggeredBy = session.nextTriggerBy,
                    requiresCvc = session.requiresCVC,
                    merchantTriggerReason = session.merchantTriggerReason,
                    listener = object : PaymentListener<PaymentConsent> {
                        override fun onFailed(exception: AirwallexException) {
                            listener.onCompleted(AirwallexPaymentStatus.Failure(exception))
                        }

                        override fun onSuccess(response: PaymentConsent) {
                            when (paymentMethod.type) {
                                PaymentMethodType.CARD.value -> {
                                    confirmPaymentIntent(
                                        paymentIntentId = paymentIntent.id,
                                        clientSecret = requireNotNull(paymentIntent.clientSecret),
                                        paymentMethod = paymentMethod,
                                        cvc = cvc,
                                        customerId = session.customerId,
                                        paymentConsentId = response.id,
                                        returnUrl = AirwallexPlugins.environment.threeDsReturnUrl(),
                                        autoCapture = session.autoCapture,
                                        listener = listener
                                    )
                                }

                                else -> {
                                    // this should not happen
                                    verifyPaymentConsent(
                                        paymentConsent = response,
                                        currency = session.currency,
                                        amount = session.amount,
                                        returnUrl = session.returnUrl,
                                        listener = listener
                                    )
                                }
                            }
                        }
                    }
                        )
                    }

                    override fun onError(error: Throwable) {
                        listener.onCompleted(AirwallexPaymentStatus.Failure(AirwallexCheckoutException(message = error.message, e = error)))
                    }
                })
            }
        }
    }

    private fun createGooglePayConsentAndVerify(
        session: AirwallexRecurringSession,
        listener: PaymentResultListener,
        googlePay: PaymentMethod.GooglePay
    ) {
        val paymentMethod = PaymentMethod.Builder()
            .setType(PaymentMethodType.GOOGLEPAY.value)
            .setGooglePay(googlePay)
            .build()
        createPaymentConsent(
            clientSecret = session.clientSecret,
            customerId = session.customerId,
            paymentMethod = paymentMethod,
            nextTriggeredBy = session.nextTriggerBy,
            requiresCvc = session.requiresCVC,
            merchantTriggerReason = session.merchantTriggerReason,
            listener = object : PaymentListener<PaymentConsent> {
                override fun onFailed(exception: AirwallexException) {
                    listener.onCompleted(
                        AirwallexPaymentStatus.Failure(exception)
                    )
                }

                override fun onSuccess(response: PaymentConsent) {
                    verifyPaymentConsent(
                        paymentConsent = response,
                        currency = session.currency,
                        amount = session.amount,
                        cvc = null,
                        returnUrl = AirwallexPlugins.environment.threeDsReturnUrl(),
                        listener = listener
                    )
                }
            }
        )
    }

    private fun createGooglePayConsentAndConfirm(
        session: AirwallexRecurringWithIntentSession,
        listener: PaymentResultListener,
        googlePay: PaymentMethod.GooglePay
    ) {
        val paymentMethod = PaymentMethod.Builder()
            .setType(PaymentMethodType.GOOGLEPAY.value)
            .setGooglePay(googlePay)
            .build()
        session.resolvePaymentIntent(object : PaymentIntentProvider.PaymentIntentCallback {
            override fun onSuccess(paymentIntent: PaymentIntent) {
                createPaymentConsent(
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
            customerId = session.customerId,
            paymentMethod = paymentMethod,
            nextTriggeredBy = session.nextTriggerBy,
            requiresCvc = session.requiresCVC,
            merchantTriggerReason = session.merchantTriggerReason,
            listener = object : PaymentListener<PaymentConsent> {
                override fun onFailed(exception: AirwallexException) {
                    listener.onCompleted(AirwallexPaymentStatus.Failure(exception))
                }

                override fun onSuccess(response: PaymentConsent) {
                    confirmPaymentIntent(
                        paymentIntentId = paymentIntent.id,
                        clientSecret = requireNotNull(paymentIntent.clientSecret),
                        paymentMethod = paymentMethod,
                        cvc = null,
                        customerId = session.customerId,
                        paymentConsentId = response.id,
                        returnUrl = AirwallexPlugins.environment.threeDsReturnUrl(),
                        autoCapture = session.autoCapture,
                        listener = listener
                    )
                }
            }
                )
            }

            override fun onError(error: Throwable) {
                listener.onCompleted(AirwallexPaymentStatus.Failure(AirwallexCheckoutException(message = error.message, e = error)))
            }
        })
    }

    private fun checkoutGooglePay(
        session: AirwallexSession,
        listener: PaymentResultListener
    ) {
        val googlePayProvider = AirwallexPlugins.getProvider(ActionComponentProviderType.GOOGLEPAY)
        if (googlePayProvider == null) {
            AirwallexLogger.error("Airwallex checkoutGooglePay: failed , Missing ${PaymentMethodType.GOOGLEPAY.dependencyName} dependency")
            listener.onCompleted(
                AirwallexPaymentStatus.Failure(
                    AirwallexCheckoutException(message = "Missing ${PaymentMethodType.GOOGLEPAY.dependencyName} dependency")
                )
            )
            return
        }

        googlePayProvider.get().handlePaymentIntentResponse(
            paymentIntentId = getIntentId(session),
            nextAction = null,
            fragment = fragment,
            activity = activity,
            applicationContext = applicationContext,
            cardNextActionModel = null,
            listener = object : PaymentResultListener {
                override fun onCompleted(status: AirwallexPaymentStatus) {
                    when (status) {
                        is AirwallexPaymentStatus.Success -> {
                            val mutableInfo = status.additionalInfo?.toMutableMap()
                            if (mutableInfo == null) {
                                AirwallexLogger.error("Airwallex checkoutGooglePay: failed , Missing Google Pay token response")
                                listener.onCompleted(
                                    AirwallexPaymentStatus.Failure(
                                        AirwallexCheckoutException(message = "Missing Google Pay token response")
                                    )
                                )
                                return
                            }
                            val googlePay = PaymentMethod.GooglePay.Builder()
                                .setBilling(mutableInfo["billing"] as? Billing)
                                .setPaymentDataType(mutableInfo["payment_data_type"] as? String)
                                .setEncryptedPaymentToken(mutableInfo["encrypted_payment_token"] as? String)
                                .build()

                            when (session) {
                                is AirwallexPaymentSession -> {
                                    session.resolvePaymentIntent(object : PaymentIntentProvider.PaymentIntentCallback {
                                        override fun onSuccess(paymentIntent: PaymentIntent) {
                                            googlePayProvider.get().confirmGooglePayIntent(
                                                fragment = fragment,
                                                activityProvider = { activity },
                                                paymentManager = paymentManager,
                                                applicationContext = applicationContext,
                                                paymentIntentId = paymentIntent.id,
                                                clientSecret = requireNotNull(paymentIntent.clientSecret),
                                                googlePay = googlePay,
                                                autoCapture = session.autoCapture,
                                                listener = listener
                                            )
                                        }

                                        override fun onError(error: Throwable) {
                                            listener.onCompleted(AirwallexPaymentStatus.Failure(AirwallexCheckoutException(message = error.message, e = error)))
                                        }
                                    })
                                }

                                is AirwallexRecurringSession -> {
                                    createGooglePayConsentAndVerify(session, listener, googlePay)
                                }

                                is AirwallexRecurringWithIntentSession -> {
                                    createGooglePayConsentAndConfirm(session, listener, googlePay)
                                }
                            }
                        }

                        else -> {
                            listener.onCompleted(status)
                        }
                    }
                }
            }
        )
    }

    private fun confirmPaymentIntent(
        paymentIntentId: String,
        clientSecret: String,
        paymentMethod: PaymentMethod,
        cvc: String? = null,
        currency: String? = null,
        customerId: String? = null,
        paymentConsentId: String? = null,
        additionalInfo: Map<String, String>? = null,
        returnUrl: String? = null,
        autoCapture: Boolean = true,
        flow: AirwallexPaymentRequestFlow? = null,
        listener: PaymentResultListener
    ) {
        val params = when (val paymentMethodType = requireNotNull(paymentMethod.type)) {
            PaymentMethodType.CARD.value -> {
                ConfirmPaymentIntentParams.createCardParams(
                    paymentIntentId = paymentIntentId,
                    clientSecret = clientSecret,
                    paymentMethod = paymentMethod,
                    cvc = cvc,
                    customerId = customerId,
                    paymentConsentId = paymentConsentId,
                    returnUrl = returnUrl,
                    autoCapture = autoCapture
                )
            }

            PaymentMethodType.GOOGLEPAY.value -> {
                ConfirmPaymentIntentParams.createGooglePayParams(
                    paymentIntentId = paymentIntentId,
                    clientSecret = clientSecret,
                    paymentMethod = paymentMethod,
                    cvc = cvc,
                    customerId = customerId,
                    paymentConsentId = paymentConsentId,
                    returnUrl = returnUrl,
                    autoCapture = autoCapture
                )
            }

            else -> {
                ConfirmPaymentIntentParams.createThirdPartPayParams(
                    paymentMethodType = paymentMethodType,
                    paymentIntentId = paymentIntentId,
                    clientSecret = clientSecret,
                    customerId = customerId,
                    paymentConsentId = paymentConsentId,
                    currency = currency,
                    additionalInfo = additionalInfo,
                    returnUrl = returnUrl,
                    flow = flow
                )
            }
        }
        confirmPaymentIntent(params, listener)
    }

    private fun confirmPaymentIntent(
        params: ConfirmPaymentIntentParams,
        listener: PaymentResultListener
    ) {
        val paymentMethodType = params.paymentMethodType
        try {
            val provider =
                AirwallexPlugins.getProvider(ActionComponentProviderType.fromValue(paymentMethodType))
            if (provider == null) {
                listener.onCompleted(
                    AirwallexPaymentStatus.Failure(
                        AirwallexComponentDependencyException(
                            dependency = Dependency.fromValue(
                                PaymentMethodType.fromValue(paymentMethodType).dependencyName
                            )
                        )
                    )
                )
                return
            }
            val device = paymentManager.buildDeviceInfo(AirwallexRisk.sessionId.toString())
            confirmPaymentIntentWithDevice(
                device = device,
                params = params,
                listener = listener
            )
        } catch (e: Exception) {
            listener.onCompleted(
                AirwallexPaymentStatus.Failure(AirwallexCheckoutException(e = e))
            )
        }
    }

    /**
     * Confirm PaymentIntent with Device Fingerprinting
     */
    fun confirmPaymentIntentWithDevice(
        device: Device? = null,
        params: ConfirmPaymentIntentParams,
        listener: PaymentResultListener
    ) {
        val options = when (params.paymentMethodType) {
            // The backend requires passing parameters using the card field in payment_method_options, even if the payment type is Google Pay.
            PaymentMethodType.CARD.value, PaymentMethodType.GOOGLEPAY.value -> {
                buildCardPaymentIntentOptions(params, device)
            }

            else -> {
                buildThirdPartPaymentIntentOptions(params, device)
            }
        }
        paymentManager.startOperation(
            options,
            object : PaymentListener<PaymentIntent> {
                override fun onFailed(exception: AirwallexException) {
                    AirwallexLogger.error("Airwallex confirmPaymentIntentWithDevice fail: type = ${params.paymentMethodType}, onFailed = ${exception.message}")
                    listener.onCompleted(AirwallexPaymentStatus.Failure(exception))
                }

                override fun onSuccess(response: PaymentIntent) {
                    AirwallexLogger.info("Airwallex confirmPaymentIntentWithDevice success: type = ${params.paymentMethodType}, nextAction = ${response.nextAction?.type}")
                    // If the nextAction is null, the payment is completed
                    if (response.nextAction == null) {
                        listener.onCompleted(
                            AirwallexPaymentStatus.Success(
                                response.id,
                                params.paymentConsentId
                            )
                        )
                        return
                    }
                    val provider =
                        AirwallexPlugins.getProvider(ActionComponentProviderType.fromValue(params.paymentMethodType))
                    if (provider == null) {
                        AirwallexLogger.error("Airwallex confirmPaymentIntentWithDevice: type = ${params.paymentMethodType}, Provider is null")
                        listener.onCompleted(
                            AirwallexPaymentStatus.Failure(AirwallexCheckoutException(message = "Missing dependency!"))
                        )
                        return
                    }
                    val nextActionModel = when (params.paymentMethodType) {
                        PaymentMethodType.CARD.value, PaymentMethodType.GOOGLEPAY.value -> CardNextActionModel(
                            paymentManager = paymentManager,
                            clientSecret = params.clientSecret,
                            device = device,
                            paymentIntentId = response.id,
                            currency = response.currency,
                            amount = response.amount,
                            activityProvider = { activity }
                        )

                        else -> null
                    }
                    provider.get().handlePaymentIntentResponse(
                        response.id,
                        response.nextAction,
                        fragment,
                        activity,
                        applicationContext,
                        nextActionModel,
                        listener,
                        params.paymentConsentId
                    )
                }
            }
        )
    }

    private fun buildCreatePaymentMethodOptions(params: CreatePaymentMethodParams): Options.CreatePaymentMethodOptions {
        return Options.CreatePaymentMethodOptions(
            clientSecret = params.clientSecret,
            request = PaymentMethodCreateRequest.Builder()
                .setCustomerId(params.customerId)
                .setRequestId(UUID.randomUUID().toString())
                .setType(PaymentMethodType.CARD)
                .setCard(params.card)
                .setBilling(params.billing)
                .build()
        )
    }

    private fun buildCreatePaymentConsentOptions(params: CreatePaymentConsentParams): Options.CreatePaymentConsentOptions {
        return Options.CreatePaymentConsentOptions(
            clientSecret = params.clientSecret,
            request = PaymentConsentCreateRequest.Builder()
                .setRequestId(UUID.randomUUID().toString())
                .setCustomerId(params.customerId)
                .setPaymentMethodRequest(
                    PaymentMethodRequest(
                        // for googlePay, id is null
                        id = params.paymentMethodId,
                        type = params.paymentMethodType,
                        // provide either id or googlePay
                        googlePay = params.googlePay,
                        paymentRequest = AirwallexPaymentRequest(
                            flow = AirwallexPaymentRequestFlow.IN_APP,
                            osType = "android"
                        )
                    )
                )
                .setNextTriggeredBy(
                    if (params.paymentMethodType == PaymentMethodType.CARD.value || params.paymentMethodType == PaymentMethodType.GOOGLEPAY.value) {
                        params.nextTriggeredBy
                    } else {
                        PaymentConsent.NextTriggeredBy.MERCHANT
                    }
                )
                .setMerchantTriggerReason(params.merchantTriggerReason)
                .setRequiresCvc(params.requiresCvc)
                .build()
        )
    }

    private fun buildCardPaymentIntentOptions(
        params: ConfirmPaymentIntentParams,
        device: Device?
    ): Options.ConfirmPaymentIntentOptions {
        val paymentConsentReference: PaymentConsentReference? =
            if (params.paymentConsentId != null) {
                PaymentConsentReference.Builder()
                    .setId(params.paymentConsentId)
                    .setCvc(params.cvc)
                    .build()
            } else {
                null
            }

        val threeDSecure = ThreeDSecure.Builder()
            .setReturnUrl(AirwallexPlugins.environment.threeDsReturnUrl())
            .build()

        val builder = PaymentIntentConfirmRequest.Builder(
            requestId = UUID.randomUUID().toString()
        )
            .setPaymentMethodOptions(
                PaymentMethodOptions.Builder()
                    .setCardOptions(
                        PaymentMethodOptions.CardOptions.Builder()
                            .setAutoCapture(params.autoCapture)
                            .setThreeDSecure(threeDSecure).build()
                    )
                    .build()
            )
            .setCustomerId(params.customerId)
            .setDevice(device)
            .setPaymentConsentReference(paymentConsentReference)
            .setPaymentMethodRequest(
                if (paymentConsentReference != null) {
                    null
                } else {
                    PaymentMethodRequest.Builder(params.paymentMethodType)
                        .setCardPaymentMethodRequest(
                            card = params.paymentMethod?.card,
                            billing = params.paymentMethod?.billing
                        )
                        .build()
                }
            )

        if (params.returnUrl != null) {
            builder.setReturnUrl(params.returnUrl)
        }

        return Options.ConfirmPaymentIntentOptions(
            clientSecret = params.clientSecret,
            paymentIntentId = params.paymentIntentId,
            request = builder.build()
        )
    }

    private fun buildThirdPartPaymentIntentOptions(
        params: ConfirmPaymentIntentParams,
        device: Device?
    ): Options.ConfirmPaymentIntentOptions {

        val paymentConsentReference: PaymentConsentReference?
        val paymentMethodRequest: PaymentMethodRequest?

        if (params.paymentConsentId != null) {
            paymentConsentReference = PaymentConsentReference.Builder()
                .setId(params.paymentConsentId)
                .build()
            paymentMethodRequest = null
        } else {
            paymentConsentReference = null
            val builder = PaymentMethodRequest.Builder(params.paymentMethodType)
            val additionalInfo = params.additionalInfo
            if (additionalInfo != null) {
                builder.setThirdPartyPaymentMethodRequest(
                    additionalInfo = additionalInfo,
                    flow = params.flow
                )
            } else {
                builder.setThirdPartyPaymentMethodRequest(
                    flow = params.flow
                )
            }
            paymentMethodRequest = builder.build()
        }
        val request = PaymentIntentConfirmRequest.Builder(
            requestId = UUID.randomUUID().toString()
        )
            .setPaymentMethodRequest(paymentMethodRequest)
            .setCustomerId(params.customerId)
            .setDevice(device)
            .setPaymentConsentReference(paymentConsentReference)
            .build()

        return Options.ConfirmPaymentIntentOptions(
            clientSecret = params.clientSecret,
            paymentIntentId = params.paymentIntentId,
            request = request
        )
    }

    suspend fun createPaymentConsent(params: CreatePaymentConsentParams): PaymentConsent {
        return paymentManager.createPaymentConsent(buildCreatePaymentConsentOptions(params))
    }

    private fun createPaymentConsent(
        clientSecret: String,
        customerId: String,
        paymentMethod: PaymentMethod,
        nextTriggeredBy: PaymentConsent.NextTriggeredBy = PaymentConsent.NextTriggeredBy.MERCHANT,
        requiresCvc: Boolean,
        merchantTriggerReason: PaymentConsent.MerchantTriggerReason? = PaymentConsent.MerchantTriggerReason.UNSCHEDULED,
        listener: PaymentListener<PaymentConsent>
    ) {
        val params: CreatePaymentConsentParams =
            when (val paymentMethodType = requireNotNull(paymentMethod.type)) {
                PaymentMethodType.CARD.value -> {
                    CreatePaymentConsentParams.createCardParams(
                        clientSecret = clientSecret,
                        customerId = customerId,
                        paymentMethodId = requireNotNull(paymentMethod.id),
                        nextTriggeredBy = nextTriggeredBy,
                        merchantTriggerReason = merchantTriggerReason,
                        requiresCvc = requiresCvc
                    )
                }

                PaymentMethodType.GOOGLEPAY.value -> {
                    CreatePaymentConsentParams.createGooglePayParams(
                        clientSecret = clientSecret,
                        customerId = customerId,
                        googlePay = requireNotNull(paymentMethod.googlePay),
                        nextTriggeredBy = nextTriggeredBy,
                        merchantTriggerReason = merchantTriggerReason,
                        requiresCvc = requiresCvc
                    )
                }

                else -> {
                    CreatePaymentConsentParams.createThirdPartParams(
                        paymentMethodType = paymentMethodType,
                        clientSecret = clientSecret,
                        customerId = customerId
                    )
                }
            }
        createPaymentConsent(params, listener)
    }

    private fun createPaymentConsent(
        params: CreatePaymentConsentParams,
        listener: PaymentListener<PaymentConsent>
    ) {
        paymentManager.startOperation(
            buildCreatePaymentConsentOptions(params),
            listener
        )
    }

    private fun verifyPaymentConsent(
        paymentConsent: PaymentConsent,
        currency: String,
        amount: BigDecimal? = null,
        cvc: String? = null,
        returnUrl: String? = null,
        listener: PaymentResultListener
    ) {
        if (paymentConsent.requiresCvc && cvc == null) {
            listener.onCompleted(
                AirwallexPaymentStatus.Failure(InvalidParamsException(message = "CVC is required!"))
            )
            return
        }
        val paymentMethodType = paymentConsent.paymentMethod?.type
        try {
            val provider =
                AirwallexPlugins.getProvider(ActionComponentProviderType.fromValue(paymentMethodType))
            if (provider == null) {
                listener.onCompleted(
                    AirwallexPaymentStatus.Failure(
                        AirwallexComponentDependencyException(
                            dependency = Dependency.fromValue(
                                PaymentMethodType.fromValue(paymentMethodType).dependencyName
                            )
                        )
                    )
                )
                return
            }
            val params = VerifyPaymentConsentParams.createParamsByMethodType(
                paymentMethodType = requireNotNull(paymentMethodType),
                clientSecret = requireNotNull(paymentConsent.clientSecret),
                paymentConsentId = requireNotNull(paymentConsent.id),
                amount = amount,
                currency = currency,
                cvc = cvc,
                returnUrl = returnUrl
            )
            val device = paymentManager.buildDeviceInfo(AirwallexRisk.sessionId.toString())
            verifyPaymentConsent(device, params, listener)
        } catch (e: Exception) {
            listener.onCompleted(
                AirwallexPaymentStatus.Failure(AirwallexCheckoutException(e = e))
            )
        }

    }

    /**
     * Checks if the current activity is an internal Airwallex UI activity.
     * Used to determine if payment_launched should be logged (only for API integration).
     * UI integration activities already log payment_launched via AirwallexCheckoutBaseActivity.
     */
    private val isAirwallexUIActivity: Boolean
        get() = activity is AirwallexInternalActivity

    companion object {
        const val AIRWALLEX_CHECKOUT_SCHEMA = "airwallexcheckout"

        /**
         * Initialize some global configurations, better to be called on Application
         */
        fun initialize(
            application: Application,
            configuration: AirwallexConfiguration
        ) {
            PaymentIntentProviderRepository.initialize(application)
            AirwallexPlugins.initialize(configuration)
            initializeComponents(application, configuration.supportComponentProviders)
            AirwallexLogger.initialize(
                application,
                configuration.enableLogging,
                configuration.saveLogToLocal
            )
            AirwallexLogger.debug("Airwallex SDK v${BuildConfigHelper.versionName} initialized")
            AirwallexLogger.debug("Current connected domain: ${configuration.environment.baseUrl()}")
            AirwallexRisk.start(
                applicationContext = application,
                accountId = null,
                configuration = RiskConfiguration(
                    environment = configuration.environment.riskEnvironment,
                    tenant = Tenant.PA,
                    bufferTimeMillis = 5_000L
                )
            )
        }

        /**
         * Initialize Airwallex Components, if you have invoked [initialize] before, no need to call this method
         */
        fun initializeComponents(
            application: Application,
            supportComponentProviders: List<ActionComponentProvider<out ActionComponent>>
        ) {
            supportComponentProviders.forEach {
                it.get().initialize(application)
            }
        }
    }
}
