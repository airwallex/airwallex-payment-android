package com.airwallex.android.googlepay

import android.app.Activity
import com.airwallex.android.core.ActionComponentProviderType
import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.AirwallexRecurringSession
import com.airwallex.android.core.AirwallexRecurringWithIntentSession
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.GooglePayOptions
import com.airwallex.android.core.TokenManager
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.Page
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.parser.AvailablePaymentMethodTypeParser
import com.airwallex.android.core.model.parser.PageParser
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.unmockkObject
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class GooglePayComponentProviderTest {
    private val defaultSession = AirwallexPaymentSession.Builder(
        PaymentIntent(
            id = "id",
            amount = BigDecimal.valueOf(100.01),
            currency = "AUD"
        ),
        "AU",
        GooglePayOptions()
    ).build()

    private val componentProvider: GooglePayComponentProvider by lazy {
        GooglePayComponentProvider()
    }

    private val mockResponse: Page<AvailablePaymentMethodType> =
        PageParser(AvailablePaymentMethodTypeParser()).parse(
            JSONObject(
                """
                    {
                    "items":[
                    {
                   "name":"googlepay",
                   "transaction_mode":"oneoff",
                   "active":true,
                   "transaction_currencies":["dollar","RMB"],
                   "flows":["inapp"],
                   "card_schemes":[{ "name": "mastercard" }]
                    }   
                    ],
                    "has_more":false
                    }
                """.trimIndent()
            )
        )

    private lateinit var task: Task<Boolean>

    @Before
    fun setUp() {
        task = mockGooglePayTask()
        mockkObject(AnalyticsLogger)
        mockkObject(TokenManager)
        every { TokenManager.updateClientSecret(any()) } just runs
        val service = mockk<GoogleApiAvailability>()
        mockkStatic(GoogleApiAvailability::class)
        every { GoogleApiAvailability.getInstance() } returns service
        every { service.isGooglePlayServicesAvailable(any()) } returns ConnectionResult.SUCCESS
    }

    @After
    fun unmockStatics() {
        unmockkStatic(PaymentsUtil::class)
        unmockkStatic(Wallet::class)
        unmockkStatic(GoogleApiAvailability::class)
        unmockkObject(AnalyticsLogger)
        unmockkObject(TokenManager)
    }

    @Test
    fun `test canHandleAction`() {
        assertEquals(componentProvider.canHandleAction(null), false)
    }

    @Test
    fun `test get instance`() {
        assertNotNull(componentProvider.get())
    }

    @Test
    fun `test getType`() {
        assertEquals(componentProvider.getType(), ActionComponentProviderType.GOOGLEPAY)
    }

    @Test
    fun `test canHandleSessionAndPaymentMethod when session doesn't have googlepay options`() =
        runTest {
            val session = AirwallexPaymentSession.Builder(
                PaymentIntent(
                    id = "id",
                    amount = BigDecimal.valueOf(100.01),
                    currency = "AUD"
                ),
                "AU"
            ).build()
            assertFalse(canHandleSessionAndPaymentMethod(session))
            assertEquals(componentProvider.get().session, session)
            assertEquals(componentProvider.get().paymentMethodType, mockResponse.items?.first())
        }

    @Test
    fun `test canHandleSessionAndPaymentMethod when skipReadinessCheck equals true`() = runTest {
        val session = AirwallexPaymentSession.Builder(
            PaymentIntent(
                id = "id",
                amount = BigDecimal.valueOf(100.01),
                currency = "AUD"
            ),
            "AU",
            GooglePayOptions(skipReadinessCheck = true)
        ).build()
        assert(canHandleSessionAndPaymentMethod(session))
    }

    @Test
    fun `test canHandleSessionAndPaymentMethod returns false for recurring session with customer nextTriggerBy`() =
        runTest {
            val session = AirwallexRecurringSession.Builder(
                customerId = "cus_123",
                clientSecret = "secret",
                currency = "AUD",
                amount = BigDecimal.valueOf(100.01),
                nextTriggerBy = PaymentConsent.NextTriggeredBy.CUSTOMER,
                countryCode = "AU"
            ).setGooglePayOptions(GooglePayOptions(skipReadinessCheck = true)).build()
            assertFalse(canHandleSessionAndPaymentMethod(session))
        }

    @Test
    fun `test canHandleSessionAndPaymentMethod returns false for recurring with intent session with customer nextTriggerBy`() =
        runTest {
            val session = AirwallexRecurringWithIntentSession.Builder(
                paymentIntent = PaymentIntent(
                    id = "id",
                    amount = BigDecimal.valueOf(100.01),
                    currency = "AUD"
                ),
                customerId = "cus_123",
                nextTriggerBy = PaymentConsent.NextTriggeredBy.CUSTOMER,
                countryCode = "AU"
            ).setGooglePayOptions(GooglePayOptions(skipReadinessCheck = true)).build()
            assertFalse(canHandleSessionAndPaymentMethod(session))
        }

    @Test
    fun `test canHandleSessionAndPaymentMethod returns true for recurring session with merchant nextTriggerBy`() =
        runTest {
            val session = AirwallexRecurringSession.Builder(
                customerId = "cus_123",
                clientSecret = "secret",
                currency = "AUD",
                amount = BigDecimal.valueOf(100.01),
                nextTriggerBy = PaymentConsent.NextTriggeredBy.MERCHANT,
                countryCode = "AU"
            ).setGooglePayOptions(GooglePayOptions(skipReadinessCheck = true)).build()
            assertTrue(canHandleSessionAndPaymentMethod(session))
        }

    private fun mockGooglePayTask(): Task<Boolean> {
        mockkStatic(PaymentsUtil::class)
        mockkStatic(Wallet::class)
        val mockClient = mockk<PaymentsClient>()
        val task = mockk<Task<Boolean>>()
        every { task.getResult(ApiException::class.java) } returns true
        every {
            PaymentsUtil.createPaymentsClient(any())
        } returns mockClient
        every {
            mockClient.isReadyToPay(any())
        } returns task

        return task
    }

    private suspend fun canHandleSessionAndPaymentMethod(session: AirwallexSession? = null):
            Boolean {
        val paymentMethodType = mockResponse.items?.first() ?: return false
        return componentProvider.canHandleSessionAndPaymentMethod(
            session = session ?: defaultSession,
            paymentMethodType = paymentMethodType,
            activity = Activity()
        )
    }
}
