package com.airwallex.android.redirect.util

import android.net.Uri
import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.ui.R
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class RedirectUtilTest {

    private val context = ContextThemeWrapper(
        ApplicationProvider.getApplicationContext(),
        R.style.AirwallexDefaultTheme
    )

    @Test
    fun createRedirectIntentTest() {
        val uri = Uri.parse("http://www.google.com")
        val intent = RedirectUtil.createRedirectIntent(context, uri)

        assertEquals(
            "http://www.google.com",
            intent.data.toString()
        )
    }
}
