package com.airwallex.android.redirect.util

import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.ui.R
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertNotEquals

@RunWith(RobolectricTestRunner::class)
class ThemeUtilTest {

    private val context = ContextThemeWrapper(
        ApplicationProvider.getApplicationContext(),
        R.style.AirwallexDefaultTheme
    )

    @Test
    fun getPrimaryThemeColorTest() {
        val color = ThemeUtil.getPrimaryThemeColor(context)
        assertNotEquals(0, color)
    }
}
