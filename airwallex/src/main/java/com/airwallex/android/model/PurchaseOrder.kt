package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PurchaseOrder internal constructor(

    // Product list
    @SerializedName("products")
    val products: List<PhysicalProduct>? = null,

    // shipping address
    @SerializedName("shipping")
    val shipping: Shipping? = null,

    // Industry category of the order
    @SerializedName("type")
    val type: String? = null
) : AirwallexModel, Parcelable {
    class Builder : ObjectBuilder<PurchaseOrder> {
        private var products: List<PhysicalProduct>? = null
        private var shipping: Shipping? = null
        private var type: String? = null

        fun setProducts(products: List<PhysicalProduct>?): Builder = apply {
            this.products = products
        }

        fun setShipping(shipping: Shipping?): Builder = apply {
            this.shipping = shipping
        }

        fun setType(type: String?): Builder = apply {
            this.type = type
        }

        override fun build(): PurchaseOrder {
            return PurchaseOrder(
                products = products,
                shipping = shipping,
                type = type
            )
        }
    }
}
