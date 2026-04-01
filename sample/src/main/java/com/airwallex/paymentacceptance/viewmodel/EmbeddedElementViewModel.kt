package com.airwallex.paymentacceptance.viewmodel

import androidx.activity.ComponentActivity
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.BillingAddressParameters
import com.airwallex.android.core.GooglePayOptions
import com.airwallex.paymentacceptance.Settings
import com.airwallex.paymentacceptance.repo.DemoReturnUrl
import com.airwallex.paymentacceptance.viewmodel.base.BaseViewModel

class EmbeddedElementViewModel : BaseViewModel() {

    private var session: AirwallexSession? = null

    /**
     * Get the current session, or null if not created yet
     */
    fun getSession(): AirwallexSession? = session

    /**
     * Handle payment result from PaymentElement
     */
    fun handlePaymentResult(status: AirwallexPaymentStatus) {
        session?.let { handlePaymentStatus(it, status) }
    }

    /**
     * Create session for embedded element
     * Returns existing session if already created (e.g., after configuration change)
     * Supports both express and traditional checkout flows
     * @param includeGooglePay whether to include Google Pay options
     * @param paymentMethods list of payment method types to include
     * @param onSuccess callback when session is created successfully
     * @param onError callback when session creation fails
     */
    fun createEmbeddedElementSession(
        includeGooglePay: Boolean,
        paymentMethods: List<String>,
        onSuccess: (AirwallexSession) -> Unit,
        onError: (Exception) -> Unit
    ) {
        // Return existing session if already created (e.g., after config change)
        session?.let {
            onSuccess(it)
            return
        }

        val googlePayOptions = if (includeGooglePay) {
            GooglePayOptions(
                billingAddressRequired = true,
                billingAddressParameters = BillingAddressParameters(BillingAddressParameters.Format.FULL),
            )
        } else {
            null
        }

        launch {
            try {
                val createdSession = if (Settings.useSession == "Enabled") {
                    // Use the new unified Session API
                    createSessionForUI(
                        googlePayOptions = googlePayOptions,
                        paymentMethods = paymentMethods,
                        returnUrl = DemoReturnUrl.EmbeddedElement
                    )
                } else {
                    // Use legacy session classes
                    createLegacySessionForUI(
                        googlePayOptions = googlePayOptions,
                        paymentMethods = paymentMethods,
                        returnUrl = DemoReturnUrl.EmbeddedElement
                    )
                }
                session = createdSession
                onSuccess(createdSession)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}