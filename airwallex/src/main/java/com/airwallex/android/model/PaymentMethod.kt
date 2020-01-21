package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentMethod internal constructor(

    @SerializedName("billing")
    val billing: Billing?,

    @SerializedName("card")
    val card: Card?,

    @SerializedName("type")
    val type: PaymentMethodType?

) : AirwallexModel, Parcelable {


    class Builder : ObjectBuilder<PaymentMethod> {
        private var billing: Billing? = null
        private var card: Card? = null
        private var type: PaymentMethodType? = null

        fun setBilling(billing: Billing?): Builder = apply {
            this.billing = billing
        }

        fun setCard(card: Card?): Builder = apply {
            this.card = card
        }

        fun setType(type: PaymentMethodType?): Builder = apply {
            this.type = type
        }

        override fun build(): PaymentMethod {
            return PaymentMethod(
                billing = billing,
                card = card,
                type = type
            )
        }
    }

    @Parcelize
    data class Card internal constructor(

        @SerializedName("cvc")
        val cvc: String?,

        @SerializedName("exp_month")
        val expMonth: String?,

        @SerializedName("exp_year")
        val expYear: String?,

        @SerializedName("name")
        val name: String?,

        @SerializedName("number")
        val number: String?
    ) : AirwallexModel, Parcelable {

        class Builder : ObjectBuilder<Card> {
            private var cvc: String? = null
            private var expMonth: String? = null
            private var expYear: String? = null
            private var name: String? = null
            private var number: String? = null

            fun setCvc(cvc: String?): Builder = apply {
                this.cvc = cvc
            }

            fun setExpMonth(expMonth: String?): Builder = apply {
                this.expMonth = expMonth
            }

            fun setExpYear(expYear: String?): Builder = apply {
                this.expYear = expYear
            }

            fun setName(name: String?): Builder = apply {
                this.name = name
            }

            fun setNumber(number: String?): Builder = apply {
                this.number = number
            }

            override fun build(): Card {
                return Card(
                    cvc = cvc,
                    expMonth = expMonth,
                    expYear = expYear,
                    name = name,
                    number = number
                )
            }
        }
    }

    @Parcelize
    data class Billing internal constructor(

        @SerializedName("first_name")
        val firstName: String? = null,

        @SerializedName("last_name")
        val lastName: String? = null,

        @SerializedName("phone_number")
        val phone: String? = null,

        @SerializedName("date_of_birth")
        val dateOfBirth: String? = null,

        @SerializedName("email")
        val email: String? = null,

        @SerializedName("address")
        val address: Address? = null
    ) : AirwallexModel, Parcelable {

        class Builder : ObjectBuilder<Billing> {
            private var firstName: String? = null
            private var lastName: String? = null
            private var phone: String? = null
            private var dateOfBirth: String? = null
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

            fun setDateForBirth(dateOfBirth: String?): Builder = apply {
                this.dateOfBirth = dateOfBirth
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
                    dateOfBirth = dateOfBirth,
                    email = email,
                    address = address
                )
            }
        }
    }

}