package com.airwallex.android.card

import android.webkit.WebView
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.card.exception.WebViewConnectionException
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexConfiguration
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.BeforeTest

@RunWith(RobolectricTestRunner::class)
class ThreeDSecureWebViewClientTest {

    private val webView = WebView(ApplicationProvider.getApplicationContext())

    @BeforeTest
    fun setup() {
        Airwallex.initialize(
            AirwallexConfiguration.Builder()
                .enableLogging(true)
                .setSupportComponentProviders(
                    listOf(
                        CardComponent.PROVIDER
                    )
                )
                .build()
        )
    }

    @Test
    fun threeDSecureWebViewClientTest() {
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

        Airwallex.initialize(
            AirwallexConfiguration.Builder()
                .enableLogging(true)
                .setSupportComponentProviders(
                    listOf(
                        CardComponent.PROVIDER
                    )
                )
                .build()
        )

        val webViewClient = ThreeDSecureWebViewClient(callback)
        val hasCallbackUrl = webViewClient.hasCallbackUrl(
            webView,
            url
        )
        assertEquals(true, hasCallbackUrl)
    }
}
