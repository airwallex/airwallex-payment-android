package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.util.*

/**
 * A PaymentMethod represents the funding source that is used by your customer when making a
 * payment. You may create and add multiple payment methods to a customer as saved payment methods
 * to help streamline your customers' checkout experience.
 */
@Parcelize
data class PaymentMethod internal constructor(

    /**
     * Unique identifier for the payment method
     */
    @SerializedName("id")
    val id: String,

    /**
     * Request id for the payment method
     */
    @SerializedName("request_id")
    val requestId: String?,

    /**
     * Customer id for the payment method
     */
    @SerializedName("customer_id")
    val customerId: String?,

    /**
     * Type of the payment method. One of card, wechatpay
     */
    @SerializedName("type")
    val type: PaymentMethodType,

    /**
     * The card info
     */
    @SerializedName("card")
    val card: Card?,

    /**
     * The wechat request that contains Wechat pay flow
     */
    @SerializedName("wechatpay")
    val wechatPayRequest: WechatPayRequest?,

    /**
     * Billing info for the payment method
     */
    @SerializedName("billing")
    val billing: Billing?,

    /**
     * A set of key-value pairs that you can attach to the payment method
     */
    @SerializedName("metadata")
    val metadata: @RawValue Map<String, Any>?,

    /**
     * Time at which the payment method was created
     */
    @SerializedName("created_at")
    val createdAt: Date?,

    /**
     * Last time at which the payment method was updated
     */
    @SerializedName("updated_at")
    val updatedAt: Date?

) : AirwallexModel, Parcelable {

    class Builder : ObjectBuilder<PaymentMethod> {
        private var id: String = ""
        private var requestId: String = ""
        private var customerId: String? = null
        private var type: PaymentMethodType = PaymentMethodType.CARD
        private var card: Card? = null
        private var wechatPayFlow: WechatPayRequest? = null
        private var billing: Billing? = null
        private var metadata: Map<String, Any>? = null
        private var createdAt: Date? = null
        private var updatedAt: Date? = null

        fun setId(id: String): Builder = apply {
            this.id = id
        }

        fun setRequestId(requestId: String): Builder = apply {
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

        fun setWechatPayFlow(wechatPayFlow: WechatPayRequest?): Builder = apply {
            this.wechatPayFlow = wechatPayFlow
        }

        fun setType(type: PaymentMethodType): Builder = apply {
            this.type = type
        }

        fun setCreatedAt(createdAt: Date?): Builder = apply {
            this.createdAt = createdAt
        }

        fun setUpdatedAt(updatedAt: Date?): Builder = apply {
            this.updatedAt = updatedAt
        }

        override fun build(): PaymentMethod {
            return PaymentMethod(
                id = id,
                requestId = requestId,
                customerId = customerId,
                billing = billing,
                card = card,
                type = type,
                wechatPayRequest = wechatPayFlow,
                metadata = metadata,
                createdAt = createdAt,
                updatedAt = updatedAt
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
}
