package com.airwallex.android.model.parser

import com.airwallex.android.model.*
import com.airwallex.android.model.AirwallexJsonUtils
import org.json.JSONObject

class ConfirmPaymentIntentParamsParser : ModelJsonParser<ConfirmPaymentIntentParams> {

    override fun parse(json: JSONObject): ConfirmPaymentIntentParams {
        return ConfirmPaymentIntentParams(
            paymentIntentId = AirwallexJsonUtils.optString(json, FIELD_PAYMENTINTENTID)!!,
            customerId = AirwallexJsonUtils.optString(json, FIELD_CUSTOMER_ID),
            paymentMethodType = PaymentMethodType.fromValue(
                AirwallexJsonUtils.optString(
                    json, FIELD_PAYMENTMETHODTYPE
                )
            )!!,
            paymentMethod = json.optJSONObject(FIELD_PAYMENT_METHOD)?.let {
                PaymentMethodParser().parse(it)
            },
            clientSecret = AirwallexJsonUtils.optString(json, FIELD_CLIENTSECRET)!!,
            cvc = AirwallexJsonUtils.optString(json, FIELD_CVC),
            currency = AirwallexJsonUtils.optString(json, FIELD_CURRENCY),
            paymentConsentId = AirwallexJsonUtils.optString(json, FIELD_PAYMENTCONSENTID)
        )
    }

    private companion object {
        private const val FIELD_PAYMENTINTENTID = "paymentIntentId"
        private const val FIELD_CUSTOMER_ID = "customerId"
        private const val FIELD_CLIENTSECRET = "clientSecret"
        private const val FIELD_PAYMENTMETHODTYPE = "paymentMethodType"
        private const val FIELD_CVC = "cvc"
        private const val FIELD_PAYMENT_METHOD = "paymentMethod"
        private const val FIELD_PAYMENTCONSENTID = "paymentConsentId"
        private const val FIELD_CURRENCY = "currency"
    }
}
