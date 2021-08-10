package com.airwallex.android.core.model

import android.os.Parcelable
import com.airwallex.android.core.model.parser.PurchaseOrderParser
import kotlinx.parcelize.Parcelize

/**
 * Purchase order information
 */
@Parcelize
data class PurchaseOrder internal constructor(

    /**
     * Product list
     */
    val products: List<PhysicalProduct>? = null,

    /**
     * Shipping address
     */
    val shipping: Shipping? = null,

    /**
     * Industry category of the order
     */
    val type: String? = null
) : AirwallexModel, AirwallexRequestModel, Parcelable {

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>()
            .plus(
                products?.let {
                    mapOf(
                        PurchaseOrderParser.FIELD_PRODUCTS to
                            it.map { product ->
                                product.toParamMap()
                            }
                    )
                }.orEmpty()
            )
            .plus(
                shipping?.let {
                    mapOf(PurchaseOrderParser.FIELD_SHIPPING to it.toParamMap())
                }.orEmpty()
            )
            .plus(
                type?.let {
                    mapOf(PurchaseOrderParser.FIELD_TYPE to it)
                }.orEmpty()
            )
    }

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
