package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Shipping internal constructor(

    @SerializedName("first_name")
    val firstName: String? = null,

    @SerializedName("last_name")
    val lastName: String? = null,

    @SerializedName("phone_number")
    val phone: String? = null,

    @SerializedName("date_of_birth")
    val dateOfBirth: String? = null,

    @SerializedName("address")
    val address: Address? = null
) : AirwallexModel, Parcelable {

    class Builder : ObjectBuilder<Shipping> {
        private var firstName: String? = null
        private var lastName: String? = null
        private var phone: String? = null
        private var dateOfBirth: String? = null
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

        fun setDateForBirth(dateOfBirth: String?): Builder = apply {
            this.dateOfBirth = dateOfBirth
        }

        fun setAddress(address: Address?): Builder = apply {
            this.address = address
        }

        override fun build(): Shipping {
            return Shipping(
                firstName = firstName,
                lastName = lastName,
                phone = phone,
                dateOfBirth = dateOfBirth,
                address = address
            )
        }
    }
}
