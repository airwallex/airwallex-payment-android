package com.airwallex.android.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Suppress("LongParameterList")
@Composable
fun StandardSolidButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    containerColor: Color = AirwallexColor.theme,
    contentColor: Color = Color.Unspecified,
    textColor: Color = AirwallexColor.textInverse,
    typography: AirwallexTypography = AirwallexTypography.Headline100,
    buttonHeight: Dp? = null,
    buttonWidth: Modifier.() -> Modifier = { this.fillMaxWidth() },
    cornerRadius: Dp = 8.dp,
    enabled: Boolean = true
) {
    val heightModifier = if (buttonHeight != null) {
        Modifier.height(buttonHeight)
    } else {
        Modifier.heightIn(min = 52.dp)
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .buttonWidth()
            .then(heightModifier),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(cornerRadius),
        enabled = enabled
    ) {
        StandardText(
            text = text,
            color = textColor,
            typography = typography,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
@Preview
private fun StandardSolidButtonPreview() {
    StandardSolidButton(
        onClick = {},
        text = "Solid Button",
    )
}