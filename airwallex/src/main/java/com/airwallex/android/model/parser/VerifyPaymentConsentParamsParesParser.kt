package com.airwallex.android.model.parser

import com.airwallex.android.model.AirwallexJsonUtils
import com.airwallex.android.model.PaymentMethodType
import com.airwallex.android.model.VerifyPaymentConsentParams
import org.json.JSONObject
import java.math.BigDecimal

class VerifyPaymentConsentParamsParesParser : ModelJsonParser<VerifyPaymentConsentParams> {

    override fun parse(json: JSONObject): VerifyPaymentConsentParams {
        return VerifyPaymentConsentParams(
            clientSecret = json.optString(FIELD_CLIENTSECRET),
            paymentConsentId = json.optString(FIELD_PAYMENTCONSENTID),
            amount = BigDecimal.valueOf(json.optDouble(FIELD_AMOUNT)),
            currency = json.optString(FIELD_CURRENCY),
            cvc = json.optString(FIELD_CVC),
            paymentMethodType = PaymentMethodType.fromValue(AirwallexJsonUtils.optString(json, FIELD_TYPE))!!,
            returnUrl = json.optString(FIELD_RETURN_URL)
        )
    }

    private companion object {
        private const val FIELD_CLIENTSECRET = "clientSecret"
        private const val FIELD_PAYMENTCONSENTID = "paymentConsentId"
        private const val FIELD_AMOUNT = "amount"
        private const val FIELD_CURRENCY = "currency"
        private const val FIELD_CVC = "cvc"
        private const val FIELD_TYPE = "paymentMethodType"
        private const val FIELD_RETURN_URL = "returnUrl"
    }
}
