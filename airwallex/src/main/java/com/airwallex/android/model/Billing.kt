package com.airwallex.android.model

import android.os.Parcelable
import com.airwallex.android.model.parser.BillingParser
import kotlinx.android.parcel.Parcelize

/**
 * Billing information.
 */
@Parcelize
data class Billing internal constructor(

    /**
     * First name of the customer
     */
    val firstName: String? = null,

    /**
     * Last name of the customer
     */
    val lastName: String? = null,

    /**
     * Phone number of the customer
     */
    val phone: String? = null,

    /**
     * Email address of the customer
     */
    val email: String? = null,

    /**
     * Date of birth of the customer in the format: YYYY-MM-DD
     */
    val dateOfBirth: String? = null,

    /**
     * The billing address as it appears on the credit card issuer’s records
     */
    val address: Address? = null
) : AirwallexModel, AirwallexRequestModel, Parcelable {

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>()
            .plus(
                firstName?.let {
                    mapOf(BillingParser.FIELD_FIRST_NAME to it)
                }.orEmpty()
            )
            .plus(
                lastName?.let {
                    mapOf(BillingParser.FIELD_LAST_NAME to it)
                }.orEmpty()
            )
            .plus(
                phone?.let {
                    mapOf(BillingParser.FIELD_PHONE to it)
                }.orEmpty()
            )
            .plus(
                email?.let {
                    mapOf(BillingParser.FIELD_EMAIL to it)
                }.orEmpty()
            )
            .plus(
                dateOfBirth?.let {
                    mapOf(BillingParser.FIELD_DATE_OF_BIRTH to it)
                }.orEmpty()
            )
            .plus(
                address?.let {
                    mapOf(BillingParser.FIELD_ADDRESS to it.toParamMap())
                }.orEmpty()
            )
    }

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
