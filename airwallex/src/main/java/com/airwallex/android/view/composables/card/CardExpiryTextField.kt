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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.airwallex.android.R
import com.airwallex.android.ui.composables.StandardTextField
import com.airwallex.android.ui.composables.StandardTextFieldOptions
import com.airwallex.android.view.composables.common.FocusState
import com.airwallex.android.view.util.ExpiryDateUtils.VALID_INPUT_LENGTH
import com.airwallex.android.view.util.ExpiryDateUtils.formatExpiryDate
import com.airwallex.android.view.util.ExpiryDateUtils.formatExpiryDateWhenDeleting
import com.airwallex.risk.AirwallexRisk

@Suppress("LongMethod", "LongParameterList")
@Composable
fun CardExpiryTextField(
    modifier: Modifier = Modifier,
    initialValue: String = "",
    onTextChanged: (TextFieldValue) -> Unit,
    onComplete: (String) -> Unit,
    onFocusLost: (String) -> Unit,
    isError: Boolean = false,
    shape: Shape = OutlinedTextFieldDefaults.shape,
) {
    var showClearButton by remember { mutableStateOf(false) }
    var textFieldValue by remember(initialValue) {
        mutableStateOf(TextFieldValue(text = initialValue, selection = TextRange(initialValue.length)))
    }
    var localFocusState by remember { mutableStateOf<FocusState>(FocusState.Initial) }

    StandardTextField(
        hint = stringResource(R.string.airwallex_expires_hint),
        text = textFieldValue,
        onTextChanged = { newText ->
            val isDeleteAction = newText.text.length < (textFieldValue.text.length)
            if (isDeleteAction) {
                val formattedText = formatExpiryDateWhenDeleting(newText.text)
                textFieldValue = TextFieldValue(
                    text = formattedText.take(VALID_INPUT_LENGTH),
                    selection = TextRange(formattedText.length),
                )
                showClearButton = textFieldValue.text.isNotEmpty()
                onTextChanged(textFieldValue)
                return@StandardTextField
            }

            val formattedDate = formatExpiryDate(newText.text)
            textFieldValue = TextFieldValue(
                text = formattedDate.take(VALID_INPUT_LENGTH),
                selection = TextRange(formattedDate.length),
            )
            showClearButton = textFieldValue.text.isNotEmpty()
            onTextChanged(textFieldValue)
            if (textFieldValue.text.length == VALID_INPUT_LENGTH) {
                onComplete(textFieldValue.text)
                showClearButton = false
            }
        },
        isError = isError,
        modifier = modifier
            .onFocusEvent { focusState ->
                if (focusState.hasFocus && textFieldValue.text.isNotEmpty()) {
                    showClearButton = true
                    AirwallexRisk.log(event = "input_card_expiry", screen = "page_create_card")
                }
            }
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    localFocusState = FocusState.Focused
                    textFieldValue = textFieldValue.copy(selection = TextRange(textFieldValue.text.length))
                } else {
                    if (localFocusState !is FocusState.Initial) {
                        // Focus has left the TextField after being focused
                        onFocusLost(textFieldValue.text)
                    }
                    showClearButton = false
                    localFocusState = FocusState.Unfocused
                }
            }
            .semantics { testTagsAsResourceId = true }
            .testTag("card-expiry-text-field"),
        options = StandardTextFieldOptions(
            inputType = StandardTextFieldOptions.InputType.NUMBER,
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