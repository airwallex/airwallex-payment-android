package com.airwallex.android.core

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.UiThread
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.airwallex.android.core.Airwallex.Companion.initialize
import com.airwallex.android.core.data.AirwallexCheckoutParam
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.exception.AirwallexComponentDependencyException
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.extension.createCardPaymentMethod
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.log.AnalyticsLogger.Field
import com.airwallex.android.core.log.Crasher
import com.airwallex.android.core.util.validateForRequiredFields
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
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodCreateRequest
import com.airwallex.android.core.model.PaymentMethodRequest
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.core.model.PaymentMethodTypeInfo
import com.airwallex.android.core.model.RetrieveAvailablePaymentConsentsParams
import com.airwallex.android.core.model.RetrieveAvailablePaymentMethodParams
import com.airwallex.android.core.model.RetrieveBankParams
import com.airwallex.android.core.model.RetrievePaymentIntentParams
import com.airwallex.android.core.model.RetrievePaymentMethodTypeInfoParams
import com.airwallex.android.core.model.TransactionMode
import com.airwallex.android.core.model.VerifyPaymentConsentParams
import com.airwallex.android.core.model.withMaestroIfMasterCard
import com.airwallex.android.core.util.BuildConfigHelper
import com.airwallex.risk.AirwallexRisk
import com.airwallex.risk.RiskConfiguration
import com.airwallex.risk.Tenant
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.util.Collections
import java.util.UUID
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger

@Suppress("LongMethod, LargeClass, LongParameterList")
class Airwallex @Suppress("LongParameterList") internal constructor(
    private val fragment: Fragment?,
    var activity: ComponentActivity,
    private val paymentManager: PaymentManager,
    private val applicationContext: Context,
    private val checkoutRouter: AirwallexSessionCheckoutRouter = AirwallexSessionCheckoutRouter(),
    private val confirmPaymentService: ConfirmPaymentService = DefaultConfirmPaymentService(
        paymentManager = paymentManager,
        fragment = fragment,
        activityProvider = { activity },
        applicationContext = applicationContext,
    ),
    private val verificationService: PaymentConsentVerificationService = DefaultPaymentConsentVerificationService(
        paymentManager = paymentManager,
        fragment = fragment,
        activityProvider = { activity },
        applicationContext = applicationContext,
    ),
    private val googlePayCheckoutDelegate: GooglePayCheckoutDelegate = GooglePayCheckoutDelegate(
        fragment = fragment,
        activityProvider = { activity },
        applicationContext = applicationContext,
    ),
    private val unifiedCheckoutExecutor: UnifiedCheckoutExecutor = UnifiedCheckoutExecutor(
        confirmPaymentService = confirmPaymentService,
        googlePayDelegate = googlePayCheckoutDelegate,
    ),
    legacyFlowCheckoutExecutor: LegacyFlowCheckoutExecutor? = null,
) {

    private val legacyFlowCheckoutExecutor: LegacyFlowCheckoutExecutor =
        legacyFlowCheckoutExecutor ?: LegacyFlowCheckoutExecutor(
            fragment = fragment,
            activityProvider = { activity },
            applicationContext = applicationContext,
            paymentManager = paymentManager,
            confirmPaymentService = confirmPaymentService,
            verificationService = verificationService,
            googlePayDelegate = googlePayCheckoutDelegate,
            createCardPaymentMethod = { session, card, billing, saveCard, listener ->
                createCardPaymentMethod(session, card, billing, saveCard, listener)
            },
            buildCreatePaymentConsentOptions = { params -> buildCreatePaymentConsentOptions(params) },
        )

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

    private fun setupAnalyticsLoggerAsApiIfNotSet(session: AirwallexSession) {
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
        setupAnalyticsLoggerAsApiIfNotSet(session)
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
        val paymentMethod = paymentConsent.paymentMethod
        val loggingListener = wrapListenerWithLogging(listener, paymentMethod?.type ?: "unknown")
        if (session !is Session && session !is AirwallexPaymentSession) {
            loggingListener.onCompleted(
                AirwallexPaymentStatus.Failure(
                    AirwallexCheckoutException(message = "checkout with paymentConsent only support AirwallexPaymentSession or Session")
                )
            )
            return
        }
        if (paymentMethod == null) {
            loggingListener.onCompleted(
                AirwallexPaymentStatus.Failure(
                    AirwallexCheckoutException(
                        message = "paymentMethod is required"
                    )
                )
            )
            return
        }
        if (paymentConsent.id.isNullOrEmpty()) {
            AirwallexLogger.info("confirmPaymentIntent, paymentConsentId isNullOrEmpty")
            loggingListener.onCompleted(
                AirwallexPaymentStatus.Failure(
                    AirwallexCheckoutException(
                        message = "paymentConsentId is required"
                    )
                )
            )
            return
        }
        checkout(
            session = session,
            paymentMethod = paymentMethod,
            paymentConsent = paymentConsent,
            listener = loggingListener
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
        // Redirect to checkout() with a minimal PaymentMethod (card type)
        // The paymentConsentId will be used to retrieve the actual payment details
        checkout(
            session = session,
            paymentMethod = PaymentMethod(type = PaymentMethodType.CARD.value),
            paymentConsent =
                if (session is AirwallexPaymentSession || (session is Session && session.isOneOffPayment)) {
                    PaymentConsent(
                        id = paymentConsentId,
                        nextTriggeredBy = PaymentConsent.NextTriggeredBy.CUSTOMER,
                    )
                } else {
                    listener.onCompleted(
                        AirwallexPaymentStatus.Failure(
                            AirwallexCheckoutException(message = "checkout with paymentConsentId only support oneoff payment")
                        )
                    )
                    return
                },
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

        setupAnalyticsLoggerAsApiIfNotSet(session)
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
        setupAnalyticsLoggerAsApiIfNotSet(session)
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
                countryCode = params.countryCode,
                languageCode = resolveLanguageCode(session.locale)
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
        val secret =
            getClientSecret(session).takeIf { !it.isNullOrBlank() } ?: return Result.failure(
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
                val methods = addMaestroWhenMasterCardPresent(
                    filterPaymentMethodsBySession(
                        retrieveMethods.await(), session.paymentMethods
                    )
                )
                val consents = retrieveConsents.await()
                Result.success(
                    Pair(
                        methods,
                        filterPaymentConsentsBySession(session, methods, consents)
                    )
                )
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

    fun getClientSecret(session: AirwallexSession): String? =
        session.clientSecret?.takeIf { it.isNotBlank() }

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

    private fun addMaestroWhenMasterCardPresent(
        methods: List<AvailablePaymentMethodType>,
    ): List<AvailablePaymentMethodType> {
        return methods.map { method ->
            if (method.name != PaymentMethodType.CARD.value) return@map method
            val schemes = method.cardSchemes
            if (schemes.isNullOrEmpty()) return@map method
            method.copy(cardSchemes = schemes.withMaestroIfMasterCard())
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
        verificationService.verify(
            device = device,
            params = params,
            locale = null,
            listener = listener
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

    private fun resolveLanguageCode(locale: Locale?): String {
        val effectiveLocale = locale ?: runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                activity.resources.configuration.locales[0]
            } else {
                @Suppress("DEPRECATION")
                activity.resources.configuration.locale
            }
        }.getOrNull() ?: Locale.getDefault()
        return AirwallexApiRepository.getLanguageCode(effectiveLocale)
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
                openId = params.openId,
                languageCode = resolveLanguageCode(params.locale)
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
                openId = params.openId,
                languageCode = resolveLanguageCode(params.locale)
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

        setupAnalyticsLoggerAsApiIfNotSet(session)
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
     * @param paymentConsent a [PaymentConsent] object used for the payment, optional. Must have a valid ID if provided.
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
        paymentConsent: PaymentConsent? = null,
        cvc: String? = null,
        additionalInfo: Map<String, String>? = null,
        flow: AirwallexPaymentRequestFlow? = null,
        listener: PaymentResultListener,
        saveCard: Boolean = false,
    ) {
        val loggingListener = wrapListenerWithLogging(listener, paymentMethod.type ?: "unknown")
        setupAnalyticsLoggerAsApiIfNotSet(session)

        // Low-level billing-field validation for the new-card flow only.
        // Google Pay billing is configured via GooglePayOptions and validated by the
        // Google Pay sheet itself; LPM billing is driven by the LPM-specific flow.
        if (paymentMethod.type == PaymentMethodType.CARD.value && paymentConsent == null) {
            paymentMethod.billing
                .validateForRequiredFields(session.resolvedRequiredBillingContactFields)
                ?.let {
                    loggingListener.onCompleted(AirwallexPaymentStatus.Failure(it))
                    return
                }
        }

        // Log payment_launched for API integration
        logPaymentLaunchedIfNeeded(paymentConsent?.id, paymentMethod.type)

        val route = checkoutRouter.route(
            session = session,
            paymentMethod = paymentMethod,
            paymentConsent = paymentConsent,
            launchType = AnalyticsLogger.getLaunchType(),
            isAirwallexUIActivity = isAirwallexUIActivity,
        )
        when (route) {
            AirwallexSessionCheckoutRoute.CvcRequired ->
                handleCheckoutApiWithCvc(paymentMethod, session, paymentConsent, loggingListener)

            AirwallexSessionCheckoutRoute.OldFlow ->
                legacyFlowCheckoutExecutor.checkout(
                    session,
                    paymentMethod,
                    cvc,
                    additionalInfo,
                    flow,
                    loggingListener
                )

            is AirwallexSessionCheckoutRoute.NewFlow ->
                unifiedCheckoutExecutor.checkout(
                    route.unifiedSession,
                    paymentMethod,
                    cvc,
                    saveCard,
                    paymentConsent,
                    loggingListener
                )

            is AirwallexSessionCheckoutRoute.UnknownSession ->
                loggingListener.onCompleted(
                    AirwallexPaymentStatus.Failure(
                        AirwallexCheckoutException(message = "Unknown session type: ${route.session.javaClass}")
                    )
                )
        }
    }

    private fun handleCheckoutApiWithCvc(
        paymentMethod: PaymentMethod,
        session: AirwallexSession,
        paymentConsent: PaymentConsent?,
        loggingListener: PaymentResultListener
    ) {
        AirwallexLogger.info("checkout, need cvc")
        val provider = AirwallexPlugins.getProvider(ActionComponentProviderType.CARD)
        provider?.get()?.let { paymentProvider ->
            paymentProvider.handlePaymentData(
                AirwallexCheckoutParam(
                    activity,
                    paymentMethod,
                    session,
                    paymentConsent
                )
            ) { status: AirwallexPaymentStatus? ->
                loggingListener.onCompleted(
                    status ?: AirwallexPaymentStatus.Failure(
                        AirwallexCheckoutException(message = "cvc unknown error")
                    )
                )
            }
        } ?: run {
            AirwallexLogger.error("checkout, Provider is null, unable to handle payment data")
            loggingListener.onCompleted(
                AirwallexPaymentStatus.Failure(
                    AirwallexComponentDependencyException(dependency = Dependency.CARD)
                )
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
        confirmPaymentService.confirmWithDevice(
            device = device,
            params = params,
            locale = null,
            listener = listener
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
                .build()
        )
    }

    suspend fun createPaymentConsent(params: CreatePaymentConsentParams): PaymentConsent {
        return paymentManager.createPaymentConsent(buildCreatePaymentConsentOptions(params))
    }

    private fun wrapListenerWithLogging(
        listener: PaymentResultListener,
        paymentMethod: String
    ): PaymentResultListener = LoggingPaymentResultListener.wrap(listener, paymentMethod)

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
            Crasher.initialize()
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
                val themeConfigClass =
                    Class.forName("com.airwallex.android.ui.composables.AirwallexThemeConfig")
                // Get the INSTANCE field for Kotlin object singleton
                val instanceField = themeConfigClass.getField("INSTANCE")
                val instance = instanceField.get(null)
                // Call initializeContext on the instance
                val initMethod =
                    themeConfigClass.getMethod("initializeContext", Context::class.java)
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