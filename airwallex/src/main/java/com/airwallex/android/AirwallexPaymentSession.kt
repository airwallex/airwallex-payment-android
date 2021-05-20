package com.airwallex.android

import android.os.Parcelable
import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.Shipping
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

/**
 * For one-off payment
 */
@Parcelize
class AirwallexPaymentSession internal constructor(
    /**
     * the ID of the [PaymentIntent], required.
     */
    val paymentIntent: PaymentIntent,

    /**
     * Amount currency. required.
     */
    override val currency: String,

    /**
     * Payment amount. This is the order amount you would like to charge your customer. required.
     */
    override val amount: BigDecimal,

    /**
     * Shipping information. optional
     */
    override val shipping: Shipping? = null,

    /**
     * The Customer who is paying for this PaymentIntent. This field is not required if the Customer is unknown (guest checkout).
     */
    override val customerId: String? = null

) : AirwallexSession(), Parcelable {

    class Builder(
        private val paymentIntent: PaymentIntent,
    ) : ObjectBuilder<AirwallexPaymentSession> {

        override fun build(): AirwallexPaymentSession {
            return AirwallexPaymentSession(
                paymentIntent = paymentIntent,
                currency = paymentIntent.currency,
                amount = paymentIntent.amount,
                shipping = paymentIntent.order?.shipping,
                customerId = paymentIntent.customerId
            )
        }
    }
}
