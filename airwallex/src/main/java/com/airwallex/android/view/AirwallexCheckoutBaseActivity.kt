package com.airwallex.android.view

import com.airwallex.android.*
import com.airwallex.android.model.*

internal abstract class AirwallexCheckoutBaseActivity : AirwallexActivity() {

    abstract val airwallex: Airwallex

    override fun onActionSave() {
        // Ignore
    }

    internal fun startCheckout(
        session: AirwallexSession,
        paymentMethod: PaymentMethod,
        paymentConsentId: String?,
        cvc: String?,
        currency: String? = null,
        name: String? = null,
        email: String? = null,
        phone: String? = null,
        bank: Bank? = null,
        listener: Airwallex.PaymentResultListener<PaymentIntent>
    ) {
        setLoadingProgress(loading = true, cancelable = false)
        airwallex.checkout(session, paymentMethod, paymentConsentId, cvc, currency, name, email, phone, bank, listener)
    }
}
