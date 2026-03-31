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
import com.airwallex.android.core.AirwallexSupportedCard
import com.airwallex.android.core.BillingAddressParameters
import com.airwallex.android.core.GooglePayOptions
import com.airwallex.android.core.PaymentMethodsLayoutType
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.view.AirwallexAddPaymentDialog
import com.airwallex.paymentacceptance.DemoPaymentIntentProvider
import com.airwallex.paymentacceptance.DemoPaymentIntentSource
import com.airwallex.paymentacceptance.Settings
import com.airwallex.paymentacceptance.autoCapture
import com.airwallex.paymentacceptance.force3DS
import com.airwallex.paymentacceptance.nextTriggerBy
import com.airwallex.paymentacceptance.repo.DemoReturnUrl
import com.airwallex.paymentacceptance.shipping
import com.airwallex.paymentacceptance.ui.EmbeddedElementActivity
import com.airwallex.paymentacceptance.viewmodel.base.BaseViewModel
import java.math.BigDecimal

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
            showsGooglePayAsPrimaryButton = true,
            paymentResultListener = object : Airwallex.PaymentResultListener {
                override fun onCompleted(status: AirwallexPaymentStatus) {
                    handlePaymentStatus(session, status)
                }
            }
        )
    }

    /**
     * Traditional flow: Launch with loading for API calls
     */
    private fun launchPaymentListTraditional(activity: ComponentActivity) = launch {
        //to perform a Google Pay transaction, you must provide an instance of GooglePayOptions
        val session = createSessionForUI(googlePayOptions)
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
            showsGooglePayAsPrimaryButton = true,
            paymentResultListener = object : Airwallex.PaymentResultListener {
                override fun onCompleted(status: AirwallexPaymentStatus) {
                    handlePaymentStatus(session, status)
                }
            }
        )
    }

    /**
     * Traditional flow: Launch custom payment list with loading for API calls
     */
    private fun launchCustomPaymentListTraditional(activity: ComponentActivity) = launch {
        //to perform a Google Pay transaction, you must provide an instance of GooglePayOptions
        val session = createSessionForUI(
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
                    handlePaymentStatus(session, status)
                }
            }
        )
    }

    /**
     * Traditional flow: Launch card payment page with loading for API calls
     */
    private fun launchCardPageTraditional(activity: ComponentActivity) = launch {
        val session = createSessionForUI()
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
                    handlePaymentStatus(session, status)
                }
            }
        )
        dialog.show()
    }

    /**
     * Traditional flow: Launch card payment dialog with loading for API calls
     */
    private fun launchCardDialogTraditional(activity: ComponentActivity) = launch {
        val session = createSessionForUI()
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
    fun launchEmbeddedElement(activity: ComponentActivity) {
        // Check if Express Checkout is enabled to determine loading strategy
        if (Settings.expressCheckout == "Enabled" && Settings.checkoutMode == AirwallexCheckoutMode.PAYMENT) {
            // Express Checkout: Create session immediately without API calls, no loading needed
            launchEmbeddedElementExpressCheckout(activity)
        } else {
            // Traditional flow: Show loading for API calls
            launchEmbeddedElementTraditional(activity)
        }
    }

    /**
     * Express Checkout: Launch embedded element immediately without loading
     */
    private fun launchEmbeddedElementExpressCheckout(activity: ComponentActivity) {
        val layoutType = PaymentMethodsLayoutType.valueOf(Settings.paymentLayout.uppercase())
        val supportedCardSchemes = enumValues<AirwallexSupportedCard>().toList()
        EmbeddedElementActivity.start(
            context = activity,
            layoutType = layoutType,
            supportedCardBrands = supportedCardSchemes,
            showsGooglePayAsPrimaryButton = true,
            paymentMethods = listOf()
        )
    }

    /**
     * Traditional flow: Launch embedded element with loading for API calls
     */
    private fun launchEmbeddedElementTraditional(activity: ComponentActivity) {
        val layoutType = PaymentMethodsLayoutType.valueOf(Settings.paymentLayout.uppercase())
        EmbeddedElementActivity.start(
            context = activity,
            layoutType = layoutType,
            supportedCardBrands = null,
            showsGooglePayAsPrimaryButton = true,
            paymentMethods = listOf()
        )
    }

    /**
     * launch the embedded element page with inline Google Pay
     */
    fun launchEmbeddedElementInlineGPay(activity: ComponentActivity) {
        val layoutType = PaymentMethodsLayoutType.valueOf(Settings.paymentLayout.uppercase())
        EmbeddedElementActivity.start(
            context = activity,
            layoutType = layoutType,
            supportedCardBrands = null,
            showsGooglePayAsPrimaryButton = false,
            paymentMethods = listOf()
        )
    }

    /**
     * launch the embedded element page with card only (no Google Pay)
     */
    fun launchEmbeddedElementCardOnly(activity: ComponentActivity) {
        // Check if Express Checkout is enabled to determine loading strategy
        if (Settings.expressCheckout == "Enabled" && Settings.checkoutMode == AirwallexCheckoutMode.PAYMENT) {
            // Express Checkout: Create session immediately without API calls, no loading needed
            launchEmbeddedElementCardOnlyExpressCheckout(activity)
        } else {
            // Traditional flow: Show loading for API calls
            launchEmbeddedElementCardOnlyTraditional(activity)
        }
    }

    /**
     * Express Checkout: Launch embedded element card only immediately without loading
     */
    private fun launchEmbeddedElementCardOnlyExpressCheckout(activity: ComponentActivity) {
        // Card only: use null layoutType with all supported card brands
        val supportedCardBrands = enumValues<AirwallexSupportedCard>().toList()
        EmbeddedElementActivity.start(
            context = activity,
            layoutType = null,
            supportedCardBrands = supportedCardBrands,
            showsGooglePayAsPrimaryButton = true,  // Not used for card-only mode
            paymentMethods = listOf()
        )
    }

    /**
     * Traditional flow: Launch embedded element card only with loading for API calls
     */
    private fun launchEmbeddedElementCardOnlyTraditional(activity: ComponentActivity) {
        // Card only: use null layoutType with all supported card brands
        val supportedCardBrands = enumValues<AirwallexSupportedCard>().toList()
        EmbeddedElementActivity.start(
            context = activity,
            layoutType = null,
            supportedCardBrands = supportedCardBrands,
            showsGooglePayAsPrimaryButton = true,  // Not used for card-only mode
            paymentMethods = listOf()
        )
    }

}
