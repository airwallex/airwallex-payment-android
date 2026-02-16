package com.airwallex.android.view.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.PaymentMethodsLayoutType
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.view.PaymentFlowListener
import com.airwallex.android.view.PaymentFlowViewModel
import com.airwallex.android.view.composables.card.CardSection
import com.airwallex.android.view.util.getSinglePaymentMethodOrNull

/**
 * Airwallex payment element composable that displays payment UI based on configuration.
 *
 * @param session The Airwallex session containing payment information
 * @param airwallex The Airwallex instance for payment operations
 * @param configuration Configuration for the payment element (Card or PaymentSheet)
 * @param paymentFlowListener Listener for payment operation callbacks
 */
@Suppress("LongMethod", "LongParameterList")
@Composable
internal fun PaymentElementComponent(
    session: AirwallexSession,
    airwallex: Airwallex,
    configuration: PaymentElementConfiguration,
    paymentFlowListener: PaymentFlowListener,
    flowViewModel: PaymentFlowViewModel
) {
    val availablePaymentMethods by flowViewModel.availablePaymentMethods.collectAsState()
    val availablePaymentConsents by flowViewModel.availablePaymentConsents.collectAsState()

    LaunchedEffect(Unit) {
        flowViewModel.paymentResult.collect { event ->
            paymentFlowListener.onLoadingStateChanged(false)
            paymentFlowListener.onPaymentResult(event.status)
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
                            configuration.supportedCardBrands
                        }

                val isSinglePaymentMethod =
                    availablePaymentMethods.getSinglePaymentMethodOrNull(availablePaymentConsents) != null

                CardSection(
                    session = session,
                    airwallex = airwallex,
                    cardSchemes = cardSchemes,
                    isSinglePaymentMethod = isSinglePaymentMethod,
                    paymentFlowListener = paymentFlowListener,
                )
            }

            is PaymentElementConfiguration.PaymentSheet -> {
                if (availableTypes.isNotEmpty()) {
                    when (configuration.layout) {
                        PaymentMethodsLayoutType.TAB -> {
                            PaymentMethodsTabSection(
                                session = session,
                                airwallex = airwallex,
                                paymentFlowListener = paymentFlowListener,
                            )
                        }

                        PaymentMethodsLayoutType.ACCORDION -> {
                            PaymentMethodsAccordionSection(
                                session = session,
                                airwallex = airwallex,
                                paymentFlowListener = paymentFlowListener,
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}