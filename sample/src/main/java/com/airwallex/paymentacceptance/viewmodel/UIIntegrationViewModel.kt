package com.airwallex.paymentacceptance.viewmodel

import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.airwallex.android.AirwallexStarter
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexCheckoutMode
import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexRecurringSession
import com.airwallex.android.core.AirwallexRecurringWithIntentSession
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.AirwallexShippingStatus
import com.airwallex.android.core.BillingAddressParameters
import com.airwallex.android.core.GooglePayOptions
import com.airwallex.android.core.PaymentMethodsLayoutType
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.view.AirwallexAddPaymentDialog
import com.airwallex.paymentacceptance.DemoPaymentIntentProvider
import com.airwallex.paymentacceptance.Settings
import com.airwallex.paymentacceptance.autoCapture
import com.airwallex.paymentacceptance.force3DS
import com.airwallex.paymentacceptance.nextTriggerBy
import com.airwallex.paymentacceptance.shipping
import com.airwallex.paymentacceptance.viewmodel.base.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

class UIIntegrationViewModel : BaseViewModel() {

    // AirwallexPaymentStatus is the result returned by the payment flow. You can add your own handling logic based on the final result.
    // Using SharedFlow with default replay=0 ensures the event is only delivered once to active collectors
    // This prevents duplicate callbacks on configuration changes
    private val _airwallexPaymentStatus = MutableSharedFlow<AirwallexPaymentStatus>()
    val airwallexPaymentStatus: SharedFlow<AirwallexPaymentStatus> =
        _airwallexPaymentStatus.asSharedFlow()

    private val _airwallexShippingStatus = MutableLiveData<AirwallexShippingStatus>()
    val airwallexShippingStatus: LiveData<AirwallexShippingStatus> = _airwallexShippingStatus

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val googlePayOptions by lazy {
        GooglePayOptions(
            billingAddressRequired = true,
            billingAddressParameters = BillingAddressParameters(BillingAddressParameters.Format.FULL),
        )
    }

    override fun init(activity: ComponentActivity) {
    }

    /**
     * launch the payment list page
     */
    fun launchPaymentList(activity: ComponentActivity) {
        // Check if Express Checkout is enabled to determine loading strategy
        if (Settings.expressCheckout == "Enabled" && Settings.checkoutMode == AirwallexCheckoutMode.PAYMENT) {
            // Express Checkout: Create session immediately without API calls, no loading needed
            launchPaymentListExpressCheckout(activity)
        } else {
            // Traditional flow: Show loading for API calls
            launchPaymentListTraditional(activity)
        }
    }

    /**
     * Express Checkout: Launch immediately without loading
     */
    private fun launchPaymentListExpressCheckout(activity: ComponentActivity) {
        //to perform a Google Pay transaction, you must provide an instance of GooglePayOptions
        val session = buildAirwallexPaymentSessionWithProvider(googlePayOptions)
        AirwallexStarter.presentEntirePaymentFlow(
            activity = activity,
            session = session,
            layoutType = PaymentMethodsLayoutType.valueOf(Settings.paymentLayout.uppercase()),
            paymentResultListener = object : Airwallex.PaymentResultListener {
                override fun onCompleted(status: AirwallexPaymentStatus) {
                    viewModelScope.launch {
                        _airwallexPaymentStatus.emit(status)
                    }
                }
            }
        )
    }

    /**
     * Traditional flow: Launch with loading for API calls
     */
    private fun launchPaymentListTraditional(activity: ComponentActivity) = run {
        //to perform a Google Pay transaction, you must provide an instance of GooglePayOptions
        val session = createSession(googlePayOptions)
        AirwallexStarter.presentEntirePaymentFlow(
            activity = activity,
            session = session,
            layoutType = PaymentMethodsLayoutType.valueOf(Settings.paymentLayout.uppercase()),
            paymentResultListener = object : Airwallex.PaymentResultListener {
                override fun onCompleted(status: AirwallexPaymentStatus) {
                    viewModelScope.launch {
                        _isLoading.value = false
                        _airwallexPaymentStatus.emit(status)
                    }
                }
            }
        )
    }

    /**
     * launch the payment list page
     * You can customize the payment methods and their order in the payment list through parameters.
     */
    fun launchCustomPaymentList(activity: ComponentActivity) {
        if (Settings.expressCheckout == "Enabled" && Settings.checkoutMode == AirwallexCheckoutMode.PAYMENT) {
            // Express Checkout: Create session immediately without API calls, no loading needed
            launchCustomPaymentListExpressCheckout(activity)
        } else {
            // Traditional flow: Show loading for API calls
            launchCustomPaymentListTraditional(activity)
        }
    }

    /**
     * Express Checkout: Launch custom payment list immediately without loading
     */
    private fun launchCustomPaymentListExpressCheckout(activity: ComponentActivity) {
        //to perform a Google Pay transaction, you must provide an instance of GooglePayOptions
        val session = buildAirwallexPaymentSessionWithProvider(
            googlePayOptions,
            //customize the payment methods and their order
            listOf("paypal", "card", "Googlepay", "fps", "alipayhk")
        )
        AirwallexStarter.presentEntirePaymentFlow(
            activity = activity,
            session = session,
            layoutType = PaymentMethodsLayoutType.valueOf(Settings.paymentLayout.uppercase()),
            paymentResultListener = object : Airwallex.PaymentResultListener {
                override fun onCompleted(status: AirwallexPaymentStatus) {
                    viewModelScope.launch {
                        _airwallexPaymentStatus.emit(status)
                    }
                }
            }
        )
    }

    /**
     * Traditional flow: Launch custom payment list with loading for API calls
     */
    private fun launchCustomPaymentListTraditional(activity: ComponentActivity) = run {
        //to perform a Google Pay transaction, you must provide an instance of GooglePayOptions
        val session = createSession(
            googlePayOptions,
            //customize the payment methods and their order
            listOf("paypal", "card", "Googlepay", "fps", "alipayhk")
        )
        AirwallexStarter.presentEntirePaymentFlow(
            activity = activity,
            session = session,
            layoutType = PaymentMethodsLayoutType.valueOf(Settings.paymentLayout.uppercase()),
            paymentResultListener = object : Airwallex.PaymentResultListener {
                override fun onCompleted(status: AirwallexPaymentStatus) {
                    viewModelScope.launch {
                        _isLoading.value = false
                        _airwallexPaymentStatus.emit(status)
                    }
                }
            }
        )
    }

    /**
     * launch the card payment page
     */
    fun launchCardPage(activity: ComponentActivity) {
        // Check if Express Checkout is enabled to determine loading strategy
        if (Settings.expressCheckout == "Enabled" && Settings.checkoutMode == AirwallexCheckoutMode.PAYMENT) {
            // Express Checkout: Create session immediately without API calls, no loading needed
            launchCardPageExpressCheckout(activity)
        } else {
            // Traditional flow: Show loading for API calls
            launchCardPageTraditional(activity)
        }
    }

    /**
     * Express Checkout: Launch card payment page immediately without loading
     */
    private fun launchCardPageExpressCheckout(activity: ComponentActivity) {
        val session = buildAirwallexPaymentSessionWithProvider()
        AirwallexStarter.presentCardPaymentFlow(
            activity = activity,
            session = session,
            paymentResultListener = object : Airwallex.PaymentResultListener {
                override fun onCompleted(status: AirwallexPaymentStatus) {
                    viewModelScope.launch {
                        _airwallexPaymentStatus.emit(status)
                    }
                }
            }
        )
    }

    /**
     * Traditional flow: Launch card payment page with loading for API calls
     */
    private fun launchCardPageTraditional(activity: ComponentActivity) = run {
        val session = createSession()
        AirwallexStarter.presentCardPaymentFlow(
            activity = activity,
            session = session,
            paymentResultListener = object : Airwallex.PaymentResultListener {
                override fun onCompleted(status: AirwallexPaymentStatus) {
                    viewModelScope.launch {
                        _isLoading.value = false
                        _airwallexPaymentStatus.emit(status)
                    }
                }
            }
        )
    }

    /**
     * launch the card payment dialog
     */
    fun launchCardDialog(activity: ComponentActivity) {
        // Check if Express Checkout is enabled to determine loading strategy
        if (Settings.expressCheckout == "Enabled" && Settings.checkoutMode == AirwallexCheckoutMode.PAYMENT) {
            // Express Checkout: Create dialog immediately without API calls, no loading needed
            launchCardDialogExpressCheckout(activity)
        } else {
            // Traditional flow: Show loading for API calls
            launchCardDialogTraditional(activity)
        }
    }

    /**
     * Express Checkout: Launch card payment dialog immediately without loading
     */
    private fun launchCardDialogExpressCheckout(activity: ComponentActivity) {
        val session = buildAirwallexPaymentSessionWithProvider()
        val dialog = AirwallexAddPaymentDialog(
            activity = activity,
            session = session,
            paymentResultListener = object : Airwallex.PaymentResultListener {
                override fun onCompleted(status: AirwallexPaymentStatus) {
                    _isLoading.value = false
                    viewModelScope.launch {
                        _airwallexPaymentStatus.emit(status)
                    }
                }
            }
        )
        dialog.show()
    }

    /**
     * Traditional flow: Launch card payment dialog with loading for API calls
     */
    private fun launchCardDialogTraditional(activity: ComponentActivity) = run {
        val session = createSession()
        val dialog = AirwallexAddPaymentDialog(
            activity = activity,
            session = session,
            paymentResultListener = object : Airwallex.PaymentResultListener {
                override fun onCompleted(status: AirwallexPaymentStatus) {
                    viewModelScope.launch {
                        _airwallexPaymentStatus.emit(status)
                    }
                }
            }
        )
        dialog.show()
    }

    /**
     * launch the shipping page
     */
    fun launchShipping(activity: AppCompatActivity) {
        AirwallexStarter.presentShippingFlow(
            activity = activity,
            shipping = shipping,
            shippingResultListener = object : Airwallex.ShippingResultListener {
                override fun onCompleted(status: AirwallexShippingStatus) {
                    _airwallexShippingStatus.value = status
                }
            })
    }

    /**
     * this method will create different types of Sessions based on the different modes.
     */
    private suspend fun createSession(
        googlePayOptions: GooglePayOptions? = null,
        paymentMethods: List<String>? = listOf()
    ): AirwallexSession {
        _isLoading.value = true
        return when (Settings.checkoutMode) {
            AirwallexCheckoutMode.PAYMENT -> {
                if (Settings.expressCheckout == "Enabled") {
                    // Use PaymentIntentProvider for on-demand payment intent creation
                    buildAirwallexPaymentSessionWithProvider(googlePayOptions, paymentMethods)
                } else {
                    //get the paymentIntent object from your server
                    //please do not directly copy this method!
                    val paymentIntent = getPaymentIntentFromServer(force3DS = force3DS)
                    // build an AirwallexPaymentSession based on the paymentIntent
                    buildAirwallexPaymentSession(googlePayOptions, paymentIntent, paymentMethods)
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
                        paymentMethods
                    )
                } else {
                    //get the customerId and paymentIntent from your server
                    //please do not directly copy these method!
                    val customerId = getCustomerIdFromServer()
                    val paymentIntent =
                        getPaymentIntentFromServer(force3DS = force3DS, customerId = customerId)
                    //build an AirwallexRecurringWithIntentSession based on the paymentIntent
                    buildAirwallexRecurringWithIntentSession(
                        googlePayOptions,
                        paymentIntent,
                        paymentMethods
                    )
                }
            }
        }
    }

    /**
     * build an AirwallexPaymentSession based on the paymentIntent
     * @param paymentIntent get this from your sever
     */
    private fun buildAirwallexPaymentSession(
        googlePayOptions: GooglePayOptions? = null,
        paymentIntent: PaymentIntent,
        paymentMethods: List<String>? = listOf()
    ) = AirwallexPaymentSession.Builder(
        paymentIntent = paymentIntent,
        countryCode = Settings.countryCode,
        googlePayOptions = googlePayOptions
    )
        .setRequireBillingInformation(true)
        .setRequireEmail(Settings.requiresEmail.toBoolean())
        .setReturnUrl(Settings.returnUrl)
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
    private fun buildAirwallexRecurringSession(
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
        .setReturnUrl(Settings.returnUrl)
        .setPaymentMethods(paymentMethods)
        .build()

    /**
     * build an AirwallexRecurringWithIntentSession based on the customerId and paymentIntent
     * @param paymentIntent get this from your sever
     */
    private fun buildAirwallexRecurringWithIntentSession(
        googlePayOptions: GooglePayOptions? = null,
        paymentIntent: PaymentIntent,
        paymentMethods: List<String>? = listOf()
    ) = AirwallexRecurringWithIntentSession.Builder(
        paymentIntent = paymentIntent,
        customerId = requireNotNull(paymentIntent.customerId) { "CustomerId is required" },
        nextTriggerBy = nextTriggerBy,
        countryCode = Settings.countryCode
    )
        .setRequireEmail(Settings.requiresEmail.toBoolean())
        .setMerchantTriggerReason(PaymentConsent.MerchantTriggerReason.UNSCHEDULED)
        .setReturnUrl(Settings.returnUrl)
        .setAutoCapture(autoCapture)
        .setGooglePayOptions(googlePayOptions)
        .setPaymentMethods(paymentMethods)
        .setShipping(shipping)
        .build()

    /**
     * build an AirwallexPaymentSession using PaymentIntentProvider for Express Checkout
     */
    private fun buildAirwallexPaymentSessionWithProvider(
        googlePayOptions: GooglePayOptions? = null,
        paymentMethods: List<String>? = listOf()
    ) = AirwallexPaymentSession.Builder(
        paymentIntentProvider = DemoPaymentIntentProvider(
            force3DS = force3DS,
            customerId = Settings.cachedCustomerId
        ),
        countryCode = Settings.countryCode,
        currency = Settings.currency,
        amount = BigDecimal.valueOf(Settings.price.toDouble()),
        customerId = Settings.cachedCustomerId,
        googlePayOptions = googlePayOptions
    )
        .setRequireBillingInformation(true)
        .setRequireEmail(Settings.requiresEmail.toBoolean())
        .setReturnUrl(Settings.returnUrl)
        .setAutoCapture(autoCapture)
        .setHidePaymentConsents(false)
        .setPaymentMethods(paymentMethods)
        .setShipping(shipping)
        .build()

    /**
     * build an AirwallexRecurringWithIntentSession using PaymentIntentProvider for Express Checkout
     */
    private fun buildAirwallexRecurringWithIntentSessionWithProvider(
        googlePayOptions: GooglePayOptions? = null,
        customerId: String,
        paymentMethods: List<String>? = listOf()
    ) = AirwallexRecurringWithIntentSession.Builder(
        paymentIntentProvider = DemoPaymentIntentProvider(
            force3DS = force3DS,
            customerId = customerId
        ),
        customerId = customerId,
        nextTriggerBy = nextTriggerBy,
        countryCode = Settings.countryCode,
        currency = Settings.currency,
        amount = BigDecimal.valueOf(Settings.price.toDouble())
    )
        .setRequireEmail(Settings.requiresEmail.toBoolean())
        .setMerchantTriggerReason(PaymentConsent.MerchantTriggerReason.UNSCHEDULED)
        .setReturnUrl(Settings.returnUrl)
        .setAutoCapture(autoCapture)
        .setGooglePayOptions(googlePayOptions)
        .setPaymentMethods(paymentMethods)
        .setShipping(shipping)
        .build()
}

