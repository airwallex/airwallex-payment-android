package com.airwallex.android.view.composables.google

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.view.PaymentFlowListener
import com.airwallex.android.view.PaymentFlowViewModel
import org.json.JSONArray

/**
 * GooglePay standalone button shown on top when showsGooglePayAsPrimaryButton is true.
 * Includes the GooglePaySection and divider when button is visible.
 *
 * @param allowedPaymentMethods JSONArray of allowed payment methods for Google Pay
 * @param paymentFlowListener Listener for payment flow events
 * @param flowViewModel ViewModel for payment flow operations
 */
@Composable
internal fun GooglePayStandaloneButton(
    allowedPaymentMethods: JSONArray?,
    paymentFlowListener: PaymentFlowListener,
    flowViewModel: PaymentFlowViewModel,
) {
    var googlePayButtonVisible by remember { mutableStateOf(false) }

    allowedPaymentMethods?.let { methods ->
        GooglePaySection(
            modifier = Modifier.fillMaxWidth(),
            allowedPaymentMethods = methods.toString().trimIndent(),
            onClick = {
                AnalyticsLogger.logAction(
                    "tap_pay_button",
                    mapOf("payment_method" to PaymentMethodType.GOOGLEPAY.value)
                )
                paymentFlowListener.onLoadingStateChanged(true)
                flowViewModel.checkoutWithGooglePay()
            },
            onScreenViewed = {
                flowViewModel.trackScreenViewed(PaymentMethodType.GOOGLEPAY.value)
            },
            onPayButtonVisibilityChanged = { isVisible ->
                googlePayButtonVisible = isVisible
            },
        )
        if (googlePayButtonVisible) {
            GooglePayDivider(modifier = Modifier.fillMaxWidth())
        }
    }
}
