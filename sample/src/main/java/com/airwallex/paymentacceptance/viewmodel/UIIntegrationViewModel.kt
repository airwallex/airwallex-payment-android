package com.airwallex.paymentacceptance.viewmodel

import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import com.airwallex.paymentacceptance.Settings
import com.airwallex.paymentacceptance.autoCapture
import com.airwallex.paymentacceptance.force3DS
import com.airwallex.paymentacceptance.nextTriggerBy
import com.airwallex.paymentacceptance.shipping
import com.airwallex.paymentacceptance.viewmodel.base.BaseViewModel
import java.math.BigDecimal

class UIIntegrationViewModel : BaseViewModel() {

    //AirwallexPaymentStatus is the result returned by the payment flow. You can add your own handling logic based on the final result.
    private val _airwallexPaymentStatus = MutableLiveData<AirwallexPaymentStatus>()
    val airwallexPaymentStatus: LiveData<AirwallexPaymentStatus> = _airwallexPaymentStatus

    private val _airwallexShippingStatus = MutableLiveData<AirwallexShippingStatus>()
    val airwallexShippingStatus: LiveData<AirwallexShippingStatus> = _airwallexShippingStatus

    private val _dialogShowed = MutableLiveData<Boolean>()
    val dialogShowed: LiveData<Boolean> = _dialogShowed

    override fun init(activity: ComponentActivity) {
    }

    /**
     * launch the payment list page
     */
    fun launchPaymentList(activity: ComponentActivity) = run {
        //to perform a Google Pay transaction, you must provide an instance of GooglePayOptions
        val googlePayOptions = GooglePayOptions(
            billingAddressRequired = true,
            billingAddressParameters = BillingAddressParameters(BillingAddressParameters.Format.FULL),
        )
        val session = createSession(googlePayOptions)
        AirwallexStarter.presentEntirePaymentFlow(
            activity = activity,
            session = session,
            layoutType = PaymentMethodsLayoutType.valueOf(Settings.paymentLayout.uppercase()),
            paymentResultListener = object : Airwallex.PaymentResultListener {

                override fun onCompleted(status: AirwallexPaymentStatus) {
                    _airwallexPaymentStatus.value = status
                }
            }
        )
    }

    /**
     * launch the payment list page
     * You can customize the payment methods and their order in the payment list through parameters.
     */
    fun launchCustomPaymentList(activity: ComponentActivity) = run {
        //to perform a Google Pay transaction, you must provide an instance of GooglePayOptions
        val googlePayOptions = GooglePayOptions(
            billingAddressRequired = true,
            billingAddressParameters = BillingAddressParameters(BillingAddressParameters.Format.FULL),
        )
        val session = createSession(
            googlePayOptions,
            listOf("paypal", "card", "Googlepay", "fps", "alipayhk",)//customize the payment methods and their order
        )
        AirwallexStarter.presentEntirePaymentFlow(
            activity = activity,
            session = session,
            layoutType = PaymentMethodsLayoutType.valueOf(Settings.paymentLayout.uppercase()),
            paymentResultListener = object : Airwallex.PaymentResultListener {

                override fun onCompleted(status: AirwallexPaymentStatus) {
                    _airwallexPaymentStatus.value = status
                }
            }
        )
    }

    /**
     * launch the card payment page
     */
    fun launchCardPage(activity: ComponentActivity) = run {
        val session = createSession()
        AirwallexStarter.presentCardPaymentFlow(
            activity = activity,
            session = session,
            paymentResultListener = object : Airwallex.PaymentResultListener {

                override fun onCompleted(status: AirwallexPaymentStatus) {
                    _airwallexPaymentStatus.value = status
                }
            }
        )
    }

    /**
     * launch the card payment dialog
     */
    fun launchCardDialog(activity: ComponentActivity) = run {
        val session = createSession()
        val dialog = AirwallexAddPaymentDialog(
            activity = activity,
            session = session,
            paymentResultListener = object : Airwallex.PaymentResultListener {
                override fun onCompleted(status: AirwallexPaymentStatus) {
                    _airwallexPaymentStatus.value = status
                }
            }
        )
        dialog.show()
        _dialogShowed.value = true
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
    private suspend fun createSession(googlePayOptions: GooglePayOptions? = null,  paymentMethods: List<String>? = listOf()): AirwallexSession {
        return when (Settings.checkoutMode) {
            AirwallexCheckoutMode.PAYMENT -> {
                //get the paymentIntent object from your server
                //please do not directly copy this method!
                val paymentIntent = getPaymentIntentFromServer(force3DS = force3DS)
                // build an AirwallexPaymentSession based on the paymentIntent
                buildAirwallexPaymentSession(googlePayOptions, paymentIntent, paymentMethods)
            }

            AirwallexCheckoutMode.RECURRING -> {
                //get the customerId and clientSecret from your server
                //please do not directly copy these method!
                val customerId = getCustomerIdFromServer()
                val clientSecret = getClientSecretFromServer(customerId)
                //build an AirwallexRecurringSession based on the customerId and clientSecret
                buildAirwallexRecurringSession(googlePayOptions, customerId, clientSecret, paymentMethods)
            }

            AirwallexCheckoutMode.RECURRING_WITH_INTENT -> {
                //get the customerId and paymentIntent from your server
                //please do not directly copy these method!
                val customerId = getCustomerIdFromServer()
                val paymentIntent = getPaymentIntentFromServer(force3DS = force3DS, customerId = customerId)
                //build an AirwallexRecurringWithIntentSession based on the paymentIntent
                buildAirwallexRecurringWithIntentSession(googlePayOptions, paymentIntent, paymentMethods)
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
            .setPaymentMethods(paymentMethods)
            .setShipping(shipping)
            .build()

    /**
     * build an AirwallexRecurringSession based on the customerId and clientSecret
     * @param customerId get this from your sever
     * @param clientSecret get this from your sever
     */
    private fun buildAirwallexRecurringSession(googlePayOptions: GooglePayOptions? = null, customerId: String, clientSecret: String, paymentMethods: List<String>? = listOf()) =
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
            //only nextTriggerBy is merchant, merchantTriggerReason is required
            .setMerchantTriggerReason(PaymentConsent.MerchantTriggerReason.SCHEDULED)
            .setGooglePayOptions(googlePayOptions)
            .setReturnUrl(Settings.returnUrl)
            .setPaymentMethods(paymentMethods)
            .build()

    /**
     * build an AirwallexRecurringWithIntentSession based on the customerId and paymentIntent
     * @param paymentIntent get this from your sever
     */
    private fun buildAirwallexRecurringWithIntentSession(googlePayOptions: GooglePayOptions? = null, paymentIntent: PaymentIntent, paymentMethods: List<String>? = listOf()) =
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
            //only nextTriggerBy is merchant, merchantTriggerReason is required
            .setMerchantTriggerReason(PaymentConsent.MerchantTriggerReason.UNSCHEDULED)
            .setReturnUrl(Settings.returnUrl)
            .setAutoCapture(autoCapture)
            .setGooglePayOptions(googlePayOptions)
            .setPaymentMethods(paymentMethods)
            .setShipping(shipping)
            .build()

}
