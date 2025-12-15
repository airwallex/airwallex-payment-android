package com.airwallex.android.threedsecurity

import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.threedsecurity.exception.WebViewConnectionException
import com.airwallex.android.ui.AirwallexWebView
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

    private lateinit var webView: AirwallexWebView
    private var confirmationCalled = false
    private var errorReceived: WebViewConnectionException? = null

    @Before
    fun setup() {
        webView = AirwallexWebView(ApplicationProvider.getApplicationContext())
        confirmationCalled = false
        errorReceived = null
    }

    @Test
    fun testThreeDSecureWebViewClientTest() {
        val url = "https://aaa?acsResponse=card"

        val callback = object : ThreeDSecureWebViewClient.Callbacks {
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

        val webViewClient = ThreeDSecureWebViewClient(callback)
        val hasCallbackUrl = webViewClient.hasCallbackUrl(webView, url)

        assertEquals(true, hasCallbackUrl)
        assertTrue(confirmationCalled)
    }

    @Test
    fun testES6ConsoleErrorDetection_arrowFunctionError() {
        val callback = createTestCallback()
        val webViewClient = ThreeDSecureWebViewClient(callback)

        webViewClient.onPageStarted(webView, "https://test.com", null)

        // Simulate console error for arrow function
        webView.consoleErrorCallback?.invoke(
            "Unexpected token =>",
            "https://test.com/script.js",
            10
        )

        assertNotNull(errorReceived)
        assertTrue(errorReceived?.message?.contains("ES6") == true)
        assertTrue(errorReceived?.message?.contains("syntax error detected") == true)
    }

    @Test
    fun testES6ConsoleErrorDetection_constError() {
        val callback = createTestCallback()
        val webViewClient = ThreeDSecureWebViewClient(callback)

        webViewClient.onPageStarted(webView, "https://test.com", null)

        // Simulate console error for const keyword
        webView.consoleErrorCallback?.invoke(
            "Unexpected reserved word: const",
            "https://test.com/script.js",
            5
        )

        assertNotNull(errorReceived)
        assertTrue(errorReceived?.message?.contains("ES6") == true)
    }

    @Test
    fun testES6ConsoleErrorDetection_syntaxError() {
        val callback = createTestCallback()
        val webViewClient = ThreeDSecureWebViewClient(callback)

        webViewClient.onPageStarted(webView, "https://test.com", null)

        // Simulate general syntax error
        webView.consoleErrorCallback?.invoke(
            "SyntaxError: Unexpected token",
            "https://test.com/script.js",
            15
        )

        assertNotNull(errorReceived)
        assertTrue(errorReceived?.message?.contains("ES6") == true)
    }

    @Test
    fun testES6ConsoleErrorDetection_nonES6Error() {
        val callback = createTestCallback()
        val webViewClient = ThreeDSecureWebViewClient(callback)

        webViewClient.onPageStarted(webView, "https://test.com", null)

        // Simulate non-ES6 error (should not trigger ES6 exception)
        webView.consoleErrorCallback?.invoke(
            "TypeError: Cannot read property 'foo' of undefined",
            "https://test.com/script.js",
            20
        )

        // Should not trigger ES6 error
        assertEquals(null, errorReceived)
    }

    @Test
    fun testHasCallbackUrl_withAcsResponse() {
        val callback = createTestCallback()
        val webViewClient = ThreeDSecureWebViewClient(callback)

        val url = "https://example.com?acsResponse=encodedPayload"
        val hasCallback = webViewClient.hasCallbackUrl(webView, url)

        assertTrue(hasCallback)
        assertTrue(confirmationCalled)
    }

    @Test
    fun testHasCallbackUrl_withAcsResponseAndMultipleParams() {
        val callback = createTestCallback()
        val webViewClient = ThreeDSecureWebViewClient(callback)

        val url = "https://example.com?acsResponse=encodedPayload&other=param"
        val hasCallback = webViewClient.hasCallbackUrl(webView, url)

        assertTrue(hasCallback)
        assertTrue(confirmationCalled)
    }

    @Test
    fun testHasCallbackUrl_withoutAcsResponse() {
        val callback = createTestCallback()
        val webViewClient = ThreeDSecureWebViewClient(callback)

        val url = "https://example.com?other=param"
        val hasCallback = webViewClient.hasCallbackUrl(webView, url)

        assertFalse(hasCallback)
        assertFalse(confirmationCalled)
    }

    @Test
    fun testHasCallbackUrl_emptyAcsResponse() {
        val callback = createTestCallback()
        val webViewClient = ThreeDSecureWebViewClient(callback)

        val url = "https://example.com?acsResponse="
        val hasCallback = webViewClient.hasCallbackUrl(webView, url)

        assertTrue(hasCallback)
        assertFalse(confirmationCalled) // Should not call confirmation with empty payload
        assertNotNull(errorReceived)
        assertTrue(errorReceived?.message?.contains("No acsResponse were obtained") == true)
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
