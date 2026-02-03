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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
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
internal fun CardSection(
    session: AirwallexSession,
    airwallex: Airwallex,
    cardSchemes: List<CardScheme>,
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
    val shouldFetch = needFetchConsentsAndSchemes && cardSchemes.isEmpty()
    LaunchedEffect(shouldFetch) {
        if (shouldFetch) {
            onOperationStart(PaymentOperation.FetchPaymentMethods)
            operationsViewModel.fetchAvailablePaymentMethodsAndConsents()
                .onSuccess { (methods, consents) ->
                    onOperationDone(
                        PaymentOperationResult.FetchPaymentMethods(
                            availablePaymentMethods = methods,
                            hasPaymentConsents = consents.isNotEmpty()
                        )
                    )
                }
                .onFailure { exception ->
                    onOperationDone(
                        PaymentOperationResult.Error(
                            exception.message ?: exception.toString(),
                            exception
                        )
                    )
                }
        }
    }

    val availablePaymentConsents by operationsViewModel.availablePaymentConsents.collectAsState()
    val availablePaymentMethods by operationsViewModel.availablePaymentMethods.collectAsState()
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

    LaunchedEffect(shouldFetch, availablePaymentMethods) {
        if (shouldFetch && availablePaymentMethods.isNotEmpty()) {
            val newSchemes = airwallex.getSupportedCardSchemes(availablePaymentMethods)
            addPaymentMethodViewModel.updateSupportedCardSchemes(newSchemes)
        }
    }

    // Observe payment results from operations ViewModel
    val paymentResult by operationsViewModel.paymentResult.collectAsState()

    LaunchedEffect(paymentResult) {
        paymentResult?.let { event ->
            val result = when (event.operationType) {
                PaymentOperationsViewModel.PaymentOperationType.CHECKOUT_WITH_CVC ->
                    PaymentOperationResult.CheckoutWithCvc(event.status)
                PaymentOperationsViewModel.PaymentOperationType.CHECKOUT_WITHOUT_CVC ->
                    PaymentOperationResult.CheckoutWithoutCvc(event.status)
                PaymentOperationsViewModel.PaymentOperationType.CHECKOUT_WITH_GOOGLE_PAY ->
                    PaymentOperationResult.CheckoutWithGooglePay(event.status)
            }
            onOperationDone(result)
            operationsViewModel.clearPaymentResult()
        }
    }

    val coroutineScope = rememberCoroutineScope()
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
                        modifier = Modifier.fillMaxWidth(),
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
                    modifier = Modifier.fillMaxWidth(),
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
                                // Find the consent from the list using the ID
                                val consent = localConsents.find { c -> c.id == it.consentId }
                                if (consent != null) {
                                    onDeleteOperationStart(
                                        operation = it,
                                        consent = consent,
                                        operationsViewModel = operationsViewModel,
                                        addPaymentMethodViewModel = addPaymentMethodViewModel,
                                        coroutineScope = coroutineScope,
                                        onOperationStart = onOperationStart,
                                        onOperationDone = onOperationDone
                                    )
                                }
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
                        onCheckoutWithCvcOperationStart(
                            operation = PaymentOperation.CheckoutWithCvc(
                                consentId = requireNotNull(consent.id),
                                paymentMethodType = consent.paymentMethod?.type
                            ),
                            consent = consent,
                            cvc = cvc,
                            operationsViewModel = operationsViewModel,
                            onOperationStart = onOperationStart,
                        )
                    },
                    onCheckoutWithoutCvc = {
                        onCheckoutWithoutCvcOperationStart(
                            operation = PaymentOperation.CheckoutWithoutCvc(
                                consentId = requireNotNull(consent.id),
                                paymentMethodType = consent.paymentMethod?.type
                            ),
                            consent = consent,
                            operationsViewModel = operationsViewModel,
                            onOperationStart = onOperationStart,
                        )
                    },
                    onScreenViewed = {
                        addPaymentMethodViewModel.trackScreenViewed(
                            PaymentMethodType.CARD.value,
                            mapOf("subtype" to "consent")
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

private fun onDeleteOperationStart(
    operation: PaymentOperation.DeleteCard,
    consent: PaymentConsent,
    operationsViewModel: PaymentOperationsViewModel,
    addPaymentMethodViewModel: AddPaymentMethodViewModel,
    coroutineScope: CoroutineScope,
    onOperationStart: (PaymentOperation) -> Unit,
    onOperationDone: (PaymentOperationResult) -> Unit,
) {
    onOperationStart(operation)

    coroutineScope.launch {
        operationsViewModel.deletePaymentConsent(consent)
            .onSuccess {
                onOperationDone(PaymentOperationResult.DeleteCard(operation.consentId))
                addPaymentMethodViewModel.deleteCardSuccess(consent)
            }
            .onFailure { exception ->
                onOperationDone(
                    PaymentOperationResult.Error(
                        exception.message ?: exception.toString(),
                        exception
                    )
                )
            }
    }
}

private fun onCheckoutWithoutCvcOperationStart(
    operation: PaymentOperation.CheckoutWithoutCvc,
    consent: PaymentConsent,
    operationsViewModel: PaymentOperationsViewModel,
    onOperationStart: (PaymentOperation.CheckoutWithoutCvc) -> Unit,
) {
    onOperationStart(operation)
    operationsViewModel.confirmPaymentIntent(consent)
}

private fun onCheckoutWithCvcOperationStart(
    operation: PaymentOperation.CheckoutWithCvc,
    consent: PaymentConsent,
    cvc: String,
    operationsViewModel: PaymentOperationsViewModel,
    onOperationStart: (PaymentOperation.CheckoutWithCvc) -> Unit,
) {
    onOperationStart(operation)
    operationsViewModel.checkoutWithCvc(consent, cvc)
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
    cardSchemes: List<CardScheme>,
    isSinglePaymentMethod: Boolean = false,
    listener: CardSectionListener,
) {
    CardSection(
        session = session,
        airwallex = airwallex,
        cardSchemes = cardSchemes,
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