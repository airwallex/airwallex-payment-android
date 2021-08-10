package com.airwallex.android.core

import android.os.Parcelable
import com.airwallex.android.core.model.ObjectBuilder
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.Shipping
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
     * Only applicable when next_triggered_by is customer and the payment_method.type is card.If true, the customer must provide cvc for the subsequent payment with this PaymentConsent.
     * Default: false
     */
    val requiresCVC: Boolean = false,

    /**
     * Indicate whether the subsequent payments are scheduled. Only applicable when next_triggered_by is merchant. One of scheduled, unscheduled.
     * Default: unscheduled
     */
    val merchantTriggerReason: PaymentConsent.MerchantTriggerReason = PaymentConsent.MerchantTriggerReason.UNSCHEDULED,

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
        private val customerId: String,
        private val nextTriggerBy: PaymentConsent.NextTriggeredBy
    ) : ObjectBuilder<AirwallexRecurringWithIntentSession> {

        private var requiresCVC: Boolean = false
        private var merchantTriggerReason: PaymentConsent.MerchantTriggerReason =
            PaymentConsent.MerchantTriggerReason.UNSCHEDULED

        fun setRequireCvc(requiresCVC: Boolean): Builder = apply {
            this.requiresCVC = requiresCVC
        }

        fun setMerchantTriggerReason(merchantTriggerReason: PaymentConsent.MerchantTriggerReason): Builder =
            apply {
                this.merchantTriggerReason = merchantTriggerReason
            }

        override fun build(): AirwallexRecurringWithIntentSession {
            if (paymentIntent.customerId == null) {
                throw Exception("Customer id is required if the PaymentIntent is created for recurring payment.")
            }
            return AirwallexRecurringWithIntentSession(
                paymentIntent = paymentIntent,
                nextTriggerBy = nextTriggerBy,
                requiresCVC = requiresCVC,
                customerId = customerId,
                currency = paymentIntent.currency,
                amount = paymentIntent.amount,
                shipping = paymentIntent.order?.shipping
            )
        }
    }
}
