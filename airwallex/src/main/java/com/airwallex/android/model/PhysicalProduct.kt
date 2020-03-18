package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PhysicalProduct internal constructor(

    // Type of product, such as electronic, handling, physical, service, or shipping
    @SerializedName("type")
    val type: String?,

    // Merchant’s product identifier code
    @SerializedName("code")
    val code: String?,

    // Name of the product
    @SerializedName("name")
    val name: String?,

    // Product stock keeping unit
    @SerializedName("sku")
    val sku: String?,

    // Product quantity
    @SerializedName("quantity")
    val quantity: Int?,

    // Product unit price
    @SerializedName("unit_price")
    val unitPrice: Double?,

    // Product description
    @SerializedName("desc")
    val desc: String?,

    // Product url
    @SerializedName("url")
    val url: String?

) : AirwallexModel, Parcelable {

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
