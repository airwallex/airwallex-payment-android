package com.airwallex.android.core

import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentIntentFixtures
import io.mockk.every
import io.mockk.just
import io.mockk.mockkObject
import io.mockk.runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AirwallexRecurringWithIntentSessionTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkObject(TokenManager)
        every { TokenManager.updateClientSecret(any()) } just runs
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun buildTest() {
        val googlePayOptions = GooglePayOptions(
            billingAddressRequired = true,
            billingAddressParameters = BillingAddressParameters(BillingAddressParameters.Format.FULL),
        )
        val airwallexRecurringWithIntentSession = AirwallexRecurringWithIntentSession.Builder(
            nextTriggerBy = PaymentConsent.NextTriggeredBy.CUSTOMER,
            customerId = "cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu",
            paymentIntent = PaymentIntentFixtures.PAYMENT_INTENT,
            countryCode = "CN"
        )
            .setMerchantTriggerReason(PaymentConsent.MerchantTriggerReason.SCHEDULED)
            .setRequireBillingInformation(false)
            .setRequireCvc(true)
            .setRequireEmail(true)
            .setReturnUrl("airwallexcheckout://com.airwallex.paymentacceptance")
            .setAutoCapture(false)
            .setGooglePayOptions(googlePayOptions)
            .setPaymentMethods(listOf("googlepay"))
            .build()

        assertNotNull(airwallexRecurringWithIntentSession)

        assertNotNull(airwallexRecurringWithIntentSession.currency)
        assertEquals("AUD", airwallexRecurringWithIntentSession.currency)

        assertNotNull(airwallexRecurringWithIntentSession.amount)
        assertEquals(BigDecimal.valueOf(100.01), airwallexRecurringWithIntentSession.amount)

        assertNotNull(airwallexRecurringWithIntentSession.nextTriggerBy)
        assertEquals(
            PaymentConsent.NextTriggeredBy.CUSTOMER,
            airwallexRecurringWithIntentSession.nextTriggerBy
        )

        assertNotNull(airwallexRecurringWithIntentSession.requiresCVC)
        assertEquals(true, airwallexRecurringWithIntentSession.requiresCVC)

        assertNotNull(airwallexRecurringWithIntentSession.isBillingInformationRequired)
        assertEquals(false, airwallexRecurringWithIntentSession.isBillingInformationRequired)

        assertTrue(airwallexRecurringWithIntentSession.isEmailRequired)

        assertEquals(null, airwallexRecurringWithIntentSession.shipping)

        assertNotNull(airwallexRecurringWithIntentSession.customerId)
        assertEquals(
            "cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu",
            airwallexRecurringWithIntentSession.customerId
        )

        assertEquals(
            true,
            airwallexRecurringWithIntentSession.googlePayOptions?.billingAddressRequired
        )
        assertEquals(
            BillingAddressParameters.Format.FULL,
            airwallexRecurringWithIntentSession.googlePayOptions?.billingAddressParameters?.format
        )

        assertEquals(
            "airwallexcheckout://com.airwallex.paymentacceptance",
            airwallexRecurringWithIntentSession.returnUrl
        )
        assertEquals(false, airwallexRecurringWithIntentSession.autoCapture)
        assertNotNull(airwallexRecurringWithIntentSession.paymentMethods)
    }
}
