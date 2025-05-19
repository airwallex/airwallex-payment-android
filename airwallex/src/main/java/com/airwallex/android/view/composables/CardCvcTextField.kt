package com.airwallex.android.view.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.airwallex.android.R
import com.airwallex.android.core.CardBrand
import com.airwallex.android.ui.composables.StandardTextField
import com.airwallex.android.ui.composables.StandardTextFieldOptions

private const val AmexCvvLength = 4
private const val DefaultCvvLength = 3

@Composable
fun CardCvcTextField(
    modifier: Modifier = Modifier,
    cardBrand: CardBrand,
    onTextChanged: (TextFieldValue) -> Unit,
    onComplete: (String) -> Unit,
    isError: Boolean = false,
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue()) }

    StandardTextField(
        hint = stringResource(R.string.airwallex_cvc_hint),
        text = textFieldValue,
        onTextChanged = { newText ->
            val cvcLength = when (cardBrand) {
                CardBrand.Amex -> AmexCvvLength
                else -> DefaultCvvLength
            }
            val newTextLength = newText.text.length
            val newCursorPosition = if (newTextLength > cvcLength) {
                cvcLength
            } else {
                newTextLength
            }
            textFieldValue = textFieldValue.copy(
                text = newText.text.take(cvcLength),
                selection = TextRange(newCursorPosition),
            )
            onTextChanged(newText)
            if (textFieldValue.text.length == cvcLength) {
                onComplete(textFieldValue.text)
            }
        },
        isError = isError,
        modifier = modifier,
        options = StandardTextFieldOptions(
            inputType = StandardTextFieldOptions.InputType.NUMBER,
            returnType = StandardTextFieldOptions.ReturnType.DONE,
        ),
        trailingAccessory = {
            Image(
                painter = painterResource(id = R.drawable.airwallex_ic_cvv),
                contentDescription = "card",
                modifier = Modifier.padding(horizontal = 2.dp),
            )
        }
    )
}