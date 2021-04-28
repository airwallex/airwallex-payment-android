package com.airwallex.android

import android.os.Parcelable
import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.PaymentConsent
import com.airwallex.android.model.Shipping
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
class AirwallexRecurringSession internal constructor(
    val nextTriggerBy: PaymentConsent.NextTriggeredBy,
    override val currency: String,
    override val amount: BigDecimal,
    override val cvc: String? = null,
    override val shipping: Shipping? = null,
    override val customerId: String? = null
) : AirwallexSession(), Parcelable {

    class Builder(
        private val nextTriggerBy: PaymentConsent.NextTriggeredBy,
        private val currency: String,
        private val amount: BigDecimal
    ) : ObjectBuilder<AirwallexRecurringSession> {

        private var cvc: String? = null
        private var shipping: Shipping? = null
        private var customerId: String? = null

        fun setCvc(cvc: String?): Builder = apply {
            this.cvc = cvc
        }

        fun setShipping(shipping: Shipping?): Builder = apply {
            this.shipping = shipping
        }

        fun setCustomerId(customerId: String?): Builder = apply {
            this.customerId = customerId
        }

        override fun build(): AirwallexRecurringSession {
            return AirwallexRecurringSession(
                nextTriggerBy = nextTriggerBy,
                currency = currency,
                amount = amount,
                cvc = cvc,
                shipping = shipping,
                customerId = customerId
            )
        }
    }
}
