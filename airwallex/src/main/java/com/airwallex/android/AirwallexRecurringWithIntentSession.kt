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
    val nextTriggerBy: PaymentConsent.NextTriggeredBy
) : AirwallexSession(), Parcelable {

    class Builder(
        private val paymentIntent: PaymentIntent,
        private val nextTriggerBy: PaymentConsent.NextTriggeredBy,
    ) : ObjectBuilder<AirwallexRecurringWithIntentSession> {

        override fun build(): AirwallexRecurringWithIntentSession {
            return AirwallexRecurringWithIntentSession(
                paymentIntent = paymentIntent,
                nextTriggerBy = nextTriggerBy
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
