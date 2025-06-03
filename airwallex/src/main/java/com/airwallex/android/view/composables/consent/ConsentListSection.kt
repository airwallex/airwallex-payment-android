package com.airwallex.android.view.composables.consent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airwallex.android.R
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.ui.composables.ScreenView
import com.airwallex.android.ui.composables.StandardAlertDialog
import java.util.Locale

@Composable
internal fun ConsentListSection(
    availablePaymentConsents: List<PaymentConsent>,
    onSelectCard: (PaymentConsent) -> Unit,
    onDeleteCard: (PaymentConsent) -> Unit,
    onScreenViewed: () -> Unit,
) {
    var localConsentToBeDeleted by remember { mutableStateOf<PaymentConsent?>(null) }

    ScreenView { onScreenViewed() }

    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        availablePaymentConsents.forEach { consent ->
            if (consent.paymentMethod?.card != null) {
                ConsentItem(
                    consent = consent,
                    onSelectCard = onSelectCard,
                    onDeleteCard = { consentToBeDeleted ->
                        localConsentToBeDeleted = consentToBeDeleted
                    },
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    if (localConsentToBeDeleted != null) {
        StandardAlertDialog(
            title = stringResource(
                R.string.airwallex_delete_consent_alert_title, String.format(
                    "%s •••• %s",
                    localConsentToBeDeleted?.paymentMethod?.card?.brand?.uppercase(Locale.ROOT)
                        .orEmpty(),
                    localConsentToBeDeleted?.paymentMethod?.card?.last4.orEmpty(),
                )
            ),
            content = stringResource(id = R.string.airwallex_delete_consent_alert_content),
            confirmButtonTitle = stringResource(R.string.airwallex_delete_payment_method_positive),
            dismissButtonTitle = stringResource(R.string.airwallex_delete_payment_method_negative),
            onConfirm = {
                localConsentToBeDeleted?.let { onDeleteCard(it) }
                localConsentToBeDeleted = null
            },
            onDismiss = {
                localConsentToBeDeleted = null
            },
        )
    }
}