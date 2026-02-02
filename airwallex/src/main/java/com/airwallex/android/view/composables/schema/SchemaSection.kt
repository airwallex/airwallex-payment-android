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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airwallex.android.R
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodTypeInfo
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.ui.composables.AirwallexTypography
import com.airwallex.android.ui.composables.ScreenView
import com.airwallex.android.ui.composables.StandardSolidButton
import com.airwallex.android.ui.composables.StandardText
import com.airwallex.android.view.PaymentMethodsViewModel
import com.airwallex.android.view.PaymentOperationsViewModel
import com.airwallex.android.view.SchemaPaymentViewModel
import com.airwallex.android.view.composables.card.PaymentOperation
import com.airwallex.android.view.composables.card.PaymentOperationResult
import kotlinx.coroutines.launch

@Suppress("ComplexMethod", "LongMethod", "LongParameterList")
@Composable
internal fun SchemaSection(
    session: AirwallexSession,
    airwallex: Airwallex,
    type: AvailablePaymentMethodType,
    onLoading: (Boolean) -> Unit,
    onOperationStart: (PaymentOperation) -> Unit,
    onOperationDone: (PaymentOperationResult) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val schemaPaymentViewModel: SchemaPaymentViewModel = viewModel(
        factory = SchemaPaymentViewModel.Factory(
            application = airwallex.activity.application,
            airwallex = airwallex,
            session = session
        ),
        viewModelStoreOwner = airwallex.activity
    )

    var fieldsToSubmit by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var validateFields: (() -> Unit)? by remember { mutableStateOf(null) }
    var schemaData by remember { mutableStateOf<PaymentMethodsViewModel.SchemaData?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isValidated by remember { mutableStateOf(false) }

    ScreenView { schemaPaymentViewModel.trackScreenViewed(type.name) }

    LaunchedEffect(type) {
        val cachedResult = schemaPaymentViewModel.retrieveSchemaDataFromCache(type)
        if (cachedResult != null) {
            cachedResult
                .onSuccess { data -> schemaData = data }
                .onFailure { exception ->
                    onOperationDone(
                        PaymentOperationResult.Error(
                            exception.message ?: "Failed to load payment fields",
                            exception
                        )
                    )
                }
        } else {
            isLoading = true
            schemaPaymentViewModel.loadSchemaFields(type)
                .onSuccess { data -> schemaData = data }
                .onFailure { exception ->
                    onOperationDone(
                        PaymentOperationResult.Error(
                            exception.message ?: "Failed to load payment fields",
                            exception
                        )
                    )
                }
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

        Spacer(modifier = Modifier.height(24.dp))

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
            text = stringResource(schemaPaymentViewModel.ctaRes),
            onClick = {
                coroutineScope.launch {
                    val cachedResult = schemaPaymentViewModel.retrieveSchemaDataFromCache(type)
                    if (cachedResult != null) {
                        cachedResult
                            .onSuccess { data -> schemaData = data }
                            .onFailure { exception ->
                                onOperationDone(
                                    PaymentOperationResult.Error(
                                        exception.message ?: "Failed to load payment fields",
                                        exception
                                    )
                                )
                                return@launch
                            }
                    } else {
                        isLoading = true
                        schemaPaymentViewModel.loadSchemaFields(type)
                            .onSuccess { data -> schemaData = data }
                            .onFailure { exception ->
                                onOperationDone(
                                    PaymentOperationResult.Error(
                                        exception.message ?: "Failed to load payment fields",
                                        exception
                                    )
                                )
                                isLoading = false
                                return@launch
                            }
                        isLoading = false
                    }

                    // BE will need to make sure no schema available is null. Currently in certain cases it is possible to be null.
                    if (schemaData == null || schemaData?.fields?.isEmpty() == true) {
                        // No fields to validate
                        onDirectPayOperation(type, schemaPaymentViewModel, onOperationStart, onOperationDone)
                    } else {
                        validateFields?.invoke()
                        if (isValidated) {
                            val paymentMethod = schemaData?.paymentMethod
                            val typeInfo = schemaData?.typeInfo
                            if (paymentMethod == null || typeInfo == null) {
                                onDirectPayOperation(type, schemaPaymentViewModel, onOperationStart, onOperationDone)
                            } else {
                                onPayWithFieldsOperation(
                                    paymentMethod,
                                    typeInfo,
                                    schemaPaymentViewModel.appendParamsToMapForSchemaSubmission(fieldsToSubmit),
                                    schemaPaymentViewModel,
                                    onOperationStart,
                                    onOperationDone
                                )
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(36.dp))
    }
}

private fun onDirectPayOperation(
    type: AvailablePaymentMethodType,
    viewModel: SchemaPaymentViewModel,
    onOperationStart: (PaymentOperation) -> Unit,
    onOperationDone: (PaymentOperationResult) -> Unit
) {
    val operation = PaymentOperation.DirectPay(type)
    onOperationStart(operation)
    AnalyticsLogger.logAction("tap_pay_button", mapOf("payment_method" to type.name))
    viewModel.checkoutWithSchema(type) { status ->
        onOperationDone(PaymentOperationResult.DirectPay(status))
    }
}

private fun onPayWithFieldsOperation(
    paymentMethod: PaymentMethod,
    info: PaymentMethodTypeInfo,
    fieldMap: Map<String, String>,
    viewModel: SchemaPaymentViewModel,
    onOperationStart: (PaymentOperation) -> Unit,
    onOperationDone: (PaymentOperationResult) -> Unit
) {
    val operation = PaymentOperation.PayWithFields(paymentMethod, info, fieldMap)
    onOperationStart(operation)
    AnalyticsLogger.logAction("tap_pay_button", mapOf("payment_method" to info.name.orEmpty()))
    viewModel.checkoutWithSchema(
        paymentMethod = paymentMethod,
        additionalInfo = fieldMap,
        typeInfo = info
    ) { status ->
        onOperationDone(PaymentOperationResult.PayWithFields(status))
    }
}