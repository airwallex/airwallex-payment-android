package com.airwallex.android.view.composables.schema

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airwallex.android.R
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodTypeInfo
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.ui.composables.AirwallexTypography
import com.airwallex.android.ui.composables.StandardSolidButton
import com.airwallex.android.ui.composables.StandardText
import com.airwallex.android.view.PaymentMethodsViewModel

@Composable
internal fun SchemaSection(
    viewModel: PaymentMethodsViewModel,
    type: AvailablePaymentMethodType,
    onDirectPay: (AvailablePaymentMethodType) -> Unit,
    onPayWithFields: (PaymentMethod, PaymentMethodTypeInfo, Map<String, String>) -> Unit,
    onLoading: (Boolean) -> Unit,
) {
    var fieldsToSubmit by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var validateFields: (() -> Unit)? by remember { mutableStateOf(null) }
    var schemaData by remember { mutableStateOf<PaymentMethodsViewModel.SchemaData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isValidated by remember { mutableStateOf(false) }

    LaunchedEffect(type) {
        try {
            isLoading = true
            schemaData = viewModel.loadSchemaFields(type)
        } finally {
            isLoading = false
        }
    }

    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        if (isLoading) {
            onLoading(true)
        } else {
            onLoading(false)
            schemaData?.let {
                if (it.fields.isNotEmpty()) {
                    SchemaFieldsSection(
                        fields = it.fields,
                        banks = it.banks,
                        onFieldsValidated = { fields ->
                            fieldsToSubmit = fields
                            isValidated = true
                        },
                        onValidationRequired = { callBack ->
                            validateFields = callBack
                        },
                    )
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.airwallex_ic_redirect),
                contentDescription = "redirect",
                modifier = Modifier.size(
                    width = 48.dp,
                    height = 40.dp,
                ),
            )

            Spacer(modifier = Modifier.width(8.dp))

            StandardText(
                text = stringResource(id = R.string.airwallex_schema_payment_redirect_message),
                color = AirwallexColor.TextPrimary,
                typography = AirwallexTypography.Body200,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        StandardSolidButton(
            text = viewModel.schemaButtonTitle,
            onClick = {
                if (schemaData?.fields?.isEmpty() == true) {
                    // No fields to validate
                    onDirectPay(type)
                } else {
                    validateFields?.invoke()
                    if (isValidated) {
                        val paymentMethod = schemaData?.paymentMethod
                        val typeInfo = schemaData?.typeInfo
                        if (paymentMethod == null || typeInfo == null) {
                            onDirectPay(type)
                        } else {
                            onPayWithFields(paymentMethod, typeInfo, viewModel.appendParamsToMapForSchemaSubmission(fieldsToSubmit))
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}