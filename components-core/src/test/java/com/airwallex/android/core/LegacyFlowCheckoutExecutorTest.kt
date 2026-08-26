package com.airwallex.android.core

import android.content.Context
import com.airwallex.android.core.Airwallex.PaymentListener
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.model.Billing
import com.airwallex.android.core.model.ConfirmPaymentIntentParams
import com.airwallex.android.core.model.Device
import com.airwallex.android.core.model.Options
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.risk.AirwallexRisk
import io.mockk.MockKAnnotations
import io.mockk.every
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
import java.math.BigDecimal
import java.util.Locale
import kotlin.test.assertIs

class LegacyFlowCheckoutExecutorTest {

    @RelaxedMockK
    private lateinit var paymentManager: PaymentManager

    @RelaxedMockK
    private lateinit var confirmPaymentService: ConfirmPaymentService

    @RelaxedMockK
    private lateinit var verificationService: PaymentConsentVerificationService

    @RelaxedMockK
    private lateinit var googlePayDelegate: GooglePayCheckoutDelegate

    private lateinit var createCardFn:
        (AirwallexSession, PaymentMethod.Card, Billing?, Boolean, PaymentListener<PaymentMethod>) -> Unit

    private lateinit var executor: LegacyFlowCheckoutExecutor

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

        createCardFn = mockk(relaxed = true)

        executor = LegacyFlowCheckoutExecutor(
            fragment = null,
            activityProvider = { mockk() },
            applicationContext = mockk<Context>(relaxed = true),
            paymentManager = paymentManager,
            confirmPaymentService = confirmPaymentService,
            verificationService = verificationService,
            googlePayDelegate = googlePayDelegate,
            createCardPaymentMethod = createCardFn,
            buildCreatePaymentConsentOptions = { mockk<Options.CreatePaymentConsentOptions>(relaxed = true) }
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    private fun cardMethod(card: PaymentMethod.Card? = null): PaymentMethod =
        PaymentMethod.Builder().setType(PaymentMethodType.CARD.value).setCard(card).build()

    private fun googlePayMethod(): PaymentMethod =
        PaymentMethod.Builder().setType(PaymentMethodType.GOOGLEPAY.value).build()

    private fun lpmMethod(type: String): PaymentMethod =
        PaymentMethod.Builder().setType(type).build()

    private fun stubResolveSuccess(
        session: AirwallexSession,
        intentId: String = "int_1",
        clientSecret: String? = "cs_1"
    ) {
        val intent = mockk<PaymentIntent> {
            every { id } returns intentId
            every { this@mockk.clientSecret } returns clientSecret
        }
        val callback = slot<PaymentIntentProvider.PaymentIntentCallback>()
        every { session.resolvePaymentIntent(capture(callback)) } answers { callback.captured.onSuccess(intent) }
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
    fun `recurring card with null card reports failure without creating payment method`() {
        val session = mockk<AirwallexRecurringSession>(relaxed = true)
        val listener = RecordingListener()

        executor.checkout(session, cardMethod(card = null), listener = listener)

        assertIs<AirwallexPaymentStatus.Failure>(listener.status)
        verify(exactly = 0) { createCardFn(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `recurring card creates payment method with save card false`() {
        val session = mockk<AirwallexRecurringSession>(relaxed = true)
        val card = mockk<PaymentMethod.Card>(relaxed = true)

        executor.checkout(session, cardMethod(card = card), listener = RecordingListener())

        verify { createCardFn(session, card, any(), false, any()) }
    }

    @Test
    fun `recurring card reports failure when payment method creation fails`() {
        val session = mockk<AirwallexRecurringSession>(relaxed = true)
        val card = mockk<PaymentMethod.Card>(relaxed = true)
        val listenerSlot = slot<PaymentListener<PaymentMethod>>()
        every {
            createCardFn(any(), any(), any(), any(), capture(listenerSlot))
        } answers {
            listenerSlot.captured.onFailed(AirwallexCheckoutException(message = "boom"))
        }
        val listener = RecordingListener()

        executor.checkout(session, cardMethod(card = card), listener = listener)

        assertIs<AirwallexPaymentStatus.Failure>(listener.status)
    }

    @Test
    fun `recurring google pay acquires token`() {
        val session = mockk<AirwallexRecurringSession>(relaxed = true)
        val listener = RecordingListener()

        executor.checkout(session, googlePayMethod(), listener = listener)

        verify { googlePayDelegate.acquireToken(session, listener, any()) }
    }

    @Test
    fun `legacy payment session card confirms with card params and 3ds return url`() {
        val session = mockk<AirwallexPaymentSession>(relaxed = true) {
            every { customerId } returns null
            every { currency } returns "USD"
            every { autoCapture } returns true
            every { returnUrl } returns "https://session.return"
        }
        stubResolveSuccess(session)
        val paramsSlot = captureConfirmParams()

        executor.checkout(session, cardMethod(), listener = RecordingListener())

        verify { confirmPaymentService.confirm(any(), any(), any()) }
        val params = paramsSlot.captured
        kotlin.test.assertEquals(PaymentMethodType.CARD.value, params.paymentMethodType)
        kotlin.test.assertEquals(testReturnUrl, params.returnUrl)
    }

    @Test
    fun `legacy payment session reports failure when intent resolution fails`() {
        val session = mockk<AirwallexPaymentSession>(relaxed = true)
        val callback = slot<PaymentIntentProvider.PaymentIntentCallback>()
        every { session.resolvePaymentIntent(capture(callback)) } answers {
            callback.captured.onError(IllegalStateException("no intent"))
        }
        val listener = RecordingListener()

        executor.checkout(session, cardMethod(), listener = listener)

        val failure = assertIs<AirwallexPaymentStatus.Failure>(listener.status)
        assertIs<AirwallexCheckoutException>(failure.exception)
        verify(exactly = 0) { confirmPaymentService.confirm(any(), any(), any()) }
    }

    @Test
    fun `recurring lpm creates consent then verifies`() {
        mockkObject(AirwallexRisk)
        every { AirwallexRisk.sessionId } returns mockk(relaxed = true)
        every { AirwallexPlugins.getProvider(any<ActionComponentProviderType>()) } returns mockk(relaxed = true)
        every { paymentManager.buildDeviceInfo(any()) } returns mockk<Device>(relaxed = true)

        val lpmType = "alipayhk"
        val session = mockk<AirwallexRecurringSession>(relaxed = true) {
            every { clientSecret } returns "cs_1"
            every { customerId } returns "cus_1"
            every { nextTriggerBy } returns PaymentConsent.NextTriggeredBy.MERCHANT
            every { merchantTriggerReason } returns PaymentConsent.MerchantTriggerReason.UNSCHEDULED
            every { currency } returns "USD"
            every { amount } returns BigDecimal.TEN
            every { returnUrl } returns "https://session.return"
            every { locale } returns Locale.US
        }

        val consent = mockk<PaymentConsent> {
            every { requiresCvc } returns false
            every { id } returns "pc_1"
            every { clientSecret } returns "cs_pc_1"
            every { paymentMethod } returns mockk { every { type } returns lpmType }
        }
        val consentListener = slot<PaymentListener<PaymentConsent>>()
        every {
            paymentManager.startOperation(any<Options.CreatePaymentConsentOptions>(), capture(consentListener))
        } answers {
            consentListener.captured.onSuccess(consent)
        }

        executor.checkout(session, lpmMethod(lpmType), listener = RecordingListener())

        verify { verificationService.verify(any(), any(), Locale.US, any()) }
    }
}
