package com.airwallex.android.view.composables.card

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.airwallex.android.R
import com.airwallex.android.core.CardBrand
import com.airwallex.android.core.model.CardScheme
import com.airwallex.android.core.util.CardUtils.formatCardNumber
import com.airwallex.android.core.util.CardUtils.getPossibleCardBrand
import com.airwallex.android.ui.composables.StandardTextField
import com.airwallex.android.ui.composables.StandardTextFieldOptions
import com.airwallex.android.view.composables.common.FocusState
import com.airwallex.android.view.util.toSupportedIcons
import com.airwallex.risk.AirwallexRisk

@Suppress("LongMethod", "LongParameterList")
@Composable
fun CardNumberTextField(
    cardSchemes: List<CardScheme>,
    onValueChange: (TextFieldValue, CardBrand) -> Unit,
    onComplete: (String) -> Unit,
    onFocusLost: (String) -> Unit,
    modifier: Modifier = Modifier,
    initialValue: String = "",
    isError: Boolean = false,
    shape: Shape = OutlinedTextFieldDefaults.shape,
) {
    var showClearButton by remember { mutableStateOf(false) }
    var textFieldValue by remember(initialValue) {
        mutableStateOf(TextFieldValue(text = initialValue, selection = TextRange(initialValue.length)))
    }
    var localFocusState by remember { mutableStateOf<FocusState>(FocusState.Initial) }
    var brand by remember(initialValue) {
        mutableStateOf(getPossibleCardBrand(initialValue, shouldNormalize = true))
    }

    StandardTextField(
        hint = stringResource(R.string.airwallex_card_number_placeholder),
        text = textFieldValue,
        onTextChanged = { newText ->
            brand = getPossibleCardBrand(newText.text, shouldNormalize = true)
            val formattedText = formatCardNumber(newText.text, brand)
            textFieldValue = TextFieldValue(
                text = formattedText,
                selection = TextRange(formattedText.length),
            )
            showClearButton = textFieldValue.text.isNotEmpty()
            onValueChange(textFieldValue, brand)
            if (textFieldValue.text.length == brand.lengths.max() + brand.spacingPattern.size - 1) {
                onComplete(textFieldValue.text)
                showClearButton = false
            }
        },
        isError = isError,
        modifier = modifier.onFocusChanged { focusState ->
            if (focusState.isFocused) {
                AirwallexRisk.log(event = "input_card_number", screen = "page_create_card")
                localFocusState = FocusState.Focused
                textFieldValue = textFieldValue.copy(selection = TextRange(textFieldValue.text.length))
                if (textFieldValue.text.isNotEmpty()) {
                    showClearButton = true
                }
            } else {
                if (localFocusState !is FocusState.Initial) {
                    onFocusLost(textFieldValue.text)
                }
                showClearButton = false
                localFocusState = FocusState.Unfocused
            }
        }
            .semantics { testTagsAsResourceId = true }
            .testTag("card-number-text-field"),
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

                CardBrandTrailingAccessory(
                    icons = cardSchemes.toSupportedIcons(),
                    brand = brand,
                    displayAllSchemes = localFocusState is FocusState.Initial || textFieldValue.text.isBlank(),
                    modifier = Modifier
                        .size(width = 28.dp, height = 19.dp)
                        .padding(horizontal = 2.dp),
                )
            }
        },
        shape = shape,
    )
}