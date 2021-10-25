package com.airwallex.android.view

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.airwallex.android.core.Airwallex
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

    fun startCheckout(
        paymentMethod: PaymentMethod,
        paymentConsentId: String? = null,
        cvc: String? = null,
        additionalInfo: Map<String, String>? = null,
        observer: Observer<AirwallexCheckoutViewModel.CheckoutResult>
    ) {
        setLoadingProgress(loading = true, cancelable = false)
        viewModel.checkout(
            paymentMethod, paymentConsentId, cvc, additionalInfo
        ).observe(this, observer)
    }

    fun retrieveBanks(
        paymentMethodTypeName: String,
        observer: Observer<Result<BankResponse>>
    ) {
        setLoadingProgress(loading = true, cancelable = false)
        viewModel.retrieveBanks(paymentMethodTypeName).observe(this, observer)
    }

    fun retrievePaymentMethodTypeInfo(
        paymentMethodTypeName: String,
        observer: Observer<Result<PaymentMethodTypeInfo>>
    ) {
        setLoadingProgress(loading = true, cancelable = false)
        viewModel.retrievePaymentMethodTypeInfo(
            paymentMethodTypeName
        ).observe(this, observer)
    }
}
