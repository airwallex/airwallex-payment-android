package com.airwallex.android.view

import com.airwallex.android.core.model.AirwallexPaymentRequestFlow
import com.airwallex.android.core.model.DynamicSchemaFieldType
import com.airwallex.android.core.model.PaymentMethodTypeInfo
import com.airwallex.android.core.model.TransactionMode
import com.airwallex.android.view.util.fetchPaymentFlow
import com.airwallex.android.view.util.filterRequiredFields
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.reflect.Type
import kotlin.test.assertEquals

class PaymentMethodTypeInfoExtensionsTest {
    @Test
    fun `test filterRequiredFields returns correct fields`() {
        val gson: Gson = GsonBuilder()
            .registerTypeAdapter(TransactionMode::class.java, TransactionModeAdapter())
            .create()
        val json = """
        {
            "name": "Test Method",
            "displayName": "Test Method Display",
            "hasSchema": true,
            "fieldSchemas": [
                {
                    "transactionMode": "oneoff",
                    "fields": [
                        {
                            "name": "field1",
                            "displayName": "Field 1",
                            "uiType": "text",
                            "type": "string",
                            "hidden": false,
                            "candidates": null,
                            "validations": null
                        },
                        {
                            "name": "field2",
                            "displayName": "Field 2",
                            "uiType": "email",
                            "type": "string",
                            "hidden": false,
                            "candidates": null,
                            "validations": null
                        },
                        {
                            "name": "hiddenField",
                            "displayName": "Hidden Field",
                            "uiType": "phone",
                            "type": "string",
                            "hidden": true,
                            "candidates": null,
                            "validations": null
                        }
                    ]
                }
            ]
        }
        """.trimIndent()

        val paymentMethodTypeInfo = gson.fromJson(json, PaymentMethodTypeInfo::class.java)
        val result = paymentMethodTypeInfo.filterRequiredFields()

        assertNotNull(result)
        assertEquals(2, result?.size)
        assertTrue(result?.any { it.name == "field1" } == true)
        assertTrue(result?.any { it.name == "field2" } == true)
        assertFalse(result?.any { it.name == "hiddenField" } == true)
    }

    @Test
    fun `test filterRequiredFields returns null when fieldSchemas is null`() {
        val gson = Gson()
        val json = """
        {
            "name": "Test Method",
            "displayName": "Test Method Display",
            "hasSchema": true,
            "fieldSchemas": null
        }
        """.trimIndent()

        val paymentMethodTypeInfo = gson.fromJson(json, PaymentMethodTypeInfo::class.java)
        val result = paymentMethodTypeInfo.filterRequiredFields()
        assertNull(result)
    }

    @Test
    fun `test filterRequiredFields returns null when fieldSchemas is empty`() {
        val gson = Gson()
        val json = """
        {
            "name": "Test Method",
            "displayName": "Test Method Display",
            "hasSchema": true,
            "fieldSchemas": []
        }
        """.trimIndent()
        val paymentMethodTypeInfo = gson.fromJson(json, PaymentMethodTypeInfo::class.java)
        val result = paymentMethodTypeInfo.filterRequiredFields()
        assertNull(result)
    }

    @Test
    fun `test filterRequiredFields returns null with no ONE_OFF TransactionMode`() {
        val gson: Gson = GsonBuilder()
            .registerTypeAdapter(TransactionMode::class.java, TransactionModeAdapter())
            .create()
        val json = """
        {
            "name": "Test Method",
            "displayName": "Test Method Display",
            "hasSchema": true,
            "fieldSchemas": [
                {
                    "transactionMode": "recurring",
                    "fields": [
                        {
                            "name": "field",
                            "displayName": "Field",
                            "uiType": "text",
                            "type": "string",
                            "hidden": false,
                            "candidates": null,
                            "validations": null
                        }
                    ]
                }
            ]
        }
        """.trimIndent()

        val paymentMethodTypeInfo = gson.fromJson(json, PaymentMethodTypeInfo::class.java)
        val result = paymentMethodTypeInfo.filterRequiredFields()
        assertNull(result)
    }

    @Test
    fun `test fetchPaymentFlow with IN_APP candidate`() {
        val gson: Gson = GsonBuilder()
            .registerTypeAdapter(TransactionMode::class.java, TransactionModeAdapter())
            .create()
        val json = """
        {
            "name": "Test Method",
            "displayName": "Test Method Display",
            "hasSchema": true,
            "fieldSchemas": [
                {
                    "transactionMode": "oneoff",
                    "fields": [
                        {
                            "name": "flow",
                            "displayName": "Flow Field",
                            "uiType": "list",
                            "type": "enum",
                            "hidden": false,
                            "candidates": [
                                {"value": "inapp"},
                                {"value": "mweb"}
                            ],
                            "validations": null
                        }
                    ]
                }
            ]
        }
        """.trimIndent()

        val paymentMethodTypeInfo = gson.fromJson(json, PaymentMethodTypeInfo::class.java)

        val result = paymentMethodTypeInfo.fetchPaymentFlow()
        assertEquals(AirwallexPaymentRequestFlow.IN_APP, result)
    }

    @Test
    fun `test fetchPaymentFlow with first candidate not IN_APP`() {
        val gson: Gson = GsonBuilder()
            .registerTypeAdapter(TransactionMode::class.java, TransactionModeAdapter())
            .create()
        val json = """
        {
            "name": "Test Method",
            "displayName": "Test Method Display",
            "hasSchema": true,
            "fieldSchemas": [
                {
                    "transactionMode": "oneoff",
                    "fields": [
                        {
                            "name": "flow",
                            "displayName": "Flow Field",
                            "uiType": "list",
                            "type": "enum",
                            "hidden": false,
                            "candidates": [
                                {"value": "mweb"}
                            ],
                            "validations": null
                        }
                    ]
                }
            ]
        }
        """.trimIndent()

        val paymentMethodTypeInfo = gson.fromJson(json, PaymentMethodTypeInfo::class.java)

        val result = paymentMethodTypeInfo.fetchPaymentFlow()
        assertEquals(AirwallexPaymentRequestFlow.M_WEB, result)
    }

    @Test
    fun `test fetchPaymentFlow with null candidates`() {
        val gson: Gson = GsonBuilder()
            .registerTypeAdapter(TransactionMode::class.java, TransactionModeAdapter())
            .create()
        val json = """
        {
            "name": "Test Method",
            "displayName": "Test Method Display",
            "hasSchema": true,
            "fieldSchemas": [
                {
                    "transactionMode": "oneoff",
                    "fields": [
                        {
                            "name": "flow",
                            "displayName": "Flow Field",
                            "uiType": "list",
                            "type": "enum",
                            "hidden": false,
                            "candidates": null,
                            "validations": null
                        }
                    ]
                }
            ]
        }
        """.trimIndent()

        val paymentMethodTypeInfo = gson.fromJson(json, PaymentMethodTypeInfo::class.java)

        val result = paymentMethodTypeInfo.fetchPaymentFlow()
        assertEquals(AirwallexPaymentRequestFlow.IN_APP, result)
    }

    @Test
    fun `test fetchPaymentFlow with empty candidates`() {
        val gson: Gson = GsonBuilder()
            .registerTypeAdapter(TransactionMode::class.java, TransactionModeAdapter())
            .create()
        val json = """
        {
            "name": "Test Method",
            "displayName": "Test Method Display",
            "hasSchema": true,
            "fieldSchemas": [
                {
                    "transactionMode": "oneoff",
                    "fields": [
                        {
                            "name": "flow",
                            "displayName": "Flow Field",
                            "uiType": "list",
                            "type": "enum",
                            "hidden": false,
                            "candidates": [],
                            "validations": null
                        }
                    ]
                }
            ]
        }
        """.trimIndent()

        val paymentMethodTypeInfo = gson.fromJson(json, PaymentMethodTypeInfo::class.java)

        val result = paymentMethodTypeInfo.fetchPaymentFlow()
        assertEquals(AirwallexPaymentRequestFlow.IN_APP, result)
    }

    @Test
    fun `test fetchPaymentFlow with missing flow field`() {
        val gson: Gson = GsonBuilder()
            .registerTypeAdapter(TransactionMode::class.java, TransactionModeAdapter())
            .create()
        val json = """
        {
            "name": "Test Method",
            "displayName": "Test Method Display",
            "hasSchema": true,
            "fieldSchemas": [
                {
                    "transactionMode": "oneoff",
                    "fields": [
                        {
                            "name": "otherField",
                            "displayName": "Other Field",
                            "uiType": "text",
                            "type": "string",
                            "hidden": false,
                            "candidates": null,
                            "validations": null
                        }
                    ]
                }
            ]
        }
        """.trimIndent()

        val paymentMethodTypeInfo = gson.fromJson(json, PaymentMethodTypeInfo::class.java)

        val result = paymentMethodTypeInfo.fetchPaymentFlow()
        assertEquals(AirwallexPaymentRequestFlow.IN_APP, result)
    }

    class TransactionModeAdapter :
        JsonSerializer<TransactionMode>,
        JsonDeserializer<TransactionMode> {

        override fun serialize(
            src: TransactionMode?,
            typeOfSrc: Type?,
            context: JsonSerializationContext?
        ): JsonElement {
            return JsonPrimitive(src?.value)
        }

        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): TransactionMode? {
            val value = json?.asString
            return TransactionMode.values().firstOrNull { it.value == value }
        }
    }

    class DynamicSchemaFieldTypeAdapter :
        JsonDeserializer<DynamicSchemaFieldType>,
        JsonSerializer<DynamicSchemaFieldType> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): DynamicSchemaFieldType? {
            return DynamicSchemaFieldType.fromValue(json?.asString)
        }

        override fun serialize(
            src: DynamicSchemaFieldType?,
            typeOfSrc: Type?,
            context: JsonSerializationContext?
        ): JsonElement {
            return JsonPrimitive(src?.value)
        }
    }

}