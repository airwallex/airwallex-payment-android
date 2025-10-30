package com.airwallex.paymentacceptance.viewmodel

import androidx.activity.ComponentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexCheckoutMode
import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexRecurringSession
import com.airwallex.android.core.AirwallexRecurringWithIntentSession
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.BillingAddressParameters
import com.airwallex.android.core.GooglePayOptions
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.RetrieveAvailablePaymentConsentsParams
import com.airwallex.android.core.model.RetrieveAvailablePaymentMethodParams
import com.airwallex.paymentacceptance.Settings
import com.airwallex.paymentacceptance.autoCapture
import com.airwallex.paymentacceptance.nextTriggerBy
import com.airwallex.paymentacceptance.shipping
import com.airwallex.paymentacceptance.viewmodel.base.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

class APIIntegrationViewModel : BaseViewModel() {
    //create your own Airwallex instance to call the APIs.
    private var airwallex: Airwallex? = null

    // AirwallexPaymentStatus is the result returned by the payment flow. You can add your own handling logic based on the final result.
    // Using SharedFlow with default replay=0 ensures the event is only delivered once to active collectors
    // This prevents duplicate callbacks on configuration changes
    private val _airwallexPaymentStatus = MutableSharedFlow<AirwallexPaymentStatus>()
    val airwallexPaymentStatus: SharedFlow<AirwallexPaymentStatus> =
        _airwallexPaymentStatus.asSharedFlow()

    private val _paymentMethodList = MutableLiveData<List<AvailablePaymentMethodType>>()
    val paymentMethodList: LiveData<List<AvailablePaymentMethodType>> = _paymentMethodList

    private val _paymentConsentList = MutableLiveData<List<PaymentConsent>>()
    val paymentConsentList: LiveData<List<PaymentConsent>> = _paymentConsentList

    override fun init(activity: ComponentActivity) {
        airwallex = Airwallex(activity)
    }

    /**
     * use the Airwallex instance to perform card payment.
     * @param card to complete this API call, you must provide a Card instance.
     * @param force3DS set force3DS to true to trigger the 3DS process during the payment flow.
     * @param saveCard set saveCard to true to save the card information while making the payment.
     */
    fun startPayWithCardDetail(
        card: PaymentMethod.Card,
        force3DS: Boolean = false,
        saveCard: Boolean = true
    ) = run {
        val session = createSession(force3DS = force3DS, saveCard = saveCard)
        airwallex?.confirmPaymentIntent(
            session = session,
            card = card,
            billing = null,
            saveCard = saveCard,
            listener = object : Airwallex.PaymentResultListener {
                override fun onCompleted(status: AirwallexPaymentStatus) {
                    viewModelScope.launch {
                        _airwallexPaymentStatus.emit(status)
                    }
                }
            }
        )
    }

    /**
     * start GooglePay
     */
    fun startGooglePay(force3DS: Boolean = false) = run {
        //to perform a Google Pay transaction, you must provide an instance of GooglePayOptions
        val googlePayOptions = GooglePayOptions(
            allowedCardAuthMethods = if (force3DS) listOf("PAN_ONLY") else null,
            billingAddressRequired = true,
            billingAddressParameters = BillingAddressParameters(BillingAddressParameters.Format.FULL),
            skipReadinessCheck = true
        )
        val session = createSession(force3DS = force3DS, googlePayOptions = googlePayOptions)
        airwallex?.startGooglePay(
            session = session,
            listener = object : Airwallex.PaymentResultListener {
                override fun onCompleted(status: AirwallexPaymentStatus) {
                    viewModelScope.launch {
                        _airwallexPaymentStatus.emit(status)
                    }
                }
            }
        )
    }

    /**
     * pay by redirection
     *
     * We do not support all redirection payment methods.
     * Before invoking this method, please ensure that the paymentType you provide is one that we support.
     * Below are some of the methods we currently support.
     * 'poli', 'fpx', 'online_banking', 'permatanet', 'bank_transfer', 'alfamart', 'indomaret', 'doku_ewallet', 'enets', 'payeasy', 'payeasy_atm',
     * 'seven_eleven', 'konbini', 'tesco_lotus', 'grabpay', 'skrill', 'eps', 'giropay', 'ideal', 'multibanco', 'p24', 'sofort', 'trustly', 'bancontact',
     * 'dragonpay', 'blik', 'mybank', 'paybybankapp', 'verkkopankki', 'maxima', 'narvesen', 'paypost', 'perlas_terminals', 'paysafecash', 'paysafecard',
     * 'paysera', 'satispay', 'family_mart', 'hi_life', 'sam_kiosk', 'axs_kiosk', 'bigc', 'esun', 'permata_atm', 'boost', 'shopee_pay', 'paypal', 'payu',
     * 'ovo', 'bitpay', 'atome', 'duit_now', 'pay_now', 'prompt_pay', 'go_pay', 'linkaja', 'jenius_pay', 'klarna', 'spei', 'afterpay'
     * You can check all methods by API reference: https://www.airwallex.com/docs/api#/Payment_Acceptance/Config/_api_v1_pa_config_payment_method_types/get JSON Object field: items.name
     */
    fun startPayByRedirection() = run {
        val session = createSession()
        airwallex?.startRedirectPay(
            session = session,
            paymentMethodName = "paypay",
            listener = object : Airwallex.PaymentResultListener {
                override fun onCompleted(status: AirwallexPaymentStatus) {
                    viewModelScope.launch {
                        _airwallexPaymentStatus.emit(status)
                    }
                }
            }
        )
    }

    /**
     * use the Airwallex instance to perform card payment.
     * @param paymentConsent to complete this API call, you must provide a PaymentConsent instance.
     */
    fun startPayWithConsent(paymentConsent: PaymentConsent) = run {
        requireNotNull(paymentConsent.id)
        val session = createSession()
        airwallex?.confirmPaymentIntent(
            session = session as AirwallexPaymentSession,
            paymentConsent = paymentConsent,
            listener = object : Airwallex.PaymentResultListener {
                override fun onCompleted(status: AirwallexPaymentStatus) {
                    viewModelScope.launch {
                        _airwallexPaymentStatus.emit(status)
                    }
                }
            }
        )
    }

    /**
     * retrieve the list of payment methods you have.
     */
    fun getPaymentMethodsList() = run {
        //to perform a Google Pay transaction, you must provide an instance of GooglePayOptions
        val googlePayOptions = GooglePayOptions(
            billingAddressRequired = true,
            billingAddressParameters = BillingAddressParameters(BillingAddressParameters.Format.FULL)
        )
        val session = createSession(googlePayOptions = googlePayOptions)
        val paymentMethods = loadPagedItems { pageNum ->
            // If you are using Java and cannot directly call the suspend function,
            // you can use the overloaded method `retrieveAvailablePaymentMethods` with a callback.
            // To use, call:
            // retrieveAvailablePaymentMethods(
            //     session = <AirwallexSession>,
            //     params = RetrieveAvailablePaymentMethodParams(<params>),
            //     callback = object : AirwallexCallback<Page<AvailablePaymentMethodType>> {
            //         override fun onSuccess(result: Page<AvailablePaymentMethodType>) {
            //             // Handle success
            //         }
            //         override fun onFailure(error: Throwable) {
            //             // Handle failure
            //         }
            //     }
            // )
            airwallex!!.retrieveAvailablePaymentMethods(
                session = session,
                params = RetrieveAvailablePaymentMethodParams.Builder(
                    clientSecret = getClientSecretFromSession(session),
                    pageNum = pageNum
                )
                    .setActive(true)
                    .setTransactionCurrency(session.currency)
                    .setCountryCode(session.countryCode)
                    .build()
            )
        }
        _paymentMethodList.value = paymentMethods
    }

    /**
     * retrieve your list of saved cards.
     */
    fun getPaymentConsentList() = run {
        //get the customerId and clientSecret from your server
        //please do not directly copy these two methods!
        val customerId = getCustomerIdFromServer()
        val clientSecret = getClientSecretFromServer(customerId)
        val paymentConsents = loadPagedItems { pageNum ->
            // If you are using Java and cannot directly call the suspend function,
            // you can use the overloaded method `retrieveAvailablePaymentConsents` with a callback.
            // To use, call:
            // retrieveAvailablePaymentConsents(
            //     params = RetrieveAvailablePaymentConsentsParams(<params>),
            //     callback = object : AirwallexCallback<Page<PaymentConsent>> {
            //         override fun onSuccess(result: Page<PaymentConsent>) {
            //             // Handle success
            //         }
            //         override fun onFailure(error: Throwable) {
            //             // Handle failure
            //         }
            //     }
            // )
            airwallex!!.retrieveAvailablePaymentConsents(
                RetrieveAvailablePaymentConsentsParams.Builder(
                    clientSecret = clientSecret,
                    customerId = customerId,
                    pageNum = pageNum
                )
                    .setNextTriggeredBy(nextTriggerBy)
                    .setStatus(PaymentConsent.PaymentConsentStatus.VERIFIED)
                    .build()
            )
        }
        _paymentConsentList.value = paymentConsents
    }

    /**
     * this method will create different types of Sessions based on the different modes.
     */
    private suspend fun createSession(
        force3DS: Boolean = false,
        saveCard: Boolean = false,
        googlePayOptions: GooglePayOptions? = null,
    ): AirwallexSession {
        when (Settings.checkoutMode) {
            AirwallexCheckoutMode.PAYMENT -> {
                //get the customerId from your server.
                //if you want to save card , customerId is required
                val customerId = if (saveCard) {
                    getCustomerIdFromServer()
                } else null
                //get the paymentIntent object from your server
                //please do not directly copy this method!
                val paymentIntent = getPaymentIntentFromServer(force3DS, customerId)
                return buildAirwallexPaymentSession(googlePayOptions, paymentIntent)
            }

            AirwallexCheckoutMode.RECURRING -> {
                //get the customerId and clientSecret from your server
                //please do not directly copy these method!
                val customerId = getCustomerIdFromServer()
                val clientSecret = getClientSecretFromServer(customerId)
                //build an AirwallexRecurringSession based on the customerId and clientSecret
                return buildAirwallexRecurringSession(googlePayOptions, customerId, clientSecret)
            }

            AirwallexCheckoutMode.RECURRING_WITH_INTENT -> {
                //get the customerId and paymentIntent from your server
                //please do not directly copy these method!
                val customerId = getCustomerIdFromServer()
                val paymentIntent =
                    getPaymentIntentFromServer(force3DS = force3DS, customerId = customerId)
                //build an AirwallexRecurringWithIntentSession based on the paymentIntent
                return buildAirwallexRecurringWithIntentSession(googlePayOptions, paymentIntent)
            }
        }
    }

    /**
     * build an AirwallexPaymentSession based on the paymentIntent and googlePayOptions
     * @param paymentIntent get this from your sever
     */
    private fun buildAirwallexPaymentSession(
        googlePayOptions: GooglePayOptions? = null,
        paymentIntent: PaymentIntent
    ) =
        AirwallexPaymentSession.Builder(
            paymentIntent = paymentIntent,
            countryCode = Settings.countryCode,
            googlePayOptions = googlePayOptions
        )
            .setRequireBillingInformation(true)
            .setRequireEmail(Settings.requiresEmail.toBoolean())
            .setReturnUrl(Settings.returnUrl)
            .setAutoCapture(autoCapture)
            .setHidePaymentConsents(false)
            .setPaymentMethods(listOf())
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
        clientSecret: String
    ) =
        AirwallexRecurringSession.Builder(
            customerId = customerId,
            clientSecret = clientSecret,
            currency = Settings.currency,
            amount = BigDecimal.valueOf(Settings.price.toDouble()),
            nextTriggerBy = nextTriggerBy,
            countryCode = Settings.countryCode,
        )
            .setRequireEmail(Settings.requiresEmail.toBoolean())
            .setShipping(shipping)
            //only nextTriggerBy is merchant, merchantTriggerReason is required
            .setMerchantTriggerReason(PaymentConsent.MerchantTriggerReason.UNSCHEDULED)
            .setReturnUrl(Settings.returnUrl)
            .setPaymentMethods(listOf())
            .setGooglePayOptions(googlePayOptions)
            .build()

    /**
     * build an AirwallexRecurringWithIntentSession based on the customerId and paymentIntent
     * @param paymentIntent get this from your sever
     */
    private fun buildAirwallexRecurringWithIntentSession(
        googlePayOptions: GooglePayOptions? = null,
        paymentIntent: PaymentIntent
    ) =
        AirwallexRecurringWithIntentSession.Builder(
            paymentIntent = paymentIntent,
            customerId = requireNotNull(
                paymentIntent.customerId,

                ) { "CustomerId is required" },
            nextTriggerBy = nextTriggerBy,
            countryCode = Settings.countryCode
        )
            .setRequireEmail(Settings.requiresEmail.toBoolean())
            //only nextTriggerBy is merchant, merchantTriggerReason is required
            .setMerchantTriggerReason(PaymentConsent.MerchantTriggerReason.SCHEDULED)
            .setReturnUrl(Settings.returnUrl)
            .setAutoCapture(autoCapture)
            .setPaymentMethods(listOf())
            .setGooglePayOptions(googlePayOptions)
            .setShipping(shipping)
            .build()

}
