package com.airwallex.android.ui.checkout

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.AnalyticsLogger
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

    override fun onCreate(savedInstanceState: Bundle?) {
        // Restore the launch bundle into intent first so the args lazy that
        // viewModel depends on can resolve after process death.
        restoreLaunchBundleIfNeeded(savedInstanceState)
        try {
            // Update the Airwallex instance in the ViewModel to ensure it always
            // refers to the current Activity. This triggers args lazy resolution
            // via the Factory's session parameter, so it can throw if args are
            // missing — handle that here to keep the host app alive.
            viewModel.updateActivity(this)
        } catch (e: IllegalArgumentException) {
            AirwallexLogger.error("$localClassName: viewModel init failed, finishing", e)
            AnalyticsLogger.logError(
                EVENT_INIT_FAILED,
                mapOf("activity" to localClassName),
            )
            super.onCreate(savedInstanceState)
            setResult(RESULT_CANCELED)
            finish()
            return
        }
        super.onCreate(savedInstanceState)
    }

    override fun onBackButtonPressed() {
        viewModel.trackPaymentCancelled()
    }

    @Suppress("LongParameterList")
    fun startCheckout(
        paymentMethod: PaymentMethod,
        paymentConsent: PaymentConsent? = null,
        cvc: String? = null,
        additionalInfo: Map<String, String>? = null,
        flow: AirwallexPaymentRequestFlow? = null,
        observer: Observer<AirwallexPaymentStatus>
    ) {
        setLoadingProgress(loading = true, cancelable = false)
        viewModel.checkout(
            paymentMethod, paymentConsent, cvc, additionalInfo, flow
        ).observe(this, observer)
    }
}
