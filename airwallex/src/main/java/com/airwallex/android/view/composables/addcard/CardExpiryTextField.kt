package com.airwallex.android.view.composables.addcard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.airwallex.android.R
import com.airwallex.android.ui.composables.StandardTextField
import com.airwallex.android.ui.composables.StandardTextFieldOptions
import com.airwallex.android.view.util.ExpiryDateUtils.VALID_INPUT_LENGTH
import com.airwallex.android.view.util.ExpiryDateUtils.formatExpiryDate

@Composable
fun CardExpiryTextField(
    modifier: Modifier = Modifier,
    onTextChanged: (TextFieldValue) -> Unit,
    onComplete: (String) -> Unit,
    onFocusLost: (String) -> Unit,
    isError: Boolean = false,
) {
    var textFieldValue by remember { mutableStateOf<TextFieldValue?>(null) }

    StandardTextField(
        hint = stringResource(R.string.airwallex_expires_hint),
        text = textFieldValue ?: TextFieldValue(),
        onTextChanged = { newText ->
            val isDeleteAction = newText.text.length < (textFieldValue?.text?.length ?: 0)
            if (isDeleteAction) {
                textFieldValue = newText
                onTextChanged(textFieldValue ?: TextFieldValue())
                return@StandardTextField
            }

            val formattedDate = formatExpiryDate(newText.text)
            textFieldValue = TextFieldValue(
                text = formattedDate.take(VALID_INPUT_LENGTH),
                selection = TextRange(formattedDate.length),
            )
            onTextChanged(textFieldValue ?: TextFieldValue())
            if (textFieldValue?.text?.length == VALID_INPUT_LENGTH) {
                onComplete(textFieldValue?.text.orEmpty())
            }
        },
        isError = isError,
        modifier = modifier.onFocusChanged { focusState ->
            if (!focusState.isFocused && textFieldValue != null) {
                // Focus has left the TextField after being focused
                onFocusLost(textFieldValue?.text.orEmpty())
            }
            if (focusState.isFocused) {
                textFieldValue = textFieldValue?.copy(
                    selection = TextRange(textFieldValue?.text?.length ?: 0),
                )
            }
        },
        options = StandardTextFieldOptions(
            inputType = StandardTextFieldOptions.InputType.NUMBER,
            returnType = StandardTextFieldOptions.ReturnType.DONE,
        ),
        onComplete = {
            onComplete(textFieldValue?.text.orEmpty())
        },
    )
}