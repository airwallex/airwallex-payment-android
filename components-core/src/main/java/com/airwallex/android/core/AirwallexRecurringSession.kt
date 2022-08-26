package com.airwallex.android.core

import android.os.Parcelable
import com.airwallex.android.core.model.ObjectBuilder
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.Shipping
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

/**
 * For recurring payment (without create payment intent)
 */
@Suppress("LongParameterList")
@Parcelize
class AirwallexRecurringSession internal constructor(
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
     * Country code
     */
    override val countryCode: String,

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
    override val customerId: String,

    /**
     * The URL to redirect your customer back to after they authenticate or cancel their payment on the PaymentMethod’s app or site. If you’d prefer to redirect to a mobile application, you can alternatively supply an application URI scheme.
     */
    override val returnUrl: String?,

    /**
     * Google Pay options
     */
    override val googlePayOptions: GooglePayOptions? = null
) : AirwallexSession(), Parcelable {

    class Builder(
        private var customerId: String,
        private val currency: String,
        private val amount: BigDecimal,
        private val nextTriggerBy: PaymentConsent.NextTriggeredBy,
        private val countryCode: String
    ) : ObjectBuilder<AirwallexRecurringSession> {

        private var shipping: Shipping? = null
        private var requiresCVC: Boolean = false
        private var merchantTriggerReason: PaymentConsent.MerchantTriggerReason =
            PaymentConsent.MerchantTriggerReason.UNSCHEDULED
        private var returnUrl: String? = null

        fun setShipping(shipping: Shipping?): Builder = apply {
            this.shipping = shipping
        }

        fun setRequireCvc(requiresCVC: Boolean): Builder = apply {
            this.requiresCVC = requiresCVC
        }

        fun setMerchantTriggerReason(merchantTriggerReason: PaymentConsent.MerchantTriggerReason): Builder =
            apply {
                this.merchantTriggerReason = merchantTriggerReason
            }

        fun setReturnUrl(returnUrl: String?): Builder = apply {
            this.returnUrl = returnUrl
        }

        override fun build(): AirwallexRecurringSession {
            return AirwallexRecurringSession(
                nextTriggerBy = nextTriggerBy,
                requiresCVC = requiresCVC,
                merchantTriggerReason = merchantTriggerReason,
                currency = currency,
                countryCode = countryCode,
                amount = amount,
                shipping = shipping,
                customerId = customerId,
                returnUrl = returnUrl
            )
        }
    }
}
