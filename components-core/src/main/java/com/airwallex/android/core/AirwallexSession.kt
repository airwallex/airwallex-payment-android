package com.airwallex.android.core

import android.os.Parcelable
import com.airwallex.android.core.model.Shipping
import java.math.BigDecimal

abstract class AirwallexSession : Parcelable {

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
    abstract val isBillingInformationRequired: Boolean

    /**
     * Whether or not email is required for card payments
     */
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
     * limit the payment methods displayed on the list screen
     */
    abstract val paymentMethods: List<String>?
}
