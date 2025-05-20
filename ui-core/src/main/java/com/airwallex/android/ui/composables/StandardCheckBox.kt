package com.airwallex.android.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun StandardCheckBox(
    checked: Boolean,
    text: String,
    modifier: Modifier = Modifier,
    onCheckedChange: ((Boolean) -> Unit)?
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .clickable {
                onCheckedChange?.let { it(!checked) }
            }
            // modifier parameters needs to be applied after clickable to incorporate padding into ripple effect
            .then(modifier),
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
            typography = AirwallexTypography.Body200,
            color = AirwallexColor.TextPrimary,
            modifier = Modifier
                .padding(top = 2.dp)
                .weight(1f),
        )
    }
}