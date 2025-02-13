package com.airwallex.android.core

import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.Shipping
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

class AirwallexRecurringSessionTest {

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
        val shipping = Shipping.Builder()
            .setFirstName("John")
            .setLastName("Doe")
            .build()

        val airwallexRecurringSession = AirwallexRecurringSession.Builder(
            nextTriggerBy = PaymentConsent.NextTriggeredBy.CUSTOMER,
            customerId = "cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu",
            currency = "AUD",
            amount = BigDecimal.valueOf(100.01),
            countryCode = "CN",
            clientSecret = "clientSecret"
        )
            .setMerchantTriggerReason(PaymentConsent.MerchantTriggerReason.SCHEDULED)
            .setRequireBillingInformation(false)
            .setRequireCvc(true)
            .setRequireEmail(true)
            .setReturnUrl("airwallexcheckout://com.airwallex.paymentacceptance")
            .setPaymentMethods(listOf("googlepay"))
            .setGooglePayOptions(googlePayOptions)
            .setShipping(shipping)
            .build()

        assertNotNull(airwallexRecurringSession)

        assertNotNull(airwallexRecurringSession.currency)
        assertEquals("AUD", airwallexRecurringSession.currency)

        assertNotNull(airwallexRecurringSession.amount)
        assertEquals(BigDecimal.valueOf(100.01), airwallexRecurringSession.amount)

        assertNotNull(airwallexRecurringSession.nextTriggerBy)
        assertEquals(
            PaymentConsent.NextTriggeredBy.CUSTOMER,
            airwallexRecurringSession.nextTriggerBy
        )

        assertNotNull(airwallexRecurringSession.requiresCVC)
        assertEquals(true, airwallexRecurringSession.requiresCVC)

        assertNotNull(airwallexRecurringSession.isBillingInformationRequired)
        assertEquals(false, airwallexRecurringSession.isBillingInformationRequired)

        assertTrue(airwallexRecurringSession.isEmailRequired)

        assertNotNull(airwallexRecurringSession.customerId)
        assertEquals("cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu", airwallexRecurringSession.customerId)

        assertEquals(true, airwallexRecurringSession.googlePayOptions?.billingAddressRequired)
        assertEquals(
            BillingAddressParameters.Format.FULL,
            airwallexRecurringSession.googlePayOptions?.billingAddressParameters?.format
        )

        assertEquals(shipping, airwallexRecurringSession.shipping)

        assertEquals(
            "airwallexcheckout://com.airwallex.paymentacceptance",
            airwallexRecurringSession.returnUrl
        )
        assertNotNull(airwallexRecurringSession.paymentMethods)
    }
}
