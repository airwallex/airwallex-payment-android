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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.airwallex.android.R
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
import com.airwallex.android.view.composables.card.CardSection
import com.airwallex.android.view.composables.schema.SchemaSection
import com.airwallex.android.view.util.toSupportedIcons

@Composable
internal fun PaymentMethodsAccordionSection(
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
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(availablePaymentMethodTypes.first()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .border(
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(8.dp),
            ),
    ) {
        availablePaymentMethodTypes.forEachIndexed { index, type ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (type == selectedOption),
                        onClick = { onOptionSelected(type) },
                        role = Role.RadioButton,
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

            if (index != availablePaymentMethodTypes.size - 1) {
                HorizontalDivider(
                    modifier = Modifier
                        .border(border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline))
                        .fillMaxWidth(),
                )
            }
        }
    }
}