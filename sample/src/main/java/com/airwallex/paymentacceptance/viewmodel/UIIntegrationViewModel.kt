package com.airwallex.paymentacceptance.viewmodel

import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.airwallex.android.AirwallexStarter
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexShippingStatus
import com.airwallex.android.core.AirwallexSupportedCard
import com.airwallex.android.core.BillingAddressParameters
import com.airwallex.android.core.GooglePayOptions
import com.airwallex.android.core.Appearance
import com.airwallex.android.core.PaymentMethodsLayoutType
import com.airwallex.android.view.AirwallexAddPaymentDialog
import com.airwallex.android.view.composables.PaymentElementConfiguration
import com.airwallex.paymentacceptance.Settings
import com.airwallex.paymentacceptance.shipping
import com.airwallex.paymentacceptance.ui.EmbeddedElementActivity
import com.airwallex.paymentacceptance.viewmodel.base.BaseViewModel

class UIIntegrationViewModel : BaseViewModel() {

    private val _airwallexShippingStatus = MutableLiveData<AirwallexShippingStatus>()
    val airwallexShippingStatus: LiveData<AirwallexShippingStatus> = _airwallexShippingStatus

    private val googlePayOptions by lazy {
        GooglePayOptions(
            billingAddressRequired = true,
            billingAddressParameters = BillingAddressParameters(BillingAddressParameters.Format.FULL),
        )
    }

    /**
     * launch the payment list page
     */
    fun launchPaymentList(activity: ComponentActivity) = launch {
        val session = createSession(googlePayOptions)
        AirwallexStarter.presentEntirePaymentFlow(
            activity = activity,
            session = session,
            configuration = PaymentElementConfiguration.PaymentSheet(
                // sample usage for customizing UI
                googlePayButton = PaymentElementConfiguration.GooglePayButton(
//                    showsAsPrimaryButton = true,
//                    buttonType = ButtonType.Donate
                ),
                checkoutButton = PaymentElementConfiguration.CheckoutButton(
//                    title = "Pay"
                ),
                appearance = Appearance(
//                    themeColor = "#DA8C21".toColorInt(), // set theme color here
//                    isDarkTheme = false // by default will follow system
                ),
                layout = PaymentMethodsLayoutType.valueOf(Settings.paymentLayout.uppercase())
            ),
            paymentResultListener = object : Airwallex.PaymentResultListener {
                override fun onCompleted(status: AirwallexPaymentStatus) {
                    handlePaymentStatus(session, status)
                }
            }
        )
    }

    /**
     * launch the payment list page
     * You can customize the payment methods and their order in the payment list through parameters.
     */
    fun launchCustomPaymentList(activity: ComponentActivity) = launch {
        val session = createSession(
            googlePayOptions,
            //customize the payment methods and their order
            listOf("paypal", "card", "Googlepay", "fps", "alipayhk")
        )
        AirwallexStarter.presentEntirePaymentFlow(
            activity = activity,
            session = session,
            layoutType = PaymentMethodsLayoutType.valueOf(Settings.paymentLayout.uppercase()),
            showsGooglePayAsPrimaryButton = true,
            paymentResultListener = object : Airwallex.PaymentResultListener {
                override fun onCompleted(status: AirwallexPaymentStatus) {
                    handlePaymentStatus(session, status)
                }
            }
        )
    }

    /**
     * launch the card payment page
     */
    fun launchCardPage(activity: ComponentActivity) = launch {
        val session = createSession()
        AirwallexStarter.presentCardPaymentFlow(
            activity = activity,
            session = session,
            paymentResultListener = object : Airwallex.PaymentResultListener {
                override fun onCompleted(status: AirwallexPaymentStatus) {
                    handlePaymentStatus(session, status)
                }
            }
        )
    }

    /**
     * launch the card payment dialog
     */
    fun launchCardDialog(activity: ComponentActivity) = launch {
        val session = createSession()
        val dialog = AirwallexAddPaymentDialog(
            activity = activity,
            session = session,
            paymentResultListener = object : Airwallex.PaymentResultListener {
                override fun onCompleted(status: AirwallexPaymentStatus) {
                    handlePaymentStatus(session, status)
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
     * launch the embedded element page
     */
    fun launchEmbeddedElement(activity: ComponentActivity, showsGooglePayAsPrimaryButton: Boolean = true) {
        val layoutType = PaymentMethodsLayoutType.valueOf(Settings.paymentLayout.uppercase())
        EmbeddedElementActivity.start(
            context = activity,
            layoutType = layoutType,
            supportedCardBrands = null,
            showsGooglePayAsPrimaryButton = showsGooglePayAsPrimaryButton,
            paymentMethods = listOf()
        )
    }

    /**
     * launch the embedded element page with card only (no Google Pay)
     */
    fun launchEmbeddedElementCardOnly(activity: ComponentActivity) {
        val supportedCardBrands = enumValues<AirwallexSupportedCard>().toList()
        EmbeddedElementActivity.start(
            context = activity,
            layoutType = null,
            supportedCardBrands = supportedCardBrands,
            showsGooglePayAsPrimaryButton = true,
            paymentMethods = listOf()
        )
    }
}
