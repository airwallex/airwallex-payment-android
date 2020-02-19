package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class PaymentMethod internal constructor(

    @SerializedName("id")
    val id: String?,

    @SerializedName("request_id")
    val requestId: String?,

    @SerializedName("customer_id")
    val customerId: String?,

    @SerializedName("type")
    val type: PaymentMethodType?,

    @SerializedName("card")
    val card: Card?,

    @SerializedName("wechatpay")
    val wechatPayFlow: WechatPayFlow?,

    @SerializedName("billing")
    val billing: Billing?,

    @SerializedName("metadata")
    val metadata: @RawValue Map<String, Any>?

) : AirwallexModel, Parcelable {

    class Builder : ObjectBuilder<PaymentMethod> {
        private var id: String? = null
        private var requestId: String? = null
        private var customerId: String? = null
        private var type: PaymentMethodType? = null
        private var card: Card? = null
        private var wechatPayFlow: WechatPayFlow? = null
        private var billing: Billing? = null
        private var metadata: Map<String, Any>? = null

        fun setId(id: String?): Builder = apply {
            this.id = id
        }

        fun setRequestId(requestId: String?): Builder = apply {
            this.requestId = requestId
        }

        fun setCustomerId(customerId: String?): Builder = apply {
            this.customerId = customerId
        }

        fun setMetadata(metadata: Map<String, Any>?): Builder = apply {
            this.metadata = metadata
        }

        fun setBilling(billing: Billing?): Builder = apply {
            this.billing = billing
        }

        fun setCard(card: Card?): Builder = apply {
            this.card = card
        }

        fun setWechatPayFlow(wechatPayFlow: WechatPayFlow?): Builder = apply {
            this.wechatPayFlow = wechatPayFlow
        }

        fun setType(type: PaymentMethodType?): Builder = apply {
            this.type = type
        }

        override fun build(): PaymentMethod {
            return PaymentMethod(
                id = id,
                requestId = requestId,
                customerId = customerId,
                billing = billing,
                card = card,
                type = type,
                wechatPayFlow = wechatPayFlow,
                metadata = metadata
            )
        }
    }

    @Parcelize
    data class Card internal constructor(

        @SerializedName("cvc")
        val cvc: String?,

        @SerializedName("expiry_month")
        val expiryMonth: String?,

        @SerializedName("expiry_year")
        val expiryYear: String?,

        @SerializedName("name")
        val name: String?,

        @SerializedName("number")
        val number: String?,

        @SerializedName("bin")
        val bin: String?,

        @SerializedName("last4")
        val last4: String?,

        @SerializedName("brand")
        val brand: String?,

        @SerializedName("country")
        val country: String?,

        @SerializedName("funding")
        val funding: String?,

        @SerializedName("fingerprint")
        val fingerprint: String?,

        @SerializedName("cvc_check")
        val cvcCheck: String?,

        @SerializedName("avs_check")
        val avsCheck: String?,

        @SerializedName("issuer_country_code")
        val issuerCountryCode: String?,

        @SerializedName("card_type")
        val cardType: String?

    ) : AirwallexModel, Parcelable {

        class Builder : ObjectBuilder<Card> {
            private var cvc: String? = null
            private var expiryMonth: String? = null
            private var expiryYear: String? = null
            private var name: String? = null
            private var number: String? = null
            private var bin: String? = null
            private var last4: String? = null
            private var brand: String? = null
            private var country: String? = null
            private var funding: String? = null
            private var fingerprint: String? = null
            private var cvcCheck: String? = null
            private var avsCheck: String? = null
            private var issuerCountryCode: String? = null
            private var cardType: String? = null
            fun setCvc(cvc: String?): Builder = apply {
                this.cvc = cvc
            }

            fun setExpiryMonth(expiryMonth: String?): Builder = apply {
                this.expiryMonth = expiryMonth
            }

            fun setExpiryYear(expiryYear: String?): Builder = apply {
                this.expiryYear = expiryYear
            }

            fun setName(name: String?): Builder = apply {
                this.name = name
            }

            fun setNumber(number: String?): Builder = apply {
                this.number = number
            }

            fun setBin(bin: String?): Builder = apply {
                this.bin = bin
            }

            fun setLast4(last4: String?): Builder = apply {
                this.last4 = last4
            }

            fun setBrand(brand: String?): Builder = apply {
                this.brand = brand
            }

            fun setCountry(country: String?): Builder = apply {
                this.country = country
            }

            fun setFunding(funding: String?): Builder = apply {
                this.funding = funding
            }

            fun setFingerprint(fingerprint: String?): Builder = apply {
                this.fingerprint = fingerprint
            }

            fun setCvcCheck(cvcCheck: String?): Builder = apply {
                this.cvcCheck = cvcCheck
            }

            fun setAvsCheck(avsCheck: String?): Builder = apply {
                this.avsCheck = avsCheck
            }

            fun setIssuerCountryCode(issuerCountryCode: String?): Builder = apply {
                this.issuerCountryCode = issuerCountryCode
            }

            fun setCardType(cardType: String?): Builder = apply {
                this.cardType = cardType
            }

            override fun build(): Card {
                return Card(
                    cvc = cvc,
                    expiryMonth = expiryMonth,
                    expiryYear = expiryYear,
                    name = name,
                    number = number,
                    bin = bin,
                    last4 = last4,
                    brand = brand,
                    country = country,
                    funding = funding,
                    fingerprint = fingerprint,
                    cvcCheck = cvcCheck,
                    avsCheck = avsCheck,
                    issuerCountryCode = issuerCountryCode,
                    cardType = cardType
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