package com.airwallex.paymentacceptance.viewmodel

import android.app.Activity
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
import com.airwallex.android.core.Environment
import com.airwallex.android.core.GooglePayOptions
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.RetrieveAvailablePaymentConsentsParams
import com.airwallex.android.core.model.RetrieveAvailablePaymentMethodParams
import com.airwallex.paymentacceptance.R
import com.airwallex.paymentacceptance.Settings
import com.airwallex.paymentacceptance.autoCapture
import com.airwallex.paymentacceptance.demoCard
import com.airwallex.paymentacceptance.demoCard3DS
import com.airwallex.paymentacceptance.nextTriggerBy
import com.airwallex.paymentacceptance.shipping
import com.airwallex.paymentacceptance.stagingCard
import com.airwallex.paymentacceptance.stagingCard3DS
import com.airwallex.paymentacceptance.viewmodel.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal

class APIIntegrationViewModel : BaseViewModel() {

    //our SDK offers three modes of payment flow.
    private var checkoutMode = AirwallexCheckoutMode.PAYMENT

    //create your own Airwallex instance to call the APIs.
    private var airwallex: Airwallex? = null

    //AirwallexPaymentStatus is the result returned by the payment flow. You can add your own handling logic based on the final result.
    private val _airwallexPaymentStatus = MutableLiveData<AirwallexPaymentStatus>()
    val airwallexPaymentStatus: LiveData<AirwallexPaymentStatus> = _airwallexPaymentStatus

    private val _paymentMethodList = MutableLiveData<List<AvailablePaymentMethodType>>()
    val paymentMethodList: LiveData<List<AvailablePaymentMethodType>> = _paymentMethodList

    private val _paymentConsentList = MutableLiveData<List<PaymentConsent>>()
    val paymentConsentList: LiveData<List<PaymentConsent>> = _paymentConsentList

    var environment = Environment.STAGING
    var card = stagingCard
    var card3DS = stagingCard3DS

    override fun init(activity: Activity) {
        airwallex = Airwallex(activity)
        environment = when (Settings.sdkEnv) {
            activity.resources.getStringArray(R.array.array_sdk_env)[0] -> Environment.STAGING
            activity.resources.getStringArray(R.array.array_sdk_env)[1] -> Environment.DEMO
            activity.resources.getStringArray(R.array.array_sdk_env)[2] -> Environment.PRODUCTION
            else -> Environment.STAGING
        }
        if (environment == Environment.STAGING) {
            card = stagingCard
            card3DS = stagingCard3DS
        } else {
            card = demoCard
            card3DS = demoCard3DS
        }
    }

    fun updateCheckoutModel(mode: Int) {
        checkoutMode = when (mode) {
            1 -> AirwallexCheckoutMode.RECURRING
            2 -> AirwallexCheckoutMode.RECURRING_WITH_INTENT
            else -> AirwallexCheckoutMode.PAYMENT
        }
        Settings.checkoutMode = checkoutMode
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
    ) {
        createSession(force3DS = force3DS) {
            airwallex?.confirmPaymentIntent(
                session = it,
                card = card,
                billing = null,
                saveCard = saveCard,
                listener = object : Airwallex.PaymentResultListener {
                    override fun onCompleted(status: AirwallexPaymentStatus) {
                        _airwallexPaymentStatus.value = status
                    }
                }
            )
        }
    }

    /**
     * start GooglePay
     */
    fun startGooglePay() {
        //to perform a Google Pay transaction, you must provide an instance of GooglePayOptions
        val googlePayOptions = GooglePayOptions(
            billingAddressRequired = true,
            billingAddressParameters = BillingAddressParameters(BillingAddressParameters.Format.FULL)
        )
        createSession(googlePayOptions = googlePayOptions) {
            airwallex?.startGooglePay(
                session = it as AirwallexPaymentSession,
                listener = object : Airwallex.PaymentResultListener {
                    override fun onCompleted(status: AirwallexPaymentStatus) {
                        _airwallexPaymentStatus.value = status
                    }
                }
            )
        }
    }

    /**
     * use the Airwallex instance to perform card payment.
     * @param paymentConsent to complete this API call, you must provide a PaymentConsent instance.
     */
    fun startPayWithConsent(paymentConsent: PaymentConsent) {
        if (paymentConsent.id == null || paymentConsent.id == "") return
        createSession {
            airwallex?.confirmPaymentIntent(
                session = it as AirwallexPaymentSession,
                paymentConsentId = paymentConsent.id!!,
                listener = object : Airwallex.PaymentResultListener {
                    override fun onCompleted(status: AirwallexPaymentStatus) {
                        _airwallexPaymentStatus.value = status
                    }
                }
            )
        }
    }

    /**
     * retrieve the list of payment methods you have.
     */
    fun getPaymentMethodsList() {
        //to perform a Google Pay transaction, you must provide an instance of GooglePayOptions
        val googlePayOptions = GooglePayOptions(
            billingAddressRequired = true,
            billingAddressParameters = BillingAddressParameters(BillingAddressParameters.Format.FULL)
        )
        createSession(googlePayOptions = googlePayOptions) { session ->
            viewModelScope.launch(Dispatchers.Main) {
                val paymentMethods = loadPagedItems { pageNum ->
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
        }
    }

    /**
     * retrieve your list of saved cards.
     */
    fun getPaymentConsentList() {
        viewModelScope.launch(Dispatchers.Main) {
            //get the customerId and clientSecret from your server
            //please do not directly copy these two methods!
            val customerId = getCustomerIdFromServer()
            val clientSecret = getClientSecretFromServer(customerId)

            val paymentConsents = loadPagedItems { pageNum ->
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
    }

    /**
     * this method will create different types of Sessions based on the different modes.
     */
    private fun createSession(
        force3DS: Boolean = false,
        googlePayOptions: GooglePayOptions? = null,
        callBack: (session: AirwallexSession) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Main + coroutineExceptionHandler) {
            when (checkoutMode) {
                AirwallexCheckoutMode.PAYMENT -> {
                    //get the paymentIntent object from your server
                    //please do not directly copy this method!
                    val paymentIntent = getPaymentIntentFromServer(force3DS)
                    callBack(buildAirwallexPaymentSession(googlePayOptions, paymentIntent))
                }

                AirwallexCheckoutMode.RECURRING -> {
                    //get the customerId and clientSecret from your server
                    //please do not directly copy these method!
                    val customerId = getCustomerIdFromServer()
                    val clientSecret = getClientSecretFromServer(customerId)
                    //build an AirwallexRecurringSession based on the customerId and clientSecret
                    callBack(buildAirwallexRecurringSession(customerId, clientSecret))
                }

                AirwallexCheckoutMode.RECURRING_WITH_INTENT -> {
                    //get the customerId and paymentIntent from your server
                    //please do not directly copy these method!
                    val customerId = getCustomerIdFromServer()
                    val paymentIntent = getPaymentIntentFromServer(customerId = customerId)
                    //build an AirwallexRecurringWithIntentSession based on the paymentIntent
                    callBack(buildAirwallexRecurringWithIntentSession(paymentIntent))
                }
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
            .build()

    /**
     * build an AirwallexRecurringSession based on the customerId and clientSecret
     * @param customerId get this from your sever
     * @param clientSecret get this from your sever
     */
    private fun buildAirwallexRecurringSession(customerId: String, clientSecret: String) =
        AirwallexRecurringSession.Builder(
            customerId = customerId,
            clientSecret = clientSecret,
            currency = Settings.currency,
            amount = BigDecimal.valueOf(Settings.price.toDouble()),
            nextTriggerBy = nextTriggerBy,
            countryCode = Settings.countryCode
        )
            .setRequireEmail(Settings.requiresEmail.toBoolean())
            .setShipping(shipping)
            .setRequireCvc(Settings.requiresCVC.toBoolean())
            .setMerchantTriggerReason(if (nextTriggerBy == PaymentConsent.NextTriggeredBy.MERCHANT) PaymentConsent.MerchantTriggerReason.SCHEDULED else PaymentConsent.MerchantTriggerReason.UNSCHEDULED)
            .setReturnUrl(Settings.returnUrl)
            .setPaymentMethods(listOf())
            .build()

    /**
     * build an AirwallexRecurringWithIntentSession based on the customerId and paymentIntent
     * @param paymentIntent get this from your sever
     */
    private fun buildAirwallexRecurringWithIntentSession(paymentIntent: PaymentIntent) =
        AirwallexRecurringWithIntentSession.Builder(
            paymentIntent = paymentIntent,
            customerId = requireNotNull(
                paymentIntent.customerId,
                { "CustomerId is required" }
            ),
            nextTriggerBy = nextTriggerBy,
            countryCode = Settings.countryCode
        )
            .setRequireEmail(Settings.requiresEmail.toBoolean())
            .setRequireCvc(Settings.requiresCVC.toBoolean())
            .setMerchantTriggerReason(if (nextTriggerBy == PaymentConsent.NextTriggeredBy.MERCHANT) PaymentConsent.MerchantTriggerReason.SCHEDULED else PaymentConsent.MerchantTriggerReason.UNSCHEDULED)
            .setReturnUrl(Settings.returnUrl)
            .setAutoCapture(autoCapture)
            .setPaymentMethods(listOf())
            .build()

}
