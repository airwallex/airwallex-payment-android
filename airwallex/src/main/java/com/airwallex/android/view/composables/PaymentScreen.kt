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
import androidx.compose.ui.unit.dp
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.view.AddPaymentMethodViewModel
import com.airwallex.android.view.PaymentMethodsViewModel
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
    onPaymentConsentClicked: (PaymentConsent) -> Unit,
    onCheckoutWithCvc: (PaymentConsent, String) -> Unit,
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
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
            addPaymentMethodViewModel = addPaymentMethodViewModel,
            availablePaymentMethodTypes = availablePaymentMethodTypes.filterNot { paymentMethodType ->
                paymentMethodType.name == PaymentMethodType.GOOGLEPAY.value
            },
            availablePaymentConsents = availablePaymentConsents,
            onAddCard = onAddCard,
            onDeleteCard = onDeleteCard,
            onPaymentConsentClicked = onPaymentConsentClicked,
            onCheckoutWithCvc = onCheckoutWithCvc,
        )
    }
}