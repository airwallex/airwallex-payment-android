package com.airwallex.android.core.model

import android.os.Parcelable
import com.airwallex.android.core.model.parser.PaymentMethodParser
import kotlinx.parcelize.Parcelize

@Parcelize
class PaymentMethodRequest(

    /**
     * Unique identifier for the payment method
     */
    val id: String? = null,

    /**
     * Type of the payment method
     */
    val type: String,

    /**
     * Payment Request
     */
    val paymentRequest: AirwallexPaymentRequest? = null,

    /**
     * Card information for the payment method
     */
    val card: PaymentMethod.Card? = null,

    /**
     * Google Pay information for the payment method
     */
    val googlePay: PaymentMethod.GooglePay? = null,

    /**
     * Billing information for the payment method
     */
    val billing: Billing? = null

) : AirwallexRequestModel, Parcelable {

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>()
            .plus(
                id?.let {
                    mapOf(PaymentMethodParser.FIELD_ID to it)
                }.orEmpty()
            )
            .plus(
                mapOf(PaymentMethodParser.FIELD_TYPE to type)
            )
            .plus(
                paymentRequest?.let { request ->
                    mapOf(
                        type to request.toParamMap().plus(
                            billing?.let {
                                mapOf(PaymentMethodParser.FIELD_BILLING to it.toParamMap())
                            }.orEmpty()
                        )
                    )
                }.orEmpty()
            )
            .plus(
                card?.let { card ->
                    mapOf(
                        PaymentMethodParser.FIELD_CARD to card.toParamMap().plus(
                            billing?.let {
                                mapOf(PaymentMethodParser.FIELD_BILLING to it.toParamMap())
                            }.orEmpty()
                        )
                    )
                }.orEmpty()
            ).plus(
                googlePay?.let { googlePay ->
                    mapOf(
                        PaymentMethodParser.FIELD_GOOGLE_PAY to googlePay.toParamMap()
                    )
                }.orEmpty()
            )
    }

    class Builder(
        val type: String
    ) : ObjectBuilder<PaymentMethodRequest> {
        private var paymentRequest: AirwallexPaymentRequest? = null

        private var card: PaymentMethod.Card? = null

        private var billing: Billing? = null

        private var googlePay: PaymentMethod.GooglePay? = null

        fun setThirdPartyPaymentMethodRequest(
            additionalInfo: Map<String, String>? = null,
            flow: AirwallexPaymentRequestFlow? = null
        ): Builder = apply {
            if (type != PaymentMethodType.CARD.value && type != PaymentMethodType.GOOGLEPAY.value) {
                paymentRequest = AirwallexPaymentRequest(
                    additionalInfo,
                    flow
                )
            }
        }

        fun setCardPaymentMethodRequest(
            card: PaymentMethod.Card?,
            billing: Billing?
        ): Builder = apply {
            this.card = card
            this.billing = billing
        }

        fun setGooglePayPaymentMethodRequest(
            googlePay: PaymentMethod.GooglePay?
        ): Builder = apply {
            this.googlePay = googlePay
        }

        override fun build(): PaymentMethodRequest {
            return PaymentMethodRequest(
                type = type,
                paymentRequest = paymentRequest,
                card = card,
                googlePay = googlePay,
                billing = billing
            )
        }
    }
}
