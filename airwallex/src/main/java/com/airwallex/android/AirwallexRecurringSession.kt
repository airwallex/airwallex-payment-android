package com.airwallex.android

import android.os.Parcelable
import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.PaymentConsent
import com.airwallex.android.model.Shipping
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

/**
 * For recurring payment (without create payment intent)
 */
@Parcelize
class AirwallexRecurringSession internal constructor(
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
        private var customerId: String,
        private val currency: String,
        private val amount: BigDecimal,
        private val nextTriggerBy: PaymentConsent.NextTriggeredBy
    ) : ObjectBuilder<AirwallexRecurringSession> {

        private var shipping: Shipping? = null

        fun setShipping(shipping: Shipping?): Builder = apply {
            this.shipping = shipping
        }

        override fun build(): AirwallexRecurringSession {
            return AirwallexRecurringSession(
                nextTriggerBy = nextTriggerBy,
                currency = currency,
                amount = amount,
                shipping = shipping,
                customerId = customerId
            )
        }
    }
}
