package com.airwallex.paymentacceptance.viewmodel

import android.app.Activity
import androidx.activity.ComponentActivity
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
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.view.AirwallexAddPaymentDialog
import com.airwallex.paymentacceptance.Settings
import com.airwallex.paymentacceptance.nextTriggerBy
import com.airwallex.paymentacceptance.shipping
import com.airwallex.paymentacceptance.viewmodel.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal

class UIIntegrationViewModel : BaseViewModel() {

    private var checkoutMode = AirwallexCheckoutMode.PAYMENT
    private val _airwallexPaymentStatus = MutableLiveData<AirwallexPaymentStatus>()
    val airwallexPaymentStatus: LiveData<AirwallexPaymentStatus> = _airwallexPaymentStatus

    private val _dialogShowed = MutableLiveData<Boolean>()
    val dialogShowed: LiveData<Boolean> = _dialogShowed

    override fun init(activity: Activity) {
    }

    fun updateCheckoutModel(mode: Int) {
        checkoutMode = when (mode) {
            1 -> AirwallexCheckoutMode.RECURRING
            2 -> AirwallexCheckoutMode.RECURRING_WITH_INTENT
            else -> AirwallexCheckoutMode.PAYMENT
        }
    }

    fun launchPaymentList(activity: ComponentActivity) {
        createSession {
            AirwallexStarter.presentEntirePaymentFlow(
                activity,
                it,
                paymentResultListener = object : Airwallex.PaymentResultListener {

                    override fun onCompleted(status: AirwallexPaymentStatus) {
                        _airwallexPaymentStatus.value = status
                    }
                }
            )
        }
    }

    fun launchCardPage(activity: ComponentActivity) {
        createSession {
            AirwallexStarter.presentCardPaymentFlow(
                activity = activity,
                session = it,
                paymentResultListener = object : Airwallex.PaymentResultListener {

                    override fun onCompleted(status: AirwallexPaymentStatus) {
                        _airwallexPaymentStatus.value = status
                    }
                }
            )
        }
    }

    fun launchCardDialog(activity: ComponentActivity) {
        createSession {
            val dialog = AirwallexAddPaymentDialog(
                activity = activity,
                session = it,
                paymentResultListener = object : Airwallex.PaymentResultListener {
                    override fun onCompleted(status: AirwallexPaymentStatus) {
                        _airwallexPaymentStatus.value = status
                    }
                }
            )
            dialog.show()
            _dialogShowed.value = true
        }
    }

    private fun createSession(callBack: (session: AirwallexSession) -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            when (checkoutMode) {
                AirwallexCheckoutMode.PAYMENT -> {
                    val paymentIntent = getPaymentIntentFromServer()
                    callBack(buildAirwallexPaymentSession(paymentIntent))
                }

                AirwallexCheckoutMode.RECURRING -> {
                    val customerId = getCustomerIdFromServer()
                    val clientSecret = getClientSecretFromServer(customerId)
                    callBack(buildAirwallexRecurringSession(customerId, clientSecret))
                }

                AirwallexCheckoutMode.RECURRING_WITH_INTENT -> {
                    val customerId = getCustomerIdFromServer()
                    val paymentIntent = getPaymentIntentFromServer(customerId = customerId)
                    callBack(buildAirwallexRecurringWithIntentSession(paymentIntent))
                }
            }
        }
    }

    private fun buildAirwallexPaymentSession(paymentIntent: PaymentIntent) =
        AirwallexPaymentSession.Builder(
            paymentIntent = paymentIntent,
            countryCode = Settings.countryCode,
        )
            .setRequireBillingInformation(true)
            .setRequireEmail(Settings.requiresEmail.toBoolean())
            .setReturnUrl(Settings.returnUrl)
            .setAutoCapture(Settings.autoCapture.toBoolean())
            .setHidePaymentConsents(false)
            .setPaymentMethods(listOf())
            .build()

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
            .setAutoCapture(Settings.autoCapture.toBoolean())
            .setPaymentMethods(listOf())
            .build()

}
