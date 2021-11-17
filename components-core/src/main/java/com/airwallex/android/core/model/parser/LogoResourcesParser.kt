package com.airwallex.android.core.model.parser

import com.airwallex.android.core.LogoResources
import org.json.JSONObject

class LogoResourcesParser : ModelJsonParser<LogoResources> {

    override fun parse(json: JSONObject): LogoResources {
        return LogoResources(
            png = json.optString(FIELD_PNG),
            svg = json.optString(FIELD_SVG)
        )
    }

    companion object {
        const val FIELD_PNG = "png"
        const val FIELD_SVG = "svg"
    }
}
