package com.airwallex.android.view.composables.schema

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
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
import com.airwallex.android.view.PaymentFlowListener
import com.airwallex.android.view.SchemaPaymentViewModel
import com.airwallex.android.view.util.AnalyticsConstants.PAYMENT_METHOD
import com.airwallex.android.view.util.AnalyticsConstants.TAP_PAY_BUTTON
import kotlinx.coroutines.launch

@Suppress("ComplexMethod", "LongMethod", "LongParameterList")
@Composable
internal fun SchemaSection(
    session: AirwallexSession,
    airwallex: Airwallex,
    type: AvailablePaymentMethodType,
    paymentFlowListener: PaymentFlowListener,
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
    var schemaData by remember { mutableStateOf<SchemaPaymentViewModel.SchemaData?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isValidated by remember { mutableStateOf(false) }

    ScreenView { schemaPaymentViewModel.trackScreenViewed(type.name) }

    LaunchedEffect(type) {
        val cachedResult = schemaPaymentViewModel.retrieveSchemaDataFromCache(type)
        if (cachedResult != null) {
            schemaData = cachedResult
        } else {
            isLoading = true
            schemaPaymentViewModel.loadSchemaFields(type)
                .onSuccess { data -> schemaData = data }
                .onFailure { exception ->
                    paymentFlowListener.onError(exception, airwallex.activity)
                }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        schemaPaymentViewModel.paymentResult.collect { status ->
            paymentFlowListener.onLoadingStateChanged(false, airwallex.activity)
            paymentFlowListener.onPaymentResult(status)
        }
    }
    Column {
        if (!isLoading) {
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
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = AirwallexColor.theme()
                )
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
                color = AirwallexColor.textPrimary(),
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
                        schemaData = cachedResult
                    } else {
                        isLoading = true
                        schemaPaymentViewModel.loadSchemaFields(type)
                            .onSuccess { data -> schemaData = data }
                            .onFailure { exception ->
                                paymentFlowListener.onError(exception, airwallex.activity)
                                isLoading = false
                                return@launch
                            }
                        isLoading = false
                    }

                    // BE will need to make sure no schema available is null. Currently in certain cases it is possible to be null.
                    if (schemaData == null || schemaData?.fields?.isEmpty() == true) {
                        // No fields to validate
                        onDirectPayOperation(type, schemaPaymentViewModel, paymentFlowListener, airwallex)
                    } else {
                        validateFields?.invoke()
                        if (isValidated) {
                            val paymentMethod = schemaData?.paymentMethod
                            val typeInfo = schemaData?.typeInfo
                            if (paymentMethod == null || typeInfo == null) {
                                onDirectPayOperation(type, schemaPaymentViewModel, paymentFlowListener, airwallex)
                            } else {
                                onPayWithFieldsOperation(
                                    paymentMethod,
                                    typeInfo,
                                    schemaPaymentViewModel.appendParamsToMapForSchemaSubmission(fieldsToSubmit),
                                    schemaPaymentViewModel,
                                    paymentFlowListener,
                                    airwallex
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
    paymentFlowListener: PaymentFlowListener,
    airwallex: Airwallex,
) {
    paymentFlowListener.onLoadingStateChanged(true, airwallex.activity)
    AnalyticsLogger.logAction(TAP_PAY_BUTTON, mapOf(PAYMENT_METHOD to type.name))
    viewModel.checkoutWithSchema(type)
}

@Suppress("LongParameterList")
private fun onPayWithFieldsOperation(
    paymentMethod: PaymentMethod,
    info: PaymentMethodTypeInfo,
    fieldMap: Map<String, String>,
    viewModel: SchemaPaymentViewModel,
    paymentFlowListener: PaymentFlowListener,
    airwallex: Airwallex,
) {
    paymentFlowListener.onLoadingStateChanged(true, airwallex.activity)
    AnalyticsLogger.logAction(TAP_PAY_BUTTON, mapOf(PAYMENT_METHOD to info.name.orEmpty()))
    viewModel.checkoutWithSchema(
        paymentMethod = paymentMethod,
        additionalInfo = fieldMap,
        typeInfo = info
    )
}