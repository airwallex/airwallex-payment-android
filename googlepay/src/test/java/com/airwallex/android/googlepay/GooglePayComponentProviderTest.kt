package com.airwallex.android.googlepay

import android.app.Activity
import com.airwallex.android.core.ActionComponentProviderType
import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.GooglePayOptions
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.AvailablePaymentMethodTypeResponse
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.parser.AvailablePaymentMethodTypeResponseParser
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import io.mockk.*
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

    private val mockResponse: AvailablePaymentMethodTypeResponse =
        AvailablePaymentMethodTypeResponseParser().parse(
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
    }

    @After
    fun unmockStatics() {
        unmockkStatic(PaymentsUtil::class)
        unmockkStatic(Wallet::class)
        unmockkObject(AnalyticsLogger)
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
    fun `test canHandleSessionAndPaymentMethod when Google Pay API returns true`() = runTest {
        every { task.getResult(ApiException::class.java) } returns true
        assertTrue(canHandleSessionAndPaymentMethod())
    }

    @Test
    fun `test canHandleSessionAndPaymentMethod when Google Pay API returns exception`() = runTest {
        every { task.getResult(ApiException::class.java) } throws ApiException(
            Status.RESULT_INTERNAL_ERROR
        )
        assertFalse(canHandleSessionAndPaymentMethod())
        verify(exactly = 1) { AnalyticsLogger.logError(any(), "googlepay_is_ready") }
    }

    @Test
    fun `test canHandleSessionAndPaymentMethod when Google Pay API returns null`() = runTest {
        every { task.getResult(ApiException::class.java) } returns null
        assertFalse(canHandleSessionAndPaymentMethod())
    }

    private fun mockGooglePayTask(): Task<Boolean> {
        mockkStatic(PaymentsUtil::class)
        mockkStatic(Wallet::class)
        val mockClient = mockk<PaymentsClient>()
        val task = mockk<Task<Boolean>>()
        every { task.getResult(ApiException::class.java) } returns true

        val slot = slot<OnCompleteListener<Boolean>>()
        every { task.addOnCompleteListener(capture(slot)) } answers {
            slot.captured.onComplete(task)
            task
        }
        every {
            PaymentsUtil.createPaymentsClient(any())
        } returns mockClient
        every {
            mockClient.isReadyToPay(any())
        } returns task

        return task
    }

    private suspend fun canHandleSessionAndPaymentMethod(session: AirwallexPaymentSession? = null):
        Boolean {
        val paymentMethodType = mockResponse.items?.first() ?: return false
        return componentProvider.canHandleSessionAndPaymentMethod(
            session = session ?: defaultSession,
            paymentMethodType = paymentMethodType,
            activity = Activity()
        )
    }
}
