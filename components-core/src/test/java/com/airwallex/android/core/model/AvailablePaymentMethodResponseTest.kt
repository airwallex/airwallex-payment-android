package com.airwallex.android.core.model

import com.airwallex.android.core.ParcelUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class AvailablePaymentMethodResponseTest {

    @Test
    fun testParcelable() {
        assertEquals(
            AvailablePaymentMethodResponseFixtures.PAYMENMETHODRESPONSE,
            ParcelUtils.create(AvailablePaymentMethodResponseFixtures.PAYMENMETHODRESPONSE)
        )
    }

    @Test
    fun testParams() {
        val availablePaymentMethodResponse =
            AvailablePaymentMethodResponseFixtures.PAYMENMETHODRESPONSE
        assertEquals(
            listOf(
                AvailablePaymentMethod(
                    name = PaymentMethodType.CARD,
                    transactionMode = AvailablePaymentMethod.TransactionMode.ONE_OFF,
                    active = true,
                    transactionCurrencies = listOf("dollar", "RMB"),
                    flows = listOf(AirwallexPaymentRequestFlow.IN_APP)
                )
            ),
            availablePaymentMethodResponse.items
        )
    }
}
