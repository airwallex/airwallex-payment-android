package com.airwallex.android.view

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.ui.checkout.AirwallexCheckoutViewModel

@Suppress("ComplexMethod", "LongMethod")
class PaymentMethodsViewModel(
    application: Application,
    airwallex: Airwallex,
    internal val session: AirwallexSession
) : AirwallexCheckoutViewModel(application, airwallex, session) {

    val pageName: String = "payment_method_list"

    fun trackPaymentSelection(paymentMethodType: String?) {
        paymentMethodType?.takeIf { it.isNotEmpty() }?.let { type ->
            AnalyticsLogger.logAction(PAYMENT_SELECT, mapOf(PAYMENT_METHOD to type))
        }
    }

    internal class Factory(
        private val application: Application,
        private val airwallex: Airwallex,
        private val session: AirwallexSession
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST") return PaymentMethodsViewModel(
                application, airwallex, session
            ) as T
        }
    }

    companion object {
        const val COUNTRY_CODE = "country_code"
        const val FLOW = "flow"
        private const val PAYMENT_METHOD = "payment_method"
        private const val PAYMENT_SELECT = "select_payment"
    }
}