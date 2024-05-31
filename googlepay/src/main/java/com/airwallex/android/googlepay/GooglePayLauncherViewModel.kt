package com.airwallex.android.googlepay

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.GooglePayOptions
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient

internal class GooglePayLauncherViewModel(
    application: Application,
    private val session: AirwallexSession,
    private val googlePayOptions: GooglePayOptions,
    private val paymentMethodType: AvailablePaymentMethodType
) : AndroidViewModel(application) {
    // A client for interacting with the Google Pay API.
    private val paymentsClient: PaymentsClient = PaymentsUtil.createPaymentsClient(application)

    fun getLoadPaymentDataTask(): Task<PaymentData> {
        val paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(
            price = session.amount,
            countryCode = session.countryCode,
            currency = session.currency,
            googlePayOptions = googlePayOptions,
            supportedCardSchemes = paymentMethodType.cardSchemes
        )
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        return paymentsClient.loadPaymentData(request)
    }

    internal class Factory(
        private val application: Application,
        private val args: GooglePayActivityLaunch.Args
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return GooglePayLauncherViewModel(
                application,
                args.session,
                args.googlePayOptions,
                args.paymentMethodType
            ) as T
        }
    }
}