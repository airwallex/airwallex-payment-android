package com.airwallex.android.ui

import android.app.Activity
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(RobolectricTestRunner::class)
class AirwallexLocalePrefsTest {

    private val context = Robolectric.buildActivity(Activity::class.java)
        .setup()
        .get()
        .applicationContext

    @After
    fun tearDown() {
        AirwallexLocalePrefs.setLocaleTag(context, null)
    }

    @Test
    fun `locale tag round trips through preferences`() {
        AirwallexLocalePrefs.setLocaleTag(context, "fr-CA")

        assertEquals(Locale.CANADA_FRENCH, AirwallexLocalePrefs.getLocale(context))
    }

    @Test
    fun `null locale tag clears stored locale`() {
        AirwallexLocalePrefs.setLocaleTag(context, "fr-CA")
        AirwallexLocalePrefs.setLocaleTag(context, null)

        assertNull(AirwallexLocalePrefs.getLocale(context))
    }
}
