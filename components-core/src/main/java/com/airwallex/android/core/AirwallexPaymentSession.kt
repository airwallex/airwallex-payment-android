package com.airwallex.android.core

import android.os.Parcelable
import com.airwallex.android.core.model.ObjectBuilder
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.Shipping
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.math.BigDecimal

/**
 * For one-off payment
 */
@Suppress("LongParameterList")
@Parcelize
class AirwallexPaymentSession internal constructor(
    /**
     * the ID of the [PaymentIntent], optional when using paymentIntentProvider.
     */
    val paymentIntent: PaymentIntent?,

    /**
     * Provider for asynchronously providing PaymentIntent, optional when paymentIntent is provided.
     * Note: This field is not parcelable and will be null after parcel/unparcel operations.
     */
    @Transient
    val paymentIntentProvider: @RawValue PaymentIntentProvider? = null,

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
    override val customerId: String?,

    /**
     * The URL to redirect your customer back to after they authenticate or cancel their payment on the PaymentMethod’s app or site. If you’d prefer to redirect to a mobile application, you can alternatively supply an application URI scheme.
     */
    override val returnUrl: String?,

    /**
     * Google Pay options
     */
    override val googlePayOptions: GooglePayOptions? = null,

    /**
     * An array of payment method type names to limit the payment methods displayed on the list screen. Only available ones from your Airwallex account will be applied, any other ones will be ignored. Also the order of payment method list will follow the order of this array.
     * API reference: https://www.airwallex.com/docs/api#/Payment_Acceptance/Config/_api_v1_pa_config_payment_method_types/get JSON Object field: items.name
     */
    override val paymentMethods: List<String>? = null,

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

    init {
        require(paymentIntent != null || paymentIntentProvider != null) {
            "Either paymentIntent or paymentIntentProvider must be provided"
        }
    }

    class Builder : ObjectBuilder<AirwallexPaymentSession> {
        private var paymentIntent: PaymentIntent? = null
        private var paymentIntentProvider: PaymentIntentProvider? = null
        private val countryCode: String
        private val currency: String
        private val amount: BigDecimal
        private var customerId: String? = null
        private val googlePayOptions: GooglePayOptions?

        /**
         * Constructor for static PaymentIntent
         */
        constructor(
            paymentIntent: PaymentIntent,
            countryCode: String,
            googlePayOptions: GooglePayOptions? = null
        ) {
            this.paymentIntent = paymentIntent
            this.countryCode = countryCode
            this.currency = paymentIntent.currency
            this.amount = paymentIntent.amount
            this.customerId = paymentIntent.customerId
            this.googlePayOptions = googlePayOptions
        }

        /**
         * Constructor for PaymentIntentProvider
         */
        constructor(
            paymentIntentProvider: PaymentIntentProvider,
            countryCode: String,
            currency: String,
            amount: BigDecimal,
            customerId: String? = null,
            googlePayOptions: GooglePayOptions? = null
        ) {
            this.paymentIntentProvider = paymentIntentProvider
            this.countryCode = countryCode
            this.currency = currency
            this.amount = amount
            this.customerId = customerId
            this.googlePayOptions = googlePayOptions
        }

        private var isBillingInformationRequired: Boolean = true
        private var isEmailRequired: Boolean = false
        private var returnUrl: String? = null
        private var autoCapture: Boolean = true
        private var hidePaymentConsents: Boolean = false
        private var paymentMethods: List<String>? = null
        private var shipping: Shipping? = null

        init {
            paymentIntent?.clientSecret?.apply {
                TokenManager.updateClientSecret(this)
            }
        }

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

        fun setPaymentMethods(paymentMethods: List<String>?): Builder = apply {
            this.paymentMethods = paymentMethods
        }

        fun setShipping(shipping: Shipping?): Builder = apply {
            this.shipping = shipping
        }

        override fun build(): AirwallexPaymentSession {
            return AirwallexPaymentSession(
                paymentIntent = paymentIntent,
                paymentIntentProvider = paymentIntentProvider,
                currency = currency,
                countryCode = countryCode,
                amount = amount,
                shipping = shipping,
                isBillingInformationRequired = isBillingInformationRequired,
                isEmailRequired = isEmailRequired,
                customerId = customerId,
                returnUrl = returnUrl,
                googlePayOptions = googlePayOptions,
                autoCapture = autoCapture,
                hidePaymentConsents = hidePaymentConsents,
                paymentMethods = paymentMethods,
            )
        }
    }
}
