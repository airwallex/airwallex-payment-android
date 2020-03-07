package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product internal constructor(
    @SerializedName("code")
    val code: String?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("desc")
    val desc: String?,

    @SerializedName("sku")
    val sku: String?,

    @SerializedName("type")
    val type: String?,

    @SerializedName("unit_price")
    val unitPrice: Double?,

    @SerializedName("url")
    val url: String?,

    @SerializedName("quantity")
    val quantity: Int?
) : AirwallexModel, Parcelable {

    class Builder : ObjectBuilder<Product> {
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

        override fun build(): Product {
            return Product(
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
