package com.airwallex.paymentacceptance.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodTypeInfo

class EmbeddedPaymentViewModel(
    application: Application,
    private val airwallex: Airwallex,
    private val session: AirwallexSession
) : AndroidViewModel(application) {

    fun deleteCard(consent: PaymentConsent, onSuccess: () -> Unit) {
        // Implement delete card logic
        onSuccess()
    }

    fun checkoutWithoutCvc(consent: PaymentConsent) {
        // Implement checkout without CVC logic
        airwallex.confirmPaymentIntent(
            session = session as com.airwallex.android.core.AirwallexPaymentSession,
            paymentConsent = consent,
            listener = object : Airwallex.PaymentResultListener {
                override fun onCompleted(status: com.airwallex.android.core.AirwallexPaymentStatus) {
                    // Handle result
                }
            }
        )
    }

    fun checkoutWithCvc(consent: PaymentConsent, cvc: String) {
        // Implement checkout with CVC logic
    }

    fun directPay(type: AvailablePaymentMethodType) {
        // Implement direct pay logic
    }

    fun payWithFields(
        method: PaymentMethod,
        typeInfo: PaymentMethodTypeInfo,
        fields: Map<String, String>
    ) {
        // Implement pay with fields logic
    }

    class Factory(
        private val application: Application,
        private val airwallex: Airwallex,
        private val session: AirwallexSession
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EmbeddedPaymentViewModel::class.java)) {
                return EmbeddedPaymentViewModel(application, airwallex, session) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
