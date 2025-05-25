package com.airwallex.android.view.composables.card

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.airwallex.android.R
import com.airwallex.android.ui.composables.StandardTextField
import com.airwallex.android.ui.composables.StandardTextFieldOptions
import com.airwallex.android.view.util.ExpiryDateUtils.VALID_INPUT_LENGTH
import com.airwallex.android.view.util.ExpiryDateUtils.formatExpiryDate
import com.airwallex.android.view.util.ExpiryDateUtils.formatExpiryDateWhenDeleting

@Composable
fun CardExpiryTextField(
    modifier: Modifier = Modifier,
    onTextChanged: (TextFieldValue) -> Unit,
    onComplete: (String) -> Unit,
    onFocusLost: (String) -> Unit,
    isError: Boolean = false,
    shape: Shape = OutlinedTextFieldDefaults.shape,
) {
    var showClearButton by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf<TextFieldValue?>(null) }

    StandardTextField(
        hint = stringResource(R.string.airwallex_expires_hint),
        text = textFieldValue ?: TextFieldValue(),
        onTextChanged = { newText ->
            val isDeleteAction = newText.text.length < (textFieldValue?.text?.length ?: 0)
            if (isDeleteAction) {
                val formattedText = formatExpiryDateWhenDeleting(newText.text)
                textFieldValue = TextFieldValue(
                    text = formattedText.take(VALID_INPUT_LENGTH),
                    selection = TextRange(formattedText.length),
                )
                showClearButton = textFieldValue?.text?.isNotEmpty() == true
                onTextChanged(textFieldValue ?: TextFieldValue())
                return@StandardTextField
            }

            val formattedDate = formatExpiryDate(newText.text)
            textFieldValue = TextFieldValue(
                text = formattedDate.take(VALID_INPUT_LENGTH),
                selection = TextRange(formattedDate.length),
            )
            showClearButton = textFieldValue?.text?.isNotEmpty() == true
            onTextChanged(textFieldValue ?: TextFieldValue())
            if (textFieldValue?.text?.length == VALID_INPUT_LENGTH) {
                onComplete(textFieldValue?.text.orEmpty())
                showClearButton = false
            }
        },
        isError = isError,
        modifier = modifier
            .onFocusEvent { focusState ->
                if (focusState.hasFocus && textFieldValue?.text?.isNotEmpty() == true) {
                    showClearButton = true
                }
            }
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    textFieldValue = textFieldValue?.copy(selection = TextRange(textFieldValue?.text?.length ?: 0)) ?: TextFieldValue()
                } else {
                    if (textFieldValue != null) {
                        // Focus has left the TextField after being focused
                        onFocusLost(textFieldValue?.text.orEmpty())
                    }
                    showClearButton = false
                }
            },
        options = StandardTextFieldOptions(
            inputType = StandardTextFieldOptions.InputType.NUMBER,
            returnType = StandardTextFieldOptions.ReturnType.DONE,
        ),
        onComplete = {
            onComplete(textFieldValue?.text.orEmpty())
        },
        trailingAccessory = {
            if (showClearButton) {
                IconButton(
                    onClick = {
                        showClearButton = false
                        textFieldValue = TextFieldValue()
                        onTextChanged(TextFieldValue())
                    },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.airwallex_ic_clear),
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = "Clear",
                    )
                }
            }
        },
        shape = shape,
    )
}