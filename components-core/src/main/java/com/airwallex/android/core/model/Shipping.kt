package com.airwallex.android.core.model

import android.os.Parcelable
import com.airwallex.android.core.model.parser.ShippingParser
import kotlinx.parcelize.Parcelize

/**
 * Shipping information
 */
@Parcelize
data class Shipping internal constructor(

    /**
     * First name of the recipient
     */
    val firstName: String? = null,

    /**
     * Last name of the recipient
     */
    val lastName: String? = null,

    /**
     * Phone number of the recipient
     */
    val phoneNumber: String? = null,

    /**
     * Shipping method for the product
     */
    val shippingMethod: String? = null,

    val address: Address? = null
) : AirwallexModel, AirwallexRequestModel, Parcelable {

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>()
            .plus(
                firstName?.let {
                    mapOf(ShippingParser.FIELD_FIRST_NAME to it)
                }.orEmpty()
            )
            .plus(
                lastName?.let {
                    mapOf(ShippingParser.FIELD_LAST_NAME to it)
                }.orEmpty()
            )
            .plus(
                phoneNumber?.let {
                    mapOf(ShippingParser.FIELD_PHONE_NUMBER to it)
                }.orEmpty()
            )
            .plus(
                shippingMethod?.let {
                    mapOf(ShippingParser.FIELD_SHIPPING_METHOD to it)
                }.orEmpty()
            )
            .plus(
                address?.let {
                    mapOf(ShippingParser.FIELD_ADDRESS to it.toParamMap())
                }.orEmpty()
            )
    }

    class Builder : ObjectBuilder<Shipping> {
        private var firstName: String? = null
        private var lastName: String? = null
        private var phoneNumber: String? = null
        private var shippingMethod: String? = null
        private var address: Address? = null

        fun setFirstName(firstName: String?): Builder = apply {
            this.firstName = firstName
        }

        fun setLastName(lastName: String?): Builder = apply {
            this.lastName = lastName
        }

        fun setPhone(phone: String?): Builder = apply {
            this.phoneNumber = phone
        }

        fun setShippingMethod(shippingMethod: String?): Builder = apply {
            this.shippingMethod = shippingMethod
        }

        fun setAddress(address: Address?): Builder = apply {
            this.address = address
        }

        override fun build(): Shipping {
            return Shipping(
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phoneNumber,
                shippingMethod = shippingMethod,
                address = address
            )
        }
    }
}
