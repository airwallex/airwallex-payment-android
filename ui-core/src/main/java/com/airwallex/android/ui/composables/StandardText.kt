package com.airwallex.android.ui.composables

import androidx.annotation.StringRes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun StandardText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
    typography: AirwallexTypography = AirwallexTypography.Body100,
    textAlign: TextAlign? = null,
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        style = typography.toComposeTextStyle(),
        textAlign = textAlign,
    )
}

@Composable
fun StandardText(
    @StringRes textRes: Int,
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
    typography: AirwallexTypography = AirwallexTypography.Body100,
    textAlign: TextAlign? = null,
) {
    StandardText(
        text = stringResource(id = textRes),
        modifier = modifier,
        color = color,
        typography = typography,
        textAlign = textAlign
    )
}

@Preview
@Composable
fun StandardTextPreview() {
    StandardText("Hello, World!")
}