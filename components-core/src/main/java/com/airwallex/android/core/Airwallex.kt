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
import com.airwallex.android.core.Airwallex.Companion.initialize
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.exception.AirwallexComponentDependencyException
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.exception.InvalidParamsException
import com.airwallex.android.core.extension.confirmGooglePayIntent
import com.airwallex.android.core.extension.convertToLegacySession
import com.airwallex.android.core.extension.convertToSession
import com.airwallex.android.core.extension.createCardPaymentMethod
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.log.AnalyticsLogger.Field
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
import com.airwallex.android.core.model.PaymentConsentOptions
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
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.math.BigDecimal
import java.util.Collections
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.cancellation.CancellationException

@Suppress("LongMethod, LargeClass, LongParameterList")
class Airwallex internal constructor(
    private val fragment: Fragment?,
    var activity: ComponentActivity,
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
        // Set up analytics for API flow if not already set up (e.g., by UI components)
        if (!AnalyticsLogger.isSessionSetup(session)) {
            AnalyticsLogger.setupSession(session, AnalyticsLogger.LaunchType.API, null)
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
     * @deprecated Use [checkout] with a [PaymentMethod] instead.
     * Create a PaymentMethod first (either via createCardPaymentMethod() or build one yourself),
     * then call checkout(session, paymentMethod, cvc = card.cvc, saveCard = saveCard, listener = listener)
     */
    @Deprecated(
        message = "Use checkout() with PaymentMethod instead",
        replaceWith = ReplaceWith(
            "checkout(session, paymentMethod, cvc = card.cvc, saveCard = saveCard, listener = listener)",
            "com.airwallex.android.core.model.PaymentMethod"
        )
    )
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
        // Wrap listener at entry point to log payment result once
        val loggingListener = wrapListenerWithLogging(listener, PaymentMethodType.CARD.value)

        val paymentMethod = PaymentMethod.Builder()
            .setType(PaymentMethodType.CARD.value)
            .setCard(card)
            .setBilling(billing)
            .build()
        checkout(
            session = session,
            paymentMethod = paymentMethod,
            listener = loggingListener,
            saveCard = saveCard
        )
    }

    /**
     * Confirm a payment intent with payment consent
     *
     * @param session an [AirwallexSession] used to start the payment flow
     * @param paymentConsent a [PaymentConsent] used to start the payment flow
     * @param listener The callback of the payment flow
     * @deprecated Use [checkout] instead.
     * Call checkout(session, paymentConsent.paymentMethod!!, paymentConsentId = paymentConsent.id, paymentConsent = paymentConsent, listener = listener)
     */
    @Deprecated(
        message = "Use checkout() instead",
        replaceWith = ReplaceWith(
            "checkout(session, paymentConsent.paymentMethod!!, paymentConsentId = paymentConsent.id, paymentConsent = paymentConsent, listener = listener)"
        )
    )
    @UiThread
    fun confirmPaymentIntent(
        session: AirwallexSession,
        paymentConsent: PaymentConsent,
        listener: PaymentResultListener
    ) {
        if (session !is Session && session !is AirwallexPaymentSession) {
            listener.onCompleted(
                AirwallexPaymentStatus.Failure(
                    AirwallexCheckoutException(message = "checkout with paymentConsent only support AirwallexPaymentSession or Session")
                )
            )
            return
        }

        val paymentMethod = paymentConsent.paymentMethod
        if (paymentMethod == null) {
            listener.onCompleted(AirwallexPaymentStatus.Failure(AirwallexCheckoutException(message = "paymentMethod is required")))
            return
        }

        // Redirect to checkout()
        checkout(
            session = session,
            paymentMethod = paymentMethod,
            paymentConsentId = paymentConsent.id,
            paymentConsent = paymentConsent,
            listener = listener
        )
    }

    /**
     * Confirm a payment intent with payment consent ID
     *
     * @param session an [AirwallexSession] used to start the payment flow
     * @param paymentConsentId the ID of the [PaymentConsent]
     * @param listener The callback of the payment flow
     * @deprecated Use [checkout] instead.
     * Create a PaymentMethod with card type and call checkout(session, PaymentMethod(type = "card"), paymentConsentId = paymentConsentId, listener = listener)
     */
    @Deprecated(
        message = "Use checkout() instead",
        replaceWith = ReplaceWith(
            "checkout(session, PaymentMethod(type = \"card\"), paymentConsentId = paymentConsentId, listener = listener)",
            "com.airwallex.android.core.model.PaymentMethod"
        )
    )
    fun confirmPaymentIntent(
        session: AirwallexSession,
        paymentConsentId: String,
        listener: PaymentResultListener
    ) {
        if (session !is Session && session !is AirwallexPaymentSession) {
            listener.onCompleted(
                AirwallexPaymentStatus.Failure(
                    AirwallexCheckoutException(message = "checkout with paymentConsent only support AirwallexPaymentSession or Session")
                )
            )
            return
        }

        // Redirect to checkout() with a minimal PaymentMethod (card type)
        // The paymentConsentId will be used to retrieve the actual payment details
        checkout(
            session = session,
            paymentMethod = PaymentMethod(type = PaymentMethodType.CARD.value),
            paymentConsentId = paymentConsentId,
            listener = listener
        )
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
        // Wrap listener at entry point to log payment result once
        val loggingListener = wrapListenerWithLogging(listener, PaymentMethodType.GOOGLEPAY.value)
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
                    checkout(
                        session = session,
                        paymentMethod = PaymentMethod(type = PaymentMethodType.GOOGLEPAY.value),
                        listener = loggingListener
                    )
                } else {
                    loggingListener.onCompleted(
                        AirwallexPaymentStatus.Failure(AirwallexCheckoutException(message = "Payment not supported via Google Pay."))
                    )
                }
            }
        } else {
            loggingListener.onCompleted(
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
            is Session -> if (session.isOneOffPayment) TransactionMode.ONE_OFF else TransactionMode.RECURRING
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
     * @param session The [AirwallexSession] which contains session information for retrieving payment methods.
     * @param params [RetrieveAvailablePaymentMethodParams] used to retrieve all [AvailablePaymentMethodType]
     * @param callback [AirwallexCallback] A callback interface to handle the success or failure of the network request.
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
     * Fetch available payment methods and consents (suspend function)
     *
     * @param session an [AirwallexSession] for fetching payment methods and consents
     * @return [Result] containing a [Pair] of payment methods list and consents list
     */
    suspend fun fetchAvailablePaymentMethodsAndConsents(session: AirwallexSession): Result<Pair<List<AvailablePaymentMethodType>, List<PaymentConsent>>> {
        val secret = getClientSecret(session).takeIf { !it.isNullOrBlank() } ?: return Result.failure(
            AirwallexCheckoutException(message = "Client secret is empty or blank")
        )
        val customerId = session.customerId
        return supervisorScope {
            val intentId = (session as? AirwallexPaymentSession)?.paymentIntent?.id
            AirwallexLogger.info("Airwallex fetchAvailablePaymentMethodsAndConsents$intentId: customerId = $customerId")
            val retrieveConsents = async {
                customerId?.takeIf { needRequestConsent(session) }
                    ?.let { retrieveAvailablePaymentConsentsPaged(secret, it) } ?: emptyList()
            }
            val retrieveMethods = async { retrieveAvailablePaymentMethodsPaged(session, secret) }
            try {
                val methods = filterPaymentMethodsBySession(
                    retrieveMethods.await(), session.paymentMethods
                )
                val consents = retrieveConsents.await()
                Result.success(Pair(methods, filterPaymentConsentsBySession(session, methods, consents)))
            } catch (exception: AirwallexException) {
                AirwallexLogger.error(
                    "Airwallex fetchAvailablePaymentMethodsAndConsents$intentId: failed ",
                    exception
                )
                Result.failure(exception)
            }
        }
    }

    @Suppress("TooGenericExceptionThrown")
    fun getPaymentIntent(session: AirwallexSession) =
        when (session) {
            is Session -> session.paymentIntent
            is AirwallexPaymentSession -> session.paymentIntent
            is AirwallexRecurringWithIntentSession -> session.paymentIntent
            is AirwallexRecurringSession -> null
            else -> throw Exception("Not supported session $session")
        }

    fun getClientSecret(session: AirwallexSession): String? = session.clientSecret?.takeIf { it.isNotBlank() }

    fun shouldHidePaymentConsents(session: AirwallexSession) = session.hidePaymentConsents

    fun getSupportedCardSchemes(availablePaymentMethodTypes: List<AvailablePaymentMethodType>): List<CardScheme> =
        availablePaymentMethodTypes.firstOrNull { paymentMethodType ->
            paymentMethodType.name == PaymentMethodType.CARD.value
        }?.cardSchemes ?: emptyList()

    private suspend fun retrieveAvailablePaymentConsentsPaged(
        clientSecret: String,
        customerId: String,
    ) = loadPagedItems(
        loadPage = { pageNum ->
            retrieveAvailablePaymentConsents(
                RetrieveAvailablePaymentConsentsParams.Builder(
                    clientSecret = clientSecret,
                    customerId = customerId,
                    pageNum = pageNum,
                ).setStatus(PaymentConsent.PaymentConsentStatus.VERIFIED).build()
            )
        }
    )

    private suspend fun retrieveAvailablePaymentMethodsPaged(
        session: AirwallexSession,
        clientSecret: String
    ) = loadPagedItems(
        loadPage = { pageNum ->
            retrieveAvailablePaymentMethods(
                session = session,
                params = RetrieveAvailablePaymentMethodParams.Builder(
                    clientSecret = clientSecret,
                    pageNum = pageNum,
                )
                    .setActive(true)
                    .setTransactionCurrency(session.currency)
                    .setCountryCode(session.countryCode).build()
            )
        }
    )

    private fun filterPaymentMethodsBySession(
        sourceList: List<AvailablePaymentMethodType>,
        filterList: List<String>?,
    ): List<AvailablePaymentMethodType> {
        if (filterList.isNullOrEmpty()) return sourceList
        return filterList.mapNotNull { name ->
            sourceList.find { it.name.equals(name, ignoreCase = true) }
        }
    }

    private fun filterPaymentConsentsBySession(
        session: AirwallexSession,
        paymentMethodList: List<AvailablePaymentMethodType>,
        paymentConsentList: List<PaymentConsent>
    ): List<PaymentConsent> {
        val cardPaymentMethod = paymentMethodList.find { it.name == PaymentMethodType.CARD.value }
        return if (cardPaymentMethod != null && session !is AirwallexRecurringSession) {
            paymentConsentList.filter { it.paymentMethod?.type == PaymentMethodType.CARD.value }
        } else {
            emptyList()
        }
    }
    private suspend fun <T> loadPagedItems(
        loadPage: suspend (Int) -> Page<T>,
        items: MutableList<T> = Collections.synchronizedList(mutableListOf()),
        pageNum: AtomicInteger = AtomicInteger(0)
    ): List<T> {
        val response = loadPage(pageNum.get())
        pageNum.incrementAndGet()
        items.addAll(response.items)
        return if (response.hasMore) {
            loadPagedItems(
                loadPage,
                items,
                pageNum,
            )
        } else {
            items
        }
    }
    private fun needRequestConsent(session: AirwallexSession): Boolean {
        // if the customerId is null or empty ,there is no need to request consents
        if (session.customerId.isNullOrEmpty()) return false
        if (session is AirwallexRecurringSession) return false
        // if payment methods is not empty and does not contain CARD, no need to request consents
        val paymentMethods = session.paymentMethods
        if (!paymentMethods.isNullOrEmpty() && !paymentMethods.contains(PaymentMethodType.CARD.value)) return false
        // if user wants to hide consents,there is no need to request consents
        return !shouldHidePaymentConsents(session)
    }

    /**
     * Verify a [PaymentConsent]
     *
     * @param device a [Device] object containing device information for fingerprinting
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
        // Wrap listener at entry point to log payment result once
        val loggingListener = wrapListenerWithLogging(listener, paymentMethodType)
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
                    loggingListener.onCompleted(AirwallexPaymentStatus.Failure(exception))
                }

                override fun onSuccess(response: PaymentConsent) {
                    handlePaymentConsentVerifySuccess(
                        response = response,
                        params = params,
                        paymentMethodType = paymentMethodType,
                        loggingListener = loggingListener
                    )
                }
            }
        )
    }

    private fun handlePaymentConsentVerifySuccess(
        response: PaymentConsent,
        params: VerifyPaymentConsentParams,
        paymentMethodType: String,
        loggingListener: PaymentResultListener
    ) {
        // for redirect, initialPaymentIntentId is empty now. so we don support recurring in redirect flow
        val paymentIntentId = response.initialPaymentIntentId

        if (response.nextAction == null) {
            loggingListener.onCompleted(
                AirwallexPaymentStatus.Success(paymentIntentId, response.id)
            )
            return
        }

        val provider = AirwallexPlugins.getProvider(
            ActionComponentProviderType.fromValue(paymentMethodType)
        )
        if (provider == null) {
            loggingListener.onCompleted(
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
                        mapOf(Field.TYPE to paymentMethodType)
                    )
                    AirwallexLogger.error("Airwallex verifyPaymentConsent: type = $paymentMethodType, paymentIntentId isNullOrEmpty")
                    loggingListener.onCompleted(
                        AirwallexPaymentStatus.Failure(
                            AirwallexCheckoutException(message = "Unsupported payment method")
                        )
                    )
                    return
                }

                val nextActionModel = createCardNextActionModel(params, paymentIntentId)

                provider.get().handlePaymentIntentResponse(
                    paymentIntentId,
                    response.nextAction,
                    fragment,
                    activity,
                    applicationContext,
                    nextActionModel,
                    loggingListener,
                    response.id
                )
            }

            else ->
                provider.get().handlePaymentIntentResponse(
                    null,
                    response.nextAction,
                    fragment,
                    activity,
                    applicationContext,
                    null,
                    loggingListener,
                    response.id
                )
        }
    }

    private fun createCardNextActionModel(
        params: VerifyPaymentConsentParams,
        paymentIntentId: String
    ): CardNextActionModel {
        return CardNextActionModel(
            paymentManager = paymentManager,
            clientSecret = params.clientSecret,
            device = null,
            paymentIntentId = paymentIntentId,
            currency = requireNotNull(params.currency),
            amount = requireNotNull(params.amount),
            activityProvider = { activity }
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

    /**
     * Retrieve available banks
     *
     * @param params [RetrieveBankParams] used to retrieve bank information
     * @param listener a [PaymentListener] to receive the response or error
     */
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

    /**
     * Retrieve payment method type information
     *
     * @param params [RetrievePaymentMethodTypeInfoParams] used to retrieve payment method type details
     * @param listener a [PaymentListener] to receive the response or error
     */
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
     * Checkout the payment. This should be the entry point to handle all checkout cases
     *
     * @param session a [AirwallexSession] used to present the Checkout flow, required.
     * @param paymentMethod a [PaymentMethod] used to present the Checkout flow, required.
     * @param paymentConsentId the ID of the [PaymentConsent], optional. (CIT & Card will need this field for subsequent payments, paymentConsentId is not empty indicating a subsequent payment, empty indicating a recurring )
     * @param paymentConsent a [PaymentConsent] object used for the payment, optional. will be used to fill in paymentConsentId also. Prefer to fill this than paymentConsentId because it's more complete
     * @param cvc the CVC of the Credit Card, optional.
     * @param additionalInfo used by LPMs
     * @param flow an [AirwallexPaymentRequestFlow], currently only supporting [AirwallexPaymentRequestFlow.IN_APP], optional.
     * @param listener The callback of checkout
     * @param saveCard whether card will be saved as a payment consent, optional.
     */
    @Suppress("LongParameterList")
    @UiThread
    fun checkout(
        session: AirwallexSession,
        paymentMethod: PaymentMethod,
        paymentConsentId: String? = null,
        paymentConsent: PaymentConsent? = null,
        cvc: String? = null,
        additionalInfo: Map<String, String>? = null,
        flow: AirwallexPaymentRequestFlow? = null,
        listener: PaymentResultListener,
        saveCard: Boolean = false,
    ) {
        // PUBLIC ENTRY POINT: Consolidates session routing logic
        // DECISION LOGIC: Use old flow if:
        // 1. session is AirwallexRecurringSession (no unified flow support yet)
        // 2. OR paymentMethod.type is NOT card/googlepay (LPMs use legacy flow)
        // Otherwise: use new unified flow with Session

        val isCardOrGooglePay = paymentMethod.type == PaymentMethodType.GOOGLEPAY.value ||
                                paymentMethod.type == PaymentMethodType.CARD.value
        val useOldFlow = session is AirwallexRecurringSession || !isCardOrGooglePay
        val paymentConsentIdValue = paymentConsentId ?: paymentConsent?.id

        // Log payment_launched for API integration
        logPaymentLaunchedIfNeeded(paymentConsentIdValue, paymentMethod.type)

        if (useOldFlow) {
            checkoutOldFlowRouting(session, paymentMethod, cvc, additionalInfo, flow, listener)
            return
        }

        // NEW FLOW: Use unified Session flow for card/googlepay
        // Convert legacy to Session if needed
        val unifiedSession = session as? Session
            ?: when (session) {
                is AirwallexPaymentSession -> session.convertToSession()
                is AirwallexRecurringWithIntentSession -> session.convertToSession()
                else -> {
                    listener.onCompleted(
                        AirwallexPaymentStatus.Failure(
                            AirwallexCheckoutException(message = "Unknown session type: ${session.javaClass}")
                        )
                    )
                    return
                }
            }

        // Call unified Session checkout
        checkoutUnified(unifiedSession, paymentMethod, cvc, saveCard, paymentConsent, listener)
    }

    /**
     * INTERNAL: Routes old flow checkout based on session type and payment method.
     * Handles special cases for AirwallexRecurringSession with card payments.
     */
    @Suppress("LongParameterList")
    @UiThread
    private fun checkoutOldFlowRouting(
        session: AirwallexSession,
        paymentMethod: PaymentMethod,
        cvc: String? = null,
        additionalInfo: Map<String, String>? = null,
        flow: AirwallexPaymentRequestFlow? = null,
        listener: PaymentResultListener,
    ) {
        // SPECIAL HANDLING: AirwallexRecurringSession with card payments
        // Note: AirwallexRecurringSession always creates NEW payment consents, never uses existing ones
        if (session is AirwallexRecurringSession && paymentMethod.type == PaymentMethodType.CARD.value) {
            val card = paymentMethod.card
            if (card == null) {
                listener.onCompleted(
                    AirwallexPaymentStatus.Failure(
                        AirwallexCheckoutException(message = "Card is required for card payment")
                    )
                )
                return
            }
            createCardPaymentMethod(
                session = session,
                card = card,
                billing = paymentMethod.billing,
                saveCard = false,
                listener = object : PaymentListener<PaymentMethod> {
                    override fun onSuccess(response: PaymentMethod) {
                        checkoutLegacySession(
                            session = session,
                            paymentMethod = response,
                            cvc = card.cvc,
                            listener = listener
                        )
                    }

                    override fun onFailed(exception: AirwallexException) {
                        listener.onCompleted(AirwallexPaymentStatus.Failure(exception))
                    }
                }
            )
            return
        }
        if (session is AirwallexRecurringSession && paymentMethod.type == PaymentMethodType.GOOGLEPAY.value) {
            checkoutGooglePay(session, listener)
            return
        }
        // OLD FLOW: Use legacy implementation for AirwallexRecurringSession and LPMs
        // Convert Session to legacy if needed
        if (session is Session) {
            handleNewSessionInOldFlow(session, paymentMethod, cvc, additionalInfo, flow, listener)
        } else {
            checkoutLegacySession(
                session = session,
                paymentMethod = paymentMethod,
                cvc = cvc,
                additionalInfo = additionalInfo,
                flow = flow,
                listener = listener
            )
        }
    }

    @Suppress("LongParameterList")
    private fun handleNewSessionInOldFlow(
        session: Session,
        paymentMethod: PaymentMethod,
        cvc: String?,
        additionalInfo: Map<String, String>?,
        flow: AirwallexPaymentRequestFlow?,
        listener: PaymentResultListener
    ) {
        activity.lifecycleScope.launch {
            try {
                val legacySession = session.convertToLegacySession()
                checkoutLegacySession(
                    session = legacySession,
                    paymentMethod = paymentMethod,
                    cvc = cvc,
                    additionalInfo = additionalInfo,
                    flow = flow,
                    listener = listener
                )
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                listener.onCompleted(
                    AirwallexPaymentStatus.Failure(
                        AirwallexCheckoutException(
                            message = error.message,
                            e = error
                        )
                    )
                )
            }
        }
    }

    /**
     * INTERNAL: Legacy checkout implementation for AirwallexRecurringSession and LPMs.
     * Handles all deprecated session types with the old flow.
     */
    @Suppress("LongParameterList")
    @UiThread
    private fun checkoutLegacySession(
        session: AirwallexSession,
        paymentMethod: PaymentMethod,
        paymentConsentId: String? = null,
        paymentConsent: PaymentConsent? = null,
        cvc: String? = null,
        additionalInfo: Map<String, String>? = null,
        flow: AirwallexPaymentRequestFlow? = null,
        listener: PaymentResultListener,
        saveCard: Boolean = false,
    ) {
        setupAnalyticsLogger(session)
        // Wrap listener at entry point to log payment result once
        val loggingListener = wrapListenerWithLogging(listener, paymentMethod.type ?: "unknown")
        AirwallexLogger.info("Airwallex checkout: saveCard = $saveCard, paymentMethod.type = ${paymentMethod.type} session type = ${session.javaClass}")

        // Legacy flow implementation
        when (session) {
            is AirwallexPaymentSession -> {
                if (paymentMethod.type == PaymentMethodType.GOOGLEPAY.value) {
                    checkoutGooglePay(session, loggingListener)
                } else if (saveCard) {
                    createPaymentConsentAndConfirmIntent(session, paymentMethod, cvc, loggingListener)
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
                                paymentConsentId = paymentConsent?.id ?: paymentConsentId,
                                additionalInfo = additionalInfo,
                                returnUrl = if (paymentMethod.type == PaymentMethodType.CARD.value) {
                                    AirwallexPlugins.environment.threeDsReturnUrl()
                                } else session.returnUrl,
                                autoCapture = session.autoCapture,
                                flow = flow,
                                listener = loggingListener
                            )
                        }

                        override fun onError(error: Throwable) {
                            loggingListener.onCompleted(AirwallexPaymentStatus.Failure(AirwallexCheckoutException(message = error.message, e = error)))
                        }
                    })
                }
            }

            is AirwallexRecurringSession, is AirwallexRecurringWithIntentSession ->
                createPaymentConsentAndConfirmIntent(session, paymentMethod, cvc, loggingListener)

            else -> {
                loggingListener.onCompleted(
                    AirwallexPaymentStatus.Failure(
                        AirwallexCheckoutException(message = "Unknown legacy session type: ${session.javaClass}")
                    )
                )
            }
        }
    }

    /**
     * INTERNAL: Unified checkout function for Session.
     * Replaces the need for separate createPaymentConsentAndConfirmIntent - everything goes through single API call.
     * Only called for card/googlepay with Session.
     *
     * @param session a [Session] used to start the payment flow
     * @param paymentMethod the payment method to use
     * @param cvc the CVC of the card
     * @param saveCard whether to save the card (creates payment consent inline)
     * @param paymentConsent the payment consent object (for saved card scenarios)
     * @param listener The callback of the payment flow
     */
    @Suppress("LongParameterList")
    @UiThread
    private fun checkoutUnified(
        session: Session,
        paymentMethod: PaymentMethod,
        cvc: String? = null,
        saveCard: Boolean = false,
        paymentConsent: PaymentConsent? = null,
        listener: PaymentResultListener
    ) {
        setupAnalyticsLogger(session)
        // Wrap listener at entry point to log payment result once
        val loggingListener = wrapListenerWithLogging(listener, paymentMethod.type ?: "unknown")
        AirwallexLogger.info("Airwallex unified checkout: saveCard = $saveCard, paymentMethod.type = ${paymentMethod.type} session type = ${session.javaClass}")

        // Handle Google Pay - fetch googlePay object then continue with normal flow
        if (paymentMethod.type == PaymentMethodType.GOOGLEPAY.value) {
            checkoutGooglePay(
                session = session,
                listener = loggingListener,
                onGooglePayReady = { googlePay ->
                    // Build complete PaymentMethod with googlePay and continue
                    val gPaymentMethod = paymentMethod.copy(googlePay = googlePay)
                    proceedWithUnifiedPayment(
                        session = session,
                        paymentMethod = gPaymentMethod,
                        cvc = null,
                        saveCard = saveCard,
                        paymentConsent = paymentConsent,
                        listener = loggingListener
                    )
                }
            )
            return
        }

        // Regular flow for card and other payment methods
        proceedWithUnifiedPayment(session, paymentMethod, cvc, saveCard, paymentConsent, loggingListener)
    }

    /**
     * Helper to proceed with unified payment flow - resolves PaymentIntent and confirms
     */
    @Suppress("LongParameterList")
    private fun proceedWithUnifiedPayment(
        session: Session,
        paymentMethod: PaymentMethod,
        cvc: String?,
        saveCard: Boolean,
        paymentConsent: PaymentConsent?,
        listener: PaymentResultListener
    ) {
        session.resolvePaymentIntent(object : PaymentIntentProvider.PaymentIntentCallback {
            override fun onSuccess(paymentIntent: PaymentIntent) {
                confirmPaymentIntentUnified(
                    session = session,
                    paymentIntentId = paymentIntent.id,
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    paymentMethod = paymentMethod,
                    cvc = cvc,
                    saveCard = saveCard,
                    paymentConsent = paymentConsent,
                    returnUrl = if (paymentMethod.type == PaymentMethodType.CARD.value || paymentMethod.type == PaymentMethodType.GOOGLEPAY.value) {
                        AirwallexPlugins.environment.threeDsReturnUrl()
                    } else session.returnUrl,
                    autoCapture = session.autoCapture,
                    listener = listener
                )
            }

            override fun onError(error: Throwable) {
                listener.onCompleted(
                    AirwallexPaymentStatus.Failure(
                        AirwallexCheckoutException(message = error.message, e = error)
                    )
                )
            }
        })
    }

    /**
     * Unified confirm function that handles all scenarios with single API call.
     * Replaces multi-step flows with single confirmPaymentIntent call.
     *
     * @param session the [Session] containing session information
     * @param paymentIntentId the ID of the PaymentIntent
     * @param clientSecret the client secret of the PaymentIntent
     * @param paymentMethod the payment method to use
     * @param cvc the CVC of the card, optional
     * @param saveCard whether card will be saved as a payment consent
     * @param paymentConsent a [PaymentConsent] object used for the payment, optional
     * @param returnUrl return URL for 3DS, optional
     * @param autoCapture whether to auto-capture
     * @param listener The callback of the payment flow
     */
    @Suppress("LongParameterList")
    private fun confirmPaymentIntentUnified(
        session: Session,
        paymentIntentId: String,
        clientSecret: String,
        paymentMethod: PaymentMethod,
        cvc: String? = null,
        saveCard: Boolean = false,
        paymentConsent: PaymentConsent? = null,
        returnUrl: String? = null,
        autoCapture: Boolean = true,
        listener: PaymentResultListener
    ) {
        // Determine PaymentConsentOptions based on context
        val paymentConsentOptions = when {
            // 1. Session already has consent options (e.g. from previous transaction) → use them
            session.paymentConsentOptions != null -> session.paymentConsentOptions

            // 2. MIT consent used for one-off payment → override to create CIT consent
            paymentConsent != null &&
                    paymentConsent.nextTriggeredBy == PaymentConsent.NextTriggeredBy.MERCHANT &&
                    session.isOneOffPayment -> PaymentConsentOptions(
                nextTriggeredBy = PaymentConsent.NextTriggeredBy.CUSTOMER
            )

            // 3. CIT with new card and saveCard is enabled → create CIT consent
            saveCard && session.customerId != null -> PaymentConsentOptions(
                nextTriggeredBy = PaymentConsent.NextTriggeredBy.CUSTOMER
            )

            // 4. one off transaction either with CIT or with new card without saving
            else -> null
        }

        val params = when (paymentMethod.type) {
            PaymentMethodType.GOOGLEPAY.value -> {
                ConfirmPaymentIntentParams.createGooglePayParams(
                    paymentIntentId = paymentIntentId,
                    clientSecret = clientSecret,
                    paymentMethod = paymentMethod,
                    cvc = cvc,
                    customerId = session.customerId,
                    paymentConsentId = paymentConsent?.id,
                    paymentConsentOptions = paymentConsentOptions,
                    returnUrl = returnUrl,
                    autoCapture = autoCapture
                )
            }

            else -> {
                ConfirmPaymentIntentParams.createCardParams(
                    paymentIntentId = paymentIntentId,
                    clientSecret = clientSecret,
                    paymentMethod = paymentMethod,
                    cvc = cvc,
                    customerId = session.customerId,
                    paymentConsentId = paymentConsent?.id,
                    paymentConsentOptions = paymentConsentOptions,
                    returnUrl = returnUrl,
                    autoCapture = autoCapture
                )
            }
        }

        confirmPaymentIntent(params, listener)
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

    /**
     * Helper to build GooglePay object from additionalInfo map
     */
    private fun buildGooglePayFromAdditionalInfo(additionalInfo: Map<String, Any>?): PaymentMethod.GooglePay? {
        if (additionalInfo == null) return null
        return PaymentMethod.GooglePay.Builder()
            .setBilling(additionalInfo["billing"] as? Billing)
            .setPaymentDataType(additionalInfo["payment_data_type"] as? String)
            .setEncryptedPaymentToken(additionalInfo["encrypted_payment_token"] as? String)
            .build()
    }

    private fun checkoutGooglePay(
        session: AirwallexSession,
        listener: PaymentResultListener,
        onGooglePayReady: ((PaymentMethod.GooglePay) -> Unit)? = null
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
                            val googlePay = buildGooglePayFromAdditionalInfo(status.additionalInfo?.toMutableMap())
                            if (googlePay == null) {
                                AirwallexLogger.error("Airwallex checkoutGooglePay: failed , Missing Google Pay token response")
                                listener.onCompleted(
                                    AirwallexPaymentStatus.Failure(
                                        AirwallexCheckoutException(message = "Missing Google Pay token response")
                                    )
                                )
                                return
                            }

                            when (session) {
                                is Session -> {
                                    onGooglePayReady?.invoke(googlePay)
                                }

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
     *
     * @param device a [Device] object containing device information for fingerprinting, optional.
     * @param params [ConfirmPaymentIntentParams] used to confirm the payment intent
     * @param listener a [PaymentResultListener] to receive the response or error
     * @deprecated This is a low-level API. For most use cases, use [checkout] instead.
     * This method remains available for advanced device fingerprinting scenarios but is no longer the recommended approach.
     */
    @Deprecated(
        message = "Use checkout() for standard payment flows. This low-level API is kept for advanced device fingerprinting only.",
        level = DeprecationLevel.WARNING
    )
    fun confirmPaymentIntentWithDevice(
        device: Device? = null,
        params: ConfirmPaymentIntentParams,
        listener: PaymentResultListener
    ) {
        // Wrap listener at entry point to log payment result once
        val loggingListener = wrapListenerWithLogging(listener, params.paymentMethodType)
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
                    loggingListener.onCompleted(AirwallexPaymentStatus.Failure(exception))
                }

                override fun onSuccess(response: PaymentIntent) {
                    AirwallexLogger.info("Airwallex confirmPaymentIntentWithDevice success: type = ${params.paymentMethodType}, nextAction = ${response.nextAction?.type}")
                    // If the nextAction is null, the payment is completed
                    if (response.nextAction == null) {
                        loggingListener.onCompleted(
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
                        loggingListener.onCompleted(
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
                        loggingListener,
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
        val threeDSecure = ThreeDSecure.Builder()
            .setReturnUrl(AirwallexPlugins.environment.threeDsReturnUrl())
            .build()

        // Determine which payment approach to use based on payment method ID presence
        // If paymentMethod.id exists → it's from a saved card (consent)
        // If paymentMethod.id is null → it's a new card
        val hasPaymentMethodId = params.paymentMethod?.id != null

        // Build payment_consent_reference (OLD flow: CIT consent without consent options)
        val paymentConsentReference = if (params.paymentConsentId != null && params.paymentConsentOptions == null) {
            PaymentConsentReference.Builder()
                .setId(params.paymentConsentId)
                .setCvc(params.cvc)
                .build()
        } else null

        // Build payment_method based on scenario
        val paymentMethodRequest = buildPaymentMethodRequest(paymentConsentReference, hasPaymentMethodId, params)

        val builder = PaymentIntentConfirmRequest.Builder(
            requestId = UUID.randomUUID().toString()
        )
            .setPaymentMethodOptions(
                PaymentMethodOptions.Builder()
                    .setCardOptions(
                        PaymentMethodOptions.CardOptions.Builder()
                            .setAutoCapture(params.autoCapture)
                            .setThreeDSecure(threeDSecure)
                            .build()
                    )
                    .build()
            )
            .setCustomerId(params.customerId)
            .setDevice(device)
            .setPaymentConsentReference(paymentConsentReference) // Old flow
            .setPaymentConsent(params.paymentConsentOptions) // New flow
            .setPaymentMethodRequest(paymentMethodRequest)

        if (params.returnUrl != null) {
            builder.setReturnUrl(params.returnUrl)
        }

        return Options.ConfirmPaymentIntentOptions(
            clientSecret = params.clientSecret,
            paymentIntentId = params.paymentIntentId,
            request = builder.build()
        )
    }

    private fun buildPaymentMethodRequest(
        paymentConsentReference: PaymentConsentReference?,
        hasPaymentMethodId: Boolean,
        params: ConfirmPaymentIntentParams
    ): PaymentMethodRequest? {
        val paymentMethodRequest = when {
            // Scenario 1: Google Pay payment
            // → payment_method: { type: "googlepay", googlepay: {...} }
            params.paymentMethod?.type == PaymentMethodType.GOOGLEPAY.value -> {
                PaymentMethodRequest.Builder(PaymentMethodType.GOOGLEPAY.value)
                    .setGooglePayPaymentMethodRequest(params.paymentMethod.googlePay)
                    .build()
            }

            // Scenario 2: OLD flow - CIT consent without consent options
            // → No payment_method in request body (uses payment_consent_reference)
            paymentConsentReference != null -> null

            // Scenario 3: NEW flow - Saved card with payment method ID AND consent options
            // → payment_method: { type, id, card?: { cvc } }
            hasPaymentMethodId && params.paymentConsentOptions != null -> {
                val builder = PaymentMethodRequest.Builder(params.paymentMethodType)
                    .setId(params.paymentMethod?.id) // Payment method ID from consent

                // Include CVC if provided
                if (params.cvc != null) {
                    builder.setCardPaymentMethodRequest(
                        card = PaymentMethod.Card.Builder()
                            .setCvc(params.cvc)
                            .build(),
                        billing = null
                    )
                }
                builder.build()
            }

            // Scenario 4: New card payment (with or without saveCard)
            // → payment_method: { type, card: {...}, billing: {...} }
            else -> {
                PaymentMethodRequest.Builder(params.paymentMethodType)
                    .setCardPaymentMethodRequest(
                        card = params.paymentMethod?.card,
                        billing = params.paymentMethod?.billing
                    )
                    .build()
            }
        }
        return paymentMethodRequest
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
     * A PaymentResultListener wrapper that logs payment result events.
     * Used to ensure payment results are logged exactly once at public API entry points.
     */
    private class LoggingPaymentResultListener(
        private val delegate: PaymentResultListener,
        private val paymentMethod: String
    ) : PaymentResultListener {
        override fun onCompleted(status: AirwallexPaymentStatus) {
            when (status) {
                is AirwallexPaymentStatus.Success -> {
                    AnalyticsLogger.logAction("payment_success", mapOf(Field.PAYMENT_METHOD to paymentMethod))
                }
                is AirwallexPaymentStatus.Cancel -> {
                    AnalyticsLogger.logAction("payment_canceled", mapOf(Field.PAYMENT_METHOD to paymentMethod))
                }
                is AirwallexPaymentStatus.Failure -> {
                    AnalyticsLogger.logAction(
                        "payment_failed",
                        mapOf(Field.PAYMENT_METHOD to paymentMethod, Field.MESSAGE to (status.exception.message ?: ""))
                    )
                }
                is AirwallexPaymentStatus.InProgress -> {
                    AnalyticsLogger.logAction("payment_in_progress", mapOf(Field.PAYMENT_METHOD to paymentMethod))
                }
            }
            delegate.onCompleted(status)
        }
    }

    /**
     * Wraps a PaymentResultListener to log payment result events.
     * If the listener is already a LoggingPaymentResultListener, returns it as-is to avoid double-logging.
     */
    private fun wrapListenerWithLogging(
        listener: PaymentResultListener,
        paymentMethod: String
    ): PaymentResultListener {
        return if (listener is LoggingPaymentResultListener) {
            listener
        } else {
            LoggingPaymentResultListener(listener, paymentMethod)
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
            // Initialize theme context using reflection to avoid dependency on ui-core
            try {
                val themeConfigClass = Class.forName("com.airwallex.android.ui.composables.AirwallexThemeConfig")
                // Get the INSTANCE field for Kotlin object singleton
                val instanceField = themeConfigClass.getField("INSTANCE")
                val instance = instanceField.get(null)
                // Call initializeContext on the instance
                val initMethod = themeConfigClass.getMethod("initializeContext", Context::class.java)
                initMethod.invoke(instance, application)
            } catch (e: Exception) {
                // ui-core module not available or reflection failed, ignore
            }
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
    private fun logPaymentLaunchedIfNeeded(paymentConsentId: String?, paymentMethod: String?) {
        if (!isAirwallexUIActivity && AnalyticsLogger.getLaunchType() == AnalyticsLogger.LaunchType.API) {
            AnalyticsLogger.logAction(
                actionName = "payment_launched",
                additionalInfo = mutableMapOf<String, Any>().apply {
                    paymentMethod?.let { put(Field.PAYMENT_METHOD, it) }
                    paymentConsentId?.let { put(Field.CONSENT_ID, it) }
                }
            )
        }
    }
}