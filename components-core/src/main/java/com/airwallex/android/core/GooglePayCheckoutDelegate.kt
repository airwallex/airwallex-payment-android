package com.airwallex.android.core

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import com.airwallex.android.core.Airwallex.PaymentResultListener
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.model.Billing
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.core.util.SessionUtils.getIntentId

/**
 * Launches the Google Pay sheet and produces a [PaymentMethod.GooglePay] token. This is the shared
 * first step of every Google Pay checkout on the [AirwallexSession] family; the token is handed to
 * [onToken] together with the resolved provider so each flow can complete the payment its own way.
 */
internal class GooglePayCheckoutDelegate(
    private val fragment: Fragment?,
    private val activityProvider: () -> ComponentActivity,
    private val applicationContext: Context,
) {

    fun acquireToken(
        session: AirwallexSession,
        listener: PaymentResultListener,
        onToken: (googlePay: PaymentMethod.GooglePay, provider: ActionComponentProvider<out ActionComponent>) -> Unit
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
            activity = activityProvider(),
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
                            onToken(googlePay, googlePayProvider)
                        }

                        else -> listener.onCompleted(status)
                    }
                }
            }
        )
    }

    private fun buildGooglePayFromAdditionalInfo(additionalInfo: Map<String, Any>?): PaymentMethod.GooglePay? {
        if (additionalInfo == null) return null
        return PaymentMethod.GooglePay.Builder()
            .setBilling(additionalInfo["billing"] as? Billing)
            .setPaymentDataType(additionalInfo["payment_data_type"] as? String)
            .setEncryptedPaymentToken(additionalInfo["encrypted_payment_token"] as? String)
            .build()
    }
}
