package com.airwallex.android.view.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airwallex.android.R
import com.airwallex.android.core.PaymentMethodsLayoutType
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.core.model.PaymentMethodTypeInfo
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.ui.composables.AirwallexTypography
import com.airwallex.android.ui.composables.StandardText
import com.airwallex.android.view.AddPaymentMethodViewModel
import com.airwallex.android.view.PaymentMethodsViewModel
import com.airwallex.android.view.composables.google.GooglePaySection
import org.json.JSONArray

@Suppress("LongMethod", "LongParameterList")
@Composable
internal fun PaymentScreen(
    layoutType: PaymentMethodsLayoutType,
    paymentMethodsViewModel: PaymentMethodsViewModel,
    addPaymentMethodViewModel: AddPaymentMethodViewModel,
    allowedPaymentMethods: JSONArray?,
    availablePaymentMethodTypes: List<AvailablePaymentMethodType>,
    availablePaymentConsents: List<PaymentConsent>,
    onAddCard: () -> Unit,
    onDeleteCard: (PaymentConsent) -> Unit,
    onCheckoutWithoutCvc: (PaymentConsent) -> Unit,
    onCheckoutWithCvc: (PaymentConsent, String) -> Unit,
    onDirectPay: (AvailablePaymentMethodType) -> Unit,
    onPayWithFields: (PaymentMethod, PaymentMethodTypeInfo, Map<String, String>) -> Unit,
    onLoading: (Boolean) -> Unit,
) {
    val availableTypes by remember {
        mutableStateOf(
            availablePaymentMethodTypes.filterNot { paymentMethodType ->
                paymentMethodType.name == PaymentMethodType.GOOGLEPAY.value
            }
        )
    }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        StandardText(
            text = stringResource(id = R.string.airwallex_payment_methods),
            color = AirwallexColor.TextPrimary,
            typography = AirwallexTypography.Title200,
            textAlign = TextAlign.Left,
            modifier = Modifier.padding(horizontal = 24.dp),
        )

        allowedPaymentMethods?.let { allowedPaymentMethods ->
            Spacer(modifier = Modifier.height(24.dp))

            GooglePaySection(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                allowedPaymentMethods = allowedPaymentMethods.toString().trimIndent(),
                onClick = {
                    AnalyticsLogger.logAction("tap_pay_button", mapOf("payment_method" to PaymentMethodType.GOOGLEPAY.value))
                    paymentMethodsViewModel.checkoutWithGooglePay()
                },
                onScreenViewed = {
                    paymentMethodsViewModel.trackScreenViewed(PaymentMethodType.GOOGLEPAY.value)
                },
            )
        }

        if (availableTypes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))

            when (layoutType) {
                PaymentMethodsLayoutType.TAB -> {
                    PaymentMethodsTabSection(
                        paymentMethodViewModel = paymentMethodsViewModel,
                        addPaymentMethodViewModel = addPaymentMethodViewModel,
                        availablePaymentMethodTypes = availableTypes,
                        availablePaymentConsents = availablePaymentConsents,
                        onAddCard = {
                            AnalyticsLogger.logAction("tap_pay_button", mapOf("payment_method" to PaymentMethodType.CARD.value))
                            onAddCard()
                        },
                        onDeleteCard = onDeleteCard,
                        onCheckoutWithoutCvc = onCheckoutWithoutCvc,
                        onDirectPay = onDirectPay,
                        onCheckoutWithCvc = onCheckoutWithCvc,
                        onPayWithFields = onPayWithFields,
                        onLoading = onLoading,
                    )
                }
                PaymentMethodsLayoutType.ACCORDION -> {
                    PaymentMethodsAccordionSection(
                        paymentMethodViewModel = paymentMethodsViewModel,
                        addPaymentMethodViewModel = addPaymentMethodViewModel,
                        availablePaymentMethodTypes = availableTypes,
                        availablePaymentConsents = availablePaymentConsents,
                        onAddCard = {
                            AnalyticsLogger.logAction("tap_pay_button", mapOf("payment_method" to PaymentMethodType.CARD.value))
                            onAddCard()
                        },
                        onDeleteCard = onDeleteCard,
                        onCheckoutWithoutCvc = onCheckoutWithoutCvc,
                        onDirectPay = onDirectPay,
                        onCheckoutWithCvc = onCheckoutWithCvc,
                        onPayWithFields = onPayWithFields,
                        onLoading = onLoading,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}