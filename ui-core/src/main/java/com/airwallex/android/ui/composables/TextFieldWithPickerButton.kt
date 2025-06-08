package com.airwallex.android.ui.composables

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airwallex.android.ui.R

@Suppress("LongParameterList")
@Composable
fun TextFieldWithPickerButton(
    modifier: Modifier = Modifier,
    hint: String? = null,
    supportText: String? = null,
    errorText: String? = null,
    title: String?,
    isFieldRequired: Boolean = false,
    enabled: Boolean = true,
    onPresentRequested: () -> Unit,
    shape: Shape = RoundedCornerShape(8.dp),
    leadingAccessory: (@Composable () -> Unit)? = null,
) {
    TextFieldWithPickerButton(
        hint = hint,
        supportText = supportText,
        errorText = errorText,
        title = title,
        modifier = modifier,
        isFieldRequired = isFieldRequired,
        enabled = enabled,
        onPresentRequested = onPresentRequested,
        trailingAccessory = {
            StandardIcon(
                drawableRes = R.drawable.airwallex_ic_chevron_down,
                size = 16.dp,
                padding = 0.dp,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        },
        shape = shape,
        leadingAccessory = leadingAccessory,
    )
}

@Suppress("LongParameterList")
@Composable
private fun TextFieldWithPickerButton(
    modifier: Modifier = Modifier,
    hint: String?,
    supportText: String? = null,
    errorText: String?,
    title: String?,
    onPresentRequested: () -> Unit,
    isFieldRequired: Boolean = false,
    enabled: Boolean = true,
    leadingAccessory: (@Composable () -> Unit)? = null,
    trailingAccessory: (@Composable () -> Unit)? = null,
    shape: Shape = RoundedCornerShape(8.dp),
) {
    StandardTextField(
        hint = hint,
        text = TextFieldValue(text = title.orEmpty()),
        onTextChanged = {
            // no op
        },
        isFieldRequired = isFieldRequired,
        modifier = modifier,
        enabled = enabled,
        readOnly = true,
        interactionSource = remember { MutableInteractionSource() }
            .also { interactionSource ->
                // Add a launch effect to observe tapping on text field in order to show the picker sheet.
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            onPresentRequested()
                        }
                    }
                }
            },
        supportText = supportText,
        errorText = errorText,
        leadingAccessory = leadingAccessory,
        trailingAccessory = trailingAccessory,
        shape = shape,
    )
}

@Composable
@Preview
private fun TextFieldWithPickerButtonPreview() {
    TextFieldWithPickerButton(
        hint = "Hint",
        title = "Title",
        onPresentRequested = {},
    )
}