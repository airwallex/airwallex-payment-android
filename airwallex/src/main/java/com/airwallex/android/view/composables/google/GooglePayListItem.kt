package com.airwallex.android.view.composables.google

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airwallex.android.R
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.ui.composables.AirwallexTypography
import com.airwallex.android.ui.composables.StandardText
import com.airwallex.android.view.PaymentFlowListener
import com.airwallex.android.view.PaymentFlowViewModel
import com.airwallex.android.view.util.AnalyticsConstants.PAYMENT_METHOD
import org.json.JSONArray

/**
 * GooglePay list item shown when GooglePay is displayed inside a payment method list.
 * Includes redirect icon, message, and GooglePaySection.
 *
 * @param allowedPaymentMethods JSONArray of allowed payment methods for Google Pay
 * @param paymentFlowListener Listener for payment flow events
 * @param flowViewModel ViewModel for payment flow operations
 * @param airwallex The Airwallex instance for payment operations
 */
@Composable
internal fun GooglePayListItem(
    allowedPaymentMethods: JSONArray?,
    paymentFlowListener: PaymentFlowListener,
    flowViewModel: PaymentFlowViewModel,
    airwallex: Airwallex,
) {
    Column {
        allowedPaymentMethods?.let { methods ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_mobile_redirect),
                    contentDescription = "redirect",
                    modifier = Modifier.size(
                        width = 48.dp,
                        height = 40.dp,
                    ),
                )

                Spacer(modifier = Modifier.width(8.dp))

                StandardText(
                    text = stringResource(id = R.string.airwallex_google_payment_redirect_message),
                    color = AirwallexColor.textPrimary,
                    typography = AirwallexTypography.Body200,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            GooglePaySection(
                modifier = Modifier.fillMaxWidth(),
                allowedPaymentMethods = methods.toString().trimIndent(),
                onClick = {
                    AnalyticsLogger.logAction(
                        "tap_pay_button",
                        mapOf(PAYMENT_METHOD to PaymentMethodType.GOOGLEPAY.value)
                    )
                    paymentFlowListener.onLoadingStateChanged(true, airwallex.activity)
                    flowViewModel.checkoutWithGooglePay()
                },
                onScreenViewed = {
                    flowViewModel.trackScreenViewed(PaymentMethodType.GOOGLEPAY.value)
                },
            )
            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}
