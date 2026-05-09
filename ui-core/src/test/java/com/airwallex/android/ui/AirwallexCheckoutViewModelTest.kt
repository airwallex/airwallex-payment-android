package com.airwallex.android.ui

import android.app.Application
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexRecurringSession
import com.airwallex.android.core.AirwallexRecurringWithIntentSession
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.model.AirwallexPaymentRequestFlow
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.BankResponse
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodTypeInfo
import com.airwallex.android.core.model.TransactionMode
import com.airwallex.android.ui.checkout.AirwallexCheckoutViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class AirwallexCheckoutViewModelTest {
    private lateinit var viewModel: AirwallexCheckoutViewModel
    private val airwallex: Airwallex = mockk()
    private val session: AirwallexSession = mockk()
    private val paymentMethod: PaymentMethod = mockk()
    private val application = mockk<Application>()

    @Before
    fun setUp() {
        every {
            airwallex.checkout(
                session,
                paymentMethod,
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } just runs
        viewModel = AirwallexCheckoutViewModel(application, airwallex, session)
    }

    @Test
    fun `test checkout when saveCard is false`() {
        val paymentSession = mockk<AirwallexPaymentSession>()
        val vm = AirwallexCheckoutViewModel(application, airwallex, paymentSession)
        every {
            airwallex.checkout(
                paymentSession,
                paymentMethod,
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } just runs

        vm.checkout(
            paymentMethod = paymentMethod,
            paymentConsentId = null,
            cvc = null
        )
        verify(exactly = 1) {
            airwallex.checkout(
                paymentSession,
                paymentMethod,
                any(),
                null,
                any(),
                any(),
                any(),
                any()
            )
        }
    }

    @Test
    fun `test checkout with one-off session passes PaymentConsent with correct id and nextTriggeredBy`() {
        val paymentSession = mockk<AirwallexPaymentSession>()
        val consentSlot = slot<PaymentConsent>()
        val vm = AirwallexCheckoutViewModel(application, airwallex, paymentSession)
        every {
            airwallex.checkout(
                paymentSession,
                paymentMethod,
                capture(consentSlot),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } just runs

        vm.checkout(
            paymentMethod = paymentMethod,
            paymentConsentId = "consent_123",
            cvc = "123"
        )

        assertEquals("consent_123", consentSlot.captured.id)
        assertEquals(PaymentConsent.NextTriggeredBy.CUSTOMER, consentSlot.captured.nextTriggeredBy)
    }

    @Test
    fun `test checkout with non-one-off session returns failure`() {
        val recurringSession = mockk<AirwallexRecurringSession>()
        val vm = AirwallexCheckoutViewModel(application, airwallex, recurringSession)

        val result = vm.checkout(
            paymentMethod = paymentMethod,
            paymentConsentId = "consent_123",
            cvc = null
        )
        assertIs<AirwallexPaymentStatus.Failure>(result.value).apply {
            assertIs<AirwallexCheckoutException>(exception).apply {
                assertEquals("Payment Consent is only supported for one-off payment sessions", message)
            }
        }
        verify(exactly = 0) { airwallex.checkout(any(), any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `test suspend function checkout`() = runTest {
        val additionalInfo = mapOf("key" to "value")
        val flow = mockk<AirwallexPaymentRequestFlow>()
        val expectedStatus = mockk<AirwallexPaymentStatus>()

        val listenerSlot = slot<Airwallex.PaymentResultListener>()

        coEvery {
            airwallex.checkout(
                session = session,
                paymentMethod = paymentMethod,
                additionalInfo = additionalInfo,
                flow = flow,
                listener = capture(listenerSlot)
            )
        } answers {
            listenerSlot.captured.onCompleted(expectedStatus)
        }

        val result = viewModel.checkout(
            paymentMethod = paymentMethod,
            additionalInfo = additionalInfo,
            flow = flow
        )

        assertEquals(expectedStatus, result)
        coVerify(exactly = 1) {
            airwallex.checkout(
                session = session,
                paymentMethod = paymentMethod,
                additionalInfo = additionalInfo,
                flow = flow,
                listener = any()
            )
        }
    }

    @Test
    fun `test suspend function checkoutGooglePay`() = runTest {
        val listenerSlot = slot<Airwallex.PaymentResultListener>()
        val expectedStatus = mockk<AirwallexPaymentStatus>()

        coEvery {
            airwallex.startGooglePay(
                session = session,
                listener = capture(listenerSlot)
            )
        } answers {
            listenerSlot.captured.onCompleted(expectedStatus)
        }

        val result = viewModel.checkoutGooglePay()

        assertEquals(expectedStatus, result)
        coVerify(exactly = 1) {
            airwallex.startGooglePay(
                session = session,
                listener = any()
            )
        }
    }

    @Test
    fun `test suspend function retrieveBanks success`() = runTest {
        val airwallexPaymentSession = mockk<AirwallexPaymentSession>()
        val paymentIntent = mockk<PaymentIntent>()
        val paymentMethodTypeName = "card"
        val bankResponse = mockk<BankResponse>()
        val listenerSlot = slot<Airwallex.PaymentListener<BankResponse>>()

        every { airwallexPaymentSession.paymentIntent } returns paymentIntent
        every { paymentIntent.clientSecret } returns "client_secret"
        every { airwallexPaymentSession.countryCode } returns "AU"

        val viewModel = AirwallexCheckoutViewModel(application, airwallex, airwallexPaymentSession)

        every {
            airwallex.retrieveBanks(any(), capture(listenerSlot))
        } answers {
            listenerSlot.captured.onSuccess(bankResponse)
        }

        val result = viewModel.retrieveBanks(paymentMethodTypeName)

        assertTrue(result.isSuccess)
        assertEquals(bankResponse, result.getOrNull())
        verify(exactly = 1) { airwallex.retrieveBanks(any(), any()) }
    }

    @Test
    fun `test suspend function retrieveBanks failure`() = runTest {
        val airwallexPaymentSession = mockk<AirwallexPaymentSession>()
        val paymentIntent = mockk<PaymentIntent>()
        val paymentMethodTypeName = "card"
        val exception = mockk<AirwallexException>()
        val listenerSlot = slot<Airwallex.PaymentListener<BankResponse>>()

        every { airwallexPaymentSession.paymentIntent } returns paymentIntent
        every { paymentIntent.clientSecret } returns "client_secret"
        every { airwallexPaymentSession.countryCode } returns "AU"

        val viewModel = AirwallexCheckoutViewModel(application, airwallex, airwallexPaymentSession)

        every {
            airwallex.retrieveBanks(any(), capture(listenerSlot))
        } answers {
            listenerSlot.captured.onFailed(exception)
        }

        val result = viewModel.retrieveBanks(paymentMethodTypeName)
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(exactly = 1) { airwallex.retrieveBanks(any(), any()) }
    }

    @Test
    fun `test suspend function retrieveBanks unsupported session`() = runTest {
        val paymentMethodTypeName = "card"

        val result = viewModel.retrieveBanks(paymentMethodTypeName)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AirwallexCheckoutException)
        assertEquals(
            "$paymentMethodTypeName just support one-off payment",
            result.exceptionOrNull()?.message
        )
    }

    @Test
    fun `test suspend function retrievePaymentMethodTypeInfo success`() = runTest {
        val airwallexPaymentSession = mockk<AirwallexPaymentSession>()
        val paymentIntent = mockk<PaymentIntent>()
        val paymentMethodTypeName = "card"
        val paymentMethodTypeInfo = mockk<PaymentMethodTypeInfo>()
        val listenerSlot = slot<Airwallex.PaymentListener<PaymentMethodTypeInfo>>()

        every { airwallexPaymentSession.paymentIntent } returns paymentIntent
        every { paymentIntent.clientSecret } returns "client_secret"

        val viewModel = AirwallexCheckoutViewModel(application, airwallex, airwallexPaymentSession)

        every {
            airwallex.retrievePaymentMethodTypeInfo(any(), capture(listenerSlot))
        } answers {
            listenerSlot.captured.onSuccess(paymentMethodTypeInfo)
        }

        val result = viewModel.retrievePaymentMethodTypeInfo(paymentMethodTypeName)

        assertTrue(result.isSuccess)
        assertEquals(paymentMethodTypeInfo, result.getOrNull())
        verify(exactly = 1) { airwallex.retrievePaymentMethodTypeInfo(any(), any()) }
    }

    @Test
    fun `test suspend function retrievePaymentMethodTypeInfo failure`() = runTest {
        val airwallexPaymentSession = mockk<AirwallexPaymentSession>()
        val paymentIntent = mockk<PaymentIntent>()
        val paymentMethodTypeName = "card"
        val exception = mockk<AirwallexException>()
        val listenerSlot = slot<Airwallex.PaymentListener<PaymentMethodTypeInfo>>()

        every { airwallexPaymentSession.paymentIntent } returns paymentIntent
        every { paymentIntent.clientSecret } returns "client_secret"

        val viewModel = AirwallexCheckoutViewModel(application, airwallex, airwallexPaymentSession)

        every {
            airwallex.retrievePaymentMethodTypeInfo(any(), capture(listenerSlot))
        } answers {
            listenerSlot.captured.onFailed(exception)
        }

        val result = viewModel.retrievePaymentMethodTypeInfo(paymentMethodTypeName)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(exactly = 1) { airwallex.retrievePaymentMethodTypeInfo(any(), any()) }
    }

    @Test
    fun `test suspend function retrievePaymentMethodTypeInfo unsupported session`() = runTest {
        val paymentMethodTypeName = "card"

        val result = viewModel.retrievePaymentMethodTypeInfo(paymentMethodTypeName)

        assertTrue(result.isFailure)
        assertIs<AirwallexCheckoutException>(result.exceptionOrNull()).apply {
            assertEquals("Session is not available", message)
        }
    }

    @Test
    fun `test transactionMode with AirwallexPaymentSession should be ONE_OFF`() {
        val paymentSession = mockk<AirwallexPaymentSession>()
        val viewModel = AirwallexCheckoutViewModel(application, airwallex, paymentSession)

        assertEquals(TransactionMode.ONE_OFF, viewModel.transactionMode)
    }

    @Test
    fun `test transactionMode with AirwallexRecurringSession should be RECURRING`() {
        val recurringSession = mockk<AirwallexRecurringSession>()
        val viewModel = AirwallexCheckoutViewModel(application, airwallex, recurringSession)

        assertEquals(TransactionMode.RECURRING, viewModel.transactionMode)
    }

    @Test
    fun `test transactionMode with AirwallexRecurringWithIntentSession should be RECURRING`() {
        val recurringWithIntentSession = mockk<AirwallexRecurringWithIntentSession>()
        val viewModel =
            AirwallexCheckoutViewModel(application, airwallex, recurringWithIntentSession)

        assertEquals(TransactionMode.RECURRING, viewModel.transactionMode)
    }

    @Test
    fun `test transactionMode with unknown session type should default to ONE_OFF`() {
        val unknownSession = mockk<AirwallexSession>()
        val viewModel = AirwallexCheckoutViewModel(application, airwallex, unknownSession)

        assertEquals(TransactionMode.ONE_OFF, viewModel.transactionMode)
    }

    @Test
    fun `test updateActivity should update the activity instance`() {
        every { airwallex.updateActivity(any()) } just runs
        val newActivity = mockk<androidx.activity.ComponentActivity>()
        viewModel.updateActivity(newActivity)
        verify(exactly = 1) { airwallex.updateActivity(newActivity) }
    }

    @Test
    fun `test retrieveBanks with AirwallexPaymentSession using PaymentIntent`() = runTest {
        val paymentIntent = mockk<PaymentIntent> {
            every { clientSecret } returns "test_client_secret"
        }
        val session = mockk<AirwallexPaymentSession> {
            every { countryCode } returns "US"
            every { this@mockk.paymentIntent } returns paymentIntent
        }

        val expectedBankResponse = mockk<BankResponse>()
        val bankListenerSlot = slot<Airwallex.PaymentListener<BankResponse>>()

        every {
            airwallex.retrieveBanks(any(), capture(bankListenerSlot))
        } answers {
            bankListenerSlot.captured.onSuccess(expectedBankResponse)
        }

        val viewModel = AirwallexCheckoutViewModel(application, airwallex, session)
        val result = viewModel.retrieveBanks("test_payment_method")

        assertTrue(result.isSuccess)
        assertEquals(expectedBankResponse, result.getOrNull())
    }

    @Test
    fun `test retrievePaymentMethodTypeInfo with AirwallexPaymentSession using PaymentIntent`() = runTest {
        val paymentIntent = mockk<PaymentIntent> {
            every { clientSecret } returns "test_client_secret"
        }
        val session = mockk<AirwallexPaymentSession> {
            every { this@mockk.paymentIntent } returns paymentIntent
        }

        val expectedTypeInfo = mockk<PaymentMethodTypeInfo>()
        val typeInfoListenerSlot = slot<Airwallex.PaymentListener<PaymentMethodTypeInfo>>()

        every {
            airwallex.retrievePaymentMethodTypeInfo(any(), capture(typeInfoListenerSlot))
        } answers {
            typeInfoListenerSlot.captured.onSuccess(expectedTypeInfo)
        }

        val viewModel = AirwallexCheckoutViewModel(application, airwallex, session)
        val result = viewModel.retrievePaymentMethodTypeInfo("test_payment_method")

        assertTrue(result.isSuccess)
        assertEquals(expectedTypeInfo, result.getOrNull())
    }

    @Test
    fun `test retrievePaymentMethodTypeInfo with AirwallexRecurringWithIntentSession resolvePaymentIntent success`() = runTest {
        val paymentIntent = mockk<PaymentIntent> {
            every { clientSecret } returns "test_recurring_client_secret"
        }
        val session = mockk<AirwallexRecurringWithIntentSession> {
            every { this@mockk.paymentIntent } returns paymentIntent
        }

        val expectedTypeInfo = mockk<PaymentMethodTypeInfo>()
        val typeInfoListenerSlot = slot<Airwallex.PaymentListener<PaymentMethodTypeInfo>>()

        every {
            airwallex.retrievePaymentMethodTypeInfo(any(), capture(typeInfoListenerSlot))
        } answers {
            typeInfoListenerSlot.captured.onSuccess(expectedTypeInfo)
        }

        val viewModel = AirwallexCheckoutViewModel(application, airwallex, session)
        val result = viewModel.retrievePaymentMethodTypeInfo("test_payment_method")

        assertTrue(result.isSuccess)
        assertEquals(expectedTypeInfo, result.getOrNull())
    }

    @Test
    fun `test retrievePaymentMethodTypeInfo with AirwallexRecurringWithIntentSession resolvePaymentIntent error`() = runTest {
        val session = mockk<AirwallexRecurringWithIntentSession>()

        // Use spyk to replace paymentIntent with null
        val sessionSpy = spyk(session) {
            every { paymentIntent } returns null
        }

        val viewModel = AirwallexCheckoutViewModel(application, airwallex, sessionSpy)
        val result = viewModel.retrievePaymentMethodTypeInfo("test_payment_method")

        assertTrue(result.isFailure)
        assertIs<AirwallexCheckoutException>(result.exceptionOrNull()).apply {
            assertEquals("Neither paymentIntent nor paymentIntentProvider available", message)
        }
    }
}