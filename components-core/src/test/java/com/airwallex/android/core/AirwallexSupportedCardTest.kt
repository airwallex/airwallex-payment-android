package com.airwallex.android.core

import kotlin.test.Test
import kotlin.test.assertEquals

class AirwallexSupportedCardTest {

    @Test
    fun `test brand names`() {
        assertEquals("visa", AirwallexSupportedCard.VISA.brandName)
        assertEquals("amex", AirwallexSupportedCard.AMEX.brandName)
        assertEquals("mastercard", AirwallexSupportedCard.MASTERCARD.brandName)
        assertEquals("discover", AirwallexSupportedCard.DISCOVER.brandName)
        assertEquals("jcb", AirwallexSupportedCard.JCB.brandName)
        assertEquals("diners", AirwallexSupportedCard.DINERS_CLUB.brandName)
        assertEquals("unionpay", AirwallexSupportedCard.UNION_PAY.brandName)
    }
}