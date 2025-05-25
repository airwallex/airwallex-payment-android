package com.airwallex.android.view.composables.consent

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.airwallex.android.R
import com.airwallex.android.core.CardBrand
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.ui.composables.AirwallexTypography
import com.airwallex.android.ui.composables.StandardText
import com.airwallex.android.view.composables.common.CardBrandIcon
import java.util.Locale

@Composable
internal fun ConsentItem(
    consent: PaymentConsent,
    onSelectCard: (PaymentConsent) -> Unit,
    onDeleteCard: (PaymentConsent) -> Unit,
) {
    val method = consent.paymentMethod ?: return
    val card = method.card ?: return

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(
                onClick = {
                    onSelectCard(consent)
                }
            )
            .fillMaxWidth()
            .padding(12.dp),
    ) {
        card.brand?.let { name ->
            CardBrand.fromName(name)?.let { brand ->
                CardBrandIcon(brand = brand)

                Spacer(modifier = Modifier.width(12.dp))
            }
        }

        StandardText(
            text = String.format(
                "%s •••• %s",
                card.brand?.replaceFirstChar {
                    if (it.isLowerCase()) {
                        it.titlecase(
                            Locale.getDefault()
                        )
                    } else it.toString()
                },
                card.last4,
            ),
            typography = AirwallexTypography.Body200,
        )

        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(id = R.drawable.airwallex_ic_three_dots_vertical),
            contentDescription = "three dots",
            modifier = Modifier.clickable(
                onClick = {
                    onDeleteCard(consent)
                },
            ),
        )
    }
}