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
import com.airwallex.android.core.CardBrand
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.util.CardUtils.formatCardNumber
import com.airwallex.android.core.util.CardUtils.getPossibleCardBrand
import com.airwallex.android.ui.composables.StandardTextField
import com.airwallex.android.ui.composables.StandardTextFieldOptions

@Composable
fun CardNumberTextField(
    type: AvailablePaymentMethodType,
    onValueChange: (TextFieldValue, CardBrand) -> Unit,
    onComplete: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue()) }
    var brand by remember { mutableStateOf(CardBrand.Unknown) }

    StandardTextField(
        hint = stringResource(R.string.airwallex_card_number_placeholder),
        text = textFieldValue,
        onTextChanged = { newText ->
            brand = getPossibleCardBrand(newText.text, shouldNormalize = true)
            val formattedText = formatCardNumber(newText.text, brand, onComplete)
            textFieldValue = TextFieldValue(
                text = formattedText,
                selection = TextRange(formattedText.length),
            )
            onValueChange(textFieldValue, brand)
        },
        isError = isError,
        modifier = modifier,
        options = StandardTextFieldOptions(
            inputType = StandardTextFieldOptions.InputType.NUMBER,
            returnType = StandardTextFieldOptions.ReturnType.DONE,
        ),
        trailingAccessory = {
            type.cardSchemes?.let { schemes ->
                CardBrandTrailingAccessory(
                    schemes = schemes,
                    brand = brand,
                    displayAllSchemes = textFieldValue.text.isBlank(),
                )
            }
        },
    )
}