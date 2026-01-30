package com.airwallex.android.view.composables

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.core.model.PaymentMethodTypeInfo
import com.airwallex.android.view.AddPaymentMethodViewModel
import com.airwallex.android.view.PaymentMethodsViewModel
import com.airwallex.android.view.PaymentOperationsViewModel
import com.airwallex.android.view.composables.card.CardSection
import com.airwallex.android.view.composables.card.PaymentOperation
import com.airwallex.android.view.composables.card.PaymentOperationResult
import com.airwallex.android.view.composables.common.PaymentMethodTabCard
import com.airwallex.android.view.composables.schema.SchemaSection
import com.airwallex.android.view.util.getSinglePaymentMethodOrNull
import kotlinx.coroutines.launch

/**
 * PaymentMethodsTabSection with internal ViewModel management.
 * Automatically fetches and manages payment methods and consents.
 *
 * @param session The Airwallex session for the payment flow
 * @param airwallex The Airwallex instance for payment operations
 * @param paymentMethodViewModel ViewModel for schema-based payment methods
 * @param addPaymentMethodViewModel ViewModel for card payment operations
 * @param onCheckoutWithoutCvc Callback for checkout without CVC
 * @param onCheckoutWithCvc Callback for checkout with CVC
 * @param onDirectPay Callback for direct payment
 * @param onPayWithFields Callback for payment with fields
 * @param onLoading Callback for loading state changes
 * @param onOperationStart Callback when a card operation starts
 * @param onOperationDone Callback when a card operation completes
 */
@Suppress("LongMethod", "LongParameterList")
@Composable
fun PaymentMethodsTabSection(
    session: AirwallexSession,
    airwallex: Airwallex,
    paymentMethodViewModel: PaymentMethodsViewModel,
    addPaymentMethodViewModel: AddPaymentMethodViewModel,
    onCheckoutWithoutCvc: (PaymentConsent) -> Unit,
    onCheckoutWithCvc: (PaymentConsent, String) -> Unit,
    onDirectPay: (AvailablePaymentMethodType) -> Unit,
    onPayWithFields: (PaymentMethod, PaymentMethodTypeInfo, Map<String, String>) -> Unit,
    onLoading: (Boolean) -> Unit,
    onOperationStart: (PaymentOperation) -> Unit,
    onOperationDone: (PaymentOperationResult) -> Unit,
) {
    val operationsViewModel: PaymentOperationsViewModel = viewModel(
        factory = PaymentOperationsViewModel.Factory(
            airwallex = airwallex,
            session = session
        ),
        viewModelStoreOwner = airwallex.activity
    )
    LaunchedEffect(Unit) {
        operationsViewModel.fetchAvailablePaymentMethodsAndConsents()
    }

    val availablePaymentMethods by operationsViewModel.availablePaymentMethods.collectAsState()
    val availablePaymentConsents by operationsViewModel.availablePaymentConsents.collectAsState()
    val isLoading by operationsViewModel.isLoading.collectAsState()

    onLoading(isLoading)

    if (availablePaymentMethods.isNotEmpty()) {
        val lazyListState = rememberLazyListState()
        val pagerState = rememberPagerState(pageCount = { availablePaymentMethods.size })
        val coroutineScope = rememberCoroutineScope()

        var type by remember { mutableStateOf(availablePaymentMethods.first()) }
        var selectedIndex by remember { mutableIntStateOf(0) }

        Column {
            if (availablePaymentMethods.getSinglePaymentMethodOrNull(availablePaymentConsents) == null) {
                LazyRow(
                    state = lazyListState,
                    modifier = Modifier.padding(horizontal = 24.dp),
                ) {
                    availablePaymentMethods.forEachIndexed { index, availablePaymentMethodType ->
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

                            if (index != availablePaymentMethods.size - 1) {
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
                            addPaymentMethodViewModel = addPaymentMethodViewModel,
                            cardSchemes = type.cardSchemes.orEmpty(),
                            onCheckoutWithoutCvc = onCheckoutWithoutCvc,
                            onCheckoutWithCvc = onCheckoutWithCvc,
                            onOperationStart = onOperationStart,
                            onOperationDone = onOperationDone,
                        )
                    }

                    else -> {
                        SchemaSection(
                            viewModel = paymentMethodViewModel,
                            type = type,
                            onDirectPay = onDirectPay,
                            onPayWithFields = onPayWithFields,
                            onLoading = onLoading,
                        )
                    }
                }
            }
        }
    }
}