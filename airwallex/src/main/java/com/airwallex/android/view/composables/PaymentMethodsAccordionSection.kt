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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.airwallex.android.R
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.ui.composables.AirwallexTypography
import com.airwallex.android.ui.composables.StandardText
import com.airwallex.android.view.PaymentFlowListener
import com.airwallex.android.view.PaymentFlowViewModel
import com.airwallex.android.view.composables.card.CardBrandTrailingAccessory
import com.airwallex.android.view.composables.card.CardSection
import com.airwallex.android.view.composables.google.GooglePayListItem
import com.airwallex.android.view.composables.google.GooglePayStandaloneButton
import com.airwallex.android.view.composables.schema.SchemaSection
import com.airwallex.android.view.util.AnalyticsConstants.PAYMENT_METHOD
import com.airwallex.android.view.util.AnalyticsConstants.PAYMENT_SELECT
import com.airwallex.android.view.util.GooglePayUtil
import com.airwallex.android.view.util.getSinglePaymentMethodOrNull
import com.airwallex.android.view.util.toSupportedIcons

@Suppress("ComplexMethod", "LongMethod", "LongParameterList")
@Composable
internal fun PaymentMethodsAccordionSection(
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
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(availablePaymentMethods.first()) }
    var selectedIndex by remember { mutableIntStateOf(0) }

    if (availablePaymentMethods.getSinglePaymentMethodOrNull(availablePaymentConsents) == null) {
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
        // Google Pay Section on top (if eligible and prioritizeGooglePay is true)
        if (prioritizeGooglePay) {
            GooglePayStandaloneButton(
                allowedPaymentMethods = allowedPaymentMethods,
                paymentFlowListener = paymentFlowListener,
                flowViewModel = flowViewModel,
                airwallex = airwallex,
            )
        }
        val paymentMethodsList = if (prioritizeGooglePay) {
            availablePaymentMethods.filterNot { paymentMethodType ->
                paymentMethodType.name == PaymentMethodType.GOOGLEPAY.value
            }
        } else {
            // If Google Pay is not prioritized, filter it out only if allowedPaymentMethods is null
            availablePaymentMethods.filterNot { paymentMethodType ->
                paymentMethodType.name == PaymentMethodType.GOOGLEPAY.value && allowedPaymentMethods == null
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            paymentMethodsList.forEachIndexed { index, type ->
                Column(
                    modifier = Modifier
                        .padding(
                            top = if (type == selectedOption && index != 0) 16.dp else 0.dp,
                            bottom = if (type == selectedOption && index != paymentMethodsList.size - 1) 16.dp else 0.dp,
                        )
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (type == selectedOption) AirwallexColor.borderDecorative() else Color.Transparent,
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
                                    AnalyticsLogger.logAction(
                                        PAYMENT_SELECT, mapOf(PAYMENT_METHOD to type)
                                    )
                                },
                                role = Role.RadioButton,
                            )
                            .border(
                                border = BorderStroke(
                                    width = (0.5).dp,
                                    color = if (type == selectedOption) Color.Transparent else AirwallexColor.borderDecorative(),
                                ),
                                shape = RoundedCornerShape(
                                    topStart = if (index == selectedIndex + 1 || index == 0) 8.dp else 0.dp,
                                    topEnd = if (index == selectedIndex + 1 || index == 0) 8.dp else 0.dp,
                                    bottomStart = if (index == selectedIndex - 1 || index == paymentMethodsList.size - 1) 8.dp else 0.dp,
                                    bottomEnd = if (index == selectedIndex - 1 || index == paymentMethodsList.size - 1) 8.dp else 0.dp,
                                ),
                            )
                            .padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = (type == selectedOption),
                            onClick = null, // null recommended for accessibility with screen readers
                            colors = RadioButtonDefaults.colors(unselectedColor = AirwallexColor.borderDecorativeStrong()),
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        AsyncImage(
                            model = type.resources?.logos?.png
                                ?: painterResource(id = R.drawable.airwallex_ic_card_default),
                            contentDescription = "payment method icon",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .width(32.dp)
                                .height(24.dp)
                                .clip(RoundedCornerShape(4.dp)),
                        )

                        StandardText(
                            text = type.displayName ?: type.name,
                            color = if (type == selectedOption) AirwallexColor.theme() else AirwallexColor.textPrimary(),
                            typography = if (type == selectedOption) AirwallexTypography.Body200Bold else AirwallexTypography.Body200,
                            textAlign = TextAlign.Left,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 8.dp),
                        )

                        if (type != selectedOption && type.name != PaymentMethodType.GOOGLEPAY.value) {
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
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
    } else {
        CardSection(
            session = session,
            airwallex = airwallex,
            cardSchemes = availablePaymentMethods.first().cardSchemes.orEmpty(),
            isSinglePaymentMethod = true,
            paymentFlowListener = paymentFlowListener
        )
    }
}