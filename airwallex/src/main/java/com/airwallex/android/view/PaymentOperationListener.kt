package com.airwallex.android.view

import com.airwallex.android.core.AirwallexPaymentStatus

interface PaymentOperationListener {
    fun onLoadingStateChanged(isLoading: Boolean)
    fun onPaymentResult(status: AirwallexPaymentStatus)
    fun onError(exception: Throwable)
}