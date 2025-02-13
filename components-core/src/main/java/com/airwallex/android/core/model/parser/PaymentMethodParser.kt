package com.airwallex.android.core.model.parser

import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.util.AirwallexJsonUtils
import org.json.JSONObject

class PaymentMethodParser : ModelJsonParser<PaymentMethod> {

    override fun parse(json: JSONObject): PaymentMethod {

        return PaymentMethod(
            id = AirwallexJsonUtils.optString(json, FIELD_ID),
            requestId = AirwallexJsonUtils.optString(json, FIELD_REQUEST_ID),
            customerId = AirwallexJsonUtils.optString(json, FIELD_CUSTOMER_ID),
            type = AirwallexJsonUtils.optString(json, FIELD_TYPE),
            card = json.optJSONObject(FIELD_CARD)?.let {
                CardParser().parse(it)
            },
            googlePay = json.optJSONObject(FIELD_GOOGLE_PAY)?.let {
                GooglePayParser().parse(it)
            },
            billing = json.optJSONObject(FIELD_BILLING)?.let {
                BillingParser().parse(it)
            },
            status = PaymentMethod.PaymentMethodStatus.fromValue(
                AirwallexJsonUtils.optString(json, FIELD_STATUS)
            ),
            metadata = AirwallexJsonUtils.optMap(json, FIELD_METADATA),
            createdAt = AirwallexJsonUtils.optString(json, FIELD_CREATED_AT)?.let {
                dateFormat.parse(it)
            },
            updatedAt = AirwallexJsonUtils.optString(json, FIELD_UPDATED_AT)?.let {
                dateFormat.parse(it)
            }
        )
    }

    companion object {
        const val FIELD_ID = "id"
        const val FIELD_REQUEST_ID = "request_id"
        const val FIELD_CUSTOMER_ID = "customer_id"
        const val FIELD_TYPE = "type"
        const val FIELD_CARD = "card"
        const val FIELD_GOOGLE_PAY = "googlepay"
        const val FIELD_BILLING = "billing"
        const val FIELD_STATUS = "status"
        const val FIELD_METADATA = "metadata"
        const val FIELD_CREATED_AT = "created_at"
        const val FIELD_UPDATED_AT = "updated_at"
    }

    class GooglePayParser : ModelJsonParser<PaymentMethod.GooglePay> {
        override fun parse(json: JSONObject): PaymentMethod.GooglePay {
            return PaymentMethod.GooglePay(
                billing = json.optJSONObject(FIELD_BILLING)?.let {
                    BillingParser().parse(it)
                },
                paymentDataType = json.optString(FIELD_PAYMENT_DATA_TYPE),
                encryptedPaymentToken = json.optString(FIELD_ENCRYPTED_PAYMENT_TOKEN)
            )
        }

        companion object {
            const val FIELD_PAYMENT_DATA_TYPE = "payment_data_type"
            const val FIELD_ENCRYPTED_PAYMENT_TOKEN = "encrypted_payment_token"
        }
    }

    class CardParser : ModelJsonParser<PaymentMethod.Card> {

        override fun parse(json: JSONObject): PaymentMethod.Card {
            return PaymentMethod.Card(
                cvc = AirwallexJsonUtils.optString(json, FIELD_CVC),
                expiryMonth = AirwallexJsonUtils.optString(json, FIELD_EXPIRY_MONTH),
                expiryYear = AirwallexJsonUtils.optString(json, FIELD_EXPIRY_YEAR),
                name = AirwallexJsonUtils.optString(json, FIELD_NAME),
                number = AirwallexJsonUtils.optString(json, FIELD_NUMBER),
                bin = AirwallexJsonUtils.optString(json, FIELD_BIN),
                last4 = AirwallexJsonUtils.optString(json, FIELD_LAST4),
                brand = AirwallexJsonUtils.optString(json, FIELD_BRAND),
                country = AirwallexJsonUtils.optString(json, FIELD_COUNTRY),
                funding = AirwallexJsonUtils.optString(json, FIELD_FUNDING),
                fingerprint = AirwallexJsonUtils.optString(json, FIELD_FINGERPRINT),
                cvcCheck = AirwallexJsonUtils.optString(json, FIELD_CVC_CHECK),
                avsCheck = AirwallexJsonUtils.optString(json, FIELD_AVS_CHECK),
                issuerCountryCode = AirwallexJsonUtils.optString(json, FIELD_ISSUER_COUNTRY_CODE),
                cardType = AirwallexJsonUtils.optString(json, FIELD_CARD_TYPE),
                numberType = PaymentMethod.Card.NumberType.fromValue(
                    AirwallexJsonUtils.optString(json, FIELD_NUMBER_TYPE)
                )
            )
        }

        companion object {
            const val FIELD_CVC = "cvc"
            const val FIELD_EXPIRY_MONTH = "expiry_month"
            const val FIELD_EXPIRY_YEAR = "expiry_year"
            const val FIELD_NAME = "name"
            const val FIELD_NUMBER = "number"
            const val FIELD_BIN = "bin"
            const val FIELD_LAST4 = "last4"
            const val FIELD_BRAND = "brand"
            const val FIELD_COUNTRY = "country"
            const val FIELD_FUNDING = "funding"
            const val FIELD_FINGERPRINT = "fingerprint"
            const val FIELD_CVC_CHECK = "cvc_check"
            const val FIELD_AVS_CHECK = "avs_check"
            const val FIELD_ISSUER_COUNTRY_CODE = "issuer_country_code"
            const val FIELD_CARD_TYPE = "card_type"
            const val FIELD_NUMBER_TYPE = "number_type"
        }
    }
}
