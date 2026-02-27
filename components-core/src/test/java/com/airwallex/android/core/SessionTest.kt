package com.airwallex.android.core

import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentConsentOptions
import com.airwallex.android.core.model.PaymentIntentFixtures
import com.airwallex.android.core.model.Shipping
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockkObject
import io.mockk.runs
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SessionTest {

    @Before
    fun setup() {
        mockkObject(TokenManager)
        clearMocks(TokenManager)
        every { TokenManager.updateClientSecret(any()) } just runs
    }

    @Test
    fun buildTest() {
        val shipping = Shipping.Builder()
            .setFirstName("John")
            .setLastName("Doe")
            .build()

        val session = Session.Builder(
            PaymentIntentFixtures.PAYMENT_INTENT, "CN"
        )
            .setRequireBillingInformation(false)
            .setRequireEmail(true)
            .setReturnUrl("airwallexcheckout://com.airwallex.paymentacceptance")
            .setPaymentMethods(listOf("card", "wechat"))
            .setHidePaymentConsents(false)
            .setAutoCapture(false)
            .setShipping(shipping)
            .build()

        assertNotNull(session)
        assertNotNull(session.paymentIntent)

        assertNotNull(session.currency)
        assertEquals("AUD", session.currency)

        assertNotNull(session.amount)
        assertEquals(BigDecimal.valueOf(100.01), session.amount)

        assertNotNull(session.isBillingInformationRequired)
        assertEquals(false, session.isBillingInformationRequired)

        assertTrue(session.isEmailRequired)

        assertEquals("cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu", session.customerId)
        assertEquals("airwallexcheckout://com.airwallex.paymentacceptance", session.returnUrl)
        assertEquals(false, session.autoCapture)
        assertEquals(false, session.hidePaymentConsents)
        assertNotNull(session.paymentMethods)
        assertEquals(session.shipping, shipping)
        assertNull(session.paymentConsentOptions)
    }

    @Test
    fun `build with payment consent options`() {
        val paymentConsentOptions = PaymentConsentOptions(
            nextTriggeredBy = PaymentConsent.NextTriggeredBy.MERCHANT,
            merchantTriggerReason = PaymentConsent.MerchantTriggerReason.UNSCHEDULED,
            termsOfUse = PaymentConsentOptions.TermsOfUse(
                paymentAmountType = "VARIABLE",
                maxPaymentAmount = 1000.0,
                paymentCurrency = "USD"
            )
        )

        val session = Session.Builder(
            PaymentIntentFixtures.PAYMENT_INTENT,
            "US"
        )
            .setPaymentConsentOptions(paymentConsentOptions)
            .build()

        assertNotNull(session)
        assertNotNull(session.paymentIntent)
        assertNotNull(session.paymentConsentOptions)

        assertEquals(PaymentConsent.NextTriggeredBy.MERCHANT, session.paymentConsentOptions?.nextTriggeredBy)
        assertEquals(PaymentConsent.MerchantTriggerReason.UNSCHEDULED, session.paymentConsentOptions?.merchantTriggerReason)
        assertNotNull(session.paymentConsentOptions?.termsOfUse)
        assertEquals("VARIABLE", session.paymentConsentOptions?.termsOfUse?.paymentAmountType)
        assertEquals(1000.0, session.paymentConsentOptions?.termsOfUse?.maxPaymentAmount)
        assertEquals("USD", session.paymentConsentOptions?.termsOfUse?.paymentCurrency)
    }

    @Test
    fun `build with minimal required fields`() {
        val session = Session.Builder(
            PaymentIntentFixtures.PAYMENT_INTENT,
            "CN"
        ).build()

        assertNotNull(session)
        assertNotNull(session.paymentIntent)
        assertEquals("AUD", session.currency)
        assertEquals(BigDecimal.valueOf(100.01), session.amount)
        assertEquals("CN", session.countryCode)

        // Verify defaults
        assertTrue(session.isBillingInformationRequired)
        assertEquals(false, session.isEmailRequired)
        assertTrue(session.autoCapture)
        assertEquals(false, session.hidePaymentConsents)
        assertNull(session.paymentConsentOptions)
        assertNull(session.shipping)
        assertNull(session.returnUrl)
        assertNull(session.paymentMethods)
    }

}
