package com.airwallex.android.view

import com.airwallex.android.*
import com.airwallex.android.model.*

internal abstract class AirwallexCheckoutBaseActivity : AirwallexActivity() {

    abstract val airwallex: Airwallex

    override fun onActionSave() {
        // Ignore
    }

    internal fun startCheckout(session: AirwallexSession, paymentMethod: PaymentMethod, paymentConsentId: String?, cvc: String?, listener: Airwallex.PaymentResultListener<PaymentIntent>) {
        setLoadingProgress(loading = true, cancelable = false)
        airwallex.checkout(session, paymentMethod, paymentConsentId, cvc, listener)
    }
}
