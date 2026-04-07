package com.airwallex.paymentacceptance.viewmodel.base

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexCheckoutMode
import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexRecurringSession
import com.airwallex.android.core.AirwallexRecurringWithIntentSession
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.GooglePayOptions
import com.airwallex.android.core.model.Page
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.paymentacceptance.DemoPaymentIntentProvider
import com.airwallex.paymentacceptance.DemoPaymentIntentSource
import com.airwallex.paymentacceptance.Settings
import com.airwallex.paymentacceptance.autoCapture
import com.airwallex.paymentacceptance.force3DS
import com.airwallex.paymentacceptance.nextTriggerBy
import com.airwallex.paymentacceptance.repo.RepositoryProvider
import com.airwallex.paymentacceptance.repo.DemoReturnUrl
import com.airwallex.paymentacceptance.shipping
import com.airwallex.paymentacceptance.util.PaymentStatusPoller
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.math.BigDecimal
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

abstract class BaseViewModel : ViewModel() {

    private val _createPaymentIntentError = MutableSharedFlow<String?>()
    val createPaymentIntentError: SharedFlow<String?> =
        _createPaymentIntentError.asSharedFlow()

    // AirwallexPaymentStatus is the result returned by the payment flow
    protected val _airwallexPaymentStatus = MutableSharedFlow<AirwallexPaymentStatus>()
    val airwallexPaymentStatus: SharedFlow<AirwallexPaymentStatus> =
        _airwallexPaymentStatus.asSharedFlow()

    // Payment status poller for handling deep link returns
    private var paymentStatusPoller: PaymentStatusPoller? = null

    // Polling result
    private val _pollingResult = MutableSharedFlow<PaymentStatusPoller.PollingResult>()
    val pollingResult: SharedFlow<PaymentStatusPoller.PollingResult> = _pollingResult.asSharedFlow()

    // Activity reference for creating Airwallex instance
    protected var activity: ComponentActivity? = null

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val coroutineExceptionHandler = CoroutineExceptionHandler { context, throwable ->
        _isLoading.postValue(false)
        viewModelScope.launch {
            if (throwable is HttpException) {
                _createPaymentIntentError.emit(
                    throwable.response()?.errorBody()?.string() ?: throwable.localizedMessage
                )
            } else {
                _createPaymentIntentError.emit(throwable.localizedMessage)
            }
        }
    }

    open fun init(activity: ComponentActivity) {
        this.activity = activity
    }

    override fun onCleared() {
        super.onCleared()
        paymentStatusPoller?.stop()
        stopLoading()
    }

    /**
     * Stop active polling (called when user cancels)
     */
    fun stopPolling() {
        paymentStatusPoller?.stop()
        paymentStatusPoller = null
        stopLoading()
    }

    /**
     * Handle payment status - start polling for InProgress, emit status for others
     */
    protected fun handlePaymentStatus(
        session: AirwallexSession,
        status: AirwallexPaymentStatus
    ) {
        viewModelScope.launch {
            if (status is AirwallexPaymentStatus.InProgress) {
                // Start polling for InProgress status
                status.paymentIntentId?.let { intentId ->
                    val clientSecret = getClientSecretFromSession(session)
                    if (clientSecret.isNotEmpty()) {
                        startPolling(intentId, clientSecret)
                    } else {
                        Log.e(TAG, "Client secret is null, cannot start polling")
                    }
                }
            } else {
                stopLoading()
            }
            _airwallexPaymentStatus.emit(status)
        }
    }

    /**
     * Start polling for payment status
     */
    private fun startPolling(intentId: String, clientSecret: String) {
        // Stop any existing poller first
        paymentStatusPoller?.stop()

        val airwallex = activity?.let { Airwallex(it) } ?: return
        val poller = PaymentStatusPoller(
            intentId = intentId,
            clientSecret = clientSecret,
            airwallex = airwallex
        )
        paymentStatusPoller = poller

        viewModelScope.launch {
            val result = poller.getPaymentAttempt()
            _pollingResult.emit(result)
            paymentStatusPoller = null
            stopLoading()
        }
    }

    private val repository = RepositoryProvider.get()

    /**
     * this method demonstrates how to retrieve a paymentIntent from the server.
     * it is only a prerequisite method for initiating the payment flow in the demo.
     * do not copy this method;instead, obtain the paymentIntent from your own server.
     */
    suspend fun getPaymentIntentFromServer(
        force3DS: Boolean = false,
        customerId: String? = null,
        returnUrl: DemoReturnUrl
    ): PaymentIntent {
        return repository.getPaymentIntentFromServer(force3DS, customerId, returnUrl)
    }

    /**
     * this method demonstrates how to retrieve a customerId from the server.
     * it is only a prerequisite method for initiating the payment flow in the demo.
     * do not copy this method;instead, obtain the customerId from your own server.
     */
    suspend fun getCustomerIdFromServer(saveCustomerIdToSetting: Boolean = true): String {
        return repository.getCustomerIdFromServer(saveCustomerIdToSetting)
    }

    /**
     * this method demonstrates how to retrieve a clientSecret from the server.
     * it is only a prerequisite method for initiating the payment flow in the demo.
     * do not copy this method;instead, obtain the clientSecret from your own server.
     */
    suspend fun getClientSecretFromServer(customerId: String): String {
        return repository.getClientSecretFromServer(customerId)
    }

    suspend fun <T> loadPagedItems(
        items: MutableList<T> = Collections.synchronizedList(mutableListOf()),
        pageNum: AtomicInteger = AtomicInteger(0),
        loadPage: suspend (Int) -> Page<T>
    ): List<T> {
        val response = loadPage(pageNum.get())
        pageNum.incrementAndGet()
        items.addAll(response.items)
        return if (response.hasMore) {
            loadPagedItems(items, pageNum, loadPage)
        } else {
            items
        }
    }

    internal fun getClientSecretFromSession(session: AirwallexSession): String {
        return when (session) {
            is AirwallexPaymentSession -> session.paymentIntent?.clientSecret ?: ""
            is AirwallexRecurringWithIntentSession -> session.paymentIntent?.clientSecret ?: ""
            is AirwallexRecurringSession -> session.clientSecret
            else -> ""
        }
    }

    fun launch(block: suspend () -> Unit) {
         viewModelScope.launch(Dispatchers.Main + coroutineExceptionHandler) {
            _isLoading.value = true
            block.invoke()
            _isLoading.value = false
        }
    }

    fun startLoading() {
        _isLoading.value = true
    }

    fun stopLoading() {
        _isLoading.value = false
    }

    /**
     * this method will create different types of Sessions based on the different modes.
     */
    internal suspend fun createSessionForUI(
        googlePayOptions: GooglePayOptions? = null,
        paymentMethods: List<String>? = listOf(),
        returnUrl: DemoReturnUrl = DemoReturnUrl.UIIntegration
    ): AirwallexSession {
        return when (Settings.checkoutMode) {
            AirwallexCheckoutMode.PAYMENT -> {
                if (Settings.expressCheckout == "Enabled") {
                    // Use PaymentIntentProvider for on-demand payment intent creation
                    buildAirwallexPaymentSessionWithProvider(googlePayOptions, paymentMethods, returnUrl)
                } else {
                    //get the paymentIntent object from your server
                    //please do not directly copy this method!
                    val paymentIntent = getPaymentIntentFromServer(force3DS = force3DS, returnUrl = returnUrl)
                    // build an AirwallexPaymentSession based on the paymentIntent
                    buildAirwallexPaymentSession(googlePayOptions, paymentIntent, paymentMethods, returnUrl)
                }
            }

            AirwallexCheckoutMode.RECURRING -> {
                //get the customerId and clientSecret from your server
                //please do not directly copy these method!
                val customerId = getCustomerIdFromServer()
                val clientSecret = getClientSecretFromServer(customerId)
                //build an AirwallexRecurringSession based on the customerId and clientSecret
                buildAirwallexRecurringSession(
                    googlePayOptions,
                    customerId,
                    clientSecret,
                    paymentMethods
                )
            }

            AirwallexCheckoutMode.RECURRING_WITH_INTENT -> {
                if (Settings.expressCheckout == "Enabled") {
                    // Get the customerId for the provider, then use PaymentIntentProvider
                    val customerId = getCustomerIdFromServer()
                    buildAirwallexRecurringWithIntentSessionWithProvider(
                        googlePayOptions,
                        customerId,
                        paymentMethods,
                        returnUrl
                    )
                } else {
                    //get the customerId and paymentIntent from your server
                    //please do not directly copy these method!
                    val customerId = getCustomerIdFromServer()
                    val paymentIntent =
                        getPaymentIntentFromServer(force3DS = force3DS, customerId = customerId, returnUrl = returnUrl)
                    //build an AirwallexRecurringWithIntentSession based on the paymentIntent
                    buildAirwallexRecurringWithIntentSession(
                        googlePayOptions,
                        paymentIntent,
                        paymentMethods,
                        returnUrl
                    )
                }
            }
        }
    }

    /**
     * build an AirwallexPaymentSession based on the paymentIntent
     * @param paymentIntent get this from your sever
     */
    protected fun buildAirwallexPaymentSession(
        googlePayOptions: GooglePayOptions? = null,
        paymentIntent: PaymentIntent,
        paymentMethods: List<String>? = listOf(),
        returnUrl: DemoReturnUrl = DemoReturnUrl.UIIntegration
    ) = AirwallexPaymentSession.Builder(
        paymentIntent = paymentIntent,
        countryCode = Settings.countryCode,
        googlePayOptions = googlePayOptions
    )
        .setRequireBillingInformation(true)
        .setRequireEmail(Settings.requiresEmail.toBoolean())
        .setReturnUrl(returnUrl.fullUrl)
        .setAutoCapture(autoCapture)
        .setHidePaymentConsents(false)
        .setPaymentMethods(paymentMethods)
        .setShipping(shipping)
        .build()

    /**
     * build an AirwallexRecurringSession based on the customerId and clientSecret
     * @param customerId get this from your sever
     * @param clientSecret get this from your sever
     */
    protected fun buildAirwallexRecurringSession(
        googlePayOptions: GooglePayOptions? = null,
        customerId: String,
        clientSecret: String,
        paymentMethods: List<String>? = listOf()
    ) = AirwallexRecurringSession.Builder(
        customerId = customerId,
        clientSecret = clientSecret,
        currency = Settings.currency,
        amount = BigDecimal.valueOf(Settings.price.toDouble()),
        nextTriggerBy = nextTriggerBy,
        countryCode = Settings.countryCode
    )
        .setRequireEmail(Settings.requiresEmail.toBoolean())
        .setShipping(shipping)
        .setMerchantTriggerReason(PaymentConsent.MerchantTriggerReason.SCHEDULED)
        .setGooglePayOptions(googlePayOptions)
        .setReturnUrl(DemoReturnUrl.UIIntegration.fullUrl)
        .setPaymentMethods(paymentMethods)
        .build()

    /**
     * build an AirwallexRecurringWithIntentSession based on the customerId and paymentIntent
     * @param paymentIntent get this from your sever
     */
    protected fun buildAirwallexRecurringWithIntentSession(
        googlePayOptions: GooglePayOptions? = null,
        paymentIntent: PaymentIntent,
        paymentMethods: List<String>? = listOf(),
        returnUrl: DemoReturnUrl = DemoReturnUrl.UIIntegration
    ) = AirwallexRecurringWithIntentSession.Builder(
        paymentIntent = paymentIntent,
        customerId = requireNotNull(paymentIntent.customerId) { "CustomerId is required" },
        nextTriggerBy = nextTriggerBy,
        countryCode = Settings.countryCode
    )
        .setRequireEmail(Settings.requiresEmail.toBoolean())
        .setMerchantTriggerReason(PaymentConsent.MerchantTriggerReason.UNSCHEDULED)
        .setReturnUrl(returnUrl.fullUrl)
        .setAutoCapture(autoCapture)
        .setGooglePayOptions(googlePayOptions)
        .setPaymentMethods(paymentMethods)
        .setShipping(shipping)
        .build()

    /**
     * build an AirwallexPaymentSession using PaymentIntentProvider for Express Checkout
     */
    protected fun buildAirwallexPaymentSessionWithProvider(
        googlePayOptions: GooglePayOptions? = null,
        paymentMethods: List<String>? = listOf(),
        returnUrl: DemoReturnUrl = DemoReturnUrl.UIIntegration
    ) = AirwallexPaymentSession.Builder(
        // You can use paymentIntentSource (Kotlin coroutine pattern) or paymentIntentProvider (Java callback pattern) based on your preference
        // Example with paymentIntentProvider: paymentIntentProvider = DemoPaymentIntentProvider(force3DS = force3DS, customerId = Settings.cachedCustomerId)
        paymentIntentSource = DemoPaymentIntentSource(
            force3DS = force3DS,
            customerId = Settings.cachedCustomerId,
            returnUrl = returnUrl
        ),
        countryCode = Settings.countryCode,
        customerId = Settings.cachedCustomerId,
        googlePayOptions = googlePayOptions
    )
        .setRequireBillingInformation(true)
        .setRequireEmail(Settings.requiresEmail.toBoolean())
        .setReturnUrl(returnUrl.fullUrl)
        .setAutoCapture(autoCapture)
        .setHidePaymentConsents(false)
        .setPaymentMethods(paymentMethods)
        .setShipping(shipping)
        .build()

    /**
     * build an AirwallexRecurringWithIntentSession using PaymentIntentProvider for Express Checkout
     */
    protected fun buildAirwallexRecurringWithIntentSessionWithProvider(
        googlePayOptions: GooglePayOptions? = null,
        customerId: String,
        paymentMethods: List<String>? = listOf(),
        returnUrl: DemoReturnUrl = DemoReturnUrl.UIIntegration
    ) = AirwallexRecurringWithIntentSession.Builder(
        // You can use paymentIntentSource (Kotlin coroutine pattern) or paymentIntentProvider (Java callback pattern) based on your preference
        // Example with paymentIntentSource: PaymentIntentSource = DemoPaymentIntentSource(force3DS = force3DS, customerId = Settings.cachedCustomerId)
        paymentIntentProvider = DemoPaymentIntentProvider(
            force3DS = force3DS,
            customerId = Settings.cachedCustomerId,
            returnUrl = returnUrl
        ),
        customerId = customerId,
        nextTriggerBy = nextTriggerBy,
        countryCode = Settings.countryCode
    )
        .setRequireEmail(Settings.requiresEmail.toBoolean())
        .setMerchantTriggerReason(PaymentConsent.MerchantTriggerReason.UNSCHEDULED)
        .setReturnUrl(returnUrl.fullUrl)
        .setAutoCapture(autoCapture)
        .setGooglePayOptions(googlePayOptions)
        .setPaymentMethods(paymentMethods)
        .setShipping(shipping)
        .build()

    companion object {
        private const val TAG = "BaseViewModel"
    }
}