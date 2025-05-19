package com.airwallex.android.view.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.ui.composables.StandardText
import com.airwallex.android.view.AddPaymentMethodViewModel
import kotlinx.coroutines.launch

@Composable
internal fun PaymentMethodsSection(
    addPaymentMethodViewModel: AddPaymentMethodViewModel,
    availablePaymentMethodTypes: List<AvailablePaymentMethodType>,
) {
    val pagerState = rememberPagerState(pageCount = { availablePaymentMethodTypes.size })
    val coroutineScope = rememberCoroutineScope()
    var type by remember { mutableStateOf(availablePaymentMethodTypes.first()) }

    Column {
        LazyRow(modifier = Modifier.padding(horizontal = 24.dp)) {
            availablePaymentMethodTypes.forEachIndexed { index, availablePaymentMethodType ->
                item(key = "payment_method_$index") {
                    Spacer(modifier = Modifier.width(12.dp))

                    PaymentMethodCard(
                        paymentMethodType = availablePaymentMethodType,
                        onClick = {
                            coroutineScope.launch {
                                type = availablePaymentMethodType
                                pagerState.scrollToPage(page = index)
                            }
                        },
                    )

                    Spacer(modifier = Modifier.width(12.dp))
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
                    AddCardScreen(
                        viewModel = addPaymentMethodViewModel,
                        type = type,
                    )
                }
                else -> {
                    // TODO
                    StandardText(text = "TODO")
                }
            }
        }
    }
}