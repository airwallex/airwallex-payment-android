package com.airwallex.android.view

import android.app.Application
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.airwallex.android.R
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexRecurringSession
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.model.AirwallexPaymentRequestFlow
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.Bank
import com.airwallex.android.core.model.DynamicSchemaField
import com.airwallex.android.core.model.DynamicSchemaFieldType
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodTypeInfo
import com.airwallex.android.ui.checkout.AirwallexCheckoutViewModel
import com.airwallex.android.view.util.filterRequiredFields
import com.airwallex.android.view.util.needHiddenParam
import com.airwallex.android.view.util.toPaymentFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing payment operations data.
 * Handles fetching and storing payment methods and consents.
 */
class SchemaPaymentViewModel(
    application: Application,
    airwallex: Airwallex,
    private val session: AirwallexSession
) : AirwallexCheckoutViewModel(application, airwallex, session)  {
    // Cache for schema data by payment method type
    @VisibleForTesting
    internal val schemaDataCache = mutableMapOf<AvailablePaymentMethodType, SchemaData?>()
    // Map for additional params. Currently only used for country code in Enum type fields.
    private val additionalParams = mutableMapOf<String, String>()

    fun retrieveSchemaDataFromCache(paymentMethodType: AvailablePaymentMethodType): SchemaData? {
        return schemaDataCache[paymentMethodType]
    }

    suspend fun loadSchemaFields(paymentMethodType: AvailablePaymentMethodType): Result<SchemaData?> {
        // Return cached data if available
        schemaDataCache[paymentMethodType]?.let { return Result.success(it) }

        AirwallexLogger.info("PaymentMethodsViewModel loadSchemaFields, type = ${paymentMethodType.name}")
        val paymentMethod = PaymentMethod.Builder().setType(paymentMethodType.name).build()
        paymentMethod.type?.let { type ->
            if (requireHandleSchemaFields(paymentMethodType)) { // Have required schema fields
                AirwallexLogger.info("PaymentMethodsViewModel get more payment Info fields on one-off flow.")
                // 1.Retrieve all required schema fields of the payment method
                val typeInfo = retrievePaymentMethodTypeInfo(type).getOrElse { exception ->
                    schemaDataCache[paymentMethodType] = null
                    return Result.failure<SchemaData>(exception)
                }
                // Ad hoc. Aligned with BE that we do not show Enum types in UI, instead we pass fixed values when we have the field.
                listOf(
                    COUNTRY_CODE to session.countryCode,
                    OS_TYPE to OS_NAME,
                    FLOW to AirwallexPaymentRequestFlow.IN_APP.value
                ).forEach { (key, value) ->
                    if (typeInfo.needHiddenParam(transactionMode, key)) {
                        additionalParams[key] = value
                    }
                }
                val fields = typeInfo.filterRequiredFields(transactionMode)
                    ?: return Result.success(null)
                AirwallexLogger.info("PaymentMethodsViewModel loadSchemaFields: filterRequiredFields = $fields")
                // 2.If all fields are hidden, start checkout directly
                if (fields.isEmpty()) {
                    val emptySchema = SchemaData()
                    schemaDataCache[paymentMethodType] = emptySchema
                    return Result.success(emptySchema)
                }
                val bankField = fields.find { field -> field.type == DynamicSchemaFieldType.BANKS }
                AirwallexLogger.info("PaymentMethodsViewModel loadSchemaFields: bankField = $bankField")
                if (bankField == null) {
                    val schemaData = SchemaData(
                        fields = fields,
                        paymentMethod = paymentMethod,
                        typeInfo = typeInfo,
                    )
                    schemaDataCache[paymentMethodType] = schemaData
                    return Result.success(schemaData)
                }
                // 3.If the bank is needed, need to retrieve the bank list.
                val banks = retrieveBanks(type).getOrElse { exception ->
                    schemaDataCache[paymentMethodType] = null
                    return Result.failure<SchemaData>(exception)
                }.items
                AirwallexLogger.info("PaymentMethodsViewModel loadSchemaFields: banks = $banks")
                // 4.If the bank is not needed or bank list is empty, then show the schema fields.
                val schemaData = if (banks.isNullOrEmpty()) {
                    SchemaData(
                        fields = fields,
                        paymentMethod = paymentMethod,
                        typeInfo = typeInfo,
                    )
                } else {
                    SchemaData(
                        fields = fields,
                        paymentMethod = paymentMethod,
                        typeInfo = typeInfo,
                        banks = banks,
                    )
                }
                schemaDataCache[paymentMethodType] = schemaData
                return Result.success(schemaData)
            }
        }
        // Default to null
        return Result.success(null)
    }

    fun appendParamsToMapForSchemaSubmission(map: Map<String, String>): Map<String, String> {
        return map + additionalParams
    }

    fun checkoutWithSchema(
        paymentMethod: PaymentMethod,
        additionalInfo: Map<String, String>,
        typeInfo: PaymentMethodTypeInfo,
        onOperationDone: (AirwallexPaymentStatus) -> Unit
    ) = viewModelScope.launch {
        AirwallexLogger.info("SchemaPaymentViewModel checkoutWithSchema, type = ${paymentMethod.type}")
        checkout(paymentMethod, additionalInfo, typeInfo.toPaymentFlow(transactionMode)).also { status ->
            onOperationDone(status)
        }
    }

    fun checkoutWithSchema(
        paymentMethodType: AvailablePaymentMethodType,
        onOperationDone: (AirwallexPaymentStatus) -> Unit
    ) = viewModelScope.launch {
        AirwallexLogger.info("SchemaPaymentViewModel checkoutWithSchema, type = ${paymentMethodType.name}")
        val paymentMethod = PaymentMethod.Builder().setType(paymentMethodType.name).build()
        AirwallexLogger.info("SchemaPaymentViewModel get more payment Info fields on one-off flow.")
        checkout(paymentMethod).also { status ->
            onOperationDone(status)
        }
    }

    @StringRes
    val ctaRes: Int = if (session is AirwallexRecurringSession) {
        R.string.airwallex_confirm
    } else {
        R.string.airwallex_pay_now
    }

    private fun requireHandleSchemaFields(paymentMethodType: AvailablePaymentMethodType) =
        paymentMethodType.resources?.hasSchema == true

    data class SchemaData(
        val fields: List<DynamicSchemaField> = emptyList(),
        val paymentMethod: PaymentMethod? = null,
        val typeInfo: PaymentMethodTypeInfo? = null,
        val banks: List<Bank> = emptyList(),
    )
    companion object {
        const val COUNTRY_CODE = "country_code"
        const val FLOW = "flow"
        private const val OS_TYPE = "os_type"
        private const val OS_NAME = "android"
    }
    class Factory(
        private val application: Application,
        private val airwallex: Airwallex,
        private val session: AirwallexSession
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return SchemaPaymentViewModel(application, airwallex, session) as T
        }
    }
}
