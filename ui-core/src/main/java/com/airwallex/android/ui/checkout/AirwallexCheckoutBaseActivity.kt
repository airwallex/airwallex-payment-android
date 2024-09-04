package com.airwallex.android.ui.checkout

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.model.*
import com.airwallex.android.ui.AirwallexActivity

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

    @Suppress("LongParameterList")
    fun startCheckout(
        paymentMethod: PaymentMethod,
        paymentConsentId: String? = null,
        cvc: String? = null,
        additionalInfo: Map<String, String>? = null,
        flow: AirwallexPaymentRequestFlow? = null,
        observer: Observer<AirwallexPaymentStatus>
    ) {
        setLoadingProgress(loading = true, cancelable = false)
        viewModel.checkout(
            paymentMethod, paymentConsentId, cvc, additionalInfo, flow
        ).observe(this, observer)
    }
}
