package com.airwallex.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import com.airwallex.android.exception.DccException
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
        listener: PaymentListener<PaymentIntent>
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
                customerId = params.customerId,
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
     * Create a [PaymentConsent], Support alipayhk, kakaopay, gcash, dana, tng
     *
     * @param listener a [PaymentListener] to receive the response or error
     */
    @UiThread
    fun createPaymentConsent(
        params: CreatePaymentConsentParams,
        listener: PaymentListener<PaymentConsent>
    ) {
        val availablePaymentMethodTypes = listOf(PaymentMethodType.ALIPAY_HK, PaymentMethodType.KAKAOPAY, PaymentMethodType.GCASH, PaymentMethodType.DANA, PaymentMethodType.TNG)
        if (!availablePaymentMethodTypes.contains(params.paymentMethodType)) {
            listener.onFailed(Exception("Not support payment method ${params.paymentMethodType}"))
            return
        }
        paymentManager.createPaymentConsent(
            AirwallexApiRepository.CreatePaymentConsentOptions(
                clientSecret = params.clientSecret,
                request = PaymentConsentCreateRequest.Builder()
                    .setRequestId(UUID.randomUUID().toString())
                    .setCustomerId(params.customerId)
                    .setPaymentMethod(
                        PaymentMethod(
                            type = params.paymentMethodType
                        )
                    )
                    .setNextTriggeredBy(PaymentConsent.NextTriggeredBy.MERCHANT)
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
        listener: PaymentListener<PaymentConsent>
    ) {
        val verificationOptions = when (params.paymentMethodType) {
            PaymentMethodType.ALIPAY_HK -> PaymentConsentVerifyRequest.VerificationOptions(alipayhk = PaymentConsentVerifyRequest.AliPayVerificationOptions(flow = ThirdPartPayRequestFlow.IN_APP, osType = "android"))
            PaymentMethodType.DANA -> PaymentConsentVerifyRequest.VerificationOptions(dana = PaymentConsentVerifyRequest.AliPayVerificationOptions(flow = ThirdPartPayRequestFlow.IN_APP, osType = "android"))
            PaymentMethodType.GCASH -> PaymentConsentVerifyRequest.VerificationOptions(gcash = PaymentConsentVerifyRequest.AliPayVerificationOptions(flow = ThirdPartPayRequestFlow.IN_APP, osType = "android"))
            PaymentMethodType.KAKAOPAY -> PaymentConsentVerifyRequest.VerificationOptions(kakaopay = PaymentConsentVerifyRequest.AliPayVerificationOptions(flow = ThirdPartPayRequestFlow.IN_APP, osType = "android"))
            PaymentMethodType.TNG -> PaymentConsentVerifyRequest.VerificationOptions(tng = PaymentConsentVerifyRequest.AliPayVerificationOptions(flow = ThirdPartPayRequestFlow.IN_APP, osType = "android"))
            else -> null
        }
        paymentManager.verifyPaymentConsent(
            AirwallexApiRepository.VerifyPaymentConsentOptions(
                clientSecret = params.clientSecret,
                paymentConsentId = params.paymentConsentId,
                request = PaymentConsentVerifyRequest.Builder()
                    .setRequestId(UUID.randomUUID().toString())
                    .setVerificationOptions(verificationOptions)
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
    fun handleAction(nextAction: PaymentIntent.NextAction?) {
        val redirectUrl = nextAction?.url
        if (TextUtils.isEmpty(redirectUrl)) {
            throw RedirectException(message = "Redirect URL is empty.")
        }
        val uri = Uri.parse(redirectUrl)
        val params = uri.queryParameterNames
        val newUri = uri.buildUpon().clearQuery()

        for (param in params) {
            if (param == "callback" && uri.getQueryParameter(param) == "null") {
                newUri.appendQueryParameter(param, REDIRECT_RESULT_SCHEME + activity.packageName)
            } else {
                newUri.appendQueryParameter(param, uri.getQueryParameter(param))
            }
        }
        RedirectUtil.makeRedirect(activity = activity, redirectUrl = newUri.build().toString())
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
    }

    /**
     * Represents a listener for PaymentMethod actions
     */
    interface PaymentMethodListener : PaymentFlowListener {
        // CVC returns only when payment is first created, otherwise null
        fun onSuccess(paymentMethod: PaymentMethod, cvc: String?)
    }

    /**
     * Represents a listener for Add PaymentMethod
     */
    interface AddPaymentMethodListener : PaymentFlowListener {
        fun onSuccess(paymentMethod: PaymentMethod, cvc: String)
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
     * @param paymentIntent a [PaymentIntent] used to present the Add Payment Method flow
     * @param addPaymentMethodFlowListener The callback of present the add payment method flow
     */
    fun presentAddPaymentMethodFlow(
        paymentIntent: PaymentIntent,
        clientSecretProvider: ClientSecretProvider,
        addPaymentMethodFlowListener: AddPaymentMethodListener
    ) {
        airwallexStarter.presentAddPaymentMethodFlow(paymentIntent, clientSecretProvider, addPaymentMethodFlowListener)
    }

    /**
     * Launch the [PaymentMethodsActivity] to allow the user to select a payment method or add a new one
     *
     * @param paymentIntent a [PaymentIntent] used to present the Select Payment Method flow
     * @param selectPaymentMethodFlowListener The callback of present the select payment method flow
     */
    fun presentSelectPaymentMethodFlow(
        paymentIntent: PaymentIntent,
        clientSecretProvider: ClientSecretProvider,
        selectPaymentMethodFlowListener: PaymentMethodListener
    ) {
        airwallexStarter.presentSelectPaymentMethodFlow(paymentIntent, clientSecretProvider, selectPaymentMethodFlowListener)
    }

    /**
     * Launch the [PaymentCheckoutActivity] to allow the user to confirm [PaymentIntent] using the specified [PaymentMethod]
     *
     * @param paymentIntent a [PaymentIntent] used to present the Checkout flow
     * @param paymentMethod a [PaymentMethod] used to present the Checkout flow
     * @param cvc CVC of [PaymentMethod], optional
     * @param paymentDetailListener The callback of present the select payment detail flow
     */
    fun presentPaymentDetailFlow(
        paymentIntent: PaymentIntent,
        paymentMethod: PaymentMethod,
        cvc: String? = null,
        paymentDetailListener: PaymentIntentListener
    ) {
        airwallexStarter.presentPaymentDetailFlow(paymentIntent, paymentMethod, cvc, paymentDetailListener)
    }

    /**
     * Launch the [PaymentMethodsActivity] to allow the user to complete the entire payment flow
     *
     * @param paymentIntent a [PaymentIntent] used to present the payment flow
     * @param paymentFlowListener The callback of present entire payment flow
     */
    fun presentPaymentFlow(
        paymentIntent: PaymentIntent,
        clientSecretProvider: ClientSecretProvider,
        paymentFlowListener: PaymentIntentListener
    ) {
        airwallexStarter.presentPaymentFlow(paymentIntent, clientSecretProvider, paymentFlowListener)
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

        /**
         * This value should be used as `returnUrl` sent on the `/api/v1/pa/payment_intents/create` call.
         */
        val REDIRECT_RESULT_SCHEME: String = BuildConfig.CHECKOUT_REDIRECT_SCHEME.toString() + "://"
    }
}
