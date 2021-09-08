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
     * Amount currency
     */
    abstract val currency: String

    /**
     * Payment amount. This is the order amount you would like to charge your customer
     */
    abstract val amount: BigDecimal

    /**
     * The URL to redirect your customer back to after they authenticate or cancel their payment on the PaymentMethod’s app or site. If you’d prefer to redirect to a mobile application, you can alternatively supply an application URI scheme.
     */
    abstract val returnUrl: String?
}
