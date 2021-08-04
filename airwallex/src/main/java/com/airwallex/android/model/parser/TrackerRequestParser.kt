package com.airwallex.android.model.parser

import com.airwallex.android.model.*
import com.airwallex.android.model.AirwallexJsonUtils
import org.json.JSONObject

class TrackerRequestParser : ModelJsonParser<TrackerRequest> {

    override fun parse(json: JSONObject): TrackerRequest {
        return TrackerRequest(
            origin = json.optString(FIELD_ORIGIN),
            complete = json.optBoolean(FIELD_COMPLETE),
            empty = json.optBoolean(FIELD_EMPTY),
            application = json.optString(FIELD_APPLICATION),
            error = json.optString(FIELD_ERROR),
            intentId = json.optString(FIELD_INTENT_ID),
            status = json.optString(FIELD_STATUS),
            nextActionType = json.optString(FIELD_NEXT_ACTION_TYPE),
            nextActionUrl = json.optString(FIELD_NEXT_ACTION_URL),
            brand = json.optString(FIELD_BRAND),
            cardBin = json.optString(FIELD_CARD_BIN),
            path = json.optString(FIELD_PATH),
            type = TrackerRequest.TrackerType.fromValue(
                AirwallexJsonUtils.optString(json, FIELD_TYPE)
            ),
            code = TrackerRequest.TrackerCode.fromValue(
                AirwallexJsonUtils.optString(json, FIELD_CODE)
            ),
            req = AirwallexJsonUtils.optMap(json, FIELD_REQ),
            res = AirwallexJsonUtils.optMap(json, FIELD_RES),
            header = AirwallexJsonUtils.optMap(json, FIELD_HEADER)
        )
    }

    private companion object {
        private const val FIELD_ORIGIN = "origin"
        private const val FIELD_COMPLETE = "complete"
        private const val FIELD_EMPTY = "empty"
        private const val FIELD_APPLICATION = "application"
        private const val FIELD_TYPE = "type"
        private const val FIELD_CODE = "code"
        private const val FIELD_ERROR = "error"
        private const val FIELD_INTENT_ID = "intent_id"
        private const val FIELD_STATUS = "status"
        private const val FIELD_NEXT_ACTION_TYPE = "next_action_type"
        private const val FIELD_NEXT_ACTION_URL = "next_action_url"
        private const val FIELD_BRAND = "brand"
        private const val FIELD_CARD_BIN = "cardBin"
        private const val FIELD_PATH = "path"
        private const val FIELD_REQ = "req"
        private const val FIELD_RES = "res"
        private const val FIELD_HEADER = "header"
    }
}
