package com.airwallex.android.model

import android.os.Parcelable
import com.airwallex.android.model.parser.PhysicalProductParser
import kotlinx.parcelize.Parcelize

/**
 * Product information
 */
@Parcelize
data class PhysicalProduct internal constructor(

    /**
     * Type of product, such as electronic, handling, physical, service, or shipping
     */
    val type: String? = null,

    /**
     * Merchant’s product identifier code
     */
    val code: String? = null,

    /**
     * Name of the product
     */
    val name: String? = null,

    /**
     * Product stock keeping unit
     */
    val sku: String? = null,

    /**
     * Product quantity
     */
    val quantity: Int? = null,

    /**
     * Product unit price
     */
    val unitPrice: Double? = null,

    /**
     * Product description
     */
    val desc: String? = null,

    /**
     * Product url
     */
    val url: String? = null

) : AirwallexModel, AirwallexRequestModel, Parcelable {

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>()
            .plus(
                type?.let {
                    mapOf(PhysicalProductParser.FIELD_TYPE to it)
                }.orEmpty()
            )
            .plus(
                code?.let {
                    mapOf(PhysicalProductParser.FIELD_CODE to it)
                }.orEmpty()
            )
            .plus(
                name?.let {
                    mapOf(PhysicalProductParser.FIELD_NAME to it)
                }.orEmpty()
            )
            .plus(
                sku?.let {
                    mapOf(PhysicalProductParser.FIELD_SKU to it)
                }.orEmpty()
            )
            .plus(
                quantity?.let {
                    mapOf(PhysicalProductParser.FIELD_QUANTITY to it)
                }.orEmpty()
            )
            .plus(
                unitPrice?.let {
                    mapOf(PhysicalProductParser.FIELD_UNIT_PRICE to it)
                }.orEmpty()
            )
            .plus(
                desc?.let {
                    mapOf(PhysicalProductParser.FIELD_DESC to it)
                }.orEmpty()
            )
            .plus(
                url?.let {
                    mapOf(PhysicalProductParser.FIELD_URL to it)
                }.orEmpty()
            )
    }

    class Builder : ObjectBuilder<PhysicalProduct> {
        private var code: String? = null
        private var name: String? = null
        private var desc: String? = null
        private var sku: String? = null
        private var type: String? = null
        private var unitPrice: Double? = null
        private var url: String? = null
        private var quantity: Int? = null

        fun setCode(code: String?): Builder = apply {
            this.code = code
        }

        fun setName(name: String?): Builder = apply {
            this.name = name
        }

        fun setDesc(desc: String?): Builder = apply {
            this.desc = desc
        }

        fun setSku(sku: String?): Builder = apply {
            this.sku = sku
        }

        fun setType(type: String?): Builder = apply {
            this.type = type
        }

        fun setUnitPrice(unitPrice: Double?): Builder = apply {
            this.unitPrice = unitPrice
        }

        fun setUrl(url: String?): Builder = apply {
            this.url = url
        }

        fun setQuantity(quantity: Int): Builder = apply {
            this.quantity = quantity
        }

        override fun build(): PhysicalProduct {
            return PhysicalProduct(
                code = code,
                name = name,
                desc = desc,
                sku = sku,
                type = type,
                unitPrice = unitPrice,
                url = url,
                quantity = quantity
            )
        }
    }
}
