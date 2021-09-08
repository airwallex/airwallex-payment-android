package com.airwallex.android.core.model

import com.airwallex.android.core.ParcelUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class AvailablePaymentMethodTest {

    @Test
    fun testParcelable() {
        assertEquals(
            AvailablePaymentMethodFixtures.PAYMENMETHOD,
            ParcelUtils.create(AvailablePaymentMethodFixtures.PAYMENMETHOD)
        )
    }

    @Test
    fun testParams() {
        val availablePaymentMethod = AvailablePaymentMethodFixtures.PAYMENMETHOD
        assertEquals(PaymentMethodType.CARD, availablePaymentMethod.name)
        assertEquals(
            AvailablePaymentMethod.TransactionMode.ONE_OFF,
            availablePaymentMethod.transactionMode
        )
        assertEquals(true, availablePaymentMethod.active)
        assertEquals(listOf("dollar", "RMB"), availablePaymentMethod.transactionCurrencies)
        assertEquals(listOf(AirwallexPaymentRequestFlow.IN_APP), availablePaymentMethod.flows)
    }
}
