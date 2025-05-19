package com.airwallex.android.view.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
    isError: Boolean = false,
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue()) }

    StandardTextField(
        hint = stringResource(R.string.airwallex_expires_hint),
        text = textFieldValue,
        onTextChanged = { newText ->
            val isDeleteAction = newText.text.length < textFieldValue.text.length
            if (isDeleteAction) {
                textFieldValue = newText
                onTextChanged(textFieldValue)
                return@StandardTextField
            }

            val formattedDate = formatExpiryDate(newText.text)
            textFieldValue = textFieldValue.copy(
                text = formattedDate.take(VALID_INPUT_LENGTH),
                selection = TextRange(formattedDate.length),
            )
            onTextChanged(textFieldValue)
            if (textFieldValue.text.length == VALID_INPUT_LENGTH) {
                onComplete(textFieldValue.text)
            }
        },
        isError = isError,
        modifier = modifier,
        options = StandardTextFieldOptions(
            inputType = StandardTextFieldOptions.InputType.NUMBER,
            returnType = StandardTextFieldOptions.ReturnType.DONE,
        ),
    )
}