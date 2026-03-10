package com.airwallex.android.core.log

import android.content.Context
import android.content.pm.PackageManager
import com.airwallex.airtracker.Tracker
import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.AirwallexPlugins
import com.airwallex.android.core.AirwallexRecurringSession
import com.airwallex.android.core.AirwallexRecurringWithIntentSession
import com.airwallex.android.core.Environment
import com.airwallex.android.core.Session
import com.airwallex.android.core.TokenManager
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.extension.getAppName
import com.airwallex.android.core.extension.getAppVersion
import com.airwallex.android.core.model.AirwallexError
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentConsentOptions
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class AnalyticsLoggerTest {
    @MockK
    lateinit var context: Context

    @MockK
    lateinit var packageManager: PackageManager

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        mockkStatic(PackageManager::getAppName)
        mockkObject(AirwallexPlugins)
        mockkObject(TokenManager)

        every { context.packageName } returns "abc"
        every { packageManager.getAppName(any()) } returns "test_app"
        every { any<PackageManager>().getAppVersion(any()) } returns "1.0.1"
        every { context.packageManager } returns packageManager
        every { AirwallexPlugins.environment } returns Environment.PRODUCTION
        every { AirwallexPlugins.enableAnalytics } returns true
        every { TokenManager.accountId } returns null

        mockkConstructor(Tracker::class)
        every { anyConstructed<Tracker>().info(any(), any()) } just runs
        every { anyConstructed<Tracker>().error(any(), any()) } just runs

        resetAnalyticsLoggerState()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    private fun resetAnalyticsLoggerState() {
        listOf(
            "tracker", "paymentIntentId", "transactionMode",
            "launchType", "expressCheckout", "layout", "currentSession"
        ).forEach { fieldName ->
            runCatching {
                AnalyticsLogger.javaClass.getDeclaredField(fieldName).apply {
                    isAccessible = true
                    set(AnalyticsLogger, null)
                }
            }
        }
    }

    @Test
    fun `test initialization, page view, error and action logging`() {
        AnalyticsLogger.initialize(context)
        val additionalInfo = mapOf("key" to "value")

        AnalyticsLogger.logPageView("page_name", mapOf("key" to "value"))

        verify(exactly = 1) {
            anyConstructed<Tracker>().info(
                "page_name", mapOf("key" to "value", "eventType" to "page_view")
            )
        }

        AnalyticsLogger.logError("error_name", additionalInfo)

        verify(exactly = 1) {
            anyConstructed<Tracker>().error("error_name", mapOf("key" to "value"))
        }

        val exception = mockk<AirwallexException>(relaxed = true)
        AnalyticsLogger.logApiError("error_name", "http://example.com", exception)

        verify(exactly = 1) {
            anyConstructed<Tracker>().error(
                "error_name",
                mapOf("eventType" to "pa_api_request", "url" to "http://example.com")
            )
        }

        every { exception.error } returns AirwallexError("code")
        every { exception.message } returns "message"

        AnalyticsLogger.logError("error_name", exception = exception)

        verify(exactly = 1) {
            anyConstructed<Tracker>().error(
                "error_name",
                mapOf("code" to "code", "message" to "message")
            )
        }

        AnalyticsLogger.logAction("action_name")

        verify(exactly = 1) {
            anyConstructed<Tracker>().info("action_name", mapOf("eventType" to "action"))
        }

        AnalyticsLogger.logAction("action_name", additionalInfo)

        verify(exactly = 1) {
            anyConstructed<Tracker>().info(
                "action_name",
                mapOf("eventType" to "action", "key" to "value")
            )
        }

        AnalyticsLogger.updateAccountId("aid")
        verify(exactly = 1) {
            anyConstructed<Tracker>() setProperty "extraCommonData" value mapOf(
                "merchantAppName" to "test_app",
                "merchantAppVersion" to "1.0.1",
                "accountId" to "aid",
                "framework" to "android"
            )
        }

        AnalyticsLogger.logPaymentView("view_name", mapOf("key" to "value"))
        verify(exactly = 1) {
            anyConstructed<Tracker>().info(
                "view_name", mapOf("key" to "value", "eventType" to "payment_method_view")
            )
        }
    }

    // region setupSession tests

    @Test
    fun `setupSession with Session one-off payment sets correct session info`() {
        AnalyticsLogger.initialize(context)
        val session = Session(
            paymentIntent = null,
            paymentConsentOptions = null,
            currency = "USD",
            countryCode = "US",
            amount = BigDecimal.ONE,
            customerId = null,
            returnUrl = null
        )

        AnalyticsLogger.setupSession(session, AnalyticsLogger.LaunchType.HPP, AnalyticsLogger.Layout.TAB)
        AnalyticsLogger.logAction("test_action")

        verify {
            anyConstructed<Tracker>().info(
                "test_action",
                match { map ->
                    map["transactionMode"] == "oneoff" &&
                        map["launchType"] == AnalyticsLogger.LaunchType.HPP &&
                        map["layout"] == AnalyticsLogger.Layout.TAB &&
                        map["expressCheckout"] == false
                }
            )
        }
    }

    @Test
    fun `setupSession with Session recurring payment sets correct transaction mode`() {
        AnalyticsLogger.initialize(context)
        val session = Session(
            paymentIntent = null,
            paymentConsentOptions = PaymentConsentOptions(
                nextTriggeredBy = PaymentConsent.NextTriggeredBy.MERCHANT
            ),
            currency = "USD",
            countryCode = "US",
            amount = BigDecimal.ONE,
            customerId = "cust_123",
            returnUrl = null
        )

        AnalyticsLogger.setupSession(session, AnalyticsLogger.LaunchType.HPP, AnalyticsLogger.Layout.ACCORDION)
        AnalyticsLogger.logAction("test_action")

        verify {
            anyConstructed<Tracker>().info(
                "test_action",
                match { map ->
                    map["transactionMode"] == "recurring" &&
                        map["launchType"] == AnalyticsLogger.LaunchType.HPP &&
                        map["layout"] == AnalyticsLogger.Layout.ACCORDION
                }
            )
        }
    }

    @Suppress("DEPRECATION")
    @Test
    fun `setupSession with AirwallexPaymentSession sets one-off transaction mode`() {
        AnalyticsLogger.initialize(context)
        val session = AirwallexPaymentSession(
            paymentIntent = null,
            currency = "USD",
            countryCode = "US",
            amount = BigDecimal.ONE,
            customerId = null,
            returnUrl = null
        )

        AnalyticsLogger.setupSession(session, AnalyticsLogger.LaunchType.EMBEDDED)
        AnalyticsLogger.logAction("test_action")

        verify {
            anyConstructed<Tracker>().info(
                "test_action",
                match { map ->
                    map["transactionMode"] == "oneoff" &&
                        map["launchType"] == AnalyticsLogger.LaunchType.EMBEDDED &&
                        map["layout"] == null
                }
            )
        }
    }

    @Test
    fun `setupSession with AirwallexRecurringSession sets recurring transaction mode`() {
        AnalyticsLogger.initialize(context)
        val session = AirwallexRecurringSession(
            nextTriggerBy = PaymentConsent.NextTriggeredBy.MERCHANT,
            clientSecret = "test_secret",
            currency = "USD",
            countryCode = "US",
            amount = BigDecimal.ONE,
            customerId = "cust_123",
            returnUrl = null
        )

        AnalyticsLogger.setupSession(session, AnalyticsLogger.LaunchType.API)
        AnalyticsLogger.logAction("test_action")

        verify {
            anyConstructed<Tracker>().info(
                "test_action",
                match { map ->
                    map["transactionMode"] == "recurring" &&
                        map["launchType"] == AnalyticsLogger.LaunchType.API &&
                        map["layout"] == null
                }
            )
        }
    }

    @Test
    fun `setupSession with AirwallexRecurringWithIntentSession sets recurring transaction mode`() {
        AnalyticsLogger.initialize(context)
        val session = AirwallexRecurringWithIntentSession(
            paymentIntent = null,
            nextTriggerBy = PaymentConsent.NextTriggeredBy.CUSTOMER,
            currency = "USD",
            countryCode = "US",
            amount = BigDecimal.ONE,
            customerId = "cust_123",
            returnUrl = null
        )

        AnalyticsLogger.setupSession(session, AnalyticsLogger.LaunchType.HPP, AnalyticsLogger.Layout.TAB)
        AnalyticsLogger.logAction("test_action")

        verify {
            anyConstructed<Tracker>().info(
                "test_action",
                match { map ->
                    map["transactionMode"] == "recurring" &&
                        map["launchType"] == AnalyticsLogger.LaunchType.HPP &&
                        map["layout"] == AnalyticsLogger.Layout.TAB
                }
            )
        }
    }

    @Suppress("DEPRECATION")
    @Test
    fun `setupSession with null layout defaults to none`() {
        AnalyticsLogger.initialize(context)
        val session = AirwallexPaymentSession(
            paymentIntent = null,
            currency = "USD",
            countryCode = "US",
            amount = BigDecimal.ONE,
            customerId = null,
            returnUrl = null
        )

        AnalyticsLogger.setupSession(session, AnalyticsLogger.LaunchType.HPP, layout = null)
        AnalyticsLogger.logAction("test_action")

        verify {
            anyConstructed<Tracker>().info(
                "test_action",
                match { map -> map["layout"] == null }
            )
        }
    }

    // endregion

    // region isSessionSetup tests
    @Suppress("DEPRECATION")
    @Test
    fun `isSessionSetup returns false for different session instance and true for the right session`() {
        val session1 = AirwallexPaymentSession(
            paymentIntent = null,
            currency = "USD",
            countryCode = "US",
            amount = BigDecimal.ONE,
            customerId = null,
            returnUrl = null
        )
        val session2 = AirwallexPaymentSession(
            paymentIntent = null,
            currency = "USD",
            countryCode = "US",
            amount = BigDecimal.ONE,
            customerId = null,
            returnUrl = null
        )

        assertFalse(AnalyticsLogger.isSessionSetup(session1))
        AnalyticsLogger.setupSession(session1, AnalyticsLogger.LaunchType.HPP)
        assertTrue(AnalyticsLogger.isSessionSetup(session1))

        assertFalse(AnalyticsLogger.isSessionSetup(session2))
    }

    // endregion

    // region getLaunchType tests

    @Test
    fun `getLaunchType returns null before any session is set up`() {
        assertNull(AnalyticsLogger.getLaunchType())
    }

    @Test
    fun `getLaunchType returns the configured launch type after setup`() {
        val session = Session(
            paymentIntent = null,
            paymentConsentOptions = null,
            currency = "USD",
            countryCode = "US",
            amount = BigDecimal.ONE,
            customerId = null,
            returnUrl = null
        )

        AnalyticsLogger.setupSession(session, AnalyticsLogger.LaunchType.EMBEDDED)

        assertEquals(AnalyticsLogger.LaunchType.EMBEDDED, AnalyticsLogger.getLaunchType())
    }

    @Test
    fun `getLaunchType reflects the most recently configured launch type`() {
        val session1 = Session(
            paymentIntent = null,
            paymentConsentOptions = null,
            currency = "USD",
            countryCode = "US",
            amount = BigDecimal.ONE,
            customerId = null,
            returnUrl = null
        )
        val session2 = Session(
            paymentIntent = null,
            paymentConsentOptions = null,
            currency = "USD",
            countryCode = "US",
            amount = BigDecimal.ONE,
            customerId = null,
            returnUrl = null
        )

        AnalyticsLogger.setupSession(session1, AnalyticsLogger.LaunchType.HPP)
        AnalyticsLogger.setupSession(session2, AnalyticsLogger.LaunchType.API)

        assertEquals(AnalyticsLogger.LaunchType.API, AnalyticsLogger.getLaunchType())
    }

    // endregion
}
