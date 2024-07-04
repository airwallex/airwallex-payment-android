package com.airwallex.android.core

import android.os.Parcelable
import com.airwallex.android.core.model.ObjectBuilder
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.Shipping
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

/**
 * For one-off payment
 */
@Suppress("LongParameterList")
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
     * Whether or not billing information is required for card payments. When set to `false`, any billing information will be ignored.
     */
    override val isBillingInformationRequired: Boolean = true,

    /**
     * Whether or not email is required for card payments. Set to 'true' if you'd like to collect customers' email
     */
    override val isEmailRequired: Boolean = false,

    /**
     * The Customer who is paying for this PaymentIntent. This field is not required if the Customer is unknown (guest checkout).
     */
    override val customerId: String? = null,

    /**
     * The URL to redirect your customer back to after they authenticate or cancel their payment on the PaymentMethod’s app or site. If you’d prefer to redirect to a mobile application, you can alternatively supply an application URI scheme.
     */
    override val returnUrl: String?,

    /**
     * Google Pay options
     */
    override val googlePayOptions: GooglePayOptions? = null,

    /**
     * Indicate if the payment shall be captured immediately after authorized. Only applicable to Card.
     * Default: true
     */
    val autoCapture: Boolean = true,

    /**
     *  control whether saved cards are displayed on the list screen
     */
    val hidePaymentConsents: Boolean = false

) : AirwallexSession(), Parcelable {

    class Builder(
        private val paymentIntent: PaymentIntent,
        private val countryCode: String,
        private val googlePayOptions: GooglePayOptions? = null
    ) : ObjectBuilder<AirwallexPaymentSession> {

        private var isBillingInformationRequired: Boolean = true
        private var isEmailRequired: Boolean = false
        private var returnUrl: String? = null
        private var autoCapture: Boolean = true
        private var hidePaymentConsents: Boolean = false

        fun setRequireBillingInformation(requiresBillingInformation: Boolean): Builder = apply {
            this.isBillingInformationRequired = requiresBillingInformation
        }

        fun setRequireEmail(requiresEmail: Boolean): Builder = apply {
            this.isEmailRequired = requiresEmail
        }

        fun setReturnUrl(returnUrl: String?): Builder = apply {
            this.returnUrl = returnUrl
        }

        fun setAutoCapture(autoCapture: Boolean): Builder = apply {
            this.autoCapture = autoCapture
        }

        fun setHidePaymentConsents(hidePaymentConsents: Boolean): Builder = apply {
            this.hidePaymentConsents = hidePaymentConsents
        }

        override fun build(): AirwallexPaymentSession {
            return AirwallexPaymentSession(
                paymentIntent = paymentIntent,
                currency = paymentIntent.currency,
                countryCode = countryCode,
                amount = paymentIntent.amount,
                shipping = paymentIntent.order?.shipping,
                isBillingInformationRequired = isBillingInformationRequired,
                isEmailRequired = isEmailRequired,
                customerId = paymentIntent.customerId,
                returnUrl = returnUrl,
                googlePayOptions = googlePayOptions,
                autoCapture = autoCapture,
                hidePaymentConsents = hidePaymentConsents
            )
        }
    }
}
