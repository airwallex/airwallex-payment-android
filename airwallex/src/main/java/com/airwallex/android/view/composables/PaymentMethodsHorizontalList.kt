package com.airwallex.android.view.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.PaymentConsent

@Composable
internal fun PaymentMethodsHorizontalList(
    availablePaymentConsents: List<PaymentConsent>,
    availablePaymentMethodTypes: List<AvailablePaymentMethodType>,
) {
    LazyRow(modifier = Modifier.padding(horizontal = 24.dp)) {
        availablePaymentConsents.forEachIndexed { index, paymentConsent ->
            item(key = "consent_$index") {
                Spacer(modifier = Modifier.width(12.dp))

                PaymentConsentCard(
                    paymentConsent = paymentConsent,
                    onClick = {}, //TODO
                )

                Spacer(modifier = Modifier.width(12.dp))
            }
        }
        availablePaymentMethodTypes.forEachIndexed { index, availablePaymentMethodType ->
            item(key = "payment_method_$index") {
                Spacer(modifier = Modifier.width(12.dp))

                DynamicPaymentCard(
                    paymentMethodType = availablePaymentMethodType,
                    onClick = {}, //TODO
                )

                Spacer(modifier = Modifier.width(12.dp))
            }
        }
    }
}