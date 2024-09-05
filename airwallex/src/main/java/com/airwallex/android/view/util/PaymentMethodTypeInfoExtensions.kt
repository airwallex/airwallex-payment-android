package com.airwallex.android.view.util

import com.airwallex.android.core.model.AirwallexPaymentRequestFlow
import com.airwallex.android.core.model.DynamicSchemaField
import com.airwallex.android.core.model.PaymentMethodTypeInfo
import com.airwallex.android.core.model.TransactionMode
import com.airwallex.android.view.PaymentMethodsViewModel.Companion.FLOW

fun PaymentMethodTypeInfo.filterRequiredFields(): List<DynamicSchemaField>? {
    return this
        .fieldSchemas
        ?.firstOrNull { schema -> schema.transactionMode == TransactionMode.ONE_OFF }
        ?.fields
        ?.filter { !it.hidden }
}

fun PaymentMethodTypeInfo.fetchPaymentFlow(): AirwallexPaymentRequestFlow {
    val flowField = this
        .fieldSchemas
        ?.firstOrNull { schema -> schema.transactionMode == TransactionMode.ONE_OFF }
        ?.fields
        ?.firstOrNull { it.name == FLOW }

    val candidates = flowField?.candidates
    return when {
        candidates?.find { it.value == AirwallexPaymentRequestFlow.IN_APP.value } != null -> {
            AirwallexPaymentRequestFlow.IN_APP
        }

        !candidates.isNullOrEmpty() -> {
            AirwallexPaymentRequestFlow.fromValue(candidates[0].value)
                ?: AirwallexPaymentRequestFlow.IN_APP
        }

        else -> {
            AirwallexPaymentRequestFlow.IN_APP
        }
    }
}
