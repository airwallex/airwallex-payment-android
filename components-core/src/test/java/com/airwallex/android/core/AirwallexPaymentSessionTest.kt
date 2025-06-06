package com.airwallex.android.core

import com.airwallex.android.core.model.PaymentIntentFixtures
import com.airwallex.android.core.model.Shipping
import io.mockk.every
import io.mockk.just
import io.mockk.mockkObject
import io.mockk.runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AirwallexPaymentSessionTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkObject(TokenManager)
        every { TokenManager.updateClientSecret(any()) } just runs
    }

    @Test
    fun buildTest() {
        val shipping = Shipping.Builder()
            .setFirstName("John")
            .setLastName("Doe")
            .build()

        val airwallexPaymentSession = AirwallexPaymentSession.Builder(
            PaymentIntentFixtures.PAYMENT_INTENT, "CN"
        )
            .setRequireBillingInformation(false)
            .setRequireEmail(true)
            .setReturnUrl("airwallexcheckout://com.airwallex.paymentacceptance")
            .setPaymentMethods(listOf("googlepay"))
            .setHidePaymentConsents(false)
            .setAutoCapture(false)
            .setShipping(shipping)
            .build()

        assertNotNull(airwallexPaymentSession)

        assertNotNull(airwallexPaymentSession.currency)
        assertEquals("AUD", airwallexPaymentSession.currency)

        assertNotNull(airwallexPaymentSession.amount)
        assertEquals(BigDecimal.valueOf(100.01), airwallexPaymentSession.amount)

        assertNotNull(airwallexPaymentSession.isBillingInformationRequired)
        assertEquals(false, airwallexPaymentSession.isBillingInformationRequired)

        assertTrue(airwallexPaymentSession.isEmailRequired)

        assertEquals("cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu", airwallexPaymentSession.customerId)
        assertEquals("airwallexcheckout://com.airwallex.paymentacceptance", airwallexPaymentSession.returnUrl)
        assertEquals(false, airwallexPaymentSession.autoCapture)
        assertEquals(false, airwallexPaymentSession.hidePaymentConsents)
        assertNotNull(airwallexPaymentSession.paymentMethods)
        assertEquals(airwallexPaymentSession.shipping, shipping)
    }
}
