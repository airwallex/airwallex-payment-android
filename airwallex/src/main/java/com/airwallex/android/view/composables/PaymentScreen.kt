package com.airwallex.android.view.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airwallex.android.R
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

@Composable
internal fun PaymentScreen(
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
    onError: () -> Unit,
) {
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
                onClick = paymentMethodsViewModel::checkoutWithGooglePay,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        PaymentMethodsSection(
            paymentMethodViewModel = paymentMethodsViewModel,
            addPaymentMethodViewModel = addPaymentMethodViewModel,
            availablePaymentMethodTypes = availablePaymentMethodTypes.filterNot { paymentMethodType ->
                paymentMethodType.name == PaymentMethodType.GOOGLEPAY.value
            },
            availablePaymentConsents = availablePaymentConsents,
            onAddCard = onAddCard,
            onDeleteCard = onDeleteCard,
            onCheckoutWithoutCvc = onCheckoutWithoutCvc,
            onDirectPay = onDirectPay,
            onCheckoutWithCvc = onCheckoutWithCvc,
            onPayWithFields = onPayWithFields,
            onLoading = onLoading,
            onError = onError,
        )
    }
}