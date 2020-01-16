package com.airwallex.android.model.parser

import android.util.Log
import com.airwallex.android.ModelJsonParser
import com.airwallex.android.PaymentIntent
import org.json.JSONObject

internal class PaymentIntentJsonParser : ModelJsonParser<PaymentIntent> {
    override fun parse(json: JSONObject): PaymentIntent? {

        Log.e("aaa", "json $json")
        val id = "1"


        return PaymentIntent(
            id
        )
    }


}
