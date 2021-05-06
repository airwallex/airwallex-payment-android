package com.airwallex.android.model

import android.os.Parcelable
import com.airwallex.android.model.parser.AddressParser
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * Address information.
 */
@Parcelize
data class Address internal constructor(

    /**
     * Country code of the address. Use the two-character ISO Standard Country Codes
     */
    val countryCode: String? = null,

    /**
     * State or province of the address
     */
    val state: String? = null,

    /**
     * City of the address
     */
    val city: String? = null,

    /**
     * Street of the address
     */
    val street: String? = null,

    /**
     * Postcode of the address
     */
    val postcode: String? = null
) : AirwallexModel, AirwallexRequestModel, Parcelable {

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>()
            .plus(
                countryCode?.let {
                    mapOf(AddressParser.FIELD_COUNTRY_CODE to it)
                }.orEmpty()
            )
            .plus(
                state?.let {
                    mapOf(AddressParser.FIELD_STATE to it)
                }.orEmpty()
            )
            .plus(
                city?.let {
                    mapOf(AddressParser.FIELD_CITY to it)
                }.orEmpty()
            )
            .plus(
                street?.let {
                    mapOf(AddressParser.FIELD_STREET to it)
                }.orEmpty()
            )
            .plus(
                postcode?.let {
                    mapOf(AddressParser.FIELD_POSTCODE to it)
                }.orEmpty()
            )
    }

    class Builder : ObjectBuilder<Address> {
        private var countryCode: String? = null
        private var state: String? = null
        private var city: String? = null
        private var street: String? = null
        private var postcode: String? = null

        fun setCountryCode(countryCode: String?): Builder = apply {
            this.countryCode = countryCode?.toUpperCase(Locale.ROOT)
        }

        fun setState(state: String?): Builder = apply {
            this.state = state
        }

        fun setCity(city: String?): Builder = apply {
            this.city = city
        }

        fun setStreet(street: String?): Builder = apply {
            this.street = street
        }

        fun setPostcode(postcode: String?): Builder = apply {
            this.postcode = postcode
        }

        override fun build(): Address {
            return Address(
                countryCode = countryCode,
                state = state,
                city = city,
                street = street,
                postcode = postcode
            )
        }
    }
}
