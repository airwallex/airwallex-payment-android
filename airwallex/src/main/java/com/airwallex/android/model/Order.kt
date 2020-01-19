package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Order internal constructor(
    @SerializedName("products")
    val products: List<Product>? = null,

    @SerializedName("shipping")
    val shipping: Shipping? = null,

    @SerializedName("type")
    val type: String? = null
) : AirwallexModel, Parcelable {
    class Builder : ObjectBuilder<Order> {
        private var products: List<Product>? = null
        private var shipping: Shipping? = null
        private var type: String? = null

        fun setProducts(products: List<Product>?): Builder = apply {
            this.products = products
        }

        fun setShipping(shipping: Shipping?): Builder = apply {
            this.shipping = shipping
        }

        fun setType(type: String?): Builder = apply {
            this.type = type
        }

        override fun build(): Order {
            return Order(
                products = products,
                shipping = shipping,
                type = type
            )
        }
    }
}