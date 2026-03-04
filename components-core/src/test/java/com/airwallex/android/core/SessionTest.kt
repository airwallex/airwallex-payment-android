package com.airwallex.android.core

import com.airwallex.android.core.extension.convertToLegacySession
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentConsentOptions
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PaymentIntentFixtures
import com.airwallex.android.core.model.Shipping
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockkObject
import io.mockk.runs
import kotlinx.coroutines.test.runTest
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
                paymentAmountType = PaymentConsentOptions.PaymentAmountType.VARIABLE,
                maxPaymentAmount = BigDecimal("1000.00"),
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

        val consentOptions = session.paymentConsentOptions
        assertNotNull(consentOptions)
        assertEquals(PaymentConsent.NextTriggeredBy.MERCHANT, consentOptions.nextTriggeredBy)
        assertEquals(PaymentConsent.MerchantTriggerReason.UNSCHEDULED, consentOptions.merchantTriggerReason)
        val tou = consentOptions.termsOfUse
        assertNotNull(tou)
        assertEquals(PaymentConsentOptions.PaymentAmountType.VARIABLE, tou.paymentAmountType)
        assertEquals(BigDecimal("1000.00"), tou.maxPaymentAmount)
        assertEquals("USD", tou.paymentCurrency)
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

    // ========== convertToLegacySession Tests ==========

    @Test
    fun `convertToLegacySession converts to AirwallexPaymentSession for one-off payment`() = runTest {
        // Arrange: Session without payment consent options (one-off payment)
        val session = Session.Builder(
            PaymentIntentFixtures.PAYMENT_INTENT,
            "US"
        )
            .setRequireBillingInformation(false)
            .setRequireEmail(true)
            .setReturnUrl("test://return")
            .setAutoCapture(false)
            .setHidePaymentConsents(true)
            .build()

        // Act
        val legacySession = session.convertToLegacySession()

        // Assert
        assertTrue(legacySession is AirwallexPaymentSession)
        val paymentSession = legacySession as AirwallexPaymentSession
        assertEquals(PaymentIntentFixtures.PAYMENT_INTENT, paymentSession.paymentIntent)
        assertEquals("US", paymentSession.countryCode)
        assertEquals(false, paymentSession.isBillingInformationRequired)
        assertEquals(true, paymentSession.isEmailRequired)
        assertEquals("test://return", paymentSession.returnUrl)
        assertEquals(false, paymentSession.autoCapture)
        assertEquals(true, paymentSession.hidePaymentConsents)
    }

    @Test
    fun `convertToLegacySession converts to AirwallexRecurringSession when amount is zero`() = runTest {
        // Arrange: Create PaymentIntent with amount = 0
        val recurringPaymentIntent = PaymentIntent(
            id = "pi_recurring",
            amount = BigDecimal.ZERO,
            currency = "USD",
            clientSecret = "test_client_secret",
            customerId = "cus_123"
        )

        val paymentConsentOptions = PaymentConsentOptions(
            nextTriggeredBy = PaymentConsent.NextTriggeredBy.MERCHANT,
            merchantTriggerReason = PaymentConsent.MerchantTriggerReason.SCHEDULED
        )

        val session = Session.Builder(
            recurringPaymentIntent,
            "US"
        )
            .setPaymentConsentOptions(paymentConsentOptions)
            .setRequireEmail(true)
            .setAutoCapture(false)
            .build()

        // Act
        val legacySession = session.convertToLegacySession()

        // Assert
        assertTrue(legacySession is AirwallexRecurringSession)
        val recurringSession = legacySession as AirwallexRecurringSession
        assertEquals("cus_123", recurringSession.customerId)
        assertEquals("test_client_secret", recurringSession.clientSecret)
        assertEquals("USD", recurringSession.currency)
        assertEquals(BigDecimal.ZERO, recurringSession.amount)
        assertEquals("US", recurringSession.countryCode)
        assertEquals(PaymentConsent.NextTriggeredBy.MERCHANT, recurringSession.nextTriggerBy)
        assertEquals(PaymentConsent.MerchantTriggerReason.SCHEDULED, recurringSession.merchantTriggerReason)
        assertEquals(true, recurringSession.isEmailRequired)
        assertEquals(false, recurringSession.autoCapture)
    }

    @Test
    fun `convertToLegacySession converts to AirwallexRecurringWithIntentSession when amount is greater than zero`() = runTest {
        // Arrange: Session with payment consent options and amount > 0
        val paymentConsentOptions = PaymentConsentOptions(
            nextTriggeredBy = PaymentConsent.NextTriggeredBy.CUSTOMER,
            merchantTriggerReason = PaymentConsent.MerchantTriggerReason.UNSCHEDULED
        )

        val session = Session.Builder(
            PaymentIntentFixtures.PAYMENT_INTENT,
            "US"
        )
            .setPaymentConsentOptions(paymentConsentOptions)
            .setRequireBillingInformation(false)
            .setRequireEmail(true)
            .setReturnUrl("test://return")
            .setAutoCapture(true)
            .build()

        // Act
        val legacySession = session.convertToLegacySession()

        // Assert
        assertTrue(legacySession is AirwallexRecurringWithIntentSession)
        val recurringWithIntentSession = legacySession as AirwallexRecurringWithIntentSession
        assertEquals(PaymentIntentFixtures.PAYMENT_INTENT, recurringWithIntentSession.paymentIntent)
        assertEquals("cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu", recurringWithIntentSession.customerId)
        assertEquals("US", recurringWithIntentSession.countryCode)
        assertEquals(PaymentConsent.NextTriggeredBy.CUSTOMER, recurringWithIntentSession.nextTriggerBy)
        assertEquals(PaymentConsent.MerchantTriggerReason.UNSCHEDULED, recurringWithIntentSession.merchantTriggerReason)
        assertEquals(false, recurringWithIntentSession.isBillingInformationRequired)
        assertEquals(true, recurringWithIntentSession.isEmailRequired)
        assertEquals("test://return", recurringWithIntentSession.returnUrl)
        assertEquals(true, recurringWithIntentSession.autoCapture)
    }

}
