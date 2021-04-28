package com.airwallex.android

import android.os.Parcelable
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.Shipping
import java.math.BigDecimal

abstract class AirwallexSession(
    open val paymentMethod: PaymentMethod? = null
) : Parcelable {

    abstract val customerId: String?

    abstract val shipping: Shipping?

    abstract val currency: String

    abstract val amount: BigDecimal

    abstract val cvc: String?
}
