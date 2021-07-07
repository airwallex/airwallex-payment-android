package com.airwallex.android.view

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.airwallex.android.*
import com.airwallex.android.model.*

internal abstract class AirwallexCheckoutBaseActivity : AirwallexActivity() {

    abstract val airwallex: Airwallex
    abstract val session: AirwallexSession

    private val viewModel: AirwallexCheckoutViewModel by lazy {
        ViewModelProvider(
            this,
            AirwallexCheckoutViewModel.Factory(
                application,
                airwallex,
                session
            )
        )[AirwallexCheckoutViewModel::class.java]
    }

    override fun onActionSave() {
        // Ignore
    }

    internal fun startCheckout(
        paymentMethod: PaymentMethod,
        paymentConsentId: String? = null,
        cvc: String? = null,
        currency: String? = null,
        name: String? = null,
        email: String? = null,
        phone: String? = null,
        bank: Bank? = null,
        observer: Observer<AirwallexCheckoutViewModel.PaymentResult>
    ) {
        setLoadingProgress(loading = true, cancelable = false)
        viewModel.checkout(
            paymentMethod, paymentConsentId, cvc, currency, name, email, phone, bank
        ).observe(this, observer)
    }
}
