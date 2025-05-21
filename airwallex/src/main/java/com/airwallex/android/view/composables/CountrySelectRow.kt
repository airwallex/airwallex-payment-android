package com.airwallex.android.view.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.ui.composables.AirwallexTypography
import com.airwallex.android.ui.composables.StandardBottomSheet
import com.airwallex.android.ui.composables.StandardText
import com.airwallex.android.ui.composables.TextFieldWithPickerButton
import com.airwallex.android.view.util.getFlagEmoji

@Suppress("LongParameterList")
@Composable
internal fun CountrySelectRow(
    modifier: Modifier = Modifier,
    options: List<Pair<String, String>>,
    label: String,
    default: String?,
    onOptionSelected: (Pair<String, String>) -> Unit,
    enabled: Boolean = true,
) {
    val focusManager = LocalFocusManager.current
    var selectedValue by remember { mutableStateOf(default) }
    selectedValue = default
    var selectedLabel by remember { mutableStateOf<String?>(null) }
    selectedLabel = options.firstOrNull { it.second == selectedValue }?.first ?: selectedLabel

    StandardBottomSheet<Unit>(
        sheetContent = { sheetDetails ->
            Column {
                Spacer(modifier = Modifier.height(24.dp))

                StandardText(
                    text = label,
                    color = AirwallexColor.TextPrimary,
                    typography = AirwallexTypography.Title200,
                    modifier = Modifier.padding(24.dp),
                )

                CountryPicker(
                    hint = "",
                    options = options,
                    searchFilter = { content, search ->
                        search.isBlank() || content.first.contains(search, ignoreCase = true)
                    },
                    content = { content, _ ->
                        CountryItem(
                            countryName = content.first,
                            countryCode = content.second,
                            isSelected = selectedValue == content.second,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                sheetDetails.onDismissRequested {}
                                selectedLabel = content.first
                                selectedValue = content.second
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
            hint = label,
            title = "${getFlagEmoji(selectedValue.orEmpty())}  ${selectedLabel.orEmpty()}",
            modifier = modifier,
            enabled = enabled,
            onPresentRequested = { onPresentRequested(Unit) },
        )
    }
}