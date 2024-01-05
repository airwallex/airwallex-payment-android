package com.airwallex.android.core

class PaymentResultManager private constructor(private val listener: Airwallex.PaymentResultListener) {
    companion object {
        @Volatile
        private var instance: PaymentResultManager? = null

        fun getInstance(
            listener: Airwallex.PaymentResultListener = object : Airwallex.PaymentResultListener {
                override fun onCompleted(status: AirwallexPaymentStatus) {
                    // no op
                }
            }
        ) = instance ?: synchronized(this) {
                instance ?: PaymentResultManager(listener).also { instance = it }
            }
    }

    fun completePayment(status: AirwallexPaymentStatus) {
        listener.onCompleted(status)
    }
}