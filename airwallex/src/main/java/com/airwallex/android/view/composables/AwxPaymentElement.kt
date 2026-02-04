package com.airwallex.android.view.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.PaymentMethodsLayoutType
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.view.PaymentOperationsViewModel
import com.airwallex.android.view.composables.card.CardSection
import com.airwallex.android.view.util.getSinglePaymentMethodOrNull

/**
 * Airwallex payment element composable that displays payment UI based on configuration.
 *
 * @param session The Airwallex session containing payment information
 * @param airwallex The Airwallex instance for payment operations
 * @param configuration Configuration for the payment element (Card or PaymentSheet)
 * @param operationListener Listener for payment operation callbacks
 */
@Suppress("LongMethod", "LongParameterList")
@Composable
fun AwxPaymentElement(
    session: AirwallexSession,
    airwallex: Airwallex,
    configuration: AwxPaymentElementConfiguration,
    operationListener: com.airwallex.android.view.PaymentOperationListener,
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

    // Observe payment results from operations ViewModel
    val paymentResult by operationsViewModel.paymentResult.collectAsState()

    LaunchedEffect(paymentResult) {
        paymentResult?.let { event ->
            operationListener.onLoadingStateChanged(false)
            operationListener.onPaymentResult(event.status)
            operationsViewModel.clearPaymentResult()
        }
    }

    // Determine if we need to fetch based on configuration
    val shouldFetch = when (configuration) {
        is AwxPaymentElementConfiguration.Card -> configuration.cardSchemes.isEmpty()
        is AwxPaymentElementConfiguration.PaymentSheet -> true
    }

    // Fetch data on first composition (if needed)
    LaunchedEffect(shouldFetch) {
        if (shouldFetch) {
            operationListener.onLoadingStateChanged(true)
            operationsViewModel.fetchAvailablePaymentMethodsAndConsents()
                .onSuccess { (_, _) ->
                    operationListener.onLoadingStateChanged(false)
                }
                .onFailure { exception ->
                    operationListener.onLoadingStateChanged(false)
                    operationListener.onError(exception)
                }
        }
    }

    // Filter out Google Pay from available types
    val availableTypes = remember(availablePaymentMethods) {
        availablePaymentMethods.filterNot { paymentMethodType ->
            paymentMethodType.name == PaymentMethodType.GOOGLEPAY.value
        }
    }

    Column {
        when (configuration) {
            is AwxPaymentElementConfiguration.Card -> {
                val cardSchemes =
                    availableTypes.firstOrNull { it.name == PaymentMethodType.CARD.value }?.cardSchemes.orEmpty()
                        .ifEmpty {
                            configuration.cardSchemes
                        }

                val isSinglePaymentMethod =
                    availablePaymentMethods.getSinglePaymentMethodOrNull(availablePaymentConsents) != null

                CardSection(
                    session = session,
                    airwallex = airwallex,
                    cardSchemes = cardSchemes,
                    isSinglePaymentMethod = isSinglePaymentMethod,
                    needFetchConsentsAndSchemes = configuration.cardSchemes.isEmpty(),
                    operationListener = operationListener,
                )
            }

            is AwxPaymentElementConfiguration.PaymentSheet -> {
                if (availableTypes.isNotEmpty()) {
                    when (configuration.type) {
                        PaymentMethodsLayoutType.TAB -> {
                            PaymentMethodsTabSection(
                                session = session,
                                airwallex = airwallex,
                                operationListener = operationListener,
                            )
                        }

                        PaymentMethodsLayoutType.ACCORDION -> {
                            PaymentMethodsAccordionSection(
                                session = session,
                                airwallex = airwallex,
                                operationListener = operationListener,
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

/**
 * Airwallex payment element with lambda callbacks.
 *
 * Uses [rememberUpdatedState] to ensure callbacks always reference the current composition,
 * avoiding stale references after configuration changes.
 *
 * @param session The Airwallex session containing payment information
 * @param airwallex The Airwallex instance for payment operations
 * @param configuration Configuration for the payment element (Card or PaymentSheet)
 * @param onLoadingStateChanged Callback invoked when loading state changes
 * @param onPaymentResult Callback invoked when a payment operation completes
 * @param onError Callback invoked when an error occurs
 */
@Suppress("LongParameterList")
@Composable
fun AwxPaymentElement(
    session: AirwallexSession,
    airwallex: Airwallex,
    configuration: AwxPaymentElementConfiguration,
    onLoadingStateChanged: (Boolean) -> Unit = {},
    onPaymentResult: (AirwallexPaymentStatus) -> Unit = {},
    onError: (Throwable) -> Unit = {},
) {
    // Use rememberUpdatedState to ensure callbacks always reference current values
    // This prevents stale references after configuration changes
    val currentOnLoadingStateChanged by rememberUpdatedState(onLoadingStateChanged)
    val currentOnPaymentResult by rememberUpdatedState(onPaymentResult)
    val currentOnError by rememberUpdatedState(onError)

    // Create a stable listener that delegates to current callbacks
    val listener = remember {
        object : com.airwallex.android.view.PaymentOperationListener {
            override fun onLoadingStateChanged(isLoading: Boolean) {
                currentOnLoadingStateChanged(isLoading)
            }

            override fun onPaymentResult(status: AirwallexPaymentStatus) {
                currentOnPaymentResult(status)
            }

            override fun onError(exception: Throwable) {
                currentOnError(exception)
            }
        }
    }

    AwxPaymentElement(
        session = session,
        airwallex = airwallex,
        configuration = configuration,
        operationListener = listener
    )
}
