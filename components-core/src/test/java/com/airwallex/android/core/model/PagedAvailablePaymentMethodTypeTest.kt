package com.airwallex.android.core.model

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class PagedAvailablePaymentMethodTypeTest {

    @Test
    fun testParams() {
        val availablePaymentMethodResponse =
            PagedAvailablePaymentMethodTypeFixtures.PAYMENMETHODRESPONSE
        assertEquals(
            listOf(
                AvailablePaymentMethodType(
                    name = PaymentMethodType.CARD.value,
                    transactionMode = TransactionMode.ONE_OFF,
                    active = true,
                    countryCodes = emptyList(),
                    transactionCurrencies = listOf("dollar", "RMB"),
                    flows = listOf(AirwallexPaymentRequestFlow.IN_APP)
                )
            ),
            availablePaymentMethodResponse.items
        )
        assertFalse(availablePaymentMethodResponse.hasMore)
    }
}
