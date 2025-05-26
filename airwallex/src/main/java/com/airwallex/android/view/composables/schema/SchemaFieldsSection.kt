package com.airwallex.android.view.composables.schema

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airwallex.android.R
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.Bank
import com.airwallex.android.core.model.DynamicSchemaField
import com.airwallex.android.core.model.DynamicSchemaFieldType
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.ui.composables.AirwallexTypography
import com.airwallex.android.ui.composables.StandardText
import com.airwallex.android.view.composables.common.BankSelectRow
import com.airwallex.android.view.composables.common.PaymentTextField
import com.airwallex.android.view.util.isValidDynamicSchemaField

@Composable
internal fun SchemaFieldsSection(
    fields: List<DynamicSchemaField>,
    banks: List<Bank>,
    onFieldsValidated: (Map<String, String>) -> Unit,
    onValidationRequired: ((() -> Unit) -> Unit)? = null,
) {
    val focusManager = LocalFocusManager.current

    val inputMap = remember {
        mutableStateMapOf<String, String>().apply {
            fields.forEach { field ->
                this[field.name] = ""
            }
        }
    }
    val errorMap = remember {
        mutableStateMapOf<String, Boolean?>().apply {
            fields.forEach { field ->
                this[field.name] = null
            }
        }
    }

    val validateFields = {
        val allValid = fields.all { field ->
            val isValid = if (field.type == DynamicSchemaFieldType.BANKS && banks.isEmpty()) {
                true
            } else {
                inputMap[field.name]?.isValidDynamicSchemaField(field.validations) == true
            }
            errorMap[field.name] = !isValid
            isValid
        }
        if (allValid) {
            onFieldsValidated(inputMap)
        }
    }

    DisposableEffect(Unit) {
        // Set up the validation function
        onValidationRequired?.invoke(validateFields)

        // Clean up when the composable is disposed
        onDispose {
            onValidationRequired?.invoke({})
        }
    }

    fields.forEach { field ->
        when (field.type) {
            DynamicSchemaFieldType.STRING -> {
                StandardText(
                    text = field.displayName,
                    textAlign = TextAlign.Left,
                    typography = AirwallexTypography.Body200,
                    color = AirwallexColor.TextPrimary,
                    modifier = Modifier.padding(vertical = 12.dp),
                )

                PaymentTextField(
                    text = inputMap[field.name] ?: "",
                    errorText = if (errorMap[field.name] == true) stringResource(R.string.airwallex_invalid_field, field.displayName.lowercase()) else null,
                    onTextChanged = { inputMap[field.name] = it.text },
                    onFocusLost = { input ->
                        if (input.isValidDynamicSchemaField(field.validations)) {
                            inputMap[field.name] = input
                            errorMap[field.name] = false
                        } else {
                            errorMap[field.name] = true
                        }
                    },
                    onComplete = { input ->
                        if (input.isValidDynamicSchemaField(field.validations)) {
                            inputMap[field.name] = input
                            errorMap[field.name] = false
                        } else {
                            errorMap[field.name] = true
                        }
                        focusManager.moveFocus(FocusDirection.Down)
                    },
                )
            }
            DynamicSchemaFieldType.BANKS -> {
                if (banks.isNotEmpty()) {
                    var selectedBank by remember { mutableStateOf<Bank?>(null) }

                    StandardText(
                        text = field.displayName,
                        textAlign = TextAlign.Left,
                        typography = AirwallexTypography.Body200,
                        color = AirwallexColor.TextPrimary,
                        modifier = Modifier.padding(vertical = 12.dp),
                    )

                    BankSelectRow(
                        options = banks,
                        default = selectedBank?.name,
                        onOptionSelected = { bank ->
                            AnalyticsLogger.logAction("select_bank", mapOf("bankName" to bank.name))
                            selectedBank = bank
                            errorMap[field.name] = false
                        },
                    )

                    if (errorMap[field.name] == true) {
                        Spacer(modifier = Modifier.height(4.dp))

                        StandardText(
                            text = stringResource(id = R.string.airwallex_invalid_field, field.displayName.lowercase()),
                            textAlign = TextAlign.Left,
                            typography = AirwallexTypography.Caption100,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 40.dp),
                        )
                    }
                }
            }
            DynamicSchemaFieldType.ENUM -> {
                // Not showing UI
            }
            DynamicSchemaFieldType.BOOLEAN -> {
                // Not supported yet.
            }
            null -> {
                // No op
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
}