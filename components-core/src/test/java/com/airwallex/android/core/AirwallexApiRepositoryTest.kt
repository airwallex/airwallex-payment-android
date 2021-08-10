package com.airwallex.android.core

import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class AirwallexApiRepositoryTest {

    @Test
    fun confirmPaymentIntentUrlTest() {
        val confirmApi =
            AirwallexApiRepository.confirmPaymentIntentUrl("https://api.airwallex.com", "abc")
        assertEquals("https://api.airwallex.com/api/v1/pa/payment_intents/abc/confirm", confirmApi)
    }

    @Test
    fun retrievePaymentIntentUrlTest() {
        val retrieveApi =
            AirwallexApiRepository.retrievePaymentIntentUrl("https://api.airwallex.com", "abc")
        assertEquals("https://api.airwallex.com/api/v1/pa/payment_intents/abc", retrieveApi)
    }
}
