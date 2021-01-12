package com.airwallex.android.model.parser

import com.airwallex.android.model.AirwallexJsonUtils
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.PaymentMethodType
import org.json.JSONObject

class PaymentMethodParser : ModelJsonParser<PaymentMethod> {

    override fun parse(json: JSONObject): PaymentMethod {

        return PaymentMethod(
            id = AirwallexJsonUtils.optString(json, FIELD_ID),
            requestId = AirwallexJsonUtils.optString(json, FIELD_REQUEST_ID),
            customerId = AirwallexJsonUtils.optString(json, FIELD_CUSTOMER_ID),
            type = PaymentMethodType.fromValue(AirwallexJsonUtils.optString(json, FIELD_TYPE)),
            card = json.optJSONObject(FIELD_CARD)?.let {
                CardParser().parse(it)
            },
            weChatPayRequest = json.optJSONObject(FIELD_WECHAT_PAY_REQUEST)?.let {
                WeChatPayRequestParser().parse(it)
            },
            aliPayCNRequest = json.optJSONObject(FIELD_ALI_PAY_CN_REQUEST)?.let {
                AliPayRequestParser().parse(it)
            },
            aliPayHKRequest = json.optJSONObject(FIELD_ALI_PAY_HK_REQUEST)?.let {
                AliPayRequestParser().parse(it)
            },
            kakaoPayRequest = json.optJSONObject(FIELD_KAO_KAO_PAY_REQUEST)?.let {
                AliPayRequestParser().parse(it)
            },
            tngRequest = json.optJSONObject(FIELD_TNG_REQUEST)?.let {
                AliPayRequestParser().parse(it)
            },
            danaRequest = json.optJSONObject(FIELD_DANA_REQUEST)?.let {
                AliPayRequestParser().parse(it)
            },
            gCashRequest = json.optJSONObject(FIELD_GCASH_REQUEST)?.let {
                AliPayRequestParser().parse(it)
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
        const val FIELD_WECHAT_PAY_REQUEST = "wechatpay"
        const val FIELD_ALI_PAY_CN_REQUEST = "alipaycn"
        const val FIELD_ALI_PAY_HK_REQUEST = "alipayhk"
        const val FIELD_KAO_KAO_PAY_REQUEST = "kakaopay"
        const val FIELD_TNG_REQUEST = "tng"
        const val FIELD_DANA_REQUEST = "dana"
        const val FIELD_GCASH_REQUEST = "gcash"
        const val FIELD_BILLING = "billing"
        const val FIELD_STATUS = "status"
        const val FIELD_METADATA = "metadata"
        const val FIELD_CREATED_AT = "created_at"
        const val FIELD_UPDATED_AT = "updated_at"
    }

    internal class CardParser : ModelJsonParser<PaymentMethod.Card> {

        override fun parse(json: JSONObject): PaymentMethod.Card? {
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
                cardType = AirwallexJsonUtils.optString(json, FIELD_CARD_TYPE)
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
        }
    }
}
