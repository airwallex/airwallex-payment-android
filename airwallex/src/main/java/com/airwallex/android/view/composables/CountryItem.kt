package com.airwallex.android.view.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import com.airwallex.android.view.util.getFlagEmoji

@Composable
internal fun CountryItem(
    countryName: String,
    countryCode: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .then(
                if (isSelected) {
                    Modifier
                        .background(
                            color = AirwallexColor.Ultraviolet10,
                            shape = RoundedCornerShape(8.dp),
                        )
                } else {
                    Modifier
                }
            )
            .clickable(
                onClick = onClick,
            )
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(24.dp))

        StandardText(
            text = "${getFlagEmoji(countryCode)}  $countryName",
            color = AirwallexColor.TextPrimary,
            typography = AirwallexTypography.Body100,
        )

        Spacer(Modifier.weight(1f))

        if (isSelected) {
            StandardIcon(
                drawableRes = R.drawable.airwallex_ic_tick,
                size = 16.dp,
                padding = 0.dp,
                tint = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.width(24.dp))
        }
    }
}

@Preview
@Composable
private fun CountryItemNotSelectedPreview() {
    CountryItem(
        countryName = "Australia",
        countryCode = "AU",
        isSelected = false,
        onClick = {},
    )
}

@Preview
@Composable
private fun CountryItemSelectedPreview() {
    CountryItem(
        countryName = "Australia",
        countryCode = "AU",
        isSelected = true,
        onClick = {},
    )
}