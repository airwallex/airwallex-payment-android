package com.airwallex.android

import android.os.Parcelable
import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.PaymentConsent
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.Shipping
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
class AirwallexRecurringWithIntentSession internal constructor(
    val paymentIntent: PaymentIntent,
    val nextTriggerBy: PaymentConsent.NextTriggeredBy,
    override val cvc: String? = null,
) : AirwallexSession(), Parcelable {

    class Builder(
        private val paymentIntent: PaymentIntent,
        private val nextTriggerBy: PaymentConsent.NextTriggeredBy,
    ) : ObjectBuilder<AirwallexRecurringWithIntentSession> {

        private var cvc: String? = null

        fun setCvc(cvc: String?): Builder = apply {
            this.cvc = cvc
        }

        override fun build(): AirwallexRecurringWithIntentSession {
            return AirwallexRecurringWithIntentSession(
                paymentIntent = paymentIntent,
                nextTriggerBy = nextTriggerBy,
                cvc = cvc
            )
        }
    }

    override val customerId: String?
        get() = paymentIntent.customerId

    override val shipping: Shipping?
        get() = paymentIntent.order?.shipping

    override val currency: String
        get() = paymentIntent.currency

    override val amount: BigDecimal
        get() = paymentIntent.amount
}
