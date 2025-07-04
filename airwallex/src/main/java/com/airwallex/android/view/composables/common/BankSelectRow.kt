package com.airwallex.android.view.composables.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airwallex.android.R
import com.airwallex.android.core.model.Bank
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.ui.composables.AirwallexTypography
import com.airwallex.android.ui.composables.StandardBottomSheet
import com.airwallex.android.ui.composables.StandardPicker
import com.airwallex.android.ui.composables.StandardText
import com.airwallex.android.ui.composables.TextFieldWithPickerButton

@Suppress("LongParameterList")
@Composable
internal fun BankSelectRow(
    modifier: Modifier = Modifier,
    options: List<Bank>,
    default: String?,
    onOptionSelected: (Bank) -> Unit,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(8.dp),
    errorText: String? = null,
) {
    val focusManager = LocalFocusManager.current
    var selectedValue by remember { mutableStateOf(default) }
    var selectedLabel by remember { mutableStateOf(options.firstOrNull { it.name == selectedValue }?.displayName) }

    StandardBottomSheet<Unit>(
        sheetContent = { sheetDetails ->
            Column {
                Spacer(modifier = Modifier.height(24.dp))

                StandardText(
                    text = stringResource(id = R.string.airwallex_select_your_bank),
                    color = AirwallexColor.TextPrimary,
                    typography = AirwallexTypography.Title200,
                    modifier = Modifier.padding(24.dp),
                )

                StandardPicker(
                    hint = "",
                    options = options,
                    searchFilter = { content, search ->
                        search.isBlank() || content.displayName.contains(search, ignoreCase = true)
                    },
                    content = { content, _ ->
                        BankItem(
                            bank = content,
                            isSelected = selectedValue == content.name,
                            modifier = Modifier
                                .padding(horizontal = 24.dp)
                                .fillMaxWidth(),
                            onClick = {
                                sheetDetails.onDismissRequested {}
                                selectedLabel = content.displayName
                                selectedValue = content.name
                                onOptionSelected(content)
                            },
                        )
                    },
                )
            }
        },
        onDismiss = {
            // Clear the focus when the picker sheet is dismissed.
            focusManager.clearFocus()
        },
    ) { onPresentRequested ->
        TextFieldWithPickerButton(
            hint = stringResource(id = R.string.airwallex_select_your_bank),
            title = selectedLabel,
            modifier = modifier,
            enabled = enabled,
            onPresentRequested = { onPresentRequested(Unit) },
            shape = shape,
            errorText = errorText,
        )
    }
}

@Composable
@Preview
private fun BankSelectRowPreview() {
    BankSelectRow(
        options = listOf(
            Bank(
                name = "Chase",
                displayName = "Chase Bank",
                resources = null,
            ),
            Bank(
                name = "Chase",
                displayName = "Chase Bank",
                resources = null,
            ),
        ),
        default = "Chase",
        onOptionSelected = {},
    )
}