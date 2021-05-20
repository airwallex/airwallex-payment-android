package com.airwallex.android

import android.os.Parcelable
import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.PaymentConsent
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.Shipping
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

/**
 * For recurring payment (need create payment intent)
 */
@Parcelize
class AirwallexRecurringWithIntentSession internal constructor(

    /**
     * the ID of the [PaymentIntent], required.
     */
    val paymentIntent: PaymentIntent,

    /**
     * The party to trigger subsequent payments. Can be one of merchant, customer. required.
     */
    val nextTriggerBy: PaymentConsent.NextTriggeredBy,

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
     * It is required if the PaymentIntent is created for recurring payment
     */
    override val customerId: String
) : AirwallexSession(), Parcelable {

    class Builder(
        private val paymentIntent: PaymentIntent,
        private val nextTriggerBy: PaymentConsent.NextTriggeredBy,
    ) : ObjectBuilder<AirwallexRecurringWithIntentSession> {

        override fun build(): AirwallexRecurringWithIntentSession {
            if (paymentIntent.customerId == null) {
                throw Exception("Customer id is required if the PaymentIntent is created for recurring payment.")
            }
            return AirwallexRecurringWithIntentSession(
                paymentIntent = paymentIntent,
                nextTriggerBy = nextTriggerBy,
                customerId = paymentIntent.customerId,
                currency = paymentIntent.currency,
                amount = paymentIntent.amount,
                shipping = paymentIntent.order?.shipping
            )
        }
    }
}
