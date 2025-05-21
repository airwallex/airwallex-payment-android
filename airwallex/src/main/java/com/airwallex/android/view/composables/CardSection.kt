package com.airwallex.android.view.composables

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airwallex.android.R
import com.airwallex.android.core.CardBrand
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.ui.composables.AirwallexTypography
import com.airwallex.android.ui.composables.StandardText
import com.airwallex.android.view.AddPaymentMethodViewModel
import com.airwallex.android.view.composables.addcard.AddCardSection
import com.airwallex.android.view.composables.common.CardBrandIcon
import com.airwallex.android.view.composables.consent.ConsentDetailSection
import com.airwallex.android.view.composables.consent.ConsentListSection
import java.util.Locale

sealed interface CardSectionType {
    val screenTitleRes: Int?
    val buttonTitleRes: Int

    object AddCard : CardSectionType {
        @StringRes override val screenTitleRes = R.string.airwallex_add_card_screen_title
        @StringRes override val buttonTitleRes = R.string.airwallex_add_card_button_title
    }

    object ConsentList : CardSectionType {
        @StringRes override val screenTitleRes = R.string.airwallex_consent_list_screen_title
        @StringRes override val buttonTitleRes = R.string.airwallex_consent_list_button_title
    }

    data class ConsentDetail(
        val consent: PaymentConsent,
    ) : CardSectionType {
        override val screenTitleRes = null
        @StringRes override val buttonTitleRes = R.string.airwallex_consent_detail_button_title
    }
}

@Composable
internal fun CardSection(
    addPaymentMethodViewModel: AddPaymentMethodViewModel,
    type: AvailablePaymentMethodType,
    availablePaymentConsents: List<PaymentConsent>,
    onAddCard: () -> Unit,
    onDeleteCard: (PaymentConsent) -> Unit,
    onPaymentConsentClicked: (PaymentConsent) -> Unit,
    onCheckoutWithCvc: (PaymentConsent, String) -> Unit,
) {
    val deleteCardEvent by addPaymentMethodViewModel.deleteCardSuccess.collectAsState()
    var localConsents by remember { mutableStateOf(availablePaymentConsents) }
    var selectedScreen by remember { mutableStateOf(if (localConsents.isEmpty()) CardSectionType.AddCard else CardSectionType.ConsentList) }

    LaunchedEffect(localConsents, deleteCardEvent) {
        localConsents = localConsents.filterNot { it.id == deleteCardEvent?.id }
        selectedScreen = if (localConsents.isEmpty()) {
            CardSectionType.AddCard
        } else {
            CardSectionType.ConsentList
        }
    }

    Column {
        when (selectedScreen) {
            is CardSectionType.AddCard -> {
                if (localConsents.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 24.dp),
                    ) {
                        StandardText(
                            text = selectedScreen.screenTitleRes?.let { stringResource(id = it) }.orEmpty(),
                            typography = AirwallexTypography.Body200Bold,
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        StandardText(
                            text = stringResource(id = selectedScreen.buttonTitleRes),
                            typography = AirwallexTypography.Body200Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable(
                                onClick = { selectedScreen = CardSectionType.ConsentList },
                            ),
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                AddCardSection(
                    viewModel = addPaymentMethodViewModel,
                    type = type,
                    onConfirm = onAddCard,
                )
            }
            is CardSectionType.ConsentList -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 24.dp),
                ) {
                    StandardText(
                        text = selectedScreen.screenTitleRes?.let { stringResource(id = it) }.orEmpty(),
                        typography = AirwallexTypography.Body200Bold,
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    StandardText(
                        text = stringResource(id = selectedScreen.buttonTitleRes),
                        typography = AirwallexTypography.Body200Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable(
                            onClick = { selectedScreen = CardSectionType.AddCard },
                        ),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                ConsentListSection(
                    availablePaymentConsents = localConsents,
                    onSelectCard = { consent ->
                        if (addPaymentMethodViewModel.isCvcRequired(paymentConsent = consent)) {
                            selectedScreen = CardSectionType.ConsentDetail(consent = consent)
                        } else {
                            onPaymentConsentClicked(consent)
                        }
                    },
                    onDeleteCard = onDeleteCard,
                )
            }
            is CardSectionType.ConsentDetail -> {
                val consent = (selectedScreen as? CardSectionType.ConsentDetail)?.consent ?: return@Column
                val card = consent.paymentMethod?.card ?: return@Column
                val cardBrand = card.brand?.let { CardBrand.fromName(it) } ?: return@Column

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 24.dp),
                ) {
                    CardBrandIcon(brand = cardBrand)

                    Spacer(modifier = Modifier.width(12.dp))

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

                    StandardText(
                        text = stringResource(id = selectedScreen.buttonTitleRes),
                        typography = AirwallexTypography.Body200Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable(
                            onClick = { selectedScreen = CardSectionType.ConsentList },
                        ),
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                ConsentDetailSection(
                    viewModel = addPaymentMethodViewModel,
                    cardBrand = cardBrand,
                    onCheckout = { cvc ->
                        onCheckoutWithCvc(consent, cvc)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                )
            }
        }
    }
}