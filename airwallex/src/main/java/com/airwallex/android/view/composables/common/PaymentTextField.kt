package com.airwallex.android.view.composables.common

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import com.airwallex.android.R
import com.airwallex.android.ui.composables.StandardTextField
import com.airwallex.android.ui.composables.StandardTextFieldOptions

@Suppress("LongParameterList")
@Composable
fun PaymentTextField(
    text: String,
    modifier: Modifier = Modifier,
    onTextChanged: (String) -> Unit,
    onComplete: (String) -> Unit,
    onFocusLost: (String) -> Unit,
    errorText: String? = null,
    options: StandardTextFieldOptions = StandardTextFieldOptions(
        inputType = StandardTextFieldOptions.InputType.NORMAL,
        returnType = StandardTextFieldOptions.ReturnType.DONE,
    ),
) {
    var showClearButton by remember { mutableStateOf(false) }
    var localText by remember { mutableStateOf(text) }
    var localFocusState by remember { mutableStateOf<FocusState>(FocusState.Initial) }

    StandardTextField(
        hint = "",
        text = localText,
        onTextChanged = { newText ->
            localText = newText
            showClearButton = localText.isNotEmpty()
            onTextChanged(localText)
        },
        modifier = modifier.onFocusChanged { focusState ->
            if (focusState.isFocused) {
                localFocusState = FocusState.Focused
                if (localText.isNotEmpty()) {
                    showClearButton = true
                }
            } else {
                // Focus has left the TextField after being focused
                if (localFocusState !is FocusState.Initial) {
                    onFocusLost(localText)
                }
                showClearButton = false
                localFocusState = FocusState.Unfocused
            }
        },
        errorText = errorText,
        options = options,
        onComplete = {
            onComplete(localText)
        },
        trailingAccessory = {
            if (showClearButton) {
                IconButton(
                    onClick = {
                        showClearButton = false
                        localText = ""
                        onTextChanged(localText)
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
    )
}