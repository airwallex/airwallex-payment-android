package com.airwallex.android.model.parser

import com.airwallex.android.model.*
import com.airwallex.android.model.AirwallexJsonUtils
import org.json.JSONObject
import java.math.BigDecimal

class PaymentConsentVerifyRequestParser : ModelJsonParser<PaymentConsentVerifyRequest> {

    override fun parse(json: JSONObject): PaymentConsentVerifyRequest {
        return PaymentConsentVerifyRequest(
            requestId = AirwallexJsonUtils.optString(json, FIELD_REQUEST_ID),
            returnUrl = AirwallexJsonUtils.optString(json, FIELD_RETURN_URL),
            verificationOptions = json.optJSONObject(FIELD_VERIFICATION_OPTIONS)?.let {
                VerificationOptionsParser().parse(it)
            }
        )
    }

    private companion object {
        private const val FIELD_REQUEST_ID = "request_id"
        private const val FIELD_VERIFICATION_OPTIONS = "verification_options"
        private const val FIELD_RETURN_URL = "return_url"
    }

    internal class VerificationOptionsParser : ModelJsonParser<PaymentConsentVerifyRequest.VerificationOptions> {

        override fun parse(json: JSONObject): PaymentConsentVerifyRequest.VerificationOptions {
            return PaymentConsentVerifyRequest.VerificationOptions(
                cardOptions = json.optJSONObject(FIELD_CARD_OPTIONS)?.let {
                    CardVerificationOptionsParser().parse(it)
                },
                thirdPartOptions = json.optJSONObject(FIELD_THIRDPART_OPTIONS)?.let {
                    ThirdPartVerificationOptionsParser().parse(it)
                },
                type = PaymentMethodType.fromValue(
                    AirwallexJsonUtils.optString(json, FIELD_TYPE)
                )!!,
            )
        }

        private companion object {
            private const val FIELD_CARD_OPTIONS = "cardOptions"
            private const val FIELD_THIRDPART_OPTIONS = "thirdPartOptions"
            private const val FIELD_TYPE = "type"
        }
    }
    internal class CardVerificationOptionsParser : ModelJsonParser<PaymentConsentVerifyRequest.CardVerificationOptions> {

        override fun parse(json: JSONObject): PaymentConsentVerifyRequest.CardVerificationOptions {
            return PaymentConsentVerifyRequest.CardVerificationOptions(
                amount = AirwallexJsonUtils.optDouble(json, FIELD_AMOUNT)?.let {
                    BigDecimal.valueOf(it)
                },
                currency = AirwallexJsonUtils.optString(json, FIELD_CURRENCY),
                cvc = AirwallexJsonUtils.optString(json, FIELD_CVC)
            )
        }

        private companion object {
            private const val FIELD_AMOUNT = "amount"
            private const val FIELD_CURRENCY = "currency"
            private const val FIELD_CVC = "cvc"
        }
    }

    internal class ThirdPartVerificationOptionsParser : ModelJsonParser<PaymentConsentVerifyRequest.ThirdPartVerificationOptions> {

        override fun parse(json: JSONObject): PaymentConsentVerifyRequest.ThirdPartVerificationOptions {
            return PaymentConsentVerifyRequest.ThirdPartVerificationOptions(
                osType = AirwallexJsonUtils.optString(json, FIELD_OSType),
                flow = AirwallexJsonUtils.optString(json, FIELD_FLOW)?.let {
                    AirwallexPaymentRequestFlow.fromValue(it)
                }
            )
        }

        private companion object {
            private const val FIELD_OSType = "osType"
            private const val FIELD_FLOW = "flow"
        }
    }
}
