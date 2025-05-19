package com.airwallex.android.view.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.airwallex.android.ui.composables.StandardTextField
import com.airwallex.android.ui.composables.StandardTextFieldOptions

@Composable
fun AddCardTextField(
    text: String,
    modifier: Modifier = Modifier,
    onTextChanged: (TextFieldValue) -> Unit,
    onComplete: (String) -> Unit,
    errorText: String? = null,
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(text)) }

    StandardTextField(
        hint = "",
        text = textFieldValue,
        onTextChanged = { newText ->
            textFieldValue = textFieldValue.copy(
                text = newText.text,
                selection = TextRange(newText.text.length),
            )
            onTextChanged(newText)
        },
        modifier = modifier.onFocusChanged { focusState ->
            if (focusState.isFocused) {
                textFieldValue = textFieldValue.copy(
                    selection = TextRange(textFieldValue.text.length),
                )
            }
        },
        errorText = errorText,
        options = StandardTextFieldOptions(
            inputType = StandardTextFieldOptions.InputType.NORMAL,
            returnType = StandardTextFieldOptions.ReturnType.DONE,
        ),
        onComplete = {
            onComplete(textFieldValue.text)
        },
    )
}