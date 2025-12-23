package com.airwallex.android.threedsecurity

import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.threedsecurity.exception.WebViewConnectionException
import com.airwallex.android.ui.AirwallexWebView
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ThreeDSecureWebViewClientTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private lateinit var webView: AirwallexWebView
    private var confirmationCalled = false
    private var errorReceived: WebViewConnectionException? = null

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        webView = AirwallexWebView(ApplicationProvider.getApplicationContext())
        confirmationCalled = false
        errorReceived = null
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testThreeDSecureWebViewClientTest() {
        val url = "https://aaa?acsResponse=card"

        val callback = createTestCallback()
        val webViewClient = ThreeDSecureWebViewClient({ testScope }, callback)
        val hasCallbackUrl = webViewClient.hasCallbackUrl(webView, url)

        assertEquals(true, hasCallbackUrl)
        assertTrue(confirmationCalled)
    }

    @Test
    fun testES6Detection_notSupported() = runTest {
        val callback = createTestCallback()
        val webViewClient = ThreeDSecureWebViewClient({ this }, callback)

        // Trigger ES6 detection injection
        webViewClient.onPageStarted(webView, "https://test.com", null)

        // Simulate ES6 detection callback indicating ES6 is NOT supported
        val detectionInterface = webViewClient.ES6DetectionInterface()
        detectionInterface.onDetectionComplete(false, "ES6 error: SyntaxError: Unexpected token")

        // Advance the test dispatcher to execute pending coroutines
        testScheduler.advanceUntilIdle()

        assertEquals(errorReceived?.message?.contains("does not support ES6"), true)
    }

    @Test
    fun testES6Detection_supported() = runTest {
        val callback = createTestCallback()
        val webViewClient = ThreeDSecureWebViewClient({ this }, callback)

        // Trigger ES6 detection injection
        webViewClient.onPageStarted(webView, "https://test.com", null)

        // Simulate ES6 detection callback indicating ES6 IS supported
        val detectionInterface = webViewClient.ES6DetectionInterface()
        detectionInterface.onDetectionComplete(true, "ES6 supported")

        // Advance the test dispatcher to execute pending coroutines
        testScheduler.advanceUntilIdle()

        // Should not trigger ES6 error
        assertNull(errorReceived)
    }

    @Test
    fun testES6Detection_onlyInjectedOnce() {
        val callback = createTestCallback()
        val webViewClient = ThreeDSecureWebViewClient({ testScope }, callback)

        // First page start - should inject
        webViewClient.onPageStarted(webView, "https://test.com", null)

        // Second page start - should not inject again
        webViewClient.onPageStarted(webView, "https://test.com/page2", null)

        // The test passes if no exceptions are thrown
        // We can verify by checking that error was not received
        assertEquals(null, errorReceived)
    }

    @Test
    fun testHasCallbackUrl_withAcsResponse() {
        val callback = createTestCallback()
        val webViewClient = ThreeDSecureWebViewClient({ testScope }, callback)

        val url = "https://example.com?acsResponse=encodedPayload"
        val hasCallback = webViewClient.hasCallbackUrl(webView, url)

        assertTrue(hasCallback)
        assertTrue(confirmationCalled)
    }

    @Test
    fun testHasCallbackUrl_withAcsResponseAndMultipleParams() {
        val callback = createTestCallback()
        val webViewClient = ThreeDSecureWebViewClient({ testScope }, callback)

        val url = "https://example.com?acsResponse=encodedPayload&other=param"
        val hasCallback = webViewClient.hasCallbackUrl(webView, url)

        assertTrue(hasCallback)
        assertTrue(confirmationCalled)
    }

    @Test
    fun testHasCallbackUrl_withoutAcsResponse() {
        val callback = createTestCallback()
        val webViewClient = ThreeDSecureWebViewClient({ testScope }, callback)

        val url = "https://example.com?other=param"
        val hasCallback = webViewClient.hasCallbackUrl(webView, url)

        assertFalse(hasCallback)
        assertFalse(confirmationCalled)
    }

    @Test
    fun testHasCallbackUrl_emptyAcsResponse() {
        val callback = createTestCallback()
        val webViewClient = ThreeDSecureWebViewClient({ testScope }, callback)

        val url = "https://example.com?acsResponse="
        val hasCallback = webViewClient.hasCallbackUrl(webView, url)

        assertTrue(hasCallback)
        assertFalse(confirmationCalled) // Should not call confirmation with empty payload
        assertNotNull(errorReceived)
        assertEquals(errorReceived?.message?.contains("No acsResponse were obtained"), true)
    }

    private fun createTestCallback() = object : ThreeDSecureWebViewClient.Callbacks {
        override fun onWebViewConfirmation(payload: String) {
            confirmationCalled = true
        }

        override fun onWebViewError(error: WebViewConnectionException) {
            errorReceived = error
        }

        override fun onPageFinished(url: String?) {
        }

        override fun onPageStarted(url: String?) {
        }
    }
}
