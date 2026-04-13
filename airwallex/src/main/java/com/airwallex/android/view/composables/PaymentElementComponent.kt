package com.airwallex.android.view.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexRecurringSession
import com.airwallex.android.core.AirwallexRecurringWithIntentSession
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.PaymentMethodsLayoutType
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.view.PaymentFlowListener
import com.airwallex.android.view.PaymentFlowViewModel
import com.airwallex.android.view.composables.card.CardSection
import com.airwallex.android.view.util.getSinglePaymentMethodOrNull
import com.google.pay.button.ButtonType

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

    Column(Modifier.background(AirwallexColor.backgroundPrimary)) {
        when (configuration) {
            is PaymentElementConfiguration.Card -> {
                val availablePaymentMethods by flowViewModel.availablePaymentMethods.collectAsState()
                val availablePaymentConsents by flowViewModel.availablePaymentConsents.collectAsState()
                val cardSchemes =
                    availablePaymentMethods.firstOrNull { it.name == PaymentMethodType.CARD.value }?.cardSchemes.orEmpty()
                        .ifEmpty {
                            // Convert AirwallexSupportedCard to CardScheme
                            configuration.supportedCardBrands.map {
                                com.airwallex.android.core.model.CardScheme(it.brandName)
                            }
                        }

                val isSinglePaymentMethod =
                    availablePaymentMethods.getSinglePaymentMethodOrNull(availablePaymentConsents) != null

                CardSection(
                    session = session,
                    airwallex = airwallex,
                    cardSchemes = cardSchemes,
                    isSinglePaymentMethod = isSinglePaymentMethod,
                    paymentFlowListener = paymentFlowListener,
                    checkoutButtonTitle = configuration.checkoutButton.title,
                )
            }

            is PaymentElementConfiguration.PaymentSheet -> {
                // Resolve Google Pay button type once based on session type
                val googlePayButtonType = configuration.googlePayButton.buttonType ?: run {
                    val isRecurring = session is AirwallexRecurringSession ||
                        session is AirwallexRecurringWithIntentSession
                    if (isRecurring) ButtonType.Subscribe else ButtonType.Buy
                }

                when (configuration.layout) {
                    PaymentMethodsLayoutType.TAB -> {
                        PaymentMethodsTabSection(
                            session = session,
                            airwallex = airwallex,
                            paymentFlowListener = paymentFlowListener,
                            showsGooglePayAsPrimaryButton = configuration.googlePayButton.showsAsPrimaryButton,
                            googlePayButtonType = googlePayButtonType,
                            checkoutButtonTitle = configuration.checkoutButton.title,
                        )
                    }

                    PaymentMethodsLayoutType.ACCORDION -> {
                        PaymentMethodsAccordionSection(
                            session = session,
                            airwallex = airwallex,
                            paymentFlowListener = paymentFlowListener,
                            showsGooglePayAsPrimaryButton = configuration.googlePayButton.showsAsPrimaryButton,
                            googlePayButtonType = googlePayButtonType,
                            checkoutButtonTitle = configuration.checkoutButton.title,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}