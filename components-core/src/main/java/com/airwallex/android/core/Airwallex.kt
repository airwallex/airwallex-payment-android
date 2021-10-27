package com.airwallex.android.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.annotation.UiThread
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.exception.InvalidParamsException
import com.airwallex.android.core.log.Logger
import com.airwallex.android.core.model.*
import java.math.BigDecimal
import java.util.*

class Airwallex internal constructor(
    private val fragment: Fragment?,
    private val activity: Activity,
    private val paymentManager: PaymentManager,
    private val applicationContext: Context,
) {

    interface PaymentListener<T> {
        fun onSuccess(response: T)
        fun onFailed(exception: AirwallexException)
    }

    interface PaymentResultListener {
        fun onSuccess(paymentIntentId: String, isRedirecting: Boolean = false)
        fun onFailed(exception: AirwallexException)
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

    constructor(activity: Activity) : this(
        null,
        activity,
        AirwallexPaymentManager(AirwallexApiRepository()),
        activity.applicationContext
    )

    @VisibleForTesting
    constructor(activity: Activity, applicationContext: Context) : this(
        null,
        activity,
        AirwallexPaymentManager(AirwallexApiRepository()),
        applicationContext
    )

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
    fun handlePaymentData(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        val provider = AirwallexPlugins.getCardProvider()
        if (provider == null) {
            Logger.error("Missing ${PaymentMethodType.CARD.dependencyName} dependency!")
            return false
        }
        if (provider.get().handleActivityResult(requestCode, resultCode, data)) {
            return true
        }
        return false
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
            AirwallexApiRepository.CreatePaymentMethodOptions(
                clientSecret = params.clientSecret,
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
            AirwallexApiRepository.RetrievePaymentIntentOptions(
                clientSecret = params.clientSecret,
                paymentIntentId = params.paymentIntentId
            ),
            listener
        )
    }

    /**
     * Retrieve available payment methods
     *
     * @param params [RetrieveAvailablePaymentMethodParams] used to retrieve the [AvailablePaymentMethodTypeResponse]
     * @param listener the callback of get [AvailablePaymentMethodTypeResponse]
     */
    @UiThread
    fun retrieveAvailablePaymentMethods(
        params: RetrieveAvailablePaymentMethodParams,
        listener: PaymentListener<AvailablePaymentMethodTypeResponse>
    ) {
        paymentManager.startOperation(
            AirwallexApiRepository.RetrieveAvailablePaymentMethodsOptions(
                clientSecret = params.clientSecret,
                pageNum = params.pageNum,
                pageSize = params.pageSize,
                active = params.active,
                transactionCurrency = params.transactionCurrency,
                transactionMode = params.transactionMode
            ),
            listener
        )
    }

    /**
     * Verify a [PaymentConsent]
     *
     * @param params [VerifyPaymentConsentParams] used to verify the [PaymentConsent]
     * @param listener a [PaymentListener] to receive the response or error
     */
    @UiThread
    fun verifyPaymentConsent(
        params: VerifyPaymentConsentParams,
        listener: PaymentResultListener
    ) {

        val verificationOptions = when (val paymentMethodType = params.paymentMethodType) {
            PaymentMethodType.CARD.value -> PaymentConsentVerifyRequest.VerificationOptions(
                type = paymentMethodType,
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
            AirwallexApiRepository.VerifyPaymentConsentOptions(
                clientSecret = params.clientSecret,
                paymentConsentId = params.paymentConsentId,
                request = PaymentConsentVerifyRequest.Builder()
                    .setRequestId(UUID.randomUUID().toString())
                    .setVerificationOptions(verificationOptions)
                    .setReturnUrl(params.returnUrl)
                    .build()
            ),
            object : PaymentListener<PaymentConsent> {
                override fun onFailed(exception: AirwallexException) {
                    listener.onFailed(exception)
                }

                override fun onSuccess(response: PaymentConsent) {
                    val provider = AirwallexPlugins.getProvider(response.nextAction)
                    if (provider == null) {
                        listener.onFailed(AirwallexCheckoutException(message = "Missing dependency!"))
                        return
                    }

                    val paymentIntentId = requireNotNull(response.initialPaymentIntentId)
                    val cardNextActionModel = when (params.paymentMethodType) {
                        PaymentMethodType.CARD.value -> CardNextActionModel(
                            fragment = fragment,
                            activity = activity,
                            paymentManager = paymentManager,
                            clientSecret = params.clientSecret,
                            device = null,
                            paymentIntentId = paymentIntentId,
                            currency = requireNotNull(params.currency),
                            amount = requireNotNull(params.amount),
                        )
                        else -> null
                    }

                    provider.get().handlePaymentIntentResponse(
                        paymentIntentId,
                        response.nextAction,
                        activity,
                        applicationContext,
                        cardNextActionModel,
                        listener
                    )
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
            AirwallexApiRepository.DisablePaymentConsentOptions(
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
            AirwallexApiRepository.RetrieveBankOptions(
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
            AirwallexApiRepository.RetrievePaymentMethodTypeInfoOptions(
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
        listener: PaymentResultListener
    ) {
        this.checkout(session, paymentMethod, null, null, null, listener)
    }

    /**
     * Checkout the payment, include one-off & recurring
     *
     * @param session a [AirwallexSession] used to present the Checkout flow, required.
     * @param paymentMethod a [PaymentMethod] used to present the Checkout flow, required.
     * @param paymentConsentId the ID of the [PaymentConsent], optional. (CIT & Card will need this field for subsequent payments, paymentConsentId is not empty indicating a subsequent payment, empty indicating a recurring )
     * @param cvc the CVC of the Credit Card, optional.
     * @param additionalInfo to support ppro payment
     * @param listener The callback of checkout
     */
    @UiThread
    fun checkout(
        session: AirwallexSession,
        paymentMethod: PaymentMethod,
        paymentConsentId: String? = null,
        cvc: String? = null,
        additionalInfo: Map<String, String>? = null,
        listener: PaymentResultListener
    ) {
        when (session) {
            is AirwallexPaymentSession -> {
                val paymentIntent = session.paymentIntent
                confirmPaymentIntent(
                    paymentIntentId = paymentIntent.id,
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    paymentMethod = paymentMethod,
                    cvc = cvc,
                    currency = session.currency,
                    customerId = paymentIntent.customerId,
                    paymentConsentId = paymentConsentId,
                    additionalInfo = additionalInfo,
                    returnUrl = session.returnUrl,
                    listener = listener
                )
            }
            is AirwallexRecurringSession -> {
                val customerId = session.customerId
                ClientSecretRepository.getInstance().retrieveClientSecret(
                    customerId,
                    object : ClientSecretRepository.ClientSecretRetrieveListener {
                        override fun onClientSecretRetrieve(clientSecret: ClientSecret) {
                            createPaymentConsent(
                                clientSecret = clientSecret.value,
                                customerId = customerId,
                                paymentMethod = paymentMethod,
                                nextTriggeredBy = session.nextTriggerBy,
                                requiresCvc = session.requiresCVC,
                                merchantTriggerReason = session.merchantTriggerReason,
                                listener = object : PaymentListener<PaymentConsent> {
                                    override fun onFailed(exception: AirwallexException) {
                                        listener.onFailed(exception)
                                    }

                                    override fun onSuccess(response: PaymentConsent) {
                                        verifyPaymentConsent(
                                            paymentConsent = response,
                                            currency = session.currency,
                                            amount = session.amount,
                                            cvc = cvc,
                                            returnUrl = session.returnUrl,
                                            listener = listener
                                        )
                                    }
                                }
                            )
                        }

                        override fun onClientSecretError(errorMessage: String) {
                            listener.onFailed(AirwallexCheckoutException(message = errorMessage))
                        }
                    }
                )
            }
            is AirwallexRecurringWithIntentSession -> {
                val paymentIntent = session.paymentIntent
                createPaymentConsent(
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    customerId = session.customerId,
                    paymentMethod = paymentMethod,
                    nextTriggeredBy = session.nextTriggerBy,
                    requiresCvc = session.requiresCVC,
                    merchantTriggerReason = session.merchantTriggerReason,
                    listener = object : PaymentListener<PaymentConsent> {
                        override fun onFailed(exception: AirwallexException) {
                            listener.onFailed(exception)
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
                                        returnUrl = session.returnUrl,
                                        listener = listener
                                    )
                                }
                                else -> {
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
        }
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
                    returnUrl = returnUrl
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
                    returnUrl = returnUrl
                )
            }
        }
        confirmPaymentIntent(params, listener)
    }

    private fun confirmPaymentIntent(
        params: ConfirmPaymentIntentParams,
        listener: PaymentResultListener
    ) {
        if (params.paymentMethodType == PaymentMethodType.CARD.value) {
            try {
                val provider = AirwallexPlugins.getCardProvider()
                if (provider == null) {
                    listener.onFailed(AirwallexCheckoutException(message = "Missing ${Dependency.CARD.value} dependency!"))
                    return
                }
                provider.get().retrieveSecurityToken(
                    params.paymentIntentId, applicationContext,
                    object : SecurityTokenListener {
                        override fun onResponse(deviceId: String) {
                            val device =
                                PaymentManager.buildDeviceInfo(deviceId, applicationContext)
                            confirmPaymentIntentWithDevice(
                                device = device,
                                params = params,
                                listener = listener
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                listener.onFailed(AirwallexCheckoutException(message = "Please add card dependency"))
            }
        } else {
            confirmPaymentIntentWithDevice(device = null, params = params, listener = listener)
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
            PaymentMethodType.CARD.value -> {
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
                    listener.onFailed(exception)
                }

                override fun onSuccess(response: PaymentIntent) {
                    val cardNextActionModel = when (params.paymentMethodType) {
                        PaymentMethodType.CARD.value -> CardNextActionModel(
                            fragment = fragment,
                            activity = activity,
                            paymentManager = paymentManager,
                            clientSecret = params.clientSecret,
                            device = device,
                            paymentIntentId = response.id,
                            currency = response.currency,
                            amount = response.amount,
                        )
                        else -> null
                    }
                    val provider = AirwallexPlugins.getProvider(response.nextAction)
                    if (provider == null) {
                        listener.onFailed(AirwallexCheckoutException(message = "Missing dependency!"))
                        return
                    }
                    provider.get().handlePaymentIntentResponse(
                        response.id,
                        response.nextAction,
                        activity,
                        applicationContext,
                        cardNextActionModel,
                        listener
                    )
                }
            }
        )
    }

    private fun buildCardPaymentIntentOptions(
        params: ConfirmPaymentIntentParams,
        device: Device?
    ): AirwallexApiRepository.ConfirmPaymentIntentOptions {
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
            .setReturnUrl("https://www.airwallex.com")
            .build()

        val request = PaymentIntentConfirmRequest.Builder(
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
            .build()

        return AirwallexApiRepository.ConfirmPaymentIntentOptions(
            clientSecret = params.clientSecret,
            paymentIntentId = params.paymentIntentId,
            request = request
        )
    }

    private fun buildThirdPartPaymentIntentOptions(
        params: ConfirmPaymentIntentParams,
        device: Device?
    ): AirwallexApiRepository.ConfirmPaymentIntentOptions {

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
                    additionalInfo = additionalInfo
                )
            } else {
                builder.setThirdPartyPaymentMethodRequest()
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

        return AirwallexApiRepository.ConfirmPaymentIntentOptions(
            clientSecret = params.clientSecret,
            paymentIntentId = params.paymentIntentId,
            request = request
        )
    }

    fun continueDccPaymentIntent(
        params: ContinuePaymentIntentParams,
        listener: PaymentResultListener
    ) {
        val request = PaymentIntentContinueRequest(
            requestId = UUID.randomUUID().toString(),
            type = params.type,
            threeDSecure = params.threeDSecure,
            device = params.device,
            useDcc = params.useDcc
        )

        val paymentListener = object : PaymentListener<PaymentIntent> {
            override fun onFailed(exception: AirwallexException) {
                // Payment failed
                listener.onFailed(exception)
            }

            override fun onSuccess(response: PaymentIntent) {
                // Handle next action
                val provider = AirwallexPlugins.getCardProvider()
                if (provider == null) {
                    listener.onFailed(AirwallexCheckoutException(message = "Missing ${PaymentMethodType.CARD.dependencyName} dependency!"))
                    return
                }
                // Only card
                provider.get().handlePaymentIntentResponse(
                    response.id,
                    response.nextAction,
                    activity,
                    applicationContext,
                    CardNextActionModel(
                        fragment = fragment,
                        activity = activity,
                        paymentManager = paymentManager,
                        clientSecret = params.clientSecret,
                        device = params.device,
                        paymentIntentId = response.id,
                        currency = response.currency,
                        amount = response.amount,
                    ),
                    listener
                )
            }
        }

        paymentManager.startOperation(
            AirwallexApiRepository.ContinuePaymentIntentOptions(
                clientSecret = params.clientSecret,
                paymentIntentId = params.paymentIntentId,
                request = request
            ),
            paymentListener
        )
    }

    private fun createPaymentConsent(
        clientSecret: String,
        customerId: String,
        paymentMethod: PaymentMethod,
        nextTriggeredBy: PaymentConsent.NextTriggeredBy = PaymentConsent.NextTriggeredBy.MERCHANT,
        requiresCvc: Boolean,
        merchantTriggerReason: PaymentConsent.MerchantTriggerReason = PaymentConsent.MerchantTriggerReason.UNSCHEDULED,
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
            AirwallexApiRepository.CreatePaymentConsentOptions(
                clientSecret = params.clientSecret,
                request = PaymentConsentCreateRequest.Builder()
                    .setRequestId(UUID.randomUUID().toString())
                    .setCustomerId(params.customerId)
                    .setPaymentMethodRequest(
                        PaymentMethodRequest(
                            id = params.paymentMethodId,
                            type = params.paymentMethodType
                        )
                    )
                    .setNextTriggeredBy(
                        if (params.paymentMethodType == PaymentMethodType.CARD.value) {
                            params.nextTriggeredBy
                        } else {
                            PaymentConsent.NextTriggeredBy.MERCHANT
                        }
                    )
                    .setMerchantTriggerReason(params.merchantTriggerReason)
                    .setRequiresCvc(params.requiresCvc)
                    .build()
            ),
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
            listener.onFailed(InvalidParamsException(message = "CVC is required!"))
            return
        }
        val paymentMethodType = paymentConsent.paymentMethod?.type
        val params: VerifyPaymentConsentParams = when (requireNotNull(paymentMethodType)) {
            PaymentMethodType.CARD.value -> {
                VerifyPaymentConsentParams.createCardParams(
                    clientSecret = requireNotNull(paymentConsent.clientSecret),
                    paymentConsentId = requireNotNull(paymentConsent.id),
                    amount = amount,
                    currency = currency,
                    cvc = cvc,
                    returnUrl = returnUrl
                )
            }
            else -> {
                VerifyPaymentConsentParams.createThirdPartParams(
                    paymentMethodType = paymentMethodType,
                    clientSecret = requireNotNull(paymentConsent.clientSecret),
                    paymentConsentId = requireNotNull(paymentConsent.id),
                    returnUrl = returnUrl
                )
            }
        }
        verifyPaymentConsent(params, listener)
    }

    companion object {
        const val AIRWALLEX_CHECKOUT_SCHEMA = "airwallexcheckout://"

        /**
         * Initialize some global configurations, better to be called on Application
         */
        fun initialize(configuration: AirwallexConfiguration) {
            initialize(configuration, null)
        }

        /**
         * Initialize some global configurations, better to be called on Application
         */
        fun initialize(
            configuration: AirwallexConfiguration,
            clientSecretProvider: ClientSecretProvider? = null
        ) {
            AirwallexPlugins.initialize(configuration)
            clientSecretProvider?.let {
                ClientSecretRepository.init(it)
            }
        }
    }
}