package com.airwallex.android.view

import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.BankResponse
import com.airwallex.android.core.model.DynamicSchemaFieldType
import com.airwallex.android.core.model.Page
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodTypeInfo
import com.airwallex.android.core.model.TransactionMode
import com.airwallex.android.core.model.parser.AvailablePaymentMethodTypeParser
import com.airwallex.android.core.model.parser.PageParser
import com.airwallex.android.core.model.parser.PaymentConsentParser
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONObject

object Constants {

   private const val AVAILABLE_PAYMENT_METHOD_TYPE_JSON = """
        {
            "name": "card",
            "displayName": "Credit/Debit Card",
            "transactionMode": "ONE_OFF",
            "resources": {
                "hasSchema": true
            }
        }
    """

    private const val GOOGLE_PAYMENT_METHOD_TYPE_JSON = """
        {
            "name": "googlepay",
            "displayName": "Credit/Debit Card",
            "transactionMode": "ONE_OFF",
            "resources": {
                "hasSchema": true
            }
        }
    """

    private const val AVAILABLE_PAYMENT_METHODS_JSON = """
        {
            "items":[ 
                  {
                   "name":"card",
                   "transaction_mode": "oneoff",
                   "active":true,
                   "transaction_currencies":["dollar","RMB"],
                   "flows":["inapp"]
                  }   
            ],
            "has_more":false
        }
    """

    private const val DEFAULT_PAYMENT_CONSENTS_JSON = """
        {
            "items":[
                {
                  "payment_method": {
                    "type": "card",
                    "card": {
                        "name": "John",
                        "issuer_name": "DISCOVER BANK",
                        "is_commercial": false,
                        "number_type": "PAN"
                    }
                  },
                  "next_triggered_by": "customer",
                  "status": "VERIFIED"
                }   
            ],
            "has_more":false
        }
    """

    private const val SIMPLE_PAYMENT_METHOD_JSON_TEMPLATE = """
        {
            "type": "{{TYPE}}"
        }
    """

    private const val SIMPLE_PAYMENT_METHOD_TYPE_INFO_JSON_TEMPLATE = """
        {
            "name": "Test Method",
            "displayName": "Test Method Display",
            "hasSchema": true,
            "fieldSchemas": {{FIELD_SCHEMAS}}
        }
    """

    private const val SIMPLE_BANK_RESPONSE_JSON_TEMPLATE = """
        {
            "hasMore": true,
            "items": {{ITEMS}}
        }
    """

    const val SAMPLE_ENUM_FIELD = """
        [
            {
                "transactionMode": "oneoff",
                "fields": [
                     {
                        "name": "name1",
                        "type": "enum"
                     }
                ]
            }
        ]
    """

    const val SAMPLE_BANK_FIELD = """
        [
            {
                "transactionMode": "oneoff",
                "fields": [
                    {
                        "name": "name1",
                        "type": "banks"
                    }
                ]
            }
        ]
    """

    const val SAMPLE_BANK_RESPONSE = """
        [
            {
                "bankName": "Bank A",
                "bankCode": "A001"
            }
        ]
    """

    // Functions
    fun createPaymentMethod(type: String = "card"): PaymentMethod {
        val json = SIMPLE_PAYMENT_METHOD_JSON_TEMPLATE.replace("{{TYPE}}", type)
        return Gson().fromJson(json, PaymentMethod::class.java)
    }

    fun createAvailablePaymentMethodType(): AvailablePaymentMethodType {
        val gson = Gson()
        return gson.fromJson(AVAILABLE_PAYMENT_METHOD_TYPE_JSON, AvailablePaymentMethodType::class.java)
    }

    fun createGooglePayPaymentMethodType(): AvailablePaymentMethodType {
        val gson = Gson()
        return gson.fromJson(GOOGLE_PAYMENT_METHOD_TYPE_JSON, AvailablePaymentMethodType::class.java)
    }

    fun createPaymentMethodTypeInfo(fieldSchemas: String? = null): PaymentMethodTypeInfo {
        val fieldSchemasJson = fieldSchemas ?: "[]"
        val json = SIMPLE_PAYMENT_METHOD_TYPE_INFO_JSON_TEMPLATE.replace("{{FIELD_SCHEMAS}}", fieldSchemasJson)
        val gson = GsonBuilder()
            .registerTypeAdapter(
                TransactionMode::class.java,
                PaymentMethodTypeInfoExtensionsTest.TransactionModeAdapter()
            )
            .registerTypeAdapter(
                DynamicSchemaFieldType::class.java,
                PaymentMethodTypeInfoExtensionsTest.DynamicSchemaFieldTypeAdapter()
            )
            .create()
        return gson.fromJson(json, PaymentMethodTypeInfo::class.java)
    }

    fun createBankResponse(items: String? = null): BankResponse {
        val itemsJson = items ?: "[]"
        val json = SIMPLE_BANK_RESPONSE_JSON_TEMPLATE.replace("{{ITEMS}}", itemsJson)
        val gson = Gson()
        return gson.fromJson(json, BankResponse::class.java)
    }

    fun createPaymentMethods(transactionMode: TransactionMode): Page<AvailablePaymentMethodType> {
        val json = AVAILABLE_PAYMENT_METHODS_JSON.replace("oneoff", transactionMode.value)
        return PageParser(AvailablePaymentMethodTypeParser()).parse(JSONObject(json))
    }

    fun createPaymentConsents(json: String? = null): Page<PaymentConsent> {
        return PageParser(PaymentConsentParser()).parse(JSONObject(json ?: DEFAULT_PAYMENT_CONSENTS_JSON))
    }
}