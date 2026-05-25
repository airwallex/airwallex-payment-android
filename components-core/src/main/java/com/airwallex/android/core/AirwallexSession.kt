package com.airwallex.android.core

import com.airwallex.android.core.model.Shipping
import java.math.BigDecimal

abstract class AirwallexSession {

    /**
     * The Customer who is paying for this PaymentIntent. This field is not required if the Customer is unknown (guest checkout). But it is required if the PaymentIntent is created for recurring payment.
     */
    abstract val customerId: String?

    /**
     * Shipping information
     */
    abstract val shipping: Shipping?

    /**
     * Whether or not billing information is required for card payments.
     */
    @Deprecated(
        message = "Use requiredBillingContactFields to explicitly declare which billing " +
            "contact fields (ADDRESS, PHONE, etc.) the SDK should collect.",
        replaceWith = ReplaceWith("requiredBillingContactFields"),
    )
    abstract val isBillingInformationRequired: Boolean

    /**
     * Whether or not email is required for card payments
     */
    @Deprecated(
        message = "Use requiredBillingContactFields and include " +
            "RequiredBillingContactField.EMAIL to require email.",
        replaceWith = ReplaceWith("requiredBillingContactFields"),
    )
    abstract val isEmailRequired: Boolean

    /**
     * Amount currency
     */
    abstract val currency: String

    /**
     * Country code
     */
    abstract val countryCode: String

    /**
     * Payment amount. This is the order amount you would like to charge your customer
     */
    abstract val amount: BigDecimal

    /**
     * The URL to redirect your customer back to after they authenticate or cancel their payment on the PaymentMethod’s app or site. If you’d prefer to redirect to a mobile application, you can alternatively supply an application URI scheme.
     */
    abstract val returnUrl: String?

    /**
     * Google Pay options
     */
    abstract val googlePayOptions: GooglePayOptions?

    /**
     * An array of payment method type names to limit the payment methods displayed on the list screen. Only available ones from your Airwallex account will be applied, any other ones will be ignored. Also the order of payment method list will follow the order of this array.
     * API reference: https://www.airwallex.com/docs/api#/Payment_Acceptance/Config/_api_v1_pa_config_payment_method_types/get JSON Object field: items.name
     */
    abstract val paymentMethods: List<String>?

    /**
     * The client secret for this session.
     * Used for authenticating API requests.
     */
    abstract val clientSecret: String?

    /**
     * Control whether saved cards are displayed on the list screen
     */
    open val hidePaymentConsents: Boolean
        get() = false

    /**
     * Billing contact fields the SDK should collect (and validate) on the new-card
     * payment screen. `null` means "derive from the legacy [isBillingInformationRequired]
     * / [isEmailRequired] flags" so unmodified integrations keep current behavior.
     * An empty set hides the entire billing section.
     *
     * See [resolvedRequiredBillingContactFields] for the effective set.
     */
    open val requiredBillingContactFields: Set<RequiredBillingContactField>?
        get() = null
}

/**
 * Effective set of billing contact fields, resolving `null` to the legacy
 * boolean-derived defaults that match Android's pre-existing UI:
 * - `NAME` is always included (the cardholder-name row was always shown).
 * - [AirwallexSession.isBillingInformationRequired] = true → adds `ADDRESS` + `PHONE`.
 * - [AirwallexSession.isEmailRequired] = true → adds `EMAIL`.
 *
 * Merchants who explicitly call `setRequiredBillingContactFields(...)` bypass this
 * derivation entirely; an empty set hides the entire billing section including the
 * cardholder-name row.
 */
@Suppress("DEPRECATION")
val AirwallexSession.resolvedRequiredBillingContactFields: Set<RequiredBillingContactField>
    get() = requiredBillingContactFields ?: buildSet {
        add(RequiredBillingContactField.NAME)
        if (isBillingInformationRequired) {
            add(RequiredBillingContactField.ADDRESS)
            add(RequiredBillingContactField.PHONE)
        }
        if (isEmailRequired) {
            add(RequiredBillingContactField.EMAIL)
        }
    }

/**
 * Indicates whether this session is configured for Express Checkout.
 * Returns true when the session uses a PaymentIntentProvider for lazy payment intent creation.
 */
val AirwallexSession.isExpressCheckout: Boolean
    get() {
        val resolvable = this as? PaymentIntentResolvableSession ?: return false
        // Check both transient provider (before binding) and provider ID (after binding to activity)
        return resolvable.paymentIntentProvider != null || resolvable.paymentIntentProviderId != null
    }
