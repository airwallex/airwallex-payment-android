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
import com.airwallex.android.view.PaymentOperationListener
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
internal fun PaymentElement(
    session: AirwallexSession,
    airwallex: Airwallex,
    configuration: PaymentElementConfiguration,
    operationListener: PaymentOperationListener,
    operationsViewModel: PaymentOperationsViewModel
) {
    val availablePaymentMethods by operationsViewModel.availablePaymentMethods.collectAsState()
    val availablePaymentConsents by operationsViewModel.availablePaymentConsents.collectAsState()
    val paymentResult by operationsViewModel.paymentResult.collectAsState()

    LaunchedEffect(paymentResult) {
        paymentResult?.let { event ->
            operationListener.onLoadingStateChanged(false)
            operationListener.onPaymentResult(event.status)
            operationsViewModel.clearPaymentResult()
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
            is PaymentElementConfiguration.Card -> {
                val cardSchemes =
                    availableTypes.firstOrNull { it.name == PaymentMethodType.CARD.value }?.cardSchemes.orEmpty()
                        .ifEmpty {
                            configuration.cardSchemes
                        }

                val isSinglePaymentMethod =
                    availablePaymentMethods.getSinglePaymentMethodOrNull(availablePaymentConsents) != null

//                CardSection(
//                    session = session,
//                    airwallex = airwallex,
//                    cardSchemes = cardSchemes,
//                    isSinglePaymentMethod = isSinglePaymentMethod,
//                    operationListener = operationListener,
//                )
            }

            is PaymentElementConfiguration.PaymentSheet -> {
                if (availableTypes.isNotEmpty()) {
                    when (configuration.type) {
                        PaymentMethodsLayoutType.TAB -> {
//                            PaymentMethodsTabSection(
//                                session = session,
//                                airwallex = airwallex,
//                                operationListener = operationListener,
//                            )
                        }

                        PaymentMethodsLayoutType.ACCORDION -> {
//                            PaymentMethodsAccordionSection(
//                                session = session,
//                                airwallex = airwallex,
//                                operationListener = operationListener,
//                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}