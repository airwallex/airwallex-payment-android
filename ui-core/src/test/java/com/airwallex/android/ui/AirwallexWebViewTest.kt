package com.airwallex.android.ui

import android.content.Context
import android.webkit.WebSettings
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.core.AirwallexPlugins
import com.airwallex.android.core.util.BuildConfigHelper
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class AirwallexWebViewTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun airwallexWebViewTest() {
        val webView = AirwallexWebView(context, null)

        val settings = webView.settings
        assertEquals(true, settings.javaScriptEnabled)
        assertEquals(true, settings.domStorageEnabled)
        assertEquals(WebSettings.LOAD_NO_CACHE, settings.cacheMode)
        assertEquals("UTF-8", settings.defaultTextEncodingName)

        val userAgent = settings.userAgentString
        assertTrue(userAgent.endsWith("${AirwallexPlugins.AIRWALLEX_USER_AGENT}/${BuildConfigHelper.versionName}"))
    }
}
