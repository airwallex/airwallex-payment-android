package com.airwallex.android.core.log

import android.content.Context
import android.content.pm.PackageManager
import com.airwallex.airtracker.Tracker
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.extension.getAppName
import com.airwallex.android.core.extension.getAppVersion
import com.airwallex.android.core.model.AirwallexError
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test

class AnalyticsLoggerTest {
    @MockK
    lateinit var context: Context

    @MockK
    lateinit var packageManager: PackageManager

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        mockkStatic(PackageManager::getAppName)

        every { context.packageName } returns "abc"
        every { packageManager.getAppName(any()) } returns "test_app"
        every { any<PackageManager>().getAppVersion(any()) } returns "1.0.1"
        every { context.packageManager } returns packageManager

        mockkConstructor(Tracker::class)
        every { anyConstructed<Tracker>().info(any(), any()) } just runs
        every { anyConstructed<Tracker>().error(any(), any()) } just runs
    }

    @Test
    fun `test page view, error and action logging`() {
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
        AnalyticsLogger.logError("error_name", "http://example.com", exception)

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
                mapOf("eventType" to "pa_api_request", "code" to "code", "message" to "message")
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
                "accountId" to "aid"
            )
        }
    }
}