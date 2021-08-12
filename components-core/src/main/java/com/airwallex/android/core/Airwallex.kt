package com.airwallex.android.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.annotation.UiThread
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
    private var componentProvider: ComponentProvider? = null
    private var airwallexStarter: AbstractAirwallexStarter? = null

    interface PaymentListener<T> {
        fun onFailed(exception: AirwallexException)
        fun onSuccess(response: T) {
            Logger.debug("Card payment success")
        }

        fun onNextActionWithWeChatPay(weChat: WeChat) {
            Logger.debug("Start WeChat Pay $weChat")
        }

        fun onNextActionWithRedirectUrl(url: String) {
            Logger.debug("Start Redirect Url Pay $url")
        }
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
        if (componentProvider?.onActivityResult(requestCode, resultCode, data) == true) {
            return true
        }
        return airwallexStarter?.onActivityResult(requestCode, resultCode, data) ?: false
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
        paymentManager.createPaymentMethod(
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
        paymentManager.retrievePaymentIntent(
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
     * @param params [RetrieveAvailablePaymentMethodParams] used to retrieve the [AvailablePaymentMethodResponse]
     * @param listener the callback of get [AvailablePaymentMethodResponse]
     */
    @UiThread
    fun retrieveAvailablePaymentMethods(
        params: RetrieveAvailablePaymentMethodParams,
        listener: PaymentListener<AvailablePaymentMethodResponse>
    ) {
        paymentManager.retrieveAvailablePaymentMethods(
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
        listener: PaymentListener<PaymentIntent>
    ) {

        val verificationOptions = when (val paymentMethodType = params.paymentMethodType) {
            PaymentMethodType.CARD -> PaymentConsentVerifyRequest.VerificationOptions(
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

        paymentManager.verifyPaymentConsent(
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
                    try {
                        componentProvider = buildComponentProvider(params.paymentMethodType)
                        val provider = requireNotNull(componentProvider)
                        provider.handlePaymentIntentResponse(
                            params.clientSecret,
                            response.nextAction,
                            null,
                            requireNotNull(response.initialPaymentIntentId),
                            currency = requireNotNull(params.currency),
                            amount = requireNotNull(params.amount),
                            listener
                        )
                    } catch (e: Exception) {
                        Logger.error("Build ComponentProvider failed", e)
                        listener.onFailed(AirwallexCheckoutException(message = "You need add the appropriate dependencies!"))
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
        paymentManager.disablePaymentConsent(
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

    /**
     * Checkout the one-off payment
     *
     * @param session a [AirwallexSession] used to present the Checkout flow, required.
     * @param paymentMethod a [PaymentMethod] used to present the Checkout flow, required.
     * @param listener The callback of checkout
     */
    @UiThread
    fun checkout(
        session: AirwallexSession,
        paymentMethod: PaymentMethod,
        listener: PaymentListener<PaymentIntent>
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
     * @param pproAdditionalInfo to support ppro payment
     * @param listener The callback of checkout
     */
    @UiThread
    fun checkout(
        session: AirwallexSession,
        paymentMethod: PaymentMethod,
        paymentConsentId: String? = null,
        cvc: String? = null,
        pproAdditionalInfo: PPROAdditionalInfo? = null,
        listener: PaymentListener<PaymentIntent>
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
                    pproAdditionalInfo = pproAdditionalInfo,
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
                                PaymentMethodType.CARD -> {
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
        pproAdditionalInfo: PPROAdditionalInfo? = null,
        returnUrl: String? = null,
        listener: PaymentListener<PaymentIntent>
    ) {
        val params = when (val paymentMethodType = requireNotNull(paymentMethod.type)) {
            PaymentMethodType.CARD -> {
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
                    pproAdditionalInfo = pproAdditionalInfo,
                    returnUrl = returnUrl
                )
            }
        }
        confirmPaymentIntent(params, listener)
    }

    private fun confirmPaymentIntent(
        params: ConfirmPaymentIntentParams,
        listener: PaymentListener<PaymentIntent>
    ) {
        if (params.paymentMethodType == PaymentMethodType.CARD) {
            try {
                val securityConnector = Class.forName(AIRWALLEX_SECURITY_CONNECTOR_CLASS_NAME)
                    .getConstructor().newInstance() as SecurityConnector
                securityConnector.retrieveSecurityToken(
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
        listener: PaymentListener<PaymentIntent>
    ) {
        try {
            componentProvider = buildComponentProvider(params.paymentMethodType)
            val provider = requireNotNull(componentProvider)
            val options = when (params.paymentMethodType) {
                PaymentMethodType.CARD -> {
                    buildCardPaymentIntentOptions(params, device)
                }
                else -> {
                    buildThirdPartPaymentIntentOptions(params, device)
                }
            }
            paymentManager.confirmPaymentIntent(
                options,
                object : PaymentListener<PaymentIntent> {
                    override fun onFailed(exception: AirwallexException) {
                        listener.onFailed(exception)
                    }

                    override fun onSuccess(response: PaymentIntent) {
                        provider.handlePaymentIntentResponse(
                            params.clientSecret,
                            response.nextAction,
                            device,
                            response.id,
                            response.currency,
                            response.amount,
                            listener
                        )
                    }
                }
            )
        } catch (e: Exception) {
            Logger.error("Build ComponentProvider failed", e)
            listener.onFailed(AirwallexCheckoutException(message = "You need add the appropriate dependencies!"))
        }
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
            val pproInfo = params.pproAdditionalInfo
            if (pproInfo != null) {
                builder.setThirdPartyPaymentMethodRequest(
                    pproInfo.name,
                    pproInfo.email,
                    pproInfo.phone,
                    if (pproInfo.bank != null) pproInfo.bank.currency else params.currency,
                    pproInfo.bank
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

    fun continuePaymentIntent(
        params: ContinuePaymentIntentParams,
        listener: PaymentListener<PaymentIntent>
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
                componentProvider?.handlePaymentIntentResponse(
                    params.clientSecret,
                    response.nextAction,
                    params.device,
                    response.id,
                    response.currency,
                    response.amount,
                    listener
                )
            }
        }

        paymentManager.continuePaymentIntent(
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
                PaymentMethodType.CARD -> {
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
        paymentManager.createPaymentConsent(
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
                        if (params.paymentMethodType == PaymentMethodType.CARD) {
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
        listener: PaymentListener<PaymentIntent>
    ) {
        if (paymentConsent.requiresCvc && cvc == null) {
            listener.onFailed(InvalidParamsException(message = "CVC is required!"))
            return
        }
        val paymentMethodType = paymentConsent.paymentMethod?.type
        val params: VerifyPaymentConsentParams = when (requireNotNull(paymentMethodType)) {
            PaymentMethodType.CARD -> {
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

    private fun buildComponentProvider(paymentMethodType: PaymentMethodType): ComponentProvider {
        val componentProviderClass = when (paymentMethodType) {
            PaymentMethodType.CARD -> {
                Class.forName(COMPONENT_PROVIDER_CARD_CLASS_NAME)
            }
            PaymentMethodType.WECHAT -> {
                Class.forName(COMPONENT_PROVIDER_WECHAT_CLASS_NAME)
            }
            else -> {
                Class.forName(COMPONENT_PROVIDER_REDIRECT_CLASS_NAME)
            }
        }

        return if (fragment != null) {
            val constructor = componentProviderClass.getConstructor(
                Fragment::class.java,
                PaymentManager::class.java
            )
            constructor.newInstance(
                fragment,
                paymentManager
            ) as ComponentProvider
        } else {
            val constructor = componentProviderClass.getConstructor(
                Activity::class.java,
                PaymentManager::class.java
            )
            constructor.newInstance(
                activity,
                paymentManager
            ) as ComponentProvider
        }
    }

    /**
     * For custom UI Flow
     */
    interface PaymentFlowListener {
        fun onCancelled()
        fun onFailed(error: AirwallexException)
    }

    interface PaymentShippingListener : PaymentFlowListener {
        fun onSuccess(shipping: Shipping)
    }

    interface PaymentIntentListener : PaymentFlowListener {
        fun onSuccess(paymentIntent: PaymentIntent) {
            Logger.debug("Card payment success")
        }

        fun onNextActionWithWeChatPay(weChat: WeChat) {
            Logger.debug("Start WeChat Pay $weChat")
        }

        fun onNextActionWithRedirectUrl(url: String) {
            Logger.debug("Start Redirect Url Pay $url")
        }
    }

    /**
     * Launch the shipping flow to allow the user to fill the shipping information
     *
     * @param shipping a [Shipping] used to present the shipping flow, it's optional
     * @param shippingFlowListener The callback of present the shipping flow
     */
    fun presentShippingFlow(
        shipping: Shipping? = null,
        shippingFlowListener: PaymentShippingListener
    ) {
        try {
            airwallexStarter = buildAirwallexStarter()
            airwallexStarter?.presentShippingFlow(shipping, shippingFlowListener)
        } catch (e: Exception) {
            Logger.error("Can not find AirwallexStarter", e)
            shippingFlowListener.onFailed(AirwallexCheckoutException(message = "Please add ui-core dependency"))
        }
    }

    /**
     * Launch the payment flow to allow the user to complete the entire payment flow
     *
     * @param session a [AirwallexSession] used to present the payment flow
     * @param clientSecretProvider a [ClientSecretProvider] used to generate client-secret, just required for recurring payments
     * @param paymentFlowListener The callback of present entire payment flow
     */
    fun presentPaymentFlow(
        session: AirwallexSession,
        clientSecretProvider: ClientSecretProvider? = null,
        paymentFlowListener: PaymentIntentListener
    ) {
        if (clientSecretProvider == null && (session is AirwallexRecurringSession || session is AirwallexRecurringWithIntentSession)) {
            paymentFlowListener.onFailed(AirwallexCheckoutException(message = "clientSecretProvider can not be null on recurring flow"))
            return
        }
        try {
            airwallexStarter = buildAirwallexStarter()
            airwallexStarter?.presentPaymentFlow(session, clientSecretProvider, paymentFlowListener)
        } catch (e: Exception) {
            Logger.error("Can not find AirwallexStarter", e)
            paymentFlowListener.onFailed(AirwallexCheckoutException(message = "Please add ui-core dependency"))
        }
    }

    private fun buildAirwallexStarter(): AbstractAirwallexStarter {
        val airwallexStarterClass = Class.forName(AIRWALLEX_STARTER_CLASS_NAME)
        return if (fragment != null) {
            val constructor = airwallexStarterClass.getConstructor(
                Fragment::class.java
            )
            constructor.newInstance(
                fragment
            ) as AbstractAirwallexStarter
        } else {
            val constructor = airwallexStarterClass.getConstructor(
                Activity::class.java
            )
            constructor.newInstance(
                activity,
            ) as AbstractAirwallexStarter
        }
    }

    companion object {

        private const val COMPONENT_PROVIDER_CARD_CLASS_NAME =
            "com.airwallex.android.card.CardComponentProvider"

        private const val COMPONENT_PROVIDER_WECHAT_CLASS_NAME =
            "com.airwallex.android.wechat.WeChatComponentProvider"

        private const val COMPONENT_PROVIDER_REDIRECT_CLASS_NAME =
            "com.airwallex.android.redirect.RedirectComponentProvider"

        private const val AIRWALLEX_STARTER_CLASS_NAME =
            "com.airwallex.android.ui.AirwallexStarter"

        private const val AIRWALLEX_SECURITY_CONNECTOR_CLASS_NAME =
            "com.airwallex.android.card.AirwallexSecurityConnector"

        const val AIRWALLEX_CHECKOUT_SCHEMA = "airwallexcheckout://"

        /**
         * Initialize some global configurations, better to be called on Application
         */
        fun initialize(configuration: AirwallexConfiguration) {
            AirwallexPlugins.initialize(configuration)
        }
    }
}
