package com.airwallex.android.model.parser

import com.airwallex.android.model.*
import com.airwallex.android.model.AirwallexJsonUtils
import org.json.JSONObject

class PaymentConsentParser : ModelJsonParser<PaymentConsent> {

    override fun parse(json: JSONObject): PaymentConsent {
        return PaymentConsent(
            id = json.optString(FIELD_ID),
            requestId = AirwallexJsonUtils.optString(json, FIELD_REQUEST_ID),
            customerId = AirwallexJsonUtils.optString(json, FIELD_CUSTOMER_ID),
            paymentMethod = json.optJSONObject(FIELD_PAYMENT_METHOD)?.let {
                PaymentMethodParser().parse(it)
            },
            initialPaymentIntentId = AirwallexJsonUtils.optString(json, FIELD_INITIAL_PAYMENT_INTENT_ID),
            nextTriggeredBy = PaymentConsent.NextTriggeredBy.fromValue(
                AirwallexJsonUtils.optString(json, FIELD_NEXT_TRIGGERED_BY)
            ),
            merchantTriggerReason = PaymentConsent.MerchantTriggerReason.fromValue(
                AirwallexJsonUtils.optString(json, FIELD_MERCHANT_TRIGGER_REASON)
            ),
            requiresCvc = AirwallexJsonUtils.optBoolean(json, FIELD_REQUIRES_CVC),
            metadata = AirwallexJsonUtils.optMap(json, FIELD_METADATA),
            status = PaymentConsent.PaymentConsentStatus.fromValue(
                AirwallexJsonUtils.optString(json, FIELD_STATUS)
            ),
            createdAt = AirwallexJsonUtils.optString(json, FIELD_CREATED_AT)?.let {
                dateFormat.parse(it)
            },
            updatedAt = AirwallexJsonUtils.optString(json, FIELD_UPDATED_AT)?.let {
                dateFormat.parse(it)
            },
            nextAction = json.optJSONObject(FIELD_NEXT_ACTION)?.let {
                NextActionParser().parse(it)
            },
            clientSecret = AirwallexJsonUtils.optString(json, FIELD_CLIENT_SECRET)
        )
    }

    private companion object {
        private const val FIELD_ID = "id"
        private const val FIELD_REQUEST_ID = "request_id"
        private const val FIELD_CUSTOMER_ID = "customer_id"
        private const val FIELD_PAYMENT_METHOD = "payment_method"
        private const val FIELD_INITIAL_PAYMENT_INTENT_ID = "initial_payment_intent_id"
        private const val FIELD_NEXT_TRIGGERED_BY = "next_triggered_by"
        private const val FIELD_MERCHANT_TRIGGER_REASON = "merchant_trigger_reason"
        private const val FIELD_REQUIRES_CVC = "requires_cvc"
        private const val FIELD_METADATA = "metadata"
        private const val FIELD_STATUS = "status"
        private const val FIELD_CREATED_AT = "created_at"
        private const val FIELD_UPDATED_AT = "updated_at"
        private const val FIELD_NEXT_ACTION = "next_action"
        private const val FIELD_CLIENT_SECRET = "client_secret"
    }
}
