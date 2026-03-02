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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Suppress("LongParameterList")
@Composable
fun StandardAlertDialog(
    title: String,
    content: String,
    confirmButtonTitle: String? = null,
    confirmButtonContainerColor: Color = AirwallexColor.textError(),
    onConfirm: (() -> Unit)? = null,
    dismissButtonTitle: String? = null,
    onDismiss: (() -> Unit)? = null,
) {
    AlertDialog(
        backgroundColor = AirwallexColor.backgroundPrimary(),
        title = {
            StandardText(
                text = title,
                color = AirwallexColor.textPrimary(),
                typography = AirwallexTypography.Title300,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        text = {
            StandardText(
                text = content,
                color = AirwallexColor.textPrimary(),
                typography = AirwallexTypography.Body200,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        buttons = {
            val showDismissButton = dismissButtonTitle != null && onDismiss != null
            val showConfirmButton = confirmButtonTitle != null && onConfirm != null

            if (showDismissButton || showConfirmButton) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            shape = RoundedCornerShape(8.dp),
                            color = AirwallexColor.backgroundPrimary(),
                        )
                        .padding(
                            vertical = 12.dp,
                            horizontal = 24.dp,
                        ),
                ) {
                    if (showDismissButton) {
                        StandardOutlinedButton(
                            text = dismissButtonTitle!!,
                            onClick = onDismiss!!,
                            textColor = AirwallexColor.textPrimary(),
                            borderColor = AirwallexColor.Transparent,
                            modifier = Modifier.weight(1f),
                        )

                        if (showConfirmButton) {
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                    }

                    if (showConfirmButton) {
                        StandardSolidButton(
                            text = confirmButtonTitle!!,
                            containerColor = confirmButtonContainerColor,
                            onClick = onConfirm!!,
                            modifier = if (showDismissButton) Modifier.weight(1f) else Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        },
        onDismissRequest = { onDismiss?.invoke() },
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