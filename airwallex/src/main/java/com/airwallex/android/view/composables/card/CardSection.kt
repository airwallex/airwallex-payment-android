package com.airwallex.android.view.composables.card

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airwallex.android.R
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.CardBrand
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.log.AnalyticsLogger.Field
import com.airwallex.android.core.model.CardScheme
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.ui.composables.AirwallexTypography
import com.airwallex.android.ui.composables.StandardText
import com.airwallex.android.view.AddPaymentMethodViewModel
import com.airwallex.android.view.PaymentFlowListener
import com.airwallex.android.view.PaymentFlowViewModel
import com.airwallex.android.view.composables.common.CardBrandIcon
import com.airwallex.android.view.composables.consent.ConsentDetailSection
import com.airwallex.android.view.composables.consent.ConsentListSection
import com.airwallex.android.view.util.AnalyticsConstants.PAYMENT_METHOD
import com.airwallex.android.view.util.AnalyticsConstants.TAP_PAY_BUTTON
import java.util.Locale

@Suppress("ComplexMethod", "LongMethod", "LongParameterList")
@Composable
internal fun CardSection(
    session: AirwallexSession,
    airwallex: Airwallex,
    cardSchemes: List<CardScheme>,
    isSinglePaymentMethod: Boolean = false,
    paymentFlowListener: PaymentFlowListener,
) {
    val paymentFlowViewModel: PaymentFlowViewModel = viewModel(
        factory = PaymentFlowViewModel.Factory(
            airwallex = airwallex,
            session = session
        ),
        viewModelStoreOwner = airwallex.activity
    )

    val availablePaymentConsents by paymentFlowViewModel.availablePaymentConsents.collectAsState()
    val addPaymentMethodViewModel: AddPaymentMethodViewModel = viewModel(
        factory = AddPaymentMethodViewModel.Factory(
            airwallex = airwallex,
            session = session,
            supportedCardSchemes = cardSchemes,
            application = airwallex.activity.application
        ),
        viewModelStoreOwner = airwallex.activity
    )
    val deletedConsents by addPaymentMethodViewModel.deletedCardList.collectAsState()

    addPaymentMethodViewModel.updateSupportedCardSchemes(cardSchemes)

    // Observe delete consent results
    LaunchedEffect(Unit) {
        paymentFlowViewModel.deleteConsentResult.collect { result ->
            paymentFlowListener.onLoadingStateChanged(false, airwallex.activity)
            when (result) {
                is PaymentFlowViewModel.DeleteConsentResult.Success -> {
                    addPaymentMethodViewModel.deleteCardSuccess(result.consent)
                }

                is PaymentFlowViewModel.DeleteConsentResult.Failure -> {
                    paymentFlowListener.onError(result.exception, airwallex.activity)
                }
            }
        }
    }

    var localConsents by remember { mutableStateOf(availablePaymentConsents) }
    var selectedScreen by remember { mutableStateOf(if (localConsents.isEmpty()) CardSectionType.AddCard else CardSectionType.ConsentList) }

    LaunchedEffect(availablePaymentConsents, deletedConsents) {
        val deletedIds = deletedConsents.map { it.id }
        localConsents = availablePaymentConsents.filterNot { it.id in deletedIds }
        selectedScreen = if (localConsents.isEmpty() || isSinglePaymentMethod) {
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
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        StandardText(
                            text = selectedScreen.screenTitleRes?.let { stringResource(id = it) }
                                .orEmpty(),
                            typography = AirwallexTypography.Body200Bold,
                            color = AirwallexColor.textPrimary
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        StandardText(
                            text = stringResource(id = selectedScreen.buttonTitleRes),
                            typography = AirwallexTypography.Body200Bold,
                            color = AirwallexColor.theme,
                            modifier = Modifier.clickable(
                                onClick = { selectedScreen = CardSectionType.ConsentList },
                            ),
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                AddCardSection(
                    viewModel = addPaymentMethodViewModel,
                    paymentFlowViewModel = paymentFlowViewModel,
                    cardSchemes = cardSchemes,
                    paymentFlowListener = paymentFlowListener,
                    activity = airwallex.activity,
                )
            }

            is CardSectionType.ConsentList -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    StandardText(
                        text = selectedScreen.screenTitleRes?.let { stringResource(id = it) }
                            .orEmpty(),
                        typography = AirwallexTypography.Body200Bold,
                        color = AirwallexColor.textPrimary
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    StandardText(
                        text = stringResource(id = selectedScreen.buttonTitleRes),
                        typography = AirwallexTypography.Body200Bold,
                        color = AirwallexColor.theme,
                        modifier = Modifier.clickable(
                            onClick = { selectedScreen = CardSectionType.AddCard },
                        ),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                ConsentListSection(
                    availablePaymentConsents = localConsents,
                    onSelectCard = { consent ->
                        selectedScreen = CardSectionType.ConsentDetail(consent = consent)
                    },
                    onDeleteCard = { consent ->
                        paymentFlowListener.onLoadingStateChanged(true, airwallex.activity)
                        paymentFlowViewModel.deletePaymentConsent(consent)
                    },
                    onScreenViewed = {
                        addPaymentMethodViewModel.trackScreenViewed(
                            PaymentMethodType.CARD.value,
                            mapOf(Field.SUBTYPE to "consent")
                        )
                    },
                )
            }

            is CardSectionType.ConsentDetail -> {
                val consent =
                    (selectedScreen as? CardSectionType.ConsentDetail)?.consent ?: return@Column
                val card = consent.paymentMethod?.card ?: return@Column
                val cardBrand = card.brand?.let { CardBrand.fromName(it) } ?: return@Column

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
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
                        color = AirwallexColor.textPrimary
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    StandardText(
                        text = stringResource(id = selectedScreen.buttonTitleRes),
                        typography = AirwallexTypography.Body200Bold,
                        color = AirwallexColor.theme,
                        modifier = Modifier.clickable(
                            onClick = { selectedScreen = CardSectionType.ConsentList },
                        ),
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                ConsentDetailSection(
                    viewModel = addPaymentMethodViewModel,
                    isCvcRequired = addPaymentMethodViewModel.isCvcRequired(consent),
                    cardBrand = cardBrand,
                    onCheckoutWithCvc = { cvc ->
                        onCheckoutWithCvcOperationStart(
                            consent = consent,
                            cvc = cvc,
                            paymentFlowListener = paymentFlowListener,
                            airwallex = airwallex,
                            paymentFlowViewModel = paymentFlowViewModel,
                        )
                    },
                    onCheckoutWithoutCvv = {
                        onCheckoutWithoutCvcOperationStart(
                            consent = consent,
                            paymentFlowListener = paymentFlowListener,
                            airwallex = airwallex,
                            paymentFlowViewModel = paymentFlowViewModel,
                        )
                    },
                    onScreenViewed = {
                        addPaymentMethodViewModel.trackScreenViewed(
                            PaymentMethodType.CARD.value,
                            mapOf(Field.SUBTYPE to "consent")
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxWidth(),
                )
            }
        }
    }
}

private fun onCheckoutWithoutCvcOperationStart(
    consent: PaymentConsent,
    paymentFlowListener: PaymentFlowListener,
    airwallex: Airwallex,
    paymentFlowViewModel: PaymentFlowViewModel,
) {
    paymentFlowListener.onLoadingStateChanged(true, airwallex.activity)
    consent.paymentMethod?.type?.let {
        AnalyticsLogger.logAction(TAP_PAY_BUTTON, mapOf(PAYMENT_METHOD to it))
    }
    paymentFlowViewModel.confirmPaymentIntent(consent)
}

private fun onCheckoutWithCvcOperationStart(
    consent: PaymentConsent,
    cvc: String,
    paymentFlowListener: PaymentFlowListener,
    airwallex: Airwallex,
    paymentFlowViewModel: PaymentFlowViewModel,
) {
    paymentFlowListener.onLoadingStateChanged(true, airwallex.activity)
    consent.paymentMethod?.type?.let {
        AnalyticsLogger.logAction(TAP_PAY_BUTTON, mapOf(PAYMENT_METHOD to it))
    }
    paymentFlowViewModel.checkoutWithCvc(consent, cvc)
}

sealed interface CardSectionType {
    val screenTitleRes: Int?
    val buttonTitleRes: Int

    object AddCard : CardSectionType {
        @StringRes
        override val screenTitleRes = R.string.airwallex_add_card_screen_title

        @StringRes
        override val buttonTitleRes = R.string.airwallex_add_card_button_title
    }

    object ConsentList : CardSectionType {
        @StringRes
        override val screenTitleRes = R.string.airwallex_consent_list_screen_title

        @StringRes
        override val buttonTitleRes = R.string.airwallex_consent_list_button_title
    }

    data class ConsentDetail(
        val consent: PaymentConsent,
    ) : CardSectionType {
        override val screenTitleRes = null

        @StringRes
        override val buttonTitleRes = R.string.airwallex_consent_detail_button_title
    }
}