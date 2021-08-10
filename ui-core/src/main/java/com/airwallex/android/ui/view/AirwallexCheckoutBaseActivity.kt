package com.airwallex.android.ui.view

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.model.PPROAdditionalInfo
import com.airwallex.android.core.model.PaymentMethod

abstract class AirwallexCheckoutBaseActivity : AirwallexActivity() {

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

    fun startCheckout(
        paymentMethod: PaymentMethod,
        paymentConsentId: String? = null,
        cvc: String? = null,
        pproAdditionalInfo: PPROAdditionalInfo? = null,
        observer: Observer<AirwallexCheckoutViewModel.PaymentResult>
    ) {
        setLoadingProgress(loading = true, cancelable = false)
        viewModel.checkout(
            paymentMethod, paymentConsentId, cvc, pproAdditionalInfo
        ).observe(this, observer)
    }
}
