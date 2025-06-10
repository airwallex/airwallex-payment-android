package com.airwallex.android.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun StandardCheckBox(
    checked: Boolean,
    text: String,
    onCheckedChange: ((Boolean) -> Unit)?,
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .clickable(
                onClick = {
                    onCheckedChange?.let { it(!checked) }
                },
            ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 2.dp)
                .fillMaxWidth(),
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(uncheckedColor = MaterialTheme.colorScheme.tertiaryContainer),
            )

            Spacer(modifier = Modifier.width(8.dp))

            StandardText(
                text = text,
                textAlign = TextAlign.Left,
                typography = AirwallexTypography.Caption100,
                color = AirwallexColor.TextPrimary,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .weight(1f),
            )
        }
    }
}

@Composable
@Preview
private fun StandardCheckBoxPreview() {
    StandardCheckBox(
        checked = true,
        text = "Checkbox",
        onCheckedChange = {},
    )
}