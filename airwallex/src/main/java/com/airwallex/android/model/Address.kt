package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.*
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address internal constructor(

    /**
     * Country code of the address. Use the two-character ISO Standard Country Codes
     */
    @SerializedName("country_code")
    val countryCode: String? = null,

    /**
     * State or province of the address
     */
    @SerializedName("state")
    val state: String? = null,

    /**
     * City of the address
     */
    @SerializedName("city")
    val city: String? = null,

    /**
     * Street of the address
     */
    @SerializedName("street")
    val street: String? = null,

    /**
     * Postcode of the address
     */
    @SerializedName("postcode")
    val postcode: String? = null
) : AirwallexModel, Parcelable {

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
