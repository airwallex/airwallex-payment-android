package com.airwallex.android.view.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.view.PaymentFlowListener
import com.airwallex.android.view.PaymentFlowViewModel
import com.airwallex.android.view.composables.card.CardSection
import com.airwallex.android.view.composables.common.PaymentMethodTabCard
import com.airwallex.android.view.composables.google.GooglePayListItem
import com.airwallex.android.view.composables.google.GooglePayStandaloneButton
import com.airwallex.android.view.composables.schema.SchemaSection
import com.airwallex.android.view.util.AnalyticsConstants.PAYMENT_METHOD
import com.airwallex.android.view.util.AnalyticsConstants.PAYMENT_SELECT
import com.airwallex.android.view.util.GooglePayUtil
import com.airwallex.android.view.util.getSinglePaymentMethodOrNull
import kotlinx.coroutines.launch

/**
 * PaymentMethodsTabSection with internal ViewModel management.
 * Automatically fetches and manages payment methods and consents.
 *
 * @param session The Airwallex session for the payment flow
 * @param airwallex The Airwallex instance for payment operations
 * @param prioritizeGooglePay If true, GooglePay is shown on top; if false, shown in LazyRow
 */
@Suppress("LongMethod", "LongParameterList")
@Composable
internal fun PaymentMethodsTabSection(
    session: AirwallexSession,
    airwallex: Airwallex,
    paymentFlowListener: PaymentFlowListener,
    prioritizeGooglePay: Boolean = true,
) {
    val flowViewModel: PaymentFlowViewModel = viewModel(
        factory = PaymentFlowViewModel.Factory(
            airwallex = airwallex,
            session = session
        ),
        viewModelStoreOwner = airwallex.activity
    )

    val availablePaymentMethods by flowViewModel.availablePaymentMethods.collectAsState()
    val availablePaymentConsents by flowViewModel.availablePaymentConsents.collectAsState()

    if (availablePaymentMethods.isNotEmpty()) {
        val lazyListState = rememberLazyListState()
        val pagerState = rememberPagerState(pageCount = { availablePaymentMethods.size })
        val coroutineScope = rememberCoroutineScope()

        var type by remember { mutableStateOf(availablePaymentMethods.first()) }
        var selectedIndex by remember { mutableIntStateOf(0) }
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
        Column {
            // Google Pay Section on top (if eligible and prioritizeGooglePay is true)
            if (prioritizeGooglePay) {
                GooglePayStandaloneButton(
                    allowedPaymentMethods = allowedPaymentMethods,
                    paymentFlowListener = paymentFlowListener,
                    flowViewModel = flowViewModel,
                    airwallex = airwallex,
                )
            }
            val paymentMethodsList = if (prioritizeGooglePay || allowedPaymentMethods == null) {
                availablePaymentMethods.filterNot { paymentMethodType ->
                    paymentMethodType.name == PaymentMethodType.GOOGLEPAY.value
                }
            } else {
                availablePaymentMethods
            }
            if (paymentMethodsList.getSinglePaymentMethodOrNull(availablePaymentConsents) == null) {
                LazyRow(
                    state = lazyListState,
                ) {
                    paymentMethodsList.forEachIndexed { index, availablePaymentMethodType ->
                        item(key = "payment_method_$index") {
                            if (index != 0) {
                                Spacer(modifier = Modifier.width(12.dp))
                            }

                            PaymentMethodTabCard(
                                isSelected = selectedIndex == index,
                                selectedType = availablePaymentMethodType,
                                onClick = {
                                    coroutineScope.launch {
                                        selectedIndex = index
                                        type = availablePaymentMethodType
                                        AnalyticsLogger.logAction(
                                            PAYMENT_SELECT, mapOf(PAYMENT_METHOD to type)
                                        )
                                        AirwallexLogger.info("PaymentMethodsActivity onPaymentMethodClick: type = ${type.name}")
                                        pagerState.scrollToPage(page = index)
                                        // Position the selected item as the second item in the UI
                                        lazyListState.animateScrollToItem(
                                            index = if (index < 1) index else index - 1,
                                            scrollOffset = 0,
                                        )
                                    }
                                },
                            )

                            if (index != paymentMethodsList.size - 1) {
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
            ) {
                when (type.name) {
                    PaymentMethodType.CARD.value -> {
                        CardSection(
                            session = session,
                            airwallex = airwallex,
                            cardSchemes = type.cardSchemes.orEmpty(),
                            paymentFlowListener = paymentFlowListener,
                        )
                    }

                    PaymentMethodType.GOOGLEPAY.value -> {
                        GooglePayListItem(
                            allowedPaymentMethods = allowedPaymentMethods,
                            paymentFlowListener = paymentFlowListener,
                            flowViewModel = flowViewModel,
                            airwallex = airwallex,
                        )
                    }

                    else -> {
                        SchemaSection(
                            session = session,
                            airwallex = airwallex,
                            type = type,
                            paymentFlowListener = paymentFlowListener,
                        )
                    }
                }
            }
        }
    }
}