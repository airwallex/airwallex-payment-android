package com.airwallex.android.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airwallex.android.ui.R

@Suppress("LongParameterList")
@Composable
fun StandardSearchBar(
    text: String,
    onTextChanged: (String?) -> Unit,
    modifier: Modifier = Modifier,
    typeface: TextStyle = AirwallexTypography.Body200.toComposeTextStyle(),
    textColorStyle: Color = AirwallexColor.TextPrimary,
    backgroundColorStyle: Color = MaterialTheme.colorScheme.secondaryContainer,
    hint: String = "",
    singleLine: Boolean = true,
) {
    StandardSearchBar(
        value = text,
        onValueChange = onTextChanged,
        modifier = modifier,
        textStyle = typeface,
        textColor = textColorStyle,
        backgroundColor = backgroundColorStyle,
        hint = hint,
        singleLine = singleLine,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongParameterList", "LongMethod")
@Composable
fun StandardSearchBar(
    value: String,
    onValueChange: (String?) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle,
    textColor: Color,
    backgroundColor: Color,
    hint: String = "",
    enabled: Boolean = true,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val activeColor = MaterialTheme.colorScheme.primary
    // Note that we are not using the leading and trailing icons as they add additional padding
    // to the decorationBox
    Row(
        modifier = modifier
            .heightIn(min = 40.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier
                .padding(
                    start = 8.dp,
                    top = 8.dp,
                    bottom = 8.dp,
                )
                .size(16.dp),
            painter = painterResource(id = R.drawable.airwallex_ic_search),
            tint = MaterialTheme.colorScheme.secondary,
            contentDescription = null,
        )
        BasicTextField(
            value = value,
            modifier = Modifier.weight(1f),
            onValueChange = onValueChange,
            enabled = true,
            readOnly = false,
            textStyle = mergedTextStyle,
            cursorBrush = SolidColor(activeColor),
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                focusManager.clearFocus()
                keyboardController?.hide()
            }),
            interactionSource = interactionSource,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            decorationBox = @Composable { innerTextField ->
                TextFieldDefaults.DecorationBox(
                    value = value,
                    contentPadding = PaddingValues(
                        start = 8.dp,
                        end = if (value.isEmpty()) 0.dp else 8.dp
                    ),
                    visualTransformation = visualTransformation,
                    innerTextField = innerTextField,
                    placeholder = {
                        StandardText(
                            text = hint,
                            color = AirwallexColor.TextPrimary,
                            typography = AirwallexTypography.Body200,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    },
                    container = {},
                    prefix = null,
                    suffix = null,
                    supportingText = null,
                    singleLine = singleLine,
                    enabled = enabled,
                    isError = false,
                    interactionSource = interactionSource,
                    colors = TextFieldDefaults.colors(
                        unfocusedLabelColor = textColor,
                        focusedLabelColor = textColor,
                        cursorColor = activeColor,
                        focusedTextColor = textColor,
                        focusedLeadingIconColor = textColor,
                        unfocusedLeadingIconColor = textColor,
                        focusedTrailingIconColor = textColor,
                        unfocusedTrailingIconColor = textColor,
                        errorIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    )
                )
            }
        )

        AnimatedVisibility(
            visible = value.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            IconButton(onClick = { onValueChange("") }) {
                Icon(
                    modifier = Modifier
                        .size(36.dp)
                        .padding(9.dp),
                    painter = painterResource(id = R.drawable.airwallex_ic_close),
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = null
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StandardSearchBar_Preview() {
    AirwallexTheme {
        Column(
            modifier = Modifier
                .width(400.dp)
                .background(Color.Gray)
                .padding(24.dp)
        ) {
            var text by remember { mutableStateOf("Hello") }
            StandardSearchBar(
                text = text,
                onTextChanged = { text = it.orEmpty() },
                hint = "Hint",
            )

            Spacer(modifier = Modifier.height(24.dp))

            StandardSearchBar(
                text = text,
                onTextChanged = { text = it.orEmpty() },
                hint = "Hint",
            )
        }
    }
}