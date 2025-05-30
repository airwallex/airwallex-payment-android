package com.airwallex.android.view.composables

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.core.model.PaymentMethodTypeInfo
import com.airwallex.android.view.AddPaymentMethodViewModel
import com.airwallex.android.view.PaymentMethodsViewModel
import com.airwallex.android.view.composables.card.CardSection
import com.airwallex.android.view.composables.common.PaymentMethodTabCard
import com.airwallex.android.view.composables.schema.SchemaSection
import kotlinx.coroutines.launch

@Composable
internal fun PaymentMethodsTabSection(
    paymentMethodViewModel: PaymentMethodsViewModel,
    addPaymentMethodViewModel: AddPaymentMethodViewModel,
    availablePaymentMethodTypes: List<AvailablePaymentMethodType>,
    availablePaymentConsents: List<PaymentConsent>,
    onAddCard: () -> Unit,
    onDeleteCard: (PaymentConsent) -> Unit,
    onCheckoutWithoutCvc: (PaymentConsent) -> Unit,
    onCheckoutWithCvc: (PaymentConsent, String) -> Unit,
    onDirectPay: (AvailablePaymentMethodType) -> Unit,
    onPayWithFields: (PaymentMethod, PaymentMethodTypeInfo, Map<String, String>) -> Unit,
    onLoading: (Boolean) -> Unit,
    onError: () -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val pagerState = rememberPagerState(pageCount = { availablePaymentMethodTypes.size })
    val coroutineScope = rememberCoroutineScope()

    var type by remember { mutableStateOf(availablePaymentMethodTypes.first()) }
    var selectedIndex by remember { mutableIntStateOf(0) }

    Column {
        LazyRow(
            state = lazyListState,
            modifier = Modifier.padding(horizontal = 24.dp),
        ) {
            availablePaymentMethodTypes.forEachIndexed { index, availablePaymentMethodType ->
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

                    if (index != availablePaymentMethodTypes.size - 1) {
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
                        addPaymentMethodViewModel = addPaymentMethodViewModel,
                        cardSchemes = type.cardSchemes.orEmpty(),
                        availablePaymentConsents = availablePaymentConsents,
                        onAddCard = onAddCard,
                        onDeleteCard = onDeleteCard,
                        onCheckoutWithoutCvc = onCheckoutWithoutCvc,
                        onCheckoutWithCvc = onCheckoutWithCvc,
                    )
                }
                else -> {
                    SchemaSection(
                        viewModel = paymentMethodViewModel,
                        type = type,
                        onDirectPay = onDirectPay,
                        onPayWithFields = onPayWithFields,
                        onLoading = onLoading,
                        onError = onError,
                    )
                }
            }
        }
    }
}