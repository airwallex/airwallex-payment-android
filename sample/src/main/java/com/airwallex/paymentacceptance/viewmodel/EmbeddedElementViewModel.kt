package com.airwallex.paymentacceptance.viewmodel

import androidx.activity.ComponentActivity
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexSession
import com.airwallex.paymentacceptance.viewmodel.base.BaseViewModel

class EmbeddedElementViewModel : BaseViewModel() {

    override fun init(activity: ComponentActivity) {
        super.init(activity)
    }

    /**
     * Handle payment result from PaymentElement
     */
    fun handlePaymentResult(session: AirwallexSession, status: AirwallexPaymentStatus) {
        handlePaymentStatus(session, status)
    }
}
