package com.airwallex.android.core.model.parser

import com.airwallex.android.core.model.PhysicalProduct
import org.json.JSONObject

class PhysicalProductParser : ModelJsonParser<PhysicalProduct> {

    override fun parse(json: JSONObject): PhysicalProduct {
        return PhysicalProduct(
            type = json.optString(FIELD_TYPE),
            code = json.optString(FIELD_CODE),
            name = json.optString(FIELD_NAME),
            sku = json.optString(FIELD_SKU),
            quantity = json.optInt(FIELD_QUANTITY),
            unitPrice = json.optDouble(FIELD_UNIT_PRICE),
            desc = json.optString(FIELD_DESC),
            url = json.optString(FIELD_URL)
        )
    }

    companion object {
        const val FIELD_TYPE = "type"
        const val FIELD_CODE = "code"
        const val FIELD_NAME = "name"
        const val FIELD_SKU = "sku"
        const val FIELD_QUANTITY = "quantity"
        const val FIELD_UNIT_PRICE = "unit_price"
        const val FIELD_DESC = "desc"
        const val FIELD_URL = "url"
    }
}
