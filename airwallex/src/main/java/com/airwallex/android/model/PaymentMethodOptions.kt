package com.airwallex.android.model

import android.os.Parcelable
import com.airwallex.android.model.parser.PaymentMethodOptionsParser
import kotlinx.parcelize.Parcelize

/**
 * Options for payment method
 */
@Parcelize
data class PaymentMethodOptions internal constructor(

    /**
     * The payment method options for card
     */
    val cardOptions: CardOptions? = null
) : AirwallexModel, AirwallexRequestModel, Parcelable {

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>()
            .plus(
                cardOptions?.let {
                    mapOf(PaymentMethodOptionsParser.FIELD_CARD_OPTIONS to it.toParamMap())
                }.orEmpty()
            )
    }

    class Builder : ObjectBuilder<PaymentMethodOptions> {
        private var cardOptions: CardOptions? = null

        fun setCardOptions(cardOptions: CardOptions?): Builder = apply {
            this.cardOptions = cardOptions
        }

        override fun build(): PaymentMethodOptions {
            return PaymentMethodOptions(
                cardOptions = cardOptions
            )
        }
    }

    @Parcelize
    data class CardOptions internal constructor(

        /**
         * Should capture automatically when confirm. Default to false. The payment intent will be captured automatically if it is true, and authorized only if it is false
         */
        val autoCapture: Boolean,

        /**
         * 3D Secure for card options
         */
        val threeDSecure: ThreeDSecure? = null

    ) : AirwallexModel, AirwallexRequestModel, Parcelable {

        override fun toParamMap(): Map<String, Any> {
            return mapOf<String, Any>(PaymentMethodOptionsParser.CardOptionsParser.FIELD_AUTO_CAPTURE to autoCapture)
                .plus(
                    threeDSecure?.let {
                        mapOf(PaymentMethodOptionsParser.CardOptionsParser.FIELD_THREE_DS to threeDSecure.toParamMap())
                    }.orEmpty()
                )
        }

        class Builder : ObjectBuilder<CardOptions> {
            private var autoCapture: Boolean = true
            private var threeDSecure: ThreeDSecure? = null

            fun setAutoCapture(autoCapture: Boolean): Builder = apply {
                this.autoCapture = autoCapture
            }

            fun setThreeDSecure(threeDSecure: ThreeDSecure): Builder = apply {
                this.threeDSecure = threeDSecure
            }

            override fun build(): CardOptions {
                return CardOptions(
                    autoCapture = autoCapture,
                    threeDSecure = threeDSecure
                )
            }
        }
    }
}
