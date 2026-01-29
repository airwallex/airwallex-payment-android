package com.airwallex.android.view.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.airwallex.android.R
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.core.model.PaymentMethodTypeInfo
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.ui.composables.AirwallexTypography
import com.airwallex.android.ui.composables.StandardText
import com.airwallex.android.view.AddPaymentMethodViewModel
import com.airwallex.android.view.PaymentMethodsViewModel
import com.airwallex.android.view.composables.card.CardBrandTrailingAccessory
import com.airwallex.android.view.composables.card.CardOperation
import com.airwallex.android.view.composables.card.CardSection
import com.airwallex.android.view.composables.schema.SchemaSection
import com.airwallex.android.view.util.toSupportedIcons

@Suppress("ComplexMethod", "LongMethod", "LongParameterList")
@Composable
internal fun PaymentMethodsAccordionSection(
    session: AirwallexSession,
    airwallex: Airwallex,
    paymentMethodViewModel: PaymentMethodsViewModel,
    addPaymentMethodViewModel: AddPaymentMethodViewModel,
    availablePaymentMethodTypes: List<AvailablePaymentMethodType>,
    availablePaymentConsents: List<PaymentConsent>,
//    onAddCard: () -> Unit,
    onDeleteCard: (PaymentConsent) -> Unit,
    onCheckoutWithoutCvc: (PaymentConsent) -> Unit,
    onCheckoutWithCvc: (PaymentConsent, String) -> Unit,
    onDirectPay: (AvailablePaymentMethodType) -> Unit,
    onPayWithFields: (PaymentMethod, PaymentMethodTypeInfo, Map<String, String>) -> Unit,
    onLoading: (Boolean) -> Unit,
    onCardLoadingChanged: ((CardOperation) -> Unit),
    onCardPaymentResult: ((AirwallexPaymentStatus) -> Unit),
) {
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(availablePaymentMethodTypes.first()) }
    var selectedIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
    ) {
        availablePaymentMethodTypes.forEachIndexed { index, type ->
            Column(
                modifier = Modifier
                    .padding(
                        top = if (type == selectedOption && index != 0) 16.dp else 0.dp,
                        bottom = if (type == selectedOption && index != availablePaymentMethodTypes.size - 1) 16.dp else 0.dp,
                    )
                    .border(
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (type == selectedOption) MaterialTheme.colorScheme.outline else Color.Transparent,
                        ),
                        shape = RoundedCornerShape(8.dp),
                    ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = (type == selectedOption),
                            onClick = {
                                selectedIndex = index
                                onOptionSelected(type)
                            },
                            role = Role.RadioButton,
                        )
                        .border(
                            border = BorderStroke(
                                width = (0.5).dp,
                                color = if (type == selectedOption) Color.Transparent else MaterialTheme.colorScheme.outline,
                            ),
                            shape = RoundedCornerShape(
                                topStart = if (index == selectedIndex + 1 || index == 0) 8.dp else 0.dp,
                                topEnd = if (index == selectedIndex + 1 || index == 0) 8.dp else 0.dp,
                                bottomStart = if (index == selectedIndex - 1 || index == availablePaymentMethodTypes.size - 1) 8.dp else 0.dp,
                                bottomEnd = if (index == selectedIndex - 1 || index == availablePaymentMethodTypes.size - 1) 8.dp else 0.dp,
                            ),
                        )
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = (type == selectedOption),
                        onClick = null, // null recommended for accessibility with screen readers
                        colors = RadioButtonDefaults.colors(unselectedColor = MaterialTheme.colorScheme.onSurface),
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    AsyncImage(
                        model = type.resources?.logos?.png ?: painterResource(id = R.drawable.airwallex_ic_card_default),
                        contentDescription = "payment method icon",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .width(32.dp)
                            .height(24.dp)
                            .clip(RoundedCornerShape(4.dp)),
                    )

                    StandardText(
                        text = type.displayName ?: type.name,
                        color = if (type == selectedOption) MaterialTheme.colorScheme.primary else AirwallexColor.TextPrimary,
                        typography = if (type == selectedOption) AirwallexTypography.Body200Bold else AirwallexTypography.Body200,
                        textAlign = TextAlign.Left,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )

                    if (type != selectedOption) {
                        type.cardSchemes?.toSupportedIcons()?.let { icons ->
                            Spacer(modifier = Modifier.weight(1f))
                            CardBrandTrailingAccessory(
                                icons = icons,
                                displayAllSchemes = true,
                                modifier = Modifier
                                    .size(width = 28.dp, height = 19.dp)
                                    .padding(horizontal = 2.dp),
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = (type == selectedOption),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    when (type.name) {
                        PaymentMethodType.CARD.value -> {
                            CardSection(
                                session = session,
                                airwallex = airwallex,
                                addPaymentMethodViewModel = addPaymentMethodViewModel,
                                cardSchemes = type.cardSchemes.orEmpty(),
//                                availablePaymentConsents = availablePaymentConsents,
//                                onAddCard = onAddCard,
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
}