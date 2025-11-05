package com.airwallex.android.view.composables.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.airwallex.android.R
import com.airwallex.android.core.CardBrand
import com.airwallex.android.ui.composables.StandardTextField
import com.airwallex.android.ui.composables.StandardTextFieldOptions
import com.airwallex.android.view.composables.common.FocusState
import com.airwallex.risk.AirwallexRisk

private const val AMEX_CVV_LENGTH = 4
private const val DEFAULT_CVV_LENGTH = 3

@Suppress("LongMethod", "LongParameterList")
@Composable
fun CardCvcTextField(
    modifier: Modifier = Modifier,
    cardBrand: CardBrand,
    initialValue: String = "",
    onTextChanged: (TextFieldValue) -> Unit,
    onComplete: (String) -> Unit,
    onFocusLost: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null,
    shape: Shape = OutlinedTextFieldDefaults.shape,
) {
    var showClearButton by remember { mutableStateOf(false) }
    var textFieldValue by remember(initialValue) {
        mutableStateOf(TextFieldValue(text = initialValue, selection = TextRange(initialValue.length)))
    }
    var localFocusState by remember { mutableStateOf<FocusState>(FocusState.Initial) }

    StandardTextField(
        hint = stringResource(R.string.airwallex_cvc_hint),
        text = textFieldValue,
        onTextChanged = { newText ->
            val cvcLength = when (cardBrand) {
                CardBrand.Amex -> AMEX_CVV_LENGTH
                else -> DEFAULT_CVV_LENGTH
            }
            val newTextLength = newText.text.length
            val newCursorPosition = if (newTextLength > cvcLength) {
                cvcLength
            } else {
                newTextLength
            }
            textFieldValue = TextFieldValue(
                text = newText.text.take(cvcLength),
                selection = TextRange(newCursorPosition),
            )
            showClearButton = textFieldValue.text.isNotEmpty()
            onTextChanged(newText)
            if (textFieldValue.text.length == cvcLength) {
                onComplete(textFieldValue.text)
                showClearButton = false
            }
        },
        isError = isError,
        errorText = errorMessage,
        modifier = modifier
            .onFocusEvent { focusState ->
                if (focusState.hasFocus && textFieldValue.text.isNotEmpty()) {
                    showClearButton = true
                    AirwallexRisk.log(event = "input_card_cvc", screen = "page_create_card")
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
            .semantics {
                testTagsAsResourceId = true
                contentType = ContentType.CreditCardSecurityCode
            }
            .testTag("card-cvc-text-field"),
        options = StandardTextFieldOptions(
            inputType = StandardTextFieldOptions.InputType.NUMBER,
            returnType = StandardTextFieldOptions.ReturnType.DONE,
        ),
        onComplete = {
            onComplete(textFieldValue.text)
        },
        trailingAccessory = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp),
            ) {
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

                Image(
                    painter = painterResource(id = R.drawable.airwallex_ic_cvv),
                    contentDescription = "card",
                    modifier = Modifier.padding(horizontal = 2.dp),
                )
            }
        },
        shape = shape,
    )
}