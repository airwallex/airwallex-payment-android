package com.airwallex.android.core

import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class PaymentResultManagerTest {
    @Test
    fun `test complete payment`() {
        val listener = mockk<Airwallex.PaymentResultListener>(relaxed = true)
        val manager = PaymentResultManager.getInstance(listener)
        val status = AirwallexPaymentStatus.InProgress("id")
        manager.completePayment(status)
        verify { listener.onCompleted(status) }
    }
}