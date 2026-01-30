package com.airwallex.android.view.composables.card

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airwallex.android.R
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.CardBrand
import com.airwallex.android.core.model.CardScheme
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.ui.composables.AirwallexTypography
import com.airwallex.android.ui.composables.StandardText
import com.airwallex.android.view.AddPaymentMethodViewModel
import com.airwallex.android.view.PaymentOperationsViewModel
import com.airwallex.android.view.composables.common.CardBrandIcon
import com.airwallex.android.view.composables.consent.ConsentDetailSection
import com.airwallex.android.view.composables.consent.ConsentListSection
import java.util.Locale

@Suppress("ComplexMethod", "LongMethod", "LongParameterList")
@Composable
fun CardSection(
    session: AirwallexSession,
    airwallex: Airwallex,
    addPaymentMethodViewModel: AddPaymentMethodViewModel,
    cardSchemes: List<CardScheme>,
    onCheckoutWithoutCvc: (PaymentConsent) -> Unit,
    onCheckoutWithCvc: (PaymentConsent, String) -> Unit,
    isSinglePaymentMethod: Boolean = false,
    onOperationStart: (PaymentOperation) -> Unit,
    onOperationDone: (PaymentOperationResult) -> Unit,
    needFetchConsentsAndSchemes: Boolean = true
) {

    val operationsViewModel: PaymentOperationsViewModel = viewModel(
        factory = PaymentOperationsViewModel.Factory(
            airwallex = airwallex,
            session = session
        ),
        viewModelStoreOwner = airwallex.activity
    )

    LaunchedEffect(needFetchConsentsAndSchemes, cardSchemes) {
        val shouldFetch = needFetchConsentsAndSchemes || cardSchemes.isEmpty()
        if (shouldFetch) {
            operationsViewModel.fetchAvailablePaymentMethodsAndConsents()
        }
    }

    val availablePaymentConsents by operationsViewModel.availablePaymentConsents.collectAsState()
    val availablePaymentMethods by operationsViewModel.availablePaymentMethods.collectAsState()
    val deletedConsents by addPaymentMethodViewModel.deletedCardList.collectAsState()

    var localConsents by remember { mutableStateOf(availablePaymentConsents) }
    var selectedScreen by remember { mutableStateOf(if (localConsents.isEmpty()) CardSectionType.AddCard else CardSectionType.ConsentList) }

    LaunchedEffect(availablePaymentConsents, deletedConsents, needFetchConsentsAndSchemes) {
        val deletedIds = deletedConsents.map { it.id }
        localConsents = if (needFetchConsentsAndSchemes) {
            availablePaymentConsents.filterNot { it.id in deletedIds }
        } else {
            emptyList()
        }
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
                        modifier = Modifier.padding(horizontal = 24.dp),
                    ) {
                        StandardText(
                            text = selectedScreen.screenTitleRes?.let { stringResource(id = it) }
                                .orEmpty(),
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
                    cardSchemes = if (needFetchConsentsAndSchemes || cardSchemes.isEmpty()) {
                        airwallex.getSupportedCardSchemes(availablePaymentMethods)
                    } else {
                        cardSchemes
                    },
                    onOperationStart = onOperationStart,
                    onOperationDone = onOperationDone,
                )
            }

            is CardSectionType.ConsentList -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 24.dp),
                ) {
                    StandardText(
                        text = selectedScreen.screenTitleRes?.let { stringResource(id = it) }
                            .orEmpty(),
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
                        selectedScreen = CardSectionType.ConsentDetail(consent = consent)
                    },
                    onOperationStart = {
                        when (it) {
                            is PaymentOperation.DeleteCard -> {
                                onDeleteOperationStart(
                                    it,
                                    operationsViewModel,
                                    addPaymentMethodViewModel,
                                    onOperationStart,
                                    onOperationDone
                                )
                            }

                            else -> {}
                        }

                    },
                    onScreenViewed = {
                        addPaymentMethodViewModel.trackScreenViewed(
                            PaymentMethodType.CARD.value,
                            mapOf("subtype" to "consent")
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
                    isCvcRequired = addPaymentMethodViewModel.isCvcRequired(consent),
                    cardBrand = cardBrand,
                    onCheckoutWithCvc = { cvc ->
                        onCheckoutWithCvc(consent, cvc)
                    },
                    onCheckoutWithoutCvv = {
                        onCheckoutWithoutCvc(consent)
                    },
                    onScreenViewed = {
                        addPaymentMethodViewModel.trackScreenViewed(
                            PaymentMethodType.CARD.value,
                            mapOf("subtype" to "consent")
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                )
            }
        }
    }
}

private fun onDeleteOperationStart(
    operation: PaymentOperation.DeleteCard,
    operationsViewModel: PaymentOperationsViewModel,
    addPaymentMethodViewModel: AddPaymentMethodViewModel,
    onOperationStart: (PaymentOperation) -> Unit,
    onOperationDone: (PaymentOperationResult) -> Unit,
) {
    val consent = operation.deletedConsent ?: return
    onOperationStart(operation)
    operationsViewModel.deletePaymentConsent(consent) { result ->
        result.fold(
            onSuccess = { consent ->
                onOperationDone(PaymentOperationResult.DeleteCard(Result.success(consent)))
                addPaymentMethodViewModel.deleteCardSuccess(consent)
            },
            onFailure = { exception ->
                onOperationDone(PaymentOperationResult.DeleteCard(Result.failure(exception)))
//                                    alert(message = it.message ?: it.toString())
            },
        )
    }
}

/**
 * Java-friendly overload of CardSection that uses a listener interface instead of lambda callbacks.
 *
 * This overload is designed for Java compatibility. Kotlin code should prefer the lambda-based version.
 *
 * @param listener Listener for card operation events
 * @see CardSectionListener
 */
@Suppress("ComplexMethod", "LongMethod", "LongParameterList")
@Composable
fun CardSection(
    session: AirwallexSession,
    airwallex: Airwallex,
    addPaymentMethodViewModel: AddPaymentMethodViewModel,
    cardSchemes: List<CardScheme>,
    onCheckoutWithoutCvc: (PaymentConsent) -> Unit,
    onCheckoutWithCvc: (PaymentConsent, String) -> Unit,
    isSinglePaymentMethod: Boolean = false,
    listener: CardSectionListener,
) {
    CardSection(
        session = session,
        airwallex = airwallex,
        addPaymentMethodViewModel = addPaymentMethodViewModel,
        cardSchemes = cardSchemes,
        onCheckoutWithoutCvc = onCheckoutWithoutCvc,
        onCheckoutWithCvc = onCheckoutWithCvc,
        isSinglePaymentMethod = isSinglePaymentMethod,
        onOperationStart = listener::onOperationStart,
        onOperationDone = listener::onOperationDone
    )
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