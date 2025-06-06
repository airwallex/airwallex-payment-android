package com.airwallex.android.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Suppress("LongParameterList")
@Composable
fun StandardOutlinedButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    buttonHeight: Dp = 52.dp,
    buttonWidth: Modifier.() -> Modifier = { this.fillMaxWidth() },
    textColor: Color = MaterialTheme.colorScheme.primary,
    borderColor: Color = AirwallexColor.Gray30,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .buttonWidth()
            .height(buttonHeight),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {

        StandardText(
            text = text,
            color = textColor,
            typography = AirwallexTypography.Headline100
        )
    }
}

@Composable
@Preview
private fun StandardOutlinedButtonPreview() {
    StandardOutlinedButton(
        onClick = {},
        text = "Outlined Button",
    )
}