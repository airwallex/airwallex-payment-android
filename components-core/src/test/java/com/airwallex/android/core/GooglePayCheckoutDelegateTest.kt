package com.airwallex.android.core

import android.content.Context
import androidx.activity.ComponentActivity
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.model.PaymentMethod
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class GooglePayCheckoutDelegateTest {

    @MockK
    private lateinit var mockActivity: ComponentActivity

    @MockK
    private lateinit var mockApplicationContext: Context

    @RelaxedMockK
    private lateinit var mockProvider: ActionComponentProvider<out ActionComponent>

    @RelaxedMockK
    private lateinit var mockActionComponent: ActionComponent

    @MockK
    private lateinit var session: Session

    private lateinit var delegate: GooglePayCheckoutDelegate

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        mockkObject(AirwallexPlugins)
        mockkObject(AirwallexLogger)

        every { AirwallexLogger.error(any<String>()) } just runs
        every { AirwallexPlugins.getProvider(ActionComponentProviderType.GOOGLEPAY) } returns mockProvider
        every { mockProvider.get() } returns mockActionComponent
        every { session.paymentIntent } returns null

        delegate = GooglePayCheckoutDelegate(
            fragment = null,
            activityProvider = { mockActivity },
            applicationContext = mockApplicationContext
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    private class RecordingListener : Airwallex.PaymentResultListener {
        var status: AirwallexPaymentStatus? = null
        override fun onCompleted(status: AirwallexPaymentStatus) {
            this.status = status
        }
    }

    private fun captureInnerListener(): io.mockk.CapturingSlot<Airwallex.PaymentResultListener> {
        val listenerSlot = slot<Airwallex.PaymentResultListener>()
        every {
            mockActionComponent.handlePaymentIntentResponse(
                any(), any(), any(), any(), any(), any(), capture(listenerSlot), any()
            )
        } just runs
        return listenerSlot
    }

    @Test
    fun `reports dependency failure when google pay provider is missing`() {
        every { AirwallexPlugins.getProvider(ActionComponentProviderType.GOOGLEPAY) } returns null
        val listener = RecordingListener()
        var tokenReceived = false

        delegate.acquireToken(session, listener) { _, _ -> tokenReceived = true }

        val failure = assertIs<AirwallexPaymentStatus.Failure>(listener.status)
        assertIs<AirwallexCheckoutException>(failure.exception)
        assert(!tokenReceived)
        verify(exactly = 0) { mockActionComponent.handlePaymentIntentResponse(any(), any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `hands token and provider to callback on success`() {
        val inner = captureInnerListener()
        val listener = RecordingListener()
        var receivedToken: PaymentMethod.GooglePay? = null
        var receivedProvider: ActionComponentProvider<out ActionComponent>? = null

        delegate.acquireToken(session, listener) { googlePay, provider ->
            receivedToken = googlePay
            receivedProvider = provider
        }
        inner.captured.onCompleted(
            AirwallexPaymentStatus.Success(
                paymentIntentId = "int_1",
                additionalInfo = mapOf("payment_data_type" to "encrypted", "encrypted_payment_token" to "tok")
            )
        )

        assertNotNull(receivedToken)
        assertNotNull(receivedProvider)
    }

    @Test
    fun `reports failure when token response is missing`() {
        val inner = captureInnerListener()
        val listener = RecordingListener()
        var tokenReceived = false

        delegate.acquireToken(session, listener) { _, _ -> tokenReceived = true }
        inner.captured.onCompleted(AirwallexPaymentStatus.Success(paymentIntentId = "int_1", additionalInfo = null))

        val failure = assertIs<AirwallexPaymentStatus.Failure>(listener.status)
        assertIs<AirwallexCheckoutException>(failure.exception)
        assert(!tokenReceived)
    }

    @Test
    fun `forwards non success status to listener`() {
        val inner = captureInnerListener()
        val listener = RecordingListener()
        var tokenReceived = false

        delegate.acquireToken(session, listener) { _, _ -> tokenReceived = true }
        inner.captured.onCompleted(AirwallexPaymentStatus.Cancel)

        assert(listener.status is AirwallexPaymentStatus.Cancel)
        assert(!tokenReceived)
    }
}
