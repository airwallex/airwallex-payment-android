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
}
