package com.airwallex.android.model.parser

import com.airwallex.android.model.AirwallexJsonUtils
import com.airwallex.android.model.PurchaseOrder
import org.json.JSONObject

class PurchaseOrderParser : ModelJsonParser<PurchaseOrder> {

    private val physicalProductParser = PhysicalProductParser()

    override fun parse(json: JSONObject): PurchaseOrder {
        val shipping = json.optJSONObject(FIELD_SHIPPING)?.let {
            ShippingParser().parse(it)
        }

        val products = json.optJSONArray(FIELD_PRODUCTS)?.let {
            (0 until it.length())
                .map { idx -> it.optJSONObject(idx) }
                .mapNotNull { jsonObject ->
                    physicalProductParser.parse(jsonObject)
                }
        }

        return PurchaseOrder(
            products = products,
            shipping = shipping,
            type = AirwallexJsonUtils.optString(json, FIELD_TYPE)
        )
    }

    companion object {
        const val FIELD_PRODUCTS = "products"
        const val FIELD_SHIPPING = "shipping"
        const val FIELD_TYPE = "type"
    }
}
