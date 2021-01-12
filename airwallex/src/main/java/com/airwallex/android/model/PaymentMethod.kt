package com.airwallex.android.model

import android.os.Parcelable
import com.airwallex.android.model.parser.PaymentMethodParser
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
    val id: String? = null,

    /**
     * Request id for the payment method
     */
    val requestId: String? = null,

    /**
     * Customer id for the payment method
     */
    val customerId: String? = null,

    /**
     * Type of the payment method. One of card, wechatpay
     */
    val type: PaymentMethodType? = null,

    /**
     * Card information for the payment method
     */
    val card: Card? = null,

    /**
     * WeChat
     */
    val weChatPayRequest: WeChatPayRequest? = null,

    /**
     * alipaycn
     */
    val aliPayCNRequest: AliPayRequest? = null,

    /**
     *  alipayhk
     */
    val aliPayHKRequest: AliPayRequest? = null,

    /**
     *  kakaopay
     */
    val kakaoPayRequest: AliPayRequest? = null,

    /**
     *  tng
     */
    val tngRequest: AliPayRequest? = null,

    /**
     *  dana
     */
    val danaRequest: AliPayRequest? = null,

    /**
     *  gcash
     */
    val gCashRequest: AliPayRequest? = null,

    /**
     * Billing information for the payment method
     */
    val billing: Billing? = null,

    /**
     * Status of the payment method, can be one of CREATED, VERIFIED, EXPIRED, INVALID
     */
    val status: PaymentMethodStatus? = null,

    /**
     * A set of key-value pairs that you can attach to the payment method
     */
    val metadata: @RawValue Map<String, Any?>? = null,

    /**
     * Time at which the payment method was created
     */
    val createdAt: Date? = null,

    /**
     * Last time at which the payment method was updated
     */
    val updatedAt: Date? = null

) : AirwallexModel, AirwallexRequestModel, Parcelable {

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>()
            .plus(
                type?.let {
                    mapOf(PaymentMethodParser.FIELD_TYPE to it.value)
                }.orEmpty()
            )
            .plus(
                weChatPayRequest?.let {
                    mapOf(PaymentMethodParser.FIELD_WECHAT_PAY_REQUEST to it.toParamMap())
                }.orEmpty()
            )
            .plus(
                aliPayCNRequest?.let {
                    mapOf(PaymentMethodParser.FIELD_ALI_PAY_CN_REQUEST to it.toParamMap())
                }.orEmpty()
            )
            .plus(
                aliPayHKRequest?.let {
                    mapOf(PaymentMethodParser.FIELD_ALI_PAY_HK_REQUEST to it.toParamMap())
                }.orEmpty()
            )
            .plus(
                kakaoPayRequest?.let {
                    mapOf(PaymentMethodParser.FIELD_KAO_KAO_PAY_REQUEST to it.toParamMap())
                }.orEmpty()
            )
            .plus(
                tngRequest?.let {
                    mapOf(PaymentMethodParser.FIELD_TNG_REQUEST to it.toParamMap())
                }.orEmpty()
            )
            .plus(
                danaRequest?.let {
                    mapOf(PaymentMethodParser.FIELD_DANA_REQUEST to it.toParamMap())
                }.orEmpty()
            )
            .plus(
                gCashRequest?.let {
                    mapOf(PaymentMethodParser.FIELD_GCASH_REQUEST to it.toParamMap())
                }.orEmpty()
            )
    }

    class Builder : ObjectBuilder<PaymentMethod> {
        private var id: String? = null
        private var requestId: String? = null
        private var customerId: String? = null
        private var type: PaymentMethodType? = null
        private var card: Card? = null
        private var weChatPayRequest: WeChatPayRequest? = null
        private var aliPayCNRequest: AliPayRequest? = null
        private var aliPayHKRequest: AliPayRequest? = null
        private var kakaoPayRequest: AliPayRequest? = null
        private var tngRequest: AliPayRequest? = null
        private var danaRequest: AliPayRequest? = null
        private var gCashRequest: AliPayRequest? = null

        private var billing: Billing? = null
        private var metadata: Map<String, Any?>? = null
        private var createdAt: Date? = null
        private var updatedAt: Date? = null
        private var status: PaymentMethodStatus? = null

        fun setId(id: String): Builder = apply {
            this.id = id
        }

        fun setRequestId(requestId: String?): Builder = apply {
            this.requestId = requestId
        }

        fun setCustomerId(customerId: String?): Builder = apply {
            this.customerId = customerId
        }

        fun setMetadata(metadata: Map<String, Any?>?): Builder = apply {
            this.metadata = metadata
        }

        fun setBilling(billing: Billing?): Builder = apply {
            this.billing = billing
        }

        fun setCard(card: Card?): Builder = apply {
            this.card = card
        }

        fun setWeChatPayRequest(weChatPayRequest: WeChatPayRequest?): Builder = apply {
            this.weChatPayRequest = weChatPayRequest
        }

        fun setAliPayCNRequest(aliPayCNRequest: AliPayRequest?): Builder = apply {
            this.aliPayCNRequest = aliPayCNRequest
        }

        fun setAliPayHkRequest(aliPayHKRequest: AliPayRequest?): Builder = apply {
            this.aliPayHKRequest = aliPayHKRequest
        }

        fun setKakaoPayRequest(kakaoPayRequest: AliPayRequest?): Builder = apply {
            this.kakaoPayRequest = kakaoPayRequest
        }

        fun setTngRequest(tngRequest: AliPayRequest?): Builder = apply {
            this.tngRequest = tngRequest
        }

        fun setDanaRequest(danaRequest: AliPayRequest?): Builder = apply {
            this.danaRequest = danaRequest
        }

        fun setGCashRequest(gCashRequest: AliPayRequest?): Builder = apply {
            this.gCashRequest = gCashRequest
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

        fun setStatus(status: PaymentMethodStatus?): Builder = apply {
            this.status = status
        }

        override fun build(): PaymentMethod {
            return PaymentMethod(
                id = id,
                requestId = requestId,
                customerId = customerId,
                billing = billing,
                card = card,
                type = type,
                weChatPayRequest = weChatPayRequest,
                aliPayCNRequest = aliPayCNRequest,
                aliPayHKRequest = aliPayHKRequest,
                kakaoPayRequest = kakaoPayRequest,
                tngRequest = tngRequest,
                danaRequest = danaRequest,
                gCashRequest = gCashRequest,
                metadata = metadata,
                createdAt = createdAt,
                updatedAt = updatedAt,
                status = status
            )
        }
    }

    /**
     * The status of a [PaymentMethod]
     */
    @Parcelize
    enum class PaymentMethodStatus(val value: String) : Parcelable {

        CREATED("CREATED"),

        VERIFIED("VERIFIED"),

        EXPIRED("EXPIRED"),

        INVALID("INVALID");

        internal companion object {
            internal fun fromValue(value: String?): PaymentMethodStatus? {
                return values().firstOrNull { it.value == value }
            }
        }
    }

    @Parcelize
    data class Card internal constructor(

        /**
         * CVC holder name
         */
        val cvc: String? = null,

        /**
         * Two digit number representing the card’s expiration month
         */
        val expiryMonth: String? = null,

        /**
         * Four digit number representing the card’s expiration year
         */
        val expiryYear: String? = null,

        /**
         * Card holder name
         */
        val name: String? = null,

        /**
         * Number of the card
         */
        val number: String? = null,

        /**
         * Bank identify number of this card
         */
        val bin: String? = null,

        /**
         * Last four digits of the card number
         */
        val last4: String? = null,

        /**
         * Brand of the card
         */
        val brand: String? = null,

        /**
         * Country of the card
         */
        val country: String? = null,

        /**
         * Funding of the card
         */
        val funding: String? = null,

        /**
         * Fingerprint of the card
         */
        val fingerprint: String? = null,

        /**
         * Whether CVC pass the check
         */
        val cvcCheck: String? = null,

        /**
         * Whether address pass the check
         */
        val avsCheck: String? = null,

        /**
         * Country code of the card issuer
         */
        val issuerCountryCode: String? = null,

        /**
         * Card type of the card
         */
        val cardType: String? = null

    ) : AirwallexModel, AirwallexRequestModel, Parcelable {

        override fun toParamMap(): Map<String, Any> {
            return mapOf<String, Any>()
                .plus(
                    cvc?.let {
                        mapOf(PaymentMethodParser.CardParser.FIELD_CVC to it)
                    }.orEmpty()
                )
                .plus(
                    expiryMonth?.let {
                        mapOf(PaymentMethodParser.CardParser.FIELD_EXPIRY_MONTH to it)
                    }.orEmpty()
                )
                .plus(
                    expiryYear?.let {
                        mapOf(PaymentMethodParser.CardParser.FIELD_EXPIRY_YEAR to it)
                    }.orEmpty()
                )
                .plus(
                    name?.let {
                        mapOf(PaymentMethodParser.CardParser.FIELD_NAME to it)
                    }.orEmpty()
                )
                .plus(
                    number?.let {
                        mapOf(PaymentMethodParser.CardParser.FIELD_NUMBER to it)
                    }.orEmpty()
                )
                .plus(
                    bin?.let {
                        mapOf(PaymentMethodParser.CardParser.FIELD_BIN to it)
                    }.orEmpty()
                )
                .plus(
                    last4?.let {
                        mapOf(PaymentMethodParser.CardParser.FIELD_LAST4 to it)
                    }.orEmpty()
                )
                .plus(
                    last4?.let {
                        mapOf(PaymentMethodParser.CardParser.FIELD_LAST4 to it)
                    }.orEmpty()
                )
                .plus(
                    brand?.let {
                        mapOf(PaymentMethodParser.CardParser.FIELD_BRAND to it)
                    }.orEmpty()
                )
                .plus(
                    country?.let {
                        mapOf(PaymentMethodParser.CardParser.FIELD_COUNTRY to it)
                    }.orEmpty()
                )
                .plus(
                    funding?.let {
                        mapOf(PaymentMethodParser.CardParser.FIELD_FUNDING to it)
                    }.orEmpty()
                )
                .plus(
                    fingerprint?.let {
                        mapOf(PaymentMethodParser.CardParser.FIELD_FINGERPRINT to it)
                    }.orEmpty()
                )
                .plus(
                    cvcCheck?.let {
                        mapOf(PaymentMethodParser.CardParser.FIELD_CVC_CHECK to it)
                    }.orEmpty()
                )
                .plus(
                    avsCheck?.let {
                        mapOf(PaymentMethodParser.CardParser.FIELD_AVS_CHECK to it)
                    }.orEmpty()
                )
                .plus(
                    issuerCountryCode?.let {
                        mapOf(PaymentMethodParser.CardParser.FIELD_ISSUER_COUNTRY_CODE to it)
                    }.orEmpty()
                )
                .plus(
                    cardType?.let {
                        mapOf(PaymentMethodParser.CardParser.FIELD_CARD_TYPE to it)
                    }.orEmpty()
                )
        }

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
