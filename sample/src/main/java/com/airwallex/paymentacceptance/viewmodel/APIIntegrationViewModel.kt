package com.airwallex.paymentacceptance.viewmodel

import androidx.lifecycle.ViewModel
import com.airwallex.android.core.AirwallexCheckoutMode

class APIIntegrationViewModel: ViewModel() {

    private var checkoutMode = AirwallexCheckoutMode.PAYMENT

    fun updateCheckoutModel(mode: Int) {
        checkoutMode = when (mode) {
            1 -> AirwallexCheckoutMode.RECURRING
            2 -> AirwallexCheckoutMode.RECURRING_WITH_INTENT
            else -> AirwallexCheckoutMode.PAYMENT
        }
    }

}
