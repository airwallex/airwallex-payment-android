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
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.ui.composables.AirwallexTypography
import com.airwallex.android.ui.composables.StandardText

@Suppress("LongMethod", "LongParameterList")
@Composable
internal fun PaymentScreen(
    paymentElementState: AwxPaymentElementManager,
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        StandardText(
            text = stringResource(id = R.string.airwallex_payment_methods),
            color = AirwallexColor.TextPrimary,
            typography = AirwallexTypography.Title200,
            textAlign = TextAlign.Left,
        )
        Spacer(modifier = Modifier.height(24.dp))

        paymentElementState.Content()
    }
}