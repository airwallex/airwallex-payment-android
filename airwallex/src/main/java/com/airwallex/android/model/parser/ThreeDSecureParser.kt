package com.airwallex.android.model.parser

import com.airwallex.android.model.AirwallexJsonUtils
import com.airwallex.android.model.ThreeDSecure
import org.json.JSONObject

class ThreeDSecureParser : ModelJsonParser<ThreeDSecure> {

    override fun parse(json: JSONObject): ThreeDSecure? {
        return ThreeDSecure(
            returnUrl = AirwallexJsonUtils.optString(json, FIELD_RETURN_URL),
            deviceDataCollectionRes = AirwallexJsonUtils.optString(json, FIELD_COLLECTION_RES),
            transactionId = AirwallexJsonUtils.optString(json, FIELD_TRANSACTION_ID)
        )
    }

    companion object {
        const val FIELD_RETURN_URL = "return_url"
        const val FIELD_COLLECTION_RES = "device_data_collection_res"
        const val FIELD_TRANSACTION_ID = "ds_transaction_id"
    }
}
