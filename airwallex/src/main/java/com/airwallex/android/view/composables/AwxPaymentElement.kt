package com.airwallex.android.view.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.view.PaymentOperationsViewModel
import com.airwallex.android.view.composables.card.CardSection
import com.airwallex.android.view.composables.card.PaymentOperation
import com.airwallex.android.view.composables.card.PaymentOperationResult
import com.airwallex.android.view.composables.google.GooglePaySection
import com.airwallex.android.view.util.GooglePayUtil
import com.airwallex.android.view.util.getSinglePaymentMethodOrNull

@Suppress("LongMethod", "LongParameterList")
@Composable
fun AwxPaymentElement(
    session: AirwallexSession,
    airwallex: Airwallex,
    configuration: AwxPaymentElementConfiguration,
    onLoading: (Boolean) -> Unit,
    onOperationStart: (PaymentOperation) -> Unit,
    onOperationDone: (PaymentOperationResult) -> Unit,
) {
    // Initialize PaymentOperationsViewModel (shared across all child sections)
    val operationsViewModel: PaymentOperationsViewModel = viewModel(
        factory = PaymentOperationsViewModel.Factory(
            airwallex = airwallex,
            session = session
        ),
        viewModelStoreOwner = airwallex.activity
    )

    val availablePaymentMethods by operationsViewModel.availablePaymentMethods.collectAsState()
    val availablePaymentConsents by operationsViewModel.availablePaymentConsents.collectAsState()
    val isLoading by operationsViewModel.isLoading.collectAsState()

    // Determine if we need to fetch based on configuration
    val shouldFetch = when (configuration) {
        is AwxPaymentElementConfiguration.Card -> configuration.cardSchemes.isEmpty()
        is AwxPaymentElementConfiguration.PaymentSheet -> true
    }

    // Fetch data on first composition (if needed)
    LaunchedEffect(shouldFetch) {
        if (shouldFetch) {
            onOperationStart(PaymentOperation.FetchPaymentMethods)
            val result = operationsViewModel.fetchAvailablePaymentMethodsAndConsents()
            onOperationDone(PaymentOperationResult.FetchPaymentMethods(result))
        }
    }

    val allowedPaymentMethods = remember(availablePaymentMethods) {
        session.googlePayOptions?.let { googlePayOptions ->
            availablePaymentMethods.firstOrNull {
                it.name == PaymentMethodType.GOOGLEPAY.value
            }?.let { paymentMethodType ->
                GooglePayUtil.retrieveAllowedPaymentMethods(
                    googlePayOptions,
                    paymentMethodType.cardSchemes,
                )
            }
        }
    }

    // Filter out Google Pay from available types
    val availableTypes = remember(availablePaymentMethods) {
        availablePaymentMethods.filterNot { paymentMethodType ->
            paymentMethodType.name == PaymentMethodType.GOOGLEPAY.value
        }
    }

    LaunchedEffect(isLoading) {
        onLoading(isLoading)
    }

    Column {
        when (configuration) {
            is AwxPaymentElementConfiguration.Card -> {
                val cardSchemes = availableTypes.firstOrNull { it.name == PaymentMethodType.CARD.value }?.cardSchemes.orEmpty().ifEmpty {
                    configuration.cardSchemes
                }

                val isSinglePaymentMethod = availablePaymentMethods.getSinglePaymentMethodOrNull(availablePaymentConsents) != null

                CardSection(
                    session = session,
                    airwallex = airwallex,
                    cardSchemes = cardSchemes,
                    isSinglePaymentMethod = isSinglePaymentMethod,
                    needFetchConsentsAndSchemes = configuration.cardSchemes.isEmpty(),
                    onOperationStart = onOperationStart,
                    onOperationDone = onOperationDone,
                )
            }

            is AwxPaymentElementConfiguration.PaymentSheet -> {
                // Google Pay Section (if eligible)
                allowedPaymentMethods?.let { allowedPaymentMethods ->
                    Spacer(modifier = Modifier.height(24.dp))

                    GooglePaySection(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth(),
                        allowedPaymentMethods = allowedPaymentMethods.toString().trimIndent(),
                        onClick = {
                            AnalyticsLogger.logAction("tap_pay_button", mapOf("payment_method" to PaymentMethodType.GOOGLEPAY.value))
                            onOperationStart(PaymentOperation.CheckoutWithGooglePay)
                            operationsViewModel.checkoutWithGooglePay { status ->
                                onOperationDone(PaymentOperationResult.CheckoutWithGooglePay(status))
                            }
                        },
                        onScreenViewed = {
                            operationsViewModel.trackScreenViewed(PaymentMethodType.GOOGLEPAY.value)
                        },
                    )
                }

                if (availableTypes.isNotEmpty()) {
                    val isSinglePaymentMethod = availablePaymentMethods.getSinglePaymentMethodOrNull(availablePaymentConsents) != null
                    if (!isSinglePaymentMethod) Spacer(modifier = Modifier.height(24.dp))

                    when (configuration.type) {
                        com.airwallex.android.core.PaymentMethodsLayoutType.TAB -> {
                            PaymentMethodsTabSection(
                                session = session,
                                airwallex = airwallex,
                                onLoading = onLoading,
                                onOperationStart = onOperationStart,
                                onOperationDone = onOperationDone,
                            )
                        }
                        com.airwallex.android.core.PaymentMethodsLayoutType.ACCORDION -> {
                            PaymentMethodsAccordionSection(
                                session = session,
                                airwallex = airwallex,
                                availablePaymentMethodTypes = availableTypes,
                                availablePaymentConsents = availablePaymentConsents,
                                onLoading = onLoading,
                                onOperationStart = onOperationStart,
                                onOperationDone = onOperationDone,
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}
