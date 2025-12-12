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
@Suppress("LongParameterList")
@Parcelize
class AirwallexRecurringWithIntentSession internal constructor(

    /**
     * The [PaymentIntent] object, optional when using paymentIntentProvider.
     */
    val paymentIntent: PaymentIntent?,

    /**
     * Internal identifier for the PaymentIntentProvider stored in the repository.
     * This is set automatically when a PaymentIntentProvider is provided to the builder.
     */
    internal val paymentIntentProviderId: String? = null,

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
     * Whether or not billing information is required for payments. When set to `false`, any billing information will be ignored.
     */
    override val isBillingInformationRequired: Boolean = true,

    /**
     * Whether or not email is required for card payments. Set to 'true' if you'd like to collect customers' email
     */
    override val isEmailRequired: Boolean = false,

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
    val autoCapture: Boolean = true
) : AirwallexSession(), Parcelable {

    /**
     * Provider for asynchronously providing PaymentIntent, optional when paymentIntent is provided.
     * This is automatically managed by the SDK repository.
     */
    val paymentIntentProvider: PaymentIntentProvider?
        get() = paymentIntentProviderId?.let { PaymentIntentProviderRepository.get(it) }

    init {
        require(paymentIntent != null || paymentIntentProviderId != null) {
            "Either paymentIntent or paymentIntentProvider must be provided"
        }
    }

    class Builder : ObjectBuilder<AirwallexRecurringWithIntentSession> {
        private var paymentIntent: PaymentIntent? = null
        private var paymentIntentProvider: PaymentIntentProvider? = null
        private val customerId: String
        private val nextTriggerBy: PaymentConsent.NextTriggeredBy
        private val countryCode: String
        private val currency: String
        private val amount: BigDecimal

        /**
         * Constructor for static PaymentIntent
         */
        constructor(
            paymentIntent: PaymentIntent,
            customerId: String,
            nextTriggerBy: PaymentConsent.NextTriggeredBy,
            countryCode: String
        ) {
            this.paymentIntent = paymentIntent
            this.customerId = customerId
            this.nextTriggerBy = nextTriggerBy
            this.countryCode = countryCode
            this.currency = paymentIntent.currency
            this.amount = paymentIntent.amount
        }

        /**
         * Constructor for PaymentIntentProvider
         */
        constructor(
            paymentIntentProvider: PaymentIntentProvider,
            customerId: String,
            nextTriggerBy: PaymentConsent.NextTriggeredBy,
            countryCode: String
        ) {
            this.paymentIntentProvider = paymentIntentProvider
            this.customerId = customerId
            this.nextTriggerBy = nextTriggerBy
            this.countryCode = countryCode
            this.currency = paymentIntentProvider.currency
            this.amount = paymentIntentProvider.amount
        }

        /**
         * Constructor for PaymentIntentSource (modern suspend-based version)
         */
        constructor(
            paymentIntentSource: PaymentIntentSource,
            customerId: String,
            nextTriggerBy: PaymentConsent.NextTriggeredBy,
            countryCode: String
        ) {
            // Automatically wrap suspend source with callback adapter
            this.paymentIntentProvider = SourceToProviderAdapter(paymentIntentSource)
            this.customerId = customerId
            this.nextTriggerBy = nextTriggerBy
            this.countryCode = countryCode
            this.currency = paymentIntentSource.currency
            this.amount = paymentIntentSource.amount
        }

        private var requiresCVC: Boolean = false
        private var isBillingInformationRequired: Boolean = true
        private var isEmailRequired: Boolean = false
        private var merchantTriggerReason: PaymentConsent.MerchantTriggerReason =
            PaymentConsent.MerchantTriggerReason.UNSCHEDULED
        private var returnUrl: String? = null
        private var autoCapture: Boolean = true
        private var paymentMethods: List<String>? = null
        private var googlePayOptions: GooglePayOptions? = null
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

        fun setAutoCapture(autoCapture: Boolean): Builder = apply {
            this.autoCapture = autoCapture
        }

        fun setPaymentMethods(paymentMethods: List<String>?): Builder = apply {
            this.paymentMethods = paymentMethods
        }

        fun setGooglePayOptions(googlePayOptions: GooglePayOptions?): Builder = apply {
            this.googlePayOptions = googlePayOptions
        }

        fun setShipping(shipping: Shipping?): Builder = apply {
            this.shipping = shipping
        }

        override fun build(): AirwallexRecurringWithIntentSession {
            return AirwallexRecurringWithIntentSession(
                paymentIntent = paymentIntent,
                paymentIntentProviderId = paymentIntentProvider?.let { PaymentIntentProviderRepository.store(it) },
                nextTriggerBy = nextTriggerBy,
                requiresCVC = requiresCVC,
                customerId = customerId,
                currency = currency,
                countryCode = countryCode,
                amount = amount,
                shipping = shipping,
                isBillingInformationRequired = isBillingInformationRequired,
                isEmailRequired = isEmailRequired,
                returnUrl = returnUrl,
                autoCapture = autoCapture,
                paymentMethods = paymentMethods,
                googlePayOptions = googlePayOptions,
                merchantTriggerReason = merchantTriggerReason
            )
        }
    }
}
