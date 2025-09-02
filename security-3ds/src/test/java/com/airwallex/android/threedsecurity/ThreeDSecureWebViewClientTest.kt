package com.airwallex.android.threedsecurity

import android.webkit.WebView
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.threedsecurity.exception.WebViewConnectionException
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ThreeDSecureWebViewClientTest {

    private val webView = WebView(ApplicationProvider.getApplicationContext())

    @Test
    fun testThreeDSecureWebViewClientTest() {
        val url =
            "https://aaa?acsResponse=card"

        val callback = object : ThreeDSecureWebViewClient.Callbacks {

            override fun onWebViewConfirmation(payload: String) {
            }

            override fun onWebViewError(error: WebViewConnectionException) {
            }

            override fun onPageFinished(url: String?) {
            }

            override fun onPageStarted(url: String?) {
            }
        }

        val webViewClient = ThreeDSecureWebViewClient(callback)
        val hasCallbackUrl = webViewClient.hasCallbackUrl(
            webView,
            url
        )
        assertEquals(true, hasCallbackUrl)
    }
}
