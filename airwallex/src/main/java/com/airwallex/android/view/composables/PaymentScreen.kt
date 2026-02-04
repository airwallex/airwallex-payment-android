package com.airwallex.android.view.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airwallex.android.R
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.PaymentMethodsLayoutType
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.ui.composables.AirwallexTypography
import com.airwallex.android.ui.composables.StandardText
import com.airwallex.android.view.util.GooglePayUtil
import com.airwallex.android.view.util.getSinglePaymentMethodOrNull

@Suppress("LongMethod", "LongParameterList")
@Composable
internal fun PaymentScreen(
    session: AirwallexSession,
    airwallex: Airwallex,
    layoutType: PaymentMethodsLayoutType,
    availablePaymentMethodTypes: List<AvailablePaymentMethodType>,
    availablePaymentConsents: List<PaymentConsent>,
    operationListener: com.airwallex.android.view.PaymentOperationListener,
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        val isSinglePaymentMethod = availablePaymentMethodTypes.getSinglePaymentMethodOrNull(availablePaymentConsents) != null
        val titleResId = if(!isSinglePaymentMethod) R.string.airwallex_payment_methods else R.string.airwallex_new_card
        StandardText(
            text = stringResource(id = titleResId),
            color = AirwallexColor.TextPrimary,
            typography = AirwallexTypography.Title200,
            textAlign = TextAlign.Left,
        )
        val allowedPaymentMethods = session.googlePayOptions?.let { googlePayOptions ->
                availablePaymentMethodTypes.firstOrNull {
                    it.name == PaymentMethodType.GOOGLEPAY.value
                }?.let { paymentMethodType ->
                    GooglePayUtil.retrieveAllowedPaymentMethods(
                        googlePayOptions,
                        paymentMethodType.cardSchemes,
                    )
                }
            }
        if(allowedPaymentMethods != null || !isSinglePaymentMethod) {
            Spacer(modifier = Modifier.height(24.dp))
        }
        AwxPaymentElement(
            session = session,
            airwallex = airwallex,
            configuration = AwxPaymentElementConfiguration.PaymentSheet(type = layoutType),
            operationListener = operationListener,
        )
    }
}