package com.airwallex.android.core

import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.model.ConfirmPaymentIntentParams
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentConsentOptions
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodType
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class UnifiedCheckoutExecutorTest {

    @RelaxedMockK
    private lateinit var confirmPaymentService: ConfirmPaymentService

    @MockK
    private lateinit var googlePayDelegate: GooglePayCheckoutDelegate

    @MockK
    private lateinit var session: Session

    private lateinit var executor: UnifiedCheckoutExecutor

    private val testReturnUrl = "https://3ds.return"

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        mockkObject(AirwallexLogger)
        mockkObject(AirwallexPlugins)
        mockkStatic("com.airwallex.android.core.PaymentIntentProviderKt")

        every { AirwallexLogger.info(any()) } just runs
        every { AirwallexPlugins.environment } returns mockk(relaxed = true) {
            every { threeDsReturnUrl() } returns testReturnUrl
        }

        every { session.customerId } returns null
        every { session.returnUrl } returns "https://session.return"
        every { session.autoCapture } returns true
        every { session.isOneOffPayment } returns true
        every { session.paymentConsentOptions } returns null
        every { session.locale } returns Locale.US

        executor = UnifiedCheckoutExecutor(
            confirmPaymentService = confirmPaymentService,
            googlePayDelegate = googlePayDelegate
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    private fun cardMethod(): PaymentMethod =
        PaymentMethod.Builder().setType(PaymentMethodType.CARD.value).build()

    private fun googlePayMethod(): PaymentMethod =
        PaymentMethod.Builder().setType(PaymentMethodType.GOOGLEPAY.value).build()

    private fun stubResolveSuccess(intentId: String = "int_1", clientSecret: String? = "cs_1") {
        val intent = mockk<PaymentIntent> {
            every { id } returns intentId
            every { this@mockk.clientSecret } returns clientSecret
        }
        val slot = slot<PaymentIntentProvider.PaymentIntentCallback>()
        every { session.resolvePaymentIntent(capture(slot)) } answers { slot.captured.onSuccess(intent) }
    }

    private fun captureConfirmParams(): io.mockk.CapturingSlot<ConfirmPaymentIntentParams> {
        val paramsSlot = slot<ConfirmPaymentIntentParams>()
        every { confirmPaymentService.confirm(capture(paramsSlot), any(), any()) } just runs
        return paramsSlot
    }

    private class RecordingListener : Airwallex.PaymentResultListener {
        var status: AirwallexPaymentStatus? = null
        override fun onCompleted(status: AirwallexPaymentStatus) {
            this.status = status
        }
    }

    @Test
    fun `card checkout confirms with card params and 3ds return url`() {
        stubResolveSuccess()
        val paramsSlot = captureConfirmParams()
        val listener = RecordingListener()

        executor.checkout(session, cardMethod(), listener = listener)

        verify { confirmPaymentService.confirm(any(), Locale.US, listener) }
        val params = paramsSlot.captured
        assertEquals(PaymentMethodType.CARD.value, params.paymentMethodType)
        assertEquals(testReturnUrl, params.returnUrl)
    }

    @Test
    fun `google pay checkout acquires token then confirms with google pay params`() {
        stubResolveSuccess()
        val paramsSlot = captureConfirmParams()
        val listener = RecordingListener()
        val tokenSlot = slot<(PaymentMethod.GooglePay, ActionComponentProvider<out ActionComponent>) -> Unit>()
        every { googlePayDelegate.acquireToken(session, listener, capture(tokenSlot)) } answers {
            tokenSlot.captured.invoke(mockk(relaxed = true), mockk(relaxed = true))
        }

        executor.checkout(session, googlePayMethod(), listener = listener)

        val params = paramsSlot.captured
        assertEquals(PaymentMethodType.GOOGLEPAY.value, params.paymentMethodType)
        assertEquals(testReturnUrl, params.returnUrl)
    }

    @Test
    fun `checkout reports failure when payment intent resolution fails`() {
        val error = IllegalStateException("no intent")
        val slot = slot<PaymentIntentProvider.PaymentIntentCallback>()
        every { session.resolvePaymentIntent(capture(slot)) } answers { slot.captured.onError(error) }
        val listener = RecordingListener()

        executor.checkout(session, cardMethod(), listener = listener)

        val failure = assertIs<AirwallexPaymentStatus.Failure>(listener.status)
        assertIs<AirwallexCheckoutException>(failure.exception)
        verify(exactly = 0) { confirmPaymentService.confirm(any(), any(), any()) }
    }

    @Test
    fun `session consent options take precedence`() {
        val sessionOptions = PaymentConsentOptions(nextTriggeredBy = PaymentConsent.NextTriggeredBy.MERCHANT)
        every { session.paymentConsentOptions } returns sessionOptions
        stubResolveSuccess()
        val paramsSlot = captureConfirmParams()

        executor.checkout(session, cardMethod(), listener = RecordingListener())

        assertEquals(sessionOptions, paramsSlot.captured.paymentConsentOptions)
    }

    @Test
    fun `save card with customer creates customer initiated consent`() {
        every { session.customerId } returns "cus_1"
        stubResolveSuccess()
        val paramsSlot = captureConfirmParams()

        executor.checkout(session, cardMethod(), saveCard = true, listener = RecordingListener())

        assertEquals(
            PaymentConsent.NextTriggeredBy.CUSTOMER,
            paramsSlot.captured.paymentConsentOptions?.nextTriggeredBy
        )
    }

    @Test
    fun `merchant consent on one off payment overrides to customer initiated`() {
        val paymentConsent = mockk<PaymentConsent> {
            every { id } returns "pc_1"
            every { nextTriggeredBy } returns PaymentConsent.NextTriggeredBy.MERCHANT
        }
        stubResolveSuccess()
        val paramsSlot = captureConfirmParams()

        executor.checkout(session, cardMethod(), paymentConsent = paymentConsent, listener = RecordingListener())

        assertEquals(
            PaymentConsent.NextTriggeredBy.CUSTOMER,
            paramsSlot.captured.paymentConsentOptions?.nextTriggeredBy
        )
    }

    @Test
    fun `one off card without save card has no consent options`() {
        stubResolveSuccess()
        val paramsSlot = captureConfirmParams()

        executor.checkout(session, cardMethod(), listener = RecordingListener())

        assertNull(paramsSlot.captured.paymentConsentOptions)
    }
}
