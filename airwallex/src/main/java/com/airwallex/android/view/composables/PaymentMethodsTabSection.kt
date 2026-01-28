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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.airwallex.android.view.composables.card.CardOperation
import com.airwallex.android.view.composables.card.CardSection
import com.airwallex.android.view.composables.common.PaymentMethodTabCard
import com.airwallex.android.view.composables.schema.SchemaSection
import kotlinx.coroutines.launch

/**
 * PaymentMethodsTabSection with internal ViewModel management.
 * Automatically fetches and manages payment methods and consents.
 *
 * @param session The Airwallex session for the payment flow
 * @param airwallex The Airwallex instance for payment operations
 * @param paymentMethodViewModel ViewModel for schema-based payment methods
 * @param addPaymentMethodViewModel ViewModel for card payment operations
 * @param onDeleteCard Callback when a card is deleted
 * @param onCheckoutWithoutCvc Callback for checkout without CVC
 * @param onCheckoutWithCvc Callback for checkout with CVC
 * @param onDirectPay Callback for direct payment
 * @param onPayWithFields Callback for payment with fields
 * @param onLoading Callback for loading state changes
 * @param onCardLoadingChanged Callback for card operation loading changes
 * @param onCardPaymentResult Callback for card payment result
 */
@Suppress("LongMethod", "LongParameterList")
@Composable
fun PaymentMethodsTabSection(
    session: AirwallexSession,
    airwallex: Airwallex,
    paymentMethodViewModel: PaymentMethodsViewModel,
    addPaymentMethodViewModel: AddPaymentMethodViewModel,
    onDeleteCard: (PaymentConsent) -> Unit,
    onCheckoutWithoutCvc: (PaymentConsent) -> Unit,
    onCheckoutWithCvc: (PaymentConsent, String) -> Unit,
    onDirectPay: (AvailablePaymentMethodType) -> Unit,
    onPayWithFields: (PaymentMethod, PaymentMethodTypeInfo, Map<String, String>) -> Unit,
    onLoading: (Boolean) -> Unit,
    onCardLoadingChanged: ((CardOperation?) -> Unit),
    onCardPaymentResult: ((AirwallexPaymentStatus) -> Unit),
) {
    val context = LocalContext.current

    val operationsViewModel: PaymentOperationsViewModel = viewModel(
        factory = PaymentOperationsViewModel.Factory(
            application = context.applicationContext as Application,
            airwallex = airwallex,
            session = session
        )
    )

    val availablePaymentMethods by operationsViewModel.availablePaymentMethods.collectAsState()
    val isLoading by operationsViewModel.isLoading.collectAsState()

    onLoading(isLoading)

    if (availablePaymentMethods.isNotEmpty()) {
        val lazyListState = rememberLazyListState()
        val pagerState = rememberPagerState(pageCount = { availablePaymentMethods.size })
        val coroutineScope = rememberCoroutineScope()

        var type by remember { mutableStateOf(availablePaymentMethods.first()) }
        var selectedIndex by remember { mutableIntStateOf(0) }

        Column {
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
                                AirwallexLogger.info("PaymentMethodsActivity onPaymentMethodClick: type = ${type.name}")
                                coroutineScope.launch {
                                    selectedIndex = index
                                    type = availablePaymentMethodType
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
//                            availablePaymentConsents = availablePaymentConsents,
                            onDeleteCard = onDeleteCard,
                            onCheckoutWithoutCvc = onCheckoutWithoutCvc,
                            onCheckoutWithCvc = onCheckoutWithCvc,
                            onLoadingChanged = onCardLoadingChanged,
                            onPaymentResult = onCardPaymentResult,
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