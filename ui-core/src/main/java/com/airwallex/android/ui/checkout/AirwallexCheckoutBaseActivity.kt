package com.airwallex.android.ui.checkout

import android.os.Bundle
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

    /**
     * The subtype for payment_launched event tracking.
     * Override with "dropin" for PaymentMethodsActivity, "component" for single payment method activities.
     * Default is null to skip logging (e.g., for intermediate screens like CVC collection).
     */
    open val paymentLaunchSubtype: String? = null

    /**
     * The payment method name for payment_launched event tracking.
     * Optional - only needed for "component" subtype.
     */
    open val paymentMethodName: String? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        // Update the Airwallex instance in the ViewModel to ensure it always refers to the current Activity
        viewModel.updateActivity(this)
        super.onCreate(savedInstanceState)
        paymentLaunchSubtype?.let { subtype ->
            viewModel.trackPaymentLaunched(
                subtype = subtype,
                paymentMethod = paymentMethodName
            )
        }
    }

    override fun onBackButtonPressed() {
        viewModel.trackPaymentCancelled()
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
