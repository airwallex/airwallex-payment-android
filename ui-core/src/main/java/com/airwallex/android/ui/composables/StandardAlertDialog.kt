package com.airwallex.android.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun StandardAlertDialog(
    title: String,
    content: String,
    confirmButtonTitle: String,
    dismissButtonTitle: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        title = {
            StandardText(
                text = title,
                color = AirwallexColor.TextPrimary,
                typography = AirwallexTypography.Title300,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        text = {
            StandardText(
                text = content,
                color = AirwallexColor.TextPrimary,
                typography = AirwallexTypography.Body200,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        shape = RoundedCornerShape(8.dp),
                        color = AirwallexColor.White,
                    )
                    .padding(
                        vertical = 12.dp,
                        horizontal = 24.dp,
                    ),
            ) {
                StandardOutlinedButton(
                    text = dismissButtonTitle,
                    onClick = onDismiss,
                    textColor = AirwallexColor.TextPrimary,
                    borderColor = AirwallexColor.Transparent,
                    modifier = Modifier.weight(1f),
                )

                Spacer(modifier = Modifier.width(12.dp))

                StandardSolidButton(
                    text = confirmButtonTitle,
                    containerColor = AirwallexColor.TextError,
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                )
            }
        },
        onDismissRequest = onDismiss,
    )
}

@Composable
@Preview
private fun StandardAlertDialogPreview() {
    StandardAlertDialog(
        title = "Title",
        content = "Content",
        confirmButtonTitle = "Confirm",
        dismissButtonTitle = "Dismiss",
        onConfirm = {},
        onDismiss = {},
    )
}