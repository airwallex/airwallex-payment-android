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
     * Redirect Request
     */
    val redirectRequest: AirwallexPaymentRequest? = null,

    /**
     * Card information for the payment method
     */
    val card: PaymentMethod.Card? = null,

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
                redirectRequest?.let {
                    mapOf(type to it.toParamMap())
                }.orEmpty()
            )
            .plus(
                card?.let {
                    mapOf(PaymentMethodParser.FIELD_CARD to it.toParamMap())
                }.orEmpty()
            )
            .plus(
                billing?.let {
                    mapOf(PaymentMethodParser.FIELD_BILLING to it.toParamMap())
                }.orEmpty()
            )
    }

    class Builder(
        val type: String
    ) : ObjectBuilder<PaymentMethodRequest> {
        private var redirectRequest: AirwallexPaymentRequest? = null

        private var card: PaymentMethod.Card? = null

        private var billing: Billing? = null

        fun setThirdPartyPaymentMethodRequest(
            additionalInfo: Map<String, String>? = null
        ): Builder = apply {
            if (type != PaymentMethodType.CARD.value) {
                redirectRequest = AirwallexPaymentRequest(
                    additionalInfo
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

        override fun build(): PaymentMethodRequest {
            return PaymentMethodRequest(
                type = type,
                redirectRequest = redirectRequest,
                card = card,
                billing = billing
            )
        }
    }
}
