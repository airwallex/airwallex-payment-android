package com.airwallex.android.core.model.parser

import com.airwallex.android.core.model.PaymentMethodOptions
import com.airwallex.android.core.util.AirwallexJsonUtils
import org.json.JSONObject

class PaymentMethodOptionsParser : ModelJsonParser<PaymentMethodOptions> {

    override fun parse(json: JSONObject): PaymentMethodOptions? {
        val cardOptions = json.optJSONObject(FIELD_CARD_OPTIONS)?.let {
            CardOptionsParser().parse(it)
        }
        return PaymentMethodOptions(
            cardOptions = cardOptions
        )
    }

    companion object {
        const val FIELD_CARD_OPTIONS = "card"
    }

    internal class CardOptionsParser : ModelJsonParser<PaymentMethodOptions.CardOptions> {

        override fun parse(json: JSONObject): PaymentMethodOptions.CardOptions {
            val threeDSecure = json.optJSONObject(FIELD_THREE_DS)?.let {
                ThreeDSecureParser().parse(it)
            }
            return PaymentMethodOptions.CardOptions(
                autoCapture = AirwallexJsonUtils.optBoolean(json, FIELD_AUTO_CAPTURE),
                threeDSecure = threeDSecure
            )
        }

        companion object {
            const val FIELD_AUTO_CAPTURE = "auto_capture"
            const val FIELD_THREE_DS = "three_ds"
        }
    }
}
