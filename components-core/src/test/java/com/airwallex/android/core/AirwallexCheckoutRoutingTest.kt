package com.airwallex.android.core

import android.content.Context
import androidx.activity.ComponentActivity
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodType
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertIs

class AirwallexCheckoutRoutingTest {

    @MockK
    private lateinit var mockActivity: ComponentActivity

    @MockK
    private lateinit var mockPaymentManager: PaymentManager

    @MockK
    private lateinit var mockApplicationContext: Context

    @RelaxedMockK
    private lateinit var checkoutRouter: AirwallexSessionCheckoutRouter

    @RelaxedMockK
    private lateinit var unifiedCheckoutExecutor: UnifiedCheckoutExecutor

    private lateinit var airwallex: Airwallex

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        mockkObject(AnalyticsLogger)
        mockkObject(AirwallexLogger)

        every { AnalyticsLogger.initialize(any()) } just runs
        every { AnalyticsLogger.isSessionSetup(any()) } returns true
        every { AnalyticsLogger.getLaunchType() } returns AnalyticsLogger.LaunchType.API
        every { AnalyticsLogger.logAction(any(), any()) } just runs
        every { AirwallexLogger.info(any()) } just runs

        airwallex = Airwallex(
            fragment = null,
            activity = mockActivity,
            paymentManager = mockPaymentManager,
            applicationContext = mockApplicationContext,
            checkoutRouter = checkoutRouter,
            unifiedCheckoutExecutor = unifiedCheckoutExecutor,
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

    private fun cardMethod(): PaymentMethod =
        PaymentMethod.Builder().setType(PaymentMethodType.CARD.value).build()

    @Test
    fun `new flow route dispatches to unified checkout executor`() {
        val unifiedSession = mockk<Session>()
        val paymentConsent = mockk<PaymentConsent>(relaxed = true)
        val method = cardMethod()
        val listener = RecordingListener()
        every { checkoutRouter.route(any(), any(), any(), any(), any()) } returns
            AirwallexSessionCheckoutRoute.NewFlow(unifiedSession)

        airwallex.checkout(
            session = mockk(relaxed = true),
            paymentMethod = method,
            paymentConsent = paymentConsent,
            cvc = "123",
            listener = listener,
            saveCard = true,
        )

        verify {
            unifiedCheckoutExecutor.checkout(
                unifiedSession, method, "123", true, paymentConsent, any()
            )
        }
    }

    @Test
    fun `unknown session route reports failure without touching executor`() {
        val session = mockk<AirwallexSession>(relaxed = true)
        val listener = RecordingListener()
        every { checkoutRouter.route(any(), any(), any(), any(), any()) } returns
            AirwallexSessionCheckoutRoute.UnknownSession(session)

        airwallex.checkout(
            session = session,
            paymentMethod = cardMethod(),
            paymentConsent = mockk(relaxed = true),
            listener = listener,
        )

        assertIs<AirwallexPaymentStatus.Failure>(listener.status)
        verify(exactly = 0) {
            unifiedCheckoutExecutor.checkout(any(), any(), any(), any(), any(), any())
        }
    }
}
