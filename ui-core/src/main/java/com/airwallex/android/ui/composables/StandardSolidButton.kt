package com.airwallex.android.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun StandardSolidButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = Color.Unspecified,
    textColor: Color = MaterialTheme.colorScheme.primaryContainer,
    typography: AirwallexTypography = AirwallexTypography.Headline100,
    buttonHeight: Dp = 52.dp,
    buttonWidth: Modifier.() -> Modifier = { this.fillMaxWidth() },
    cornerRadius: Dp = 8.dp,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .buttonWidth()
            .height(buttonHeight),
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
            typography = typography
        )
    }
}