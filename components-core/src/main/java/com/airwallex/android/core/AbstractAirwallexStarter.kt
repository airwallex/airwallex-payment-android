package com.airwallex.android.core

import android.content.Intent
import com.airwallex.android.core.model.Shipping

abstract class AbstractAirwallexStarter {

    abstract fun presentShippingFlow(
        shipping: Shipping? = null,
        shippingFlowListener: Airwallex.PaymentShippingListener
    )

    abstract fun presentPaymentFlow(
        session: AirwallexSession,
        clientSecretProvider: ClientSecretProvider? = null,
        paymentFlowListener: Airwallex.PaymentIntentListener
    )

    abstract fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ): Boolean
}
