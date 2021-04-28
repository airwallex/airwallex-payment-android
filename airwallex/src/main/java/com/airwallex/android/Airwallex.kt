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
                    .setType(AvaliablePaymentMethodType.CARD)
                    .setCard(params.card)
                    .setBilling(params.billing)
                    .build()
            ),
            listener
        )
    }

    /**
     * Disable a payment method
     *
     * @param params [DisablePaymentMethodParams] used to disable the [PaymentMethod]
     * @param listener the callback of create [PaymentMethod]
     */
    @UiThread
    internal fun disablePaymentMethod(
        params: DisablePaymentMethodParams,
        listener: PaymentListener<PaymentMethod>
    ) {
        paymentManager.disablePaymentMethod(
            AirwallexApiRepository.DisablePaymentMethodOptions(
                clientSecret = params.clientSecret,
                paymentMethodId = params.paymentMethodId,
                request = PaymentMethodDisableRequest.Builder()
                    .setRequestId(UUID.randomUUID().toString())
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
        val availablePaymentMethodTypes = listOf(
            PaymentMethodType.CARD,
            PaymentMethodType.GCASH,
            PaymentMethodType.TNG,
            PaymentMethodType.KAKAOPAY,
            PaymentMethodType.DANA,
            PaymentMethodType.ALIPAY_HK
        )
        if (!availablePaymentMethodTypes.contains(params.paymentMethodType)) {
            listener.onFailed(InvalidParamsException("Not support payment method ${params.paymentMethodType}"))
            return
        }

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
        val availablePaymentMethodTypes = listOf(
            PaymentMethodType.CARD,
            PaymentMethodType.GCASH,
            PaymentMethodType.TNG,
            PaymentMethodType.KAKAOPAY,
            PaymentMethodType.DANA,
            PaymentMethodType.ALIPAY_HK
        )
        if (!availablePaymentMethodTypes.contains(params.paymentMethodType)) {
            listener.onFailed(InvalidParamsException("Not support payment method ${params.paymentMethodType}"))
            return
        }

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
        fun onSuccess(paymentMethod: PaymentMethod, cvc: String?)
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
     * @param paymentDetailListener The callback of present the select payment detail flow
     */
    fun presentPaymentDetailFlow(
        session: AirwallexSession,
        paymentDetailListener: PaymentIntentCardListener
    ) {
        airwallexStarter.presentPaymentDetailFlow(session, paymentDetailListener)
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

    companion object {
        /**
         * Initialize some global configurations, better to be called on Application
         */
        fun initialize(configuration: AirwallexConfiguration) {
            AirwallexPlugins.initialize(configuration)
        }
    }
}
