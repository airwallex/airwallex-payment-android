package com.airwallex.android

import android.os.Parcelable
import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.Shipping
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
class AirwallexPaymentSession internal constructor(
    val paymentIntent: PaymentIntent,
    override val cvc: String? = null
) : AirwallexSession(), Parcelable {

    class Builder(
        private val paymentIntent: PaymentIntent,
    ) : ObjectBuilder<AirwallexPaymentSession> {

        private var cvc: String? = null

        fun setCvc(cvc: String?): Builder = apply {
            this.cvc = cvc
        }

        override fun build(): AirwallexPaymentSession {
            return AirwallexPaymentSession(
                paymentIntent = paymentIntent,
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
