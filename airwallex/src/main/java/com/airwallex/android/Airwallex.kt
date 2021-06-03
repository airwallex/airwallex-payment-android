package com.airwallex.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import com.airwallex.android.exception.DccException
import com.airwallex.android.exception.InvalidParamsException
import com.airwallex.android.exception.RedirectException
import com.airwallex.android.exception.ThreeDSException
import com.airwallex.android.model.*
import com.airwallex.android.view.*
import com.airwallex.android.view.DccActivityLaunch
import com.cardinalcommerce.cardinalmobilesdk.models.CardinalActionCode
import com.cardinalcommerce.cardinalmobilesdk.models.ValidateResponse
import java.math.BigDecimal
import java.util.*
import kotlin.jvm.Throws

/**
 * Entry-point to the Airwallex SDK.
 */
class Airwallex internal constructor(
    private val activity: Activity,
    private val paymentManager: PaymentManager,
    private val airwallexStarter: AirwallexStarter,
    private val applicationContext: Context,
    private val dccActivityLaunch: DccActivityLaunch,
    private val threeDSecureActivityLaunch: ThreeDSecureActivityLaunch
) {
    private val securityConnector: SecurityConnector by lazy {
        AirwallexSecurityConnector()
    }

    /**
     * Generic interface for an Airwallex API operation callback that either returns a [T], or an [Exception]
     */
    interface PaymentListener<T> {
        fun onFailed(exception: Exception)
        fun onSuccess(response: T)
    }

    interface PaymentResultListener<T> : PaymentListener<T> {
        fun onNextActionWithWeChatPay(weChat: WeChat)
        fun onNextActionWithAlipayUrl(url: String)
    }

    /**
     * Constructor of [Airwallex]
     */
    constructor(fragment: Fragment) : this(
        fragment.requireActivity(),
        AirwallexPaymentManager(AirwallexApiRepository()),
        AirwallexStarter(fragment),
        fragment.requireContext().applicationContext,
        DccActivityLaunch(fragment),
        ThreeDSecureActivityLaunch(fragment)
    )

    constructor(activity: Activity) : this(
        activity,
        AirwallexPaymentManager(AirwallexApiRepository()),
        AirwallexStarter(activity),
        activity.applicationContext,
        DccActivityLaunch(activity),
        ThreeDSecureActivityLaunch(activity)
    )

    /**
     * Confirm a [PaymentIntent] by ID
     *
     * @param params [ConfirmPaymentIntentParams] used to confirm [PaymentIntent]
     * @param listener a [PaymentListener] to receive the response or error
     */
    @UiThread
    fun confirmPaymentIntent(
        params: ConfirmPaymentIntentParams,
        listener: PaymentResultListener<PaymentIntent>
    ) {
        // Retrieve Device Fingerprinting
        securityConnector.retrieveSecurityToken(
            params.paymentIntentId, applicationContext,
            object : AirwallexSecurityConnector.SecurityTokenListener {
                override fun onResponse(deviceId: String) {
                    // Confirm PaymentIntent with Device Fingerprinting
                    paymentManager.confirmPaymentIntent(applicationContext, deviceId, params, dccActivityLaunch, threeDSecureActivityLaunch, listener)
                }
            }
        )
    }

    /**
     * Continue a [PaymentIntent] - select your current currency
     *
     * @param applicationContext the Application Context that is start confirm the payment intent
     * @param params [ContinuePaymentIntentParams] used to continue [PaymentIntent]
     * @param listener a [PaymentListener] to receive the response or error
     */
    @UiThread
    internal fun continuePaymentIntent(
        applicationContext: Context,
        threeDSecureActivityLaunch: ThreeDSecureActivityLaunch,
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
        paymentManager.continueDccPaymentIntent(
            applicationContext,
            threeDSecureActivityLaunch,
            AirwallexApiRepository.ContinuePaymentIntentOptions(
                clientSecret = params.clientSecret,
                paymentIntentId = params.paymentIntentId,
                request = request
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
     * Retrieve available payment methods
     *
     * @param params [RetrieveAvailablePaymentMethodParams] used to retrieve the [AvailablePaymentMethodResponse]
     * @param listener the callback of get [AvailablePaymentMethodResponse]
     */
    @UiThread
    internal fun retrieveAvailablePaymentMethods(
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
     * Create a [PaymentConsent], Support alipayhk, kakaopay, gcash, dana, tng
     *
     * @param listener a [PaymentListener] to receive the response or error
     */
    @UiThread
    fun createPaymentConsent(
        params: CreatePaymentConsentParams,
        listener: PaymentListener<PaymentConsent>
    ) {
        if (params.paymentMethodType != PaymentMethodType.CARD && params.nextTriggeredBy == PaymentConsent.NextTriggeredBy.CUSTOMER) {
            listener.onFailed(InvalidParamsException("next_triggered_by must be merchant with ${params.paymentMethodType}"))
            return
        }

        when (params.nextTriggeredBy) {
            PaymentConsent.NextTriggeredBy.MERCHANT -> {
                if (params.requiresCvc) {
                    listener.onFailed(InvalidParamsException("requires_cvc can only be set to true when next_triggered_by is customer"))
                    return
                }
            }
            PaymentConsent.NextTriggeredBy.CUSTOMER -> {
                if (params.merchantTriggerReason == PaymentConsent.MerchantTriggerReason.SCHEDULED) {
                    listener.onFailed(InvalidParamsException("merchant_trigger_reason can only be set to scheduled when next_triggered_by is merchant"))
                    return
                }
            }
        }

        paymentManager.createPaymentConsent(
            AirwallexApiRepository.CreatePaymentConsentOptions(
                clientSecret = params.clientSecret,
                request = PaymentConsentCreateRequest.Builder()
                    .setRequestId(UUID.randomUUID().toString())
                    .setCustomerId(params.customerId)
                    .setPaymentMethod(
                        PaymentMethod(
                            id = params.paymentMethodId,
                            type = params.paymentMethodType
                        )
                    )
                    .setNextTriggeredBy(params.nextTriggeredBy)
                    .setMerchantTriggerReason(params.merchantTriggerReason)
                    .setRequiresCvc(params.requiresCvc)
                    .build()
            ),
            listener
        )
    }

    /**
     * Verify a [PaymentConsent]
     *
     * @param listener a [PaymentListener] to receive the response or error
     */
    @UiThread
    fun verifyPaymentConsent(
        params: VerifyPaymentConsentParams,
        listener: PaymentResultListener<PaymentIntent>
    ) {
        paymentManager.verifyPaymentConsent(applicationContext, params, dccActivityLaunch, threeDSecureActivityLaunch, listener)
    }

    /**
     * Disable a [PaymentConsent] by ID
     *
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
     * Retrieve a [PaymentConsent] by ID
     *
     * @param listener a [PaymentListener] to receive the response or error
     */
    @UiThread
    fun retrievePaymentConsent(
        params: RetrievePaymentConsentParams,
        listener: PaymentListener<PaymentConsent>
    ) {
        paymentManager.retrievePaymentConsent(
            AirwallexApiRepository.RetrievePaymentConsentOptions(
                clientSecret = params.clientSecret,
                paymentConsentId = params.paymentConsentId
            ),
            listener
        )
    }

    @Throws(RedirectException::class)
    fun handleAction(redirectUrl: String) {
        RedirectUtil.makeRedirect(activity = activity, redirectUrl = redirectUrl)
    }

    // For the custom flow
    interface PaymentFlowListener {
        fun onCancelled()
    }

    /**
     * Represents a listener for PaymentShipping actions
     */
    interface PaymentShippingListener : PaymentFlowListener {
        fun onSuccess(shipping: Shipping)
    }

    /**
     * Represents a listener for PaymentIntent actions
     */
    interface PaymentIntentListener : PaymentFlowListener {
        fun onSuccess(paymentIntent: PaymentIntent)
        fun onFailed(error: Exception)
        fun onNextActionWithWeChatPay(weChat: WeChat)
        fun onNextActionWithAlipayUrl(url: String)
    }

    /**
     * Represents a listener for PaymentIntent Card actions
     */
    interface PaymentIntentCardListener : PaymentFlowListener {
        fun onSuccess(paymentIntent: PaymentIntent)
        fun onFailed(error: Exception)
    }

    /**
     * Represents a listener for PaymentMethod actions
     */
    interface PaymentMethodListener : PaymentFlowListener {
        // CVC returns only when payment is first created, otherwise null
        fun onSuccess(paymentMethod: PaymentMethod, paymentConsentId: String?, cvc: String?)
        fun onFailed(error: Exception)
    }

    /**
     * Represents a listener for Add PaymentMethod
     */
    interface AddPaymentMethodListener : PaymentFlowListener {
        fun onSuccess(paymentMethod: PaymentMethod, cvc: String)
        fun onFailed(error: Exception)
    }

    /**
     * Launch the [PaymentShippingActivity] to allow the user to fill the shipping information
     *
     * @param shipping a [Shipping] used to present the shipping flow, it's optional
     * @param shippingFlowListener The callback of present the shipping flow
     */
    fun presentShippingFlow(
        shipping: Shipping? = null,
        shippingFlowListener: PaymentShippingListener
    ) {
        airwallexStarter.presentShippingFlow(shipping, shippingFlowListener)
    }

    /**
     * Launch the [AddPaymentMethodActivity] to allow the user to add a payment method
     *
     * @param session a [AirwallexSession] used to present the Add Payment Method flow
     * @param clientSecretProvider a [ClientSecretProvider] used to generate client-secret
     * @param addPaymentMethodFlowListener The callback of present the add payment method flow
     */
    fun presentAddPaymentMethodFlow(
        session: AirwallexSession,
        clientSecretProvider: ClientSecretProvider,
        addPaymentMethodFlowListener: AddPaymentMethodListener
    ) {
        airwallexStarter.presentAddPaymentMethodFlow(session, clientSecretProvider, addPaymentMethodFlowListener)
    }

    /**
     * Launch the [PaymentMethodsActivity] to allow the user to select a payment method or add a new one
     *
     * @param session a [AirwallexSession] used to present the Select Payment Method flow
     * @param clientSecretProvider a [ClientSecretProvider] used to generate client-secret
     * @param selectPaymentMethodFlowListener The callback of present the select payment method flow
     */
    fun presentSelectPaymentMethodFlow(
        session: AirwallexSession,
        clientSecretProvider: ClientSecretProvider,
        selectPaymentMethodFlowListener: PaymentMethodListener
    ) {
        airwallexStarter.presentSelectPaymentMethodFlow(session, clientSecretProvider, selectPaymentMethodFlowListener)
    }

    /**
     * Launch the [PaymentCheckoutActivity] to allow the user to confirm [PaymentIntent] using the specified [PaymentMethod]
     *
     * @param session a [AirwallexSession] used to present the Checkout flow
     * @param paymentMethod a [PaymentMethod] used to present the Checkout flow
     * @param paymentConsentId the ID of the [PaymentConsent], required.
     * @param cvc the CVC of the Credit Card, required.
     * @param paymentDetailListener The callback of present the select payment detail flow
     */
    fun presentPaymentDetailFlow(
        session: AirwallexSession,
        paymentMethod: PaymentMethod,
        paymentConsentId: String?,
        cvc: String?,
        paymentDetailListener: PaymentIntentCardListener
    ) {
        airwallexStarter.presentPaymentDetailFlow(session, paymentMethod, paymentConsentId, cvc, paymentDetailListener)
    }

    /**
     * Launch the [PaymentMethodsActivity] to allow the user to complete the entire payment flow
     *
     * @param session a [AirwallexSession] used to present the payment flow
     * @param clientSecretProvider a [ClientSecretProvider] used to generate client-secret
     * @param paymentFlowListener The callback of present entire payment flow
     */
    fun presentPaymentFlow(
        session: AirwallexSession,
        clientSecretProvider: ClientSecretProvider,
        paymentFlowListener: PaymentIntentListener
    ) {
        airwallexStarter.presentPaymentFlow(session, clientSecretProvider, paymentFlowListener)
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
    fun handlePaymentData(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode == DccActivityLaunch.REQUEST_CODE) {
            paymentManager.dccCallback?.let {
                try {
                    handleDccData(data, resultCode, it)
                } catch (e: Exception) {
                    it.onFailed(DccException(message = e.localizedMessage ?: "Dcc failed."))
                }
            }
            return true
        } else if (requestCode == ThreeDSecureActivityLaunch.REQUEST_CODE) {
            paymentManager.threeDSecureCallback?.let {
                try {
                    handleThreeDSecureData(data, it)
                } catch (e: Exception) {
                    it.onFailed(ThreeDSException(message = e.localizedMessage ?: "3DS failed."))
                }
            }
            return true
        }
        return airwallexStarter.onActivityResult(requestCode, resultCode, data)
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
                    val cancel = data.getBooleanExtra(ThreeDSecureActivity.EXTRA_THREE_CANCEL, false)
                    if (cancel) {
                        Logger.debug("3DS 1.0 canceled")
                        callback.onFailed(ThreeDSException(message = "3DS 1.0 failed. Reason: User cancel the 3DS 1.0"))
                    } else {
                        val reason = data.getStringExtra(ThreeDSecureActivity.EXTRA_THREE_FAILED_REASON)
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
                val validateResponse = data.getSerializableExtra(ThreeDSecureActivity.EXTRA_VALIDATION_RESPONSE) as ValidateResponse
                if (validateResponse.actionCode != null && validateResponse.actionCode == CardinalActionCode.CANCEL) {
                    Logger.debug("3DS 2.0 canceled")
                    callback.onFailed(ThreeDSException(message = "3DS 2.0 failed. Reason: User cancel the 3DS 2.0"))
                } else {
                    if (validateResponse.errorDescription.toLowerCase(Locale.ROOT) == "success") {
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

    private fun createPaymentConsent(
        clientSecret: String,
        customerId: String,
        paymentMethod: PaymentMethod,
        nextTriggeredBy: PaymentConsent.NextTriggeredBy = PaymentConsent.NextTriggeredBy.MERCHANT,
        merchantTriggerReason: PaymentConsent.MerchantTriggerReason = PaymentConsent.MerchantTriggerReason.UNSCHEDULED,
        requiresCvc: Boolean = false,
        listener: PaymentListener<PaymentConsent>
    ) {
        val params: CreatePaymentConsentParams = when (requireNotNull(paymentMethod.type)) {
            PaymentMethodType.CARD -> {
                if (requiresCvc && nextTriggeredBy == PaymentConsent.NextTriggeredBy.MERCHANT) {
                    listener.onFailed(InvalidParamsException(message = "Only applicable when next_triggered_by is customer and the payment_method.type is card"))
                    return
                }
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
                    paymentMethodType = paymentMethod.type,
                    clientSecret = clientSecret,
                    customerId = customerId,
                    merchantTriggerReason = merchantTriggerReason
                )
            }
        }
        createPaymentConsent(params, listener)
    }

    private fun verifyPaymentConsent(
        paymentConsent: PaymentConsent,
        currency: String,
        amount: BigDecimal? = null,
        cvc: String? = null,
        listener: PaymentResultListener<PaymentIntent>
    ) {
        if (paymentConsent.requiresCvc && cvc == null) {
            listener.onFailed(InvalidParamsException(message = "CVC is required!"))
        }
        val returnUrl = "$AIRWALLEX_CHECKOUT_SCHEMA${activity.packageName}"
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

    private fun confirmPaymentIntent(
        paymentIntentId: String,
        clientSecret: String,
        paymentMethod: PaymentMethod,
        cvc: String? = null,
        customerId: String? = null,
        paymentConsentId: String? = null,
        name: String? = null,
        email: String? = null,
        phone: String? = null,
        listener: PaymentResultListener<PaymentIntent>
    ) {
        val params = when (requireNotNull(paymentMethod.type)) {
            PaymentMethodType.CARD -> {
                ConfirmPaymentIntentParams.createCardParams(
                    paymentIntentId = paymentIntentId,
                    clientSecret = clientSecret,
                    paymentMethodId = requireNotNull(paymentMethod.id),
                    cvc = requireNotNull(cvc),
                    customerId = customerId,
                    paymentConsentId = paymentConsentId
                )
            }
            else -> {
                ConfirmPaymentIntentParams.createThirdPartPayParams(
                    paymentMethodType = paymentMethod.type,
                    paymentIntentId = paymentIntentId,
                    clientSecret = clientSecret,
                    customerId = customerId,
                    paymentConsentId = paymentConsentId,
                    name = name,
                    email = email,
                    phone = phone
                )
            }
        }
        confirmPaymentIntent(params, listener)
    }

    /**
     * Checkout the payment, include one-off & recurring
     *
     * @param session a [AirwallexSession] used to present the Checkout flow, required.
     * @param paymentMethod a [PaymentMethod] used to present the Checkout flow, required.
     * @param paymentConsentId the ID of the [PaymentConsent], optional. (CIT & Card will need this field for subsequent payments, paymentConsentId is not empty indicating a subsequent payment, empty indicating a recurring )
     * @param cvc the CVC of the Credit Card, optional. (Card payment requires cvc)
     * @param listener The callback of checkout
     */
    fun checkout(
        session: AirwallexSession,
        paymentMethod: PaymentMethod,
        paymentConsentId: String? = null,
        cvc: String? = null,
        listener: PaymentResultListener<PaymentIntent>
    ) {
        if (paymentMethod.type == PaymentMethodType.CARD && cvc == null) {
            listener.onFailed(InvalidParamsException(message = "CVC is required!"))
            return
        }

        if (session is AirwallexPaymentSession) {
            when (paymentMethod.type) {
                PaymentMethodType.POLI -> {
                    if (session.name == null) {
                        listener.onFailed(InvalidParamsException(message = "Name is required for POLi!"))
                        return
                    }
                }
                PaymentMethodType.FPX -> {
                    if (session.email == null || session.phone == null || session.name == null) {
                        listener.onFailed(InvalidParamsException(message = "Name, Phone and Email is required for FPX!"))
                        return
                    }
                }
                else -> Unit
            }
        }
        when (session) {
            is AirwallexPaymentSession -> {
                val paymentIntent = session.paymentIntent
                confirmPaymentIntent(
                    paymentIntentId = paymentIntent.id,
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    paymentMethod = paymentMethod,
                    cvc = cvc,
                    customerId = paymentIntent.customerId,
                    paymentConsentId = paymentConsentId,
                    name = session.name,
                    email = session.email,
                    phone = session.phone,
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
                                nextTriggeredBy = if (paymentMethod.type == PaymentMethodType.CARD) session.nextTriggerBy else PaymentConsent.NextTriggeredBy.MERCHANT,
                                listener = object : PaymentListener<PaymentConsent> {
                                    override fun onFailed(exception: Exception) {
                                        listener.onFailed(exception)
                                    }

                                    override fun onSuccess(response: PaymentConsent) {
                                        verifyPaymentConsent(
                                            paymentConsent = response,
                                            currency = session.currency,
                                            amount = session.amount,
                                            cvc = cvc,
                                            listener = listener
                                        )
                                    }
                                }
                            )
                        }

                        override fun onClientSecretError(errorMessage: String) {
                            listener.onFailed(Exception(errorMessage))
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
                    nextTriggeredBy = if (paymentMethod.type == PaymentMethodType.CARD) session.nextTriggerBy else PaymentConsent.NextTriggeredBy.MERCHANT,
                    listener = object : PaymentListener<PaymentConsent> {
                        override fun onFailed(exception: Exception) {
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
                                        listener = listener
                                    )
                                }
                                else -> {
                                    verifyPaymentConsent(
                                        paymentConsent = response,
                                        currency = session.currency,
                                        amount = session.amount,
                                        cvc = cvc,
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

    companion object {

        private const val AIRWALLEX_CHECKOUT_SCHEMA = "airwallexcheckout://"

        const val AIRWALLEX_USER_AGENT = "Airwallex-Android-SDK"

        /**
         * Initialize some global configurations, better to be called on Application
         */
        fun initialize(configuration: AirwallexConfiguration) {
            AirwallexPlugins.initialize(configuration)
        }
    }
}
