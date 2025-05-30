package com.airwallex.android.view.composables.google

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.airwallex.android.R
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.ui.composables.AirwallexTypography
import com.airwallex.android.ui.composables.StandardText
import com.google.pay.button.ButtonTheme
import com.google.pay.button.ButtonType
import com.google.pay.button.PayButton

@Composable
internal fun GooglePaySection(
    allowedPaymentMethods: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var payButtonVisible by remember { mutableStateOf(false) }

    PayButtonWithVisibilityChecker(
        onVisibilityChanged = { isVisible -> payButtonVisible = isVisible },
    ) {
        PayButton(
            onClick = onClick,
            allowedPaymentMethods = allowedPaymentMethods,
            theme = ButtonTheme.Dark,
            type = ButtonType.Buy,
            radius = 8.dp,
            enabled = true,
            modifier = modifier,
        )
    }

    if (payButtonVisible) {
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))

            StandardText(
                textRes = R.string.airwallex_or_pay_with,
                color = AirwallexColor.Gray60,
                typography = AirwallexTypography.Body200,
                modifier = Modifier.padding(horizontal = 24.dp),
            )

            HorizontalDivider(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun PayButtonWithVisibilityChecker(
    modifier: Modifier = Modifier,
    onVisibilityChanged: (Boolean) -> Unit,
    payButtonContent: @Composable () -> Unit,
) {
    var size by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier.onGloballyPositioned { coordinates ->
            val isVisible = coordinates.size.width > 0 && coordinates.size.height > 0
            onVisibilityChanged(isVisible)
            size = coordinates.size
        },
    ) {
        payButtonContent()
    }
}

@Composable
@Preview
private fun GooglePaySectionPreview() {
    GooglePaySection(
        allowedPaymentMethods = "",
        onClick = {},
    )
}