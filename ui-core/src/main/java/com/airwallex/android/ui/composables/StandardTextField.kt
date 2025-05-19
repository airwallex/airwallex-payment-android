package com.airwallex.android.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun StandardTextField(
    text: TextFieldValue,
    hint: String?,
    onTextChanged: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    supportText: String? = null,
    errorText: String? = null,
    isError: Boolean = false,
    isFieldRequired: Boolean = false,
    options: StandardTextFieldOptions = StandardTextFieldOptions(),
    leadingAccessory: @Composable (() -> Unit)? = null,
    trailingAccessory: @Composable (() -> Unit)? = null,
    onFocusChanged: ((Boolean) -> Unit)? = null,
    onComplete: (() -> Unit)? = null,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    AirwallexTheme {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChanged,
            modifier = modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    onFocusChanged?.invoke(focusState.isFocused)
                },
            enabled = enabled,
            readOnly = readOnly,
            interactionSource = interactionSource,
            placeholder = hint?.let { { Hint(isFieldRequired, it) } },
            leadingIcon = leadingAccessory,
            trailingIcon = trailingAccessory,
            supportingText = supportTextOrNull(supportText = supportText, errorText = errorText),
            isError = isError || errorText.isNullOrEmpty().not(),
            keyboardOptions = options.makeKeyboardOptions(),
            keyboardActions = options.returnType.makeKeyboardAction(onComplete),
            singleLine = options.singleLine,
            maxLines = 1,
            textStyle = textStyle,
            colors = textFieldColors(),
        )
    }
}

@Composable
private fun supportTextOrNull(
    supportText: String?,
    errorText: String?,
): @Composable (() -> Unit)? {
    return if (supportText.isNullOrEmpty() && errorText.isNullOrEmpty()) {
        null
    } else {
        {
            SupportingText(
                description = supportText,
                errorMessage = errorText,
            )
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = AirwallexColor.TextPrimary,
    unfocusedTextColor = AirwallexColor.TextPrimary,
    unfocusedLabelColor = MaterialTheme.colorScheme.tertiaryContainer,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
)

@Composable
private fun SupportingText(
    description: String?,
    errorMessage: String?,
    /**
     * By default, bodySmall is used. We override it here.
     */
    textStyle: TextStyle = AirwallexTypography.Caption100.toComposeTextStyle(),
) {
    if (description.isNullOrEmpty() && errorMessage.isNullOrEmpty()) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primaryContainer),
    ) {
        if (!description.isNullOrEmpty()) {
            Text(
                text = description,
                style = textStyle
                    .copy(color = AirwallexColor.TextPrimary),
            )
        }

        if (!errorMessage.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                style = textStyle
                    .copy(color = MaterialTheme.colorScheme.error),
            )
        }
    }
}

@Composable
private fun Hint(
    isFieldRequired: Boolean,
    hint: String,
) {
    if (isFieldRequired) {
        Text(buildAnnotatedString {
            val text = "$hint *"
            append(text)

            val baseStyle = MaterialTheme.typography.bodyMedium
                .toSpanStyle()
                .copy(color = MaterialTheme.colorScheme.error)
            addStyle(style = baseStyle, start = text.length - 1, end = text.length)
        })
    } else {
        Text(
            text = hint,
            textAlign = TextAlign.Left,
            color = AirwallexColor.Gray50,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

data class StandardTextFieldOptions(
    val singleLine: Boolean = true,
    val inputType: InputType = InputType.NORMAL,
    val returnType: ReturnType = ReturnType.DONE
) {
    /**
     * See [android.widget.TextView.getInputType] for details.
     */
    enum class InputType {
        NORMAL,
        NUMBER,
        DECIMALS,
        NUMBER_PASSWORD;

        fun makeKeyboardType(): KeyboardType = when (this) {
            NORMAL -> KeyboardType.Text
            NUMBER -> KeyboardType.Number
            DECIMALS -> KeyboardType.Decimal
            NUMBER_PASSWORD -> KeyboardType.NumberPassword
        }
    }

    enum class ReturnType {
        DONE;

        fun makeImeAction(): ImeAction = when (this) {
            DONE -> ImeAction.Done
        }

        @Composable
        fun makeKeyboardAction(onComplete: (() -> Unit)?): KeyboardActions {
            return when (this) {
                DONE -> KeyboardActions {
                    onComplete?.invoke()
                }
            }
        }
    }

    fun makeKeyboardOptions() = KeyboardOptions(
        capitalization = KeyboardCapitalization.Sentences,
        autoCorrectEnabled = true,
        keyboardType = inputType.makeKeyboardType(),
        imeAction = returnType.makeImeAction(),
    )
}