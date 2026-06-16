package com.airwallex.android.view.composables.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.ui.composables.AirwallexTypography
import com.airwallex.android.ui.composables.StandardBottomSheet
import com.airwallex.android.ui.composables.StandardPicker
import com.airwallex.android.ui.composables.StandardText
import com.airwallex.android.ui.composables.TextFieldWithPickerButton

@Suppress("LongParameterList")
@Composable
internal fun StateSelectRow(
    modifier: Modifier = Modifier,
    hint: String,
    options: List<Pair<String, String>>,
    default: String?,
    onOptionSelected: (Pair<String, String>) -> Unit,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(8.dp),
    isError: Boolean = false,
) {
    val focusManager = LocalFocusManager.current
    var selectedValue by remember { mutableStateOf(default) }
    var selectedLabel by remember { mutableStateOf(options.firstOrNull { it.first == selectedValue }?.second) }

    LaunchedEffect(default, options) {
        selectedValue = default
        selectedLabel = options.firstOrNull { it.first == selectedValue }?.second
    }

    Box(modifier = modifier) {
        StandardBottomSheet<Unit>(
            sheetContent = { sheetDetails ->
                Column {
                    Spacer(modifier = Modifier.height(24.dp))

                    StandardText(
                        text = hint,
                        color = AirwallexColor.textPrimary,
                        typography = AirwallexTypography.Title200,
                        modifier = Modifier.padding(24.dp),
                    )

                    StandardPicker(
                        hint = "",
                        options = options,
                        searchFilter = { content, search ->
                            search.isBlank() || content.second.contains(search, ignoreCase = true)
                        },
                        content = { content, _ ->
                            StateItem(
                                label = content.second,
                                isSelected = selectedValue == content.first,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    sheetDetails.onDismissRequested {}
                                    selectedLabel = content.second
                                    selectedValue = content.first
                                    onOptionSelected(content)
                                },
                            )
                        },
                    )
                }
            },
            onDismiss = {
                focusManager.clearFocus()
            },
        ) { onPresentRequested ->
            TextFieldWithPickerButton(
                hint = hint,
                title = selectedLabel,
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
                onPresentRequested = { onPresentRequested(Unit) },
                shape = shape,
                isError = isError,
            )
        }
    }
}

@Composable
@Preview
private fun StateSelectRowPreview() {
    StateSelectRow(
        hint = "State",
        options = listOf(
            "CA" to "California",
            "NY" to "New York",
        ),
        default = "CA",
        onOptionSelected = {},
    )
}
