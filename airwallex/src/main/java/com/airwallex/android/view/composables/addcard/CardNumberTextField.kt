package com.airwallex.android.view.composables.addcard

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.airwallex.android.R
import com.airwallex.android.core.CardBrand
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.util.CardUtils.formatCardNumber
import com.airwallex.android.core.util.CardUtils.getPossibleCardBrand
import com.airwallex.android.ui.composables.StandardTextField
import com.airwallex.android.ui.composables.StandardTextFieldOptions
import com.airwallex.android.view.util.toSupportedIcons

@Composable
fun CardNumberTextField(
    type: AvailablePaymentMethodType,
    onValueChange: (TextFieldValue, CardBrand) -> Unit,
    onComplete: (String) -> Unit,
    onFocusLost: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
) {
    var showClearButton by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf<TextFieldValue?>(null) }
    var brand by remember { mutableStateOf(CardBrand.Unknown) }

    StandardTextField(
        hint = stringResource(R.string.airwallex_card_number_placeholder),
        text = textFieldValue ?: TextFieldValue(),
        onTextChanged = { newText ->
            brand = getPossibleCardBrand(newText.text, shouldNormalize = true)
            val formattedText = formatCardNumber(newText.text, brand)
            textFieldValue = TextFieldValue(
                text = formattedText,
                selection = TextRange(formattedText.length),
            )
            showClearButton = textFieldValue?.text?.isNotEmpty() == true
            onValueChange(textFieldValue ?: TextFieldValue(), brand)
            if (textFieldValue?.text?.length == brand.lengths.max() + brand.spacingPattern.size - 1) {
                onComplete(textFieldValue?.text.orEmpty())
                showClearButton = false
            }
        },
        isError = isError,
        modifier = modifier.onFocusChanged { focusState ->
            if (focusState.isFocused) {
                textFieldValue = textFieldValue?.copy(selection = TextRange(textFieldValue?.text?.length ?: 0)) ?: TextFieldValue()
                if (textFieldValue?.text?.isNotEmpty() == true) {
                    showClearButton = true
                }
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp),
            ) {
                if (showClearButton) {
                    IconButton(
                        onClick = {
                            showClearButton = false
                            textFieldValue = TextFieldValue()
                            onValueChange(TextFieldValue(), brand)
                        },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.airwallex_ic_clear),
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = "Clear",
                        )
                    }
                }

                type.cardSchemes?.toSupportedIcons()?.let { icons ->
                    CardBrandTrailingAccessory(
                        icons = icons,
                        brand = brand,
                        displayAllSchemes = textFieldValue == null || textFieldValue?.text?.isBlank() == true,
                        modifier = Modifier
                            .size(width = 28.dp, height = 19.dp)
                            .padding(horizontal = 2.dp),
                    )
                }
            }
        },
    )
}