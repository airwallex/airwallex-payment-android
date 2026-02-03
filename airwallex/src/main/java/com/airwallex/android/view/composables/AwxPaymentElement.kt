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
import com.airwallex.android.core.PaymentMethodsLayoutType
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.view.PaymentOperationsViewModel
import com.airwallex.android.view.composables.card.CardSection
import com.airwallex.android.view.composables.card.PaymentOperation
import com.airwallex.android.view.composables.card.PaymentOperationResult
import com.airwallex.android.view.composables.google.GooglePaySection
import com.airwallex.android.view.util.GooglePayUtil
import com.airwallex.android.view.util.getSinglePaymentMethodOrNull

/**
 * Airwallex Payment Element - A unified composable for rendering payment UI.
 *
 * This component provides a flexible payment interface that can be configured to show either:
 * - A standalone card payment UI ([AwxPaymentElementConfiguration.Card])
 * - A full payment sheet with multiple payment methods ([AwxPaymentElementConfiguration.PaymentSheet])
 *
 * **PCI-DSS Compliance:**
 * All payment operations are designed to be PCI-DSS compliant. Sensitive data (CVC codes, full card details)
 * are never stored in operation objects that might be logged or persisted. Operations only carry
 * non-sensitive identifiers (consent IDs, payment method IDs) and non-sensitive metadata
 * (payment method types for analytics).
 *
 * The component manages payment operations internally using [PaymentOperationsViewModel] and
 * automatically handles fetching payment methods, consents, and rendering the appropriate UI
 * based on the configuration.
 *
 * ## Configuration
 *
 * ### Card Configuration
 * Use [AwxPaymentElementConfiguration.Card] for a standalone card payment interface:
 * ```kotlin
 * AwxPaymentElement(
 *     session = session,
 *     airwallex = airwallex,
 *     configuration = AwxPaymentElementConfiguration.Card(
 *         cardSchemes = listOf(CardScheme("visa"), CardScheme("mastercard"))
 *     ),
 *     onOperationStart = { operation -> /* Handle operation start */ },
 *     onOperationDone = { result -> /* Handle operation result */ }
 * )
 * ```
 *
 * **Features:**
 * - Shows card input form
 * - if card schemes provided, no additional API fetch is made
 * - Otherwise, automatically fetches card schemes if not provided and displays saved cards (if available and customer is authenticated)
 * -

 *
 * ### Payment Sheet Configuration
 * Use [AwxPaymentElementConfiguration.PaymentSheet] for a multi-payment method interface:
 * ```kotlin
 * AwxPaymentElement(
 *     session = session,
 *     airwallex = airwallex,
 *     configuration = AwxPaymentElementConfiguration.PaymentSheet(
 *         type = PaymentMethodsLayoutType.TAB // or ACCORDION
 *     ),
 *     onOperationStart = { operation -> /* Handle operation start */ },
 *     onOperationDone = { result -> /* Handle operation result */ }
 * )
 * ```
 *
 * **Features:**
 * - Shows Google Pay (if eligible)
 * - Shows multiple payment methods (cards, bank transfers, wallets, etc.)
 * - Can display as tabs or accordion
 * - Automatically fetches available payment methods
 *
 * ## Callbacks
 *
 * ### onOperationStart
 * Called when a payment operation begins. Use this to show loading indicators or track analytics.
 *
 * **Common Operations:**
 * - [PaymentOperation.FetchPaymentMethods] - Fetching available payment methods
 * - [PaymentOperation.AddCard] - User submitted card for payment
 * - [PaymentOperation.CheckoutWithCvc] - User entered CVC for saved card
 * - [PaymentOperation.CheckoutWithoutCvc] - Using saved card without CVC
 * - [PaymentOperation.CheckoutWithGooglePay] - User tapped Google Pay button
 * - [PaymentOperation.DeleteCard] - User requested to delete a saved card
 * - [PaymentOperation.DirectPay] - Direct payment with schema (no additional fields)
 * - [PaymentOperation.PayWithFields] - Payment with additional schema fields
 *
 * **Example:**
 * ```kotlin
 * onOperationStart = { operation ->
 *     when (operation) {
 *         is PaymentOperation.AddCard -> {
 *             showLoading(true)
 *             logEvent("card_payment_started")
 *         }
 *         is PaymentOperation.CheckoutWithGooglePay -> {
 *             showLoading(true)
 *             logEvent("google_pay_started")
 *         }
 *         is PaymentOperation.FetchPaymentMethods -> {
 *             showLoading(true)
 *         }
 *         else -> {}
 *     }
 * }
 * ```
 *
 * ### onOperationDone
 * Called when a payment operation completes. Handle the result to update UI or navigate.
 *
 * **Common Results:**
 * - [PaymentOperationResult.FetchPaymentMethods] - Payment methods/consents fetched successfully (data available in ViewModel state)
 * - [PaymentOperationResult.AddCard] - Contains [AirwallexPaymentStatus] after card payment
 * - [PaymentOperationResult.CheckoutWithCvc] - Contains [AirwallexPaymentStatus] after CVC checkout
 * - [PaymentOperationResult.CheckoutWithoutCvc] - Contains [AirwallexPaymentStatus] after no-CVC checkout
 * - [PaymentOperationResult.CheckoutWithGooglePay] - Contains [AirwallexPaymentStatus] after Google Pay
 * - [PaymentOperationResult.DeleteCard] - Card deleted successfully (contains deleted consent ID)
 * - [PaymentOperationResult.DirectPay] - Contains [AirwallexPaymentStatus] after direct payment
 * - [PaymentOperationResult.PayWithFields] - Contains [AirwallexPaymentStatus] after schema payment
 * - [PaymentOperationResult.Error] - Contains error message and optional exception
 *
 * **Example:**
 * ```kotlin
 * onOperationDone = { result ->
 *     when (result) {
 *         is PaymentOperationResult.AddCard -> {
 *             showLoading(false)
 *             when (result.status) {
 *                 is AirwallexPaymentStatus.Success -> {
 *                     navigateToSuccess(result.status.paymentIntentId)
 *                 }
 *                 is AirwallexPaymentStatus.Failure -> {
 *                     showError(result.status.exception.message)
 *                 }
 *                 is AirwallexPaymentStatus.Cancel -> {
 *                     // User cancelled
 *                 }
 *                 else -> {}
 *             }
 *         }
 *         is PaymentOperationResult.FetchPaymentMethods -> {
 *             showLoading(false)
 *             // Data is already available in ViewModel state flows
 *         }
 *         is PaymentOperationResult.CheckoutWithGooglePay -> {
 *             handlePaymentStatus(result.status)
 *         }
 *         is PaymentOperationResult.Error -> {
 *             showLoading(false)
 *             showError(result.message)
 *         }
 *         else -> {}
 *     }
 * }
 * ```
 *
 * ## Payment Flow
 *
 * ### Card Payment Flow:
 * 1. User enters card details
 * 2. `onOperationStart(PaymentOperation.AddCard)` called
 * 3. Payment processed
 * 4. `onOperationDone(PaymentOperationResult.AddCard)` called with status
 *
 * ### Google Pay Flow:
 * 1. User taps Google Pay button
 * 2. `onOperationStart(PaymentOperation.CheckoutWithGooglePay)` called
 * 3. Google Pay sheet shown
 * 4. `onOperationDone(PaymentOperationResult.CheckoutWithGooglePay)` called with status
 *
 * ### Saved Card with CVC Flow:
 * 1. User selects saved card and enters CVC
 * 2. `onOperationStart(PaymentOperation.CheckoutWithCvc)` called
 * 3. Payment processed
 * 4. `onOperationDone(PaymentOperationResult.CheckoutWithCvc)` called with status
 *
 * @param session The Airwallex session containing payment intent or recurring session information
 * @param airwallex The Airwallex SDK instance for processing payments
 * @param configuration The UI configuration - either [AwxPaymentElementConfiguration.Card] for standalone
 *                      card UI or [AwxPaymentElementConfiguration.PaymentSheet] for multi-method UI
 * @param onOperationStart Callback invoked when a payment operation begins. Use this to show loading
 *                         indicators or track analytics events
 * @param onOperationDone Callback invoked when a payment operation completes. Handle the result to
 *                        update UI, navigate, or show errors
 *
 * @see AwxPaymentElementConfiguration
 * @see PaymentOperation
 * @see PaymentOperationResult
 * @see com.airwallex.android.core.AirwallexPaymentStatus
 */
@Suppress("LongMethod", "LongParameterList")
@Composable
fun AwxPaymentElement(
    session: AirwallexSession,
    airwallex: Airwallex,
    configuration: AwxPaymentElementConfiguration,
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

    // Determine if we need to fetch based on configuration
    val shouldFetch = when (configuration) {
        is AwxPaymentElementConfiguration.Card -> configuration.cardSchemes.isEmpty()
        is AwxPaymentElementConfiguration.PaymentSheet -> true
    }

    // Fetch data on first composition (if needed)
    LaunchedEffect(shouldFetch) {
        if (shouldFetch) {
            onOperationStart(PaymentOperation.FetchPaymentMethods)
            operationsViewModel.fetchAvailablePaymentMethodsAndConsents()
                .onSuccess {
                    onOperationDone(PaymentOperationResult.FetchPaymentMethods)
                }
                .onFailure { exception ->
                    onOperationDone(
                        PaymentOperationResult.Error(
                            exception.message ?: "Failed to fetch payment methods",
                            exception
                        )
                    )
                }
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
                            operationsViewModel.checkoutWithGooglePay()
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
                        PaymentMethodsLayoutType.TAB -> {
                            PaymentMethodsTabSection(
                                session = session,
                                airwallex = airwallex,
                                onOperationStart = onOperationStart,
                                onOperationDone = onOperationDone,
                            )
                        }
                        PaymentMethodsLayoutType.ACCORDION -> {
                            PaymentMethodsAccordionSection(
                                session = session,
                                airwallex = airwallex,
                                availablePaymentMethodTypes = availableTypes,
                                availablePaymentConsents = availablePaymentConsents,
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
