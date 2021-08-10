package com.airwallex.android.model.parser

import com.airwallex.android.model.*
import com.airwallex.android.model.AirwallexJsonUtils
import org.json.JSONObject

class CreatePaymentConsentParamsParser : ModelJsonParser<CreatePaymentConsentParams> {

    override fun parse(json: JSONObject): CreatePaymentConsentParams {
        return CreatePaymentConsentParams(
            clientSecret = json.optString(FIELD_CLIENTSECRET),
            customerId = json.optString(FIELD_CUSTOMERID),
            paymentMethodId = json.optString(FIELD_PAYMENTMETHODID),
            nextTriggeredBy = PaymentConsent.NextTriggeredBy.fromValue(AirwallexJsonUtils.optString(json, FIELD_NEXTTRIGGEREDBY))!!,
            merchantTriggerReason = PaymentConsent.MerchantTriggerReason.fromValue(AirwallexJsonUtils.optString(json, FIELD_MERCHANTTRIGGERREASON))!!,
            paymentMethodType = PaymentMethodType.fromValue(AirwallexJsonUtils.optString(json, FIELD_TYPE))!!,
            requiresCvc = json.optBoolean(FIELD_REQUESTCVC)
        )
    }

    private companion object {
        private const val FIELD_CLIENTSECRET = "clientSecret"
        private const val FIELD_CUSTOMERID = "customerId"
        private const val FIELD_PAYMENTMETHODID = "paymentMethodId"
        private const val FIELD_NEXTTRIGGEREDBY = "nextTriggeredBy"
        private const val FIELD_MERCHANTTRIGGERREASON = "merchantTriggerReason"
        private const val FIELD_TYPE = "paymentMethodType"
        private const val FIELD_REQUESTCVC = "requiresCvc"
    }
}
