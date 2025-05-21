package com.airwallex.android.view.composables.addcard

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.airwallex.android.R
import com.airwallex.android.ui.composables.StandardTextField
import com.airwallex.android.ui.composables.StandardTextFieldOptions

@Suppress("LongParameterList")
@Composable
fun BillingTextField(
    text: String,
    modifier: Modifier = Modifier,
    onTextChanged: (TextFieldValue) -> Unit,
    onComplete: (String) -> Unit,
    onFocusLost: (String) -> Unit,
    isError: Boolean = false,
    enabled: Boolean = true,
    hint: String = "",
    shape: Shape = OutlinedTextFieldDefaults.shape,
) {
    var showClearButton by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf(TextFieldValue(text)) }

    LaunchedEffect(text) {
        // Update the text when clicking same address
        textFieldValue = textFieldValue.copy(text = text)
    }

    StandardTextField(
        hint = hint,
        text = textFieldValue,
        onTextChanged = { newText ->
            textFieldValue = textFieldValue.copy(
                text = newText.text,
                selection = TextRange(newText.text.length),
            )
            showClearButton = textFieldValue.text.isNotEmpty()
            onTextChanged(newText)
        },
        modifier = modifier.onFocusChanged { focusState ->
            if (focusState.isFocused) {
                textFieldValue = textFieldValue.copy(selection = TextRange(textFieldValue.text.length))
                if (textFieldValue.text.isNotEmpty()) {
                    showClearButton = true
                }
            } else {
                showClearButton = false
                // Focus has left the TextField after being focused
                onFocusLost(textFieldValue.text)
            }
        },
        enabled = enabled,
        isError = isError,
        options = StandardTextFieldOptions(
            inputType = StandardTextFieldOptions.InputType.NORMAL,
            returnType = StandardTextFieldOptions.ReturnType.DONE,
        ),
        onComplete = {
            onComplete(textFieldValue.text)
        },
        trailingAccessory = {
            if (showClearButton) {
                IconButton(
                    onClick = {
                        showClearButton = false
                        textFieldValue = TextFieldValue()
                        onTextChanged(textFieldValue)
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