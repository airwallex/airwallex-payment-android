package com.airwallex.android.core

import android.content.Context
import androidx.activity.ComponentActivity
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.exception.AirwallexComponentDependencyException
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.ConfirmPaymentIntentParams
import com.airwallex.android.core.model.Device
import com.airwallex.android.core.model.NextAction
import com.airwallex.android.core.model.Options
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.risk.AirwallexRisk
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
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DefaultConfirmPaymentServiceTest {

    @MockK
    private lateinit var mockPaymentManager: PaymentManager

    @MockK
    private lateinit var mockActivity: ComponentActivity

    @MockK
    private lateinit var mockApplicationContext: Context

    @RelaxedMockK
    private lateinit var mockProvider: ActionComponentProvider<out ActionComponent>

    @RelaxedMockK
    private lateinit var mockActionComponent: ActionComponent

    @RelaxedMockK
    private lateinit var mockDevice: Device

    private lateinit var service: DefaultConfirmPaymentService

    private val testClientSecret = "test_client_secret"
    private val testPaymentIntentId = "int_test_id"
    private val testPaymentConsentId = "cst_test_id"

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        mockkObject(AirwallexPlugins)
        mockkObject(AnalyticsLogger)
        mockkObject(AirwallexLogger)
        mockkObject(AirwallexRisk)

        every { AnalyticsLogger.logAction(any(), any()) } just runs
        every { AirwallexLogger.info(any()) } just runs
        every { AirwallexLogger.error(any<String>()) } just runs
        every { AirwallexRisk.sessionId } returns mockk(relaxed = true)

        every { AirwallexPlugins.environment } returns mockk(relaxed = true)
        every { AirwallexPlugins.getProvider(any<ActionComponentProviderType>()) } returns mockProvider
        every { mockProvider.get() } returns mockActionComponent

        every { mockPaymentManager.buildDeviceInfo(any()) } returns mockDevice

        service = DefaultConfirmPaymentService(
            paymentManager = mockPaymentManager,
            fragment = null,
            activityProvider = { mockActivity },
            applicationContext = mockApplicationContext
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    private fun cardParams(
        paymentConsentId: String? = null
    ): ConfirmPaymentIntentParams =
        ConfirmPaymentIntentParams.Builder(
            paymentIntentId = testPaymentIntentId,
            clientSecret = testClientSecret,
            paymentMethodType = PaymentMethodType.CARD.value
        ).setPaymentConsentId(paymentConsentId).build()

    private fun thirdPartyParams(): ConfirmPaymentIntentParams =
        ConfirmPaymentIntentParams.createThirdPartPayParams(
            paymentMethodType = "alipaycn",
            paymentIntentId = testPaymentIntentId,
            clientSecret = testClientSecret
        )

    private class RecordingListener : Airwallex.PaymentResultListener {
        var status: AirwallexPaymentStatus? = null
        override fun onCompleted(status: AirwallexPaymentStatus) {
            this.status = status
        }
    }

    private fun captureOperationListener(): io.mockk.CapturingSlot<Airwallex.PaymentListener<PaymentIntent>> {
        val listenerSlot = slot<Airwallex.PaymentListener<PaymentIntent>>()
        every { mockPaymentManager.startOperation(any(), capture(listenerSlot)) } just runs
        return listenerSlot
    }

    private fun successIntent(nextAction: NextAction?): PaymentIntent =
        mockk {
            every { id } returns testPaymentIntentId
            every { this@mockk.nextAction } returns nextAction
            every { currency } returns "USD"
            every { amount } returns BigDecimal.TEN
        }

    @Test
    fun `confirm reports dependency failure when provider is missing`() {
        every { AirwallexPlugins.getProvider(any<ActionComponentProviderType>()) } returns null
        val listener = RecordingListener()

        service.confirm(cardParams(), locale = null, listener = listener)

        val failure = assertIs<AirwallexPaymentStatus.Failure>(listener.status)
        assertIs<AirwallexComponentDependencyException>(failure.exception)
        verify(exactly = 0) { mockPaymentManager.startOperation(any(), any<Airwallex.PaymentListener<PaymentIntent>>()) }
    }

    @Test
    fun `confirm builds device info and starts operation when provider is present`() {
        val captured = captureOperationListener()

        service.confirm(cardParams(), locale = null, listener = RecordingListener())

        verify { mockPaymentManager.buildDeviceInfo(any()) }
        assertNotNull(captured.captured)
    }

    @Test
    fun `confirm reports checkout failure when setup throws`() {
        every { mockPaymentManager.buildDeviceInfo(any()) } throws RuntimeException("boom")
        val listener = RecordingListener()

        service.confirm(cardParams(), locale = null, listener = listener)

        val failure = assertIs<AirwallexPaymentStatus.Failure>(listener.status)
        assertIs<AirwallexCheckoutException>(failure.exception)
    }

    @Test
    fun `confirmWithDevice reports success when next action is null`() {
        val captured = captureOperationListener()
        val listener = RecordingListener()

        service.confirmWithDevice(
            device = mockDevice,
            params = cardParams(paymentConsentId = testPaymentConsentId),
            locale = null,
            listener = listener
        )
        captured.captured.onSuccess(successIntent(nextAction = null))

        val success = assertIs<AirwallexPaymentStatus.Success>(listener.status)
        assertEquals(testPaymentIntentId, success.paymentIntentId)
        assertEquals(testPaymentConsentId, success.consentId)
    }

    @Test
    fun `confirmWithDevice reports failure when operation fails`() {
        val captured = captureOperationListener()
        val listener = RecordingListener()
        val exception = AirwallexCheckoutException(message = "network error")

        service.confirmWithDevice(mockDevice, cardParams(), locale = null, listener = listener)
        captured.captured.onFailed(exception)

        val failure = assertIs<AirwallexPaymentStatus.Failure>(listener.status)
        assertEquals(exception, failure.exception)
    }

    @Test
    fun `confirmWithDevice reports failure when next action present but provider missing`() {
        val captured = captureOperationListener()
        val listener = RecordingListener()

        service.confirmWithDevice(mockDevice, cardParams(), locale = null, listener = listener)
        every { AirwallexPlugins.getProvider(any<ActionComponentProviderType>()) } returns null
        captured.captured.onSuccess(successIntent(nextAction = mockk(relaxed = true)))

        val failure = assertIs<AirwallexPaymentStatus.Failure>(listener.status)
        assertIs<AirwallexCheckoutException>(failure.exception)
    }

    @Test
    fun `confirmWithDevice delegates to provider when next action present`() {
        val captured = captureOperationListener()
        val nextAction = mockk<NextAction>(relaxed = true)

        service.confirmWithDevice(mockDevice, cardParams(), locale = null, listener = RecordingListener())
        captured.captured.onSuccess(successIntent(nextAction = nextAction))

        verify {
            mockActionComponent.handlePaymentIntentResponse(
                testPaymentIntentId,
                nextAction,
                null,
                mockActivity,
                mockApplicationContext,
                any(),
                any(),
                any()
            )
        }
    }

    @Test
    fun `confirmWithDevice builds card options for card type`() {
        val optionsSlot = slotForOptions()

        service.confirmWithDevice(mockDevice, cardParams(), locale = null, listener = RecordingListener())

        val options = assertIs<Options.ConfirmPaymentIntentOptions>(optionsSlot.captured)
        assertNotNull(options.request.paymentMethodOptions)
    }

    @Test
    fun `confirmWithDevice builds third party options for non card type`() {
        val optionsSlot = slotForOptions()

        service.confirmWithDevice(mockDevice, thirdPartyParams(), locale = null, listener = RecordingListener())

        val options = assertIs<Options.ConfirmPaymentIntentOptions>(optionsSlot.captured)
        assertNull(options.request.paymentMethodOptions)
        assertNotNull(options.request.paymentMethodRequest)
    }

    private fun slotForOptions(): io.mockk.CapturingSlot<Options> {
        val optionsSlot = slot<Options>()
        every {
            mockPaymentManager.startOperation(capture(optionsSlot), any<Airwallex.PaymentListener<PaymentIntent>>())
        } just runs
        return optionsSlot
    }
}
