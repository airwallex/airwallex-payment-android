package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Billing internal constructor(

    @SerializedName("first_name")
    val firstName: String? = null,

    @SerializedName("last_name")
    val lastName: String? = null,

    @SerializedName("phone_number")
    val phone: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("address")
    val address: Address? = null
) : AirwallexModel, Parcelable {

    class Builder : ObjectBuilder<Billing> {
        private var firstName: String? = null
        private var lastName: String? = null
        private var phone: String? = null
        private var email: String? = null
        private var address: Address? = null

        fun setFirstName(firstName: String?): Builder = apply {
            this.firstName = firstName
        }

        fun setLastName(lastName: String?): Builder = apply {
            this.lastName = lastName
        }

        fun setPhone(phone: String?): Builder = apply {
            this.phone = phone
        }

        fun setEmail(email: String?): Builder = apply {
            this.email = email
        }

        fun setAddress(address: Address?): Builder = apply {
            this.address = address
        }

        override fun build(): Billing {
            return Billing(
                firstName = firstName,
                lastName = lastName,
                phone = phone,
                email = email,
                address = address
            )
        }
    }
}
