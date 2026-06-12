package com.airwallex.android.view.composables.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airwallex.android.R
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.ui.composables.AirwallexTypography
import com.airwallex.android.ui.composables.StandardIcon
import com.airwallex.android.ui.composables.StandardText

@Composable
internal fun StateItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .then(
                if (isSelected) {
                    Modifier.background(
                        color = AirwallexColor.backgroundPrimary,
                        shape = RoundedCornerShape(8.dp),
                    )
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(24.dp))

        StandardText(
            text = label,
            color = AirwallexColor.textPrimary,
            typography = AirwallexTypography.Body100,
        )

        Spacer(Modifier.weight(1f))

        if (isSelected) {
            StandardIcon(
                drawableRes = R.drawable.airwallex_ic_tick,
                size = 16.dp,
                padding = 0.dp,
                tint = AirwallexColor.theme,
            )

            Spacer(modifier = Modifier.width(24.dp))
        }
    }
}

@Preview
@Composable
private fun StateItemNotSelectedPreview() {
    StateItem(
        label = "California",
        isSelected = false,
        onClick = {},
    )
}

@Preview
@Composable
private fun StateItemSelectedPreview() {
    StateItem(
        label = "California",
        isSelected = true,
        onClick = {},
    )
}
