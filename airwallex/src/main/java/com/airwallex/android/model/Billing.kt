package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Billing internal constructor(

    /**
     * First name of the customer
     */
    @SerializedName("first_name")
    val firstName: String? = null,

    /**
     * Last name of the customer
     */
    @SerializedName("last_name")
    val lastName: String? = null,

    /**
     * Phone number of the customer
     */
    @SerializedName("phone_number")
    val phone: String? = null,

    /**
     * Email address of the customer
     */
    @SerializedName("email")
    val email: String? = null,

    /**
     * Date of birth of the customer in the format: YYYY-MM-DD
     */
    @SerializedName("date_of_birth")
    val dateOfBirth: String? = null,

    /**
     * Address of the billing info
     */
    @SerializedName("address")
    val address: Address? = null
) : AirwallexModel, Parcelable {

    class Builder : ObjectBuilder<Billing> {
        private var firstName: String? = null
        private var lastName: String? = null
        private var phone: String? = null
        private var email: String? = null
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

        fun setEmail(email: String?): Builder = apply {
            this.email = email
        }

        fun setDateOfBirth(dateOfBirth: String?): Builder = apply {
            this.dateOfBirth = dateOfBirth
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
                dateOfBirth = dateOfBirth,
                address = address
            )
        }
    }
}
