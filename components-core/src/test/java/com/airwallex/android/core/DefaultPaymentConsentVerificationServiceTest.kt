package com.airwallex.android.core

import android.content.Context
import androidx.activity.ComponentActivity
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.Device
import com.airwallex.android.core.model.NextAction
import com.airwallex.android.core.model.Options
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.core.model.VerifyPaymentConsentParams
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

class DefaultPaymentConsentVerificationServiceTest {

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

    private lateinit var service: DefaultPaymentConsentVerificationService

    private val testClientSecret = "test_client_secret"
    private val testConsentId = "cst_test_id"
    private val testPaymentIntentId = "int_test_id"

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        mockkObject(AirwallexPlugins)
        mockkObject(AnalyticsLogger)
        mockkObject(AirwallexLogger)

        every { AnalyticsLogger.logError(any<String>(), any<Map<String, Any>>()) } just runs
        every { AirwallexLogger.info(any()) } just runs
        every { AirwallexLogger.error(any<String>()) } just runs

        every { AirwallexPlugins.getProvider(any<ActionComponentProviderType>()) } returns mockProvider
        every { mockProvider.get() } returns mockActionComponent

        service = DefaultPaymentConsentVerificationService(
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

    private fun cardParams(paymentMethodType: String = PaymentMethodType.CARD.value): VerifyPaymentConsentParams =
        VerifyPaymentConsentParams.Builder(
            clientSecret = testClientSecret,
            paymentConsentId = testConsentId,
            paymentMethodType = paymentMethodType
        ).setAmount(BigDecimal.TEN).setCurrency("USD").build()

    private fun thirdPartyParams(): VerifyPaymentConsentParams =
        VerifyPaymentConsentParams.Builder(
            clientSecret = testClientSecret,
            paymentConsentId = testConsentId,
            paymentMethodType = "alipaycn"
        ).build()

    private class RecordingListener : Airwallex.PaymentResultListener {
        var status: AirwallexPaymentStatus? = null
        override fun onCompleted(status: AirwallexPaymentStatus) {
            this.status = status
        }
    }

    private fun captureOperationListener(): io.mockk.CapturingSlot<Airwallex.PaymentListener<PaymentConsent>> {
        val listenerSlot = slot<Airwallex.PaymentListener<PaymentConsent>>()
        every { mockPaymentManager.startOperation(any(), capture(listenerSlot)) } just runs
        return listenerSlot
    }

    private fun slotForOptions(): io.mockk.CapturingSlot<Options> {
        val optionsSlot = slot<Options>()
        every {
            mockPaymentManager.startOperation(capture(optionsSlot), any<Airwallex.PaymentListener<PaymentConsent>>())
        } just runs
        return optionsSlot
    }

    private fun consent(
        nextAction: NextAction?,
        initialPaymentIntentId: String? = testPaymentIntentId
    ): PaymentConsent =
        mockk {
            every { id } returns testConsentId
            every { this@mockk.nextAction } returns nextAction
            every { this@mockk.initialPaymentIntentId } returns initialPaymentIntentId
        }

    @Test
    fun `verify starts operation with card verification options for card type`() {
        val optionsSlot = slotForOptions()

        service.verify(mockDevice, cardParams(), locale = null, listener = RecordingListener())

        val options = assertIs<Options.VerifyPaymentConsentOptions>(optionsSlot.captured)
        assertEquals(
            PaymentMethodType.CARD.value,
            options.request.verificationOptions?.type
        )
    }

    @Test
    fun `verify uses card verification options for google pay type`() {
        val optionsSlot = slotForOptions()

        service.verify(mockDevice, cardParams(PaymentMethodType.GOOGLEPAY.value), locale = null, listener = RecordingListener())

        val options = assertIs<Options.VerifyPaymentConsentOptions>(optionsSlot.captured)
        assertEquals(
            PaymentMethodType.CARD.value,
            options.request.verificationOptions?.type
        )
    }

    @Test
    fun `verify uses third party verification options for non card type`() {
        val optionsSlot = slotForOptions()

        service.verify(mockDevice, thirdPartyParams(), locale = null, listener = RecordingListener())

        val options = assertIs<Options.VerifyPaymentConsentOptions>(optionsSlot.captured)
        assertEquals("alipaycn", options.request.verificationOptions?.type)
    }

    @Test
    fun `verify reports failure when operation fails`() {
        val captured = captureOperationListener()
        val listener = RecordingListener()
        val exception = AirwallexCheckoutException(message = "network error")

        service.verify(mockDevice, cardParams(), locale = null, listener = listener)
        captured.captured.onFailed(exception)

        val failure = assertIs<AirwallexPaymentStatus.Failure>(listener.status)
        assertEquals(exception, failure.exception)
    }

    @Test
    fun `verify reports success when next action is null`() {
        val captured = captureOperationListener()
        val listener = RecordingListener()

        service.verify(mockDevice, cardParams(), locale = null, listener = listener)
        captured.captured.onSuccess(consent(nextAction = null))

        val success = assertIs<AirwallexPaymentStatus.Success>(listener.status)
        assertEquals(testPaymentIntentId, success.paymentIntentId)
        assertEquals(testConsentId, success.consentId)
    }

    @Test
    fun `verify reports failure when next action present but provider missing`() {
        val captured = captureOperationListener()
        val listener = RecordingListener()

        service.verify(mockDevice, cardParams(), locale = null, listener = listener)
        every { AirwallexPlugins.getProvider(any<ActionComponentProviderType>()) } returns null
        captured.captured.onSuccess(consent(nextAction = mockk(relaxed = true)))

        val failure = assertIs<AirwallexPaymentStatus.Failure>(listener.status)
        assertIs<AirwallexCheckoutException>(failure.exception)
    }

    @Test
    fun `verify reports failure when card next action present but payment intent id missing`() {
        val captured = captureOperationListener()
        val listener = RecordingListener()

        service.verify(mockDevice, cardParams(), locale = null, listener = listener)
        captured.captured.onSuccess(consent(nextAction = mockk(relaxed = true), initialPaymentIntentId = null))

        val failure = assertIs<AirwallexPaymentStatus.Failure>(listener.status)
        assertIs<AirwallexCheckoutException>(failure.exception)
        verify(exactly = 0) { mockActionComponent.handlePaymentIntentResponse(any(), any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `verify delegates card next action to provider`() {
        val captured = captureOperationListener()
        val nextAction = mockk<NextAction>(relaxed = true)

        service.verify(mockDevice, cardParams(), locale = null, listener = RecordingListener())
        captured.captured.onSuccess(consent(nextAction = nextAction))

        verify {
            mockActionComponent.handlePaymentIntentResponse(
                testPaymentIntentId,
                nextAction,
                null,
                mockActivity,
                mockApplicationContext,
                any(),
                any(),
                testConsentId
            )
        }
    }

    @Test
    fun `verify delegates third party next action to provider with null intent`() {
        val captured = captureOperationListener()
        val nextAction = mockk<NextAction>(relaxed = true)

        service.verify(mockDevice, thirdPartyParams(), locale = null, listener = RecordingListener())
        captured.captured.onSuccess(consent(nextAction = nextAction))

        verify {
            mockActionComponent.handlePaymentIntentResponse(
                null,
                nextAction,
                null,
                mockActivity,
                mockApplicationContext,
                null,
                any(),
                testConsentId
            )
        }
    }
}
