package com.airwallex.android.ui

import android.content.Context
import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AirwallexLocalePrefsTest {

    private val context = mockk<Context>()
    private val preferences = mockk<SharedPreferences>()
    private val editor = mockk<SharedPreferences.Editor>(relaxed = true)
    private var storedLocaleTag: String? = null

    @Before
    fun setUp() {
        storedLocaleTag = null
        every {
            context.getSharedPreferences("airwallex-locale-prefs", Context.MODE_PRIVATE)
        } returns preferences
        every { preferences.edit() } returns editor
        every { editor.putString("airwallex-locale-tag", any()) } answers {
            storedLocaleTag = secondArg()
            editor
        }
        every {
            preferences.getString("airwallex-locale-tag", null)
        } answers { storedLocaleTag }
    }

    @Test
    fun `locale tag round trips through preferences`() {
        AirwallexLocalePrefs.setLocaleTag(context, "fr-CA")

        assertEquals(Locale.CANADA_FRENCH, AirwallexLocalePrefs.getLocale(context))
        verify { editor.apply() }
    }

    @Test
    fun `null locale tag clears stored locale`() {
        AirwallexLocalePrefs.setLocaleTag(context, "fr-CA")
        AirwallexLocalePrefs.setLocaleTag(context, null)

        assertNull(AirwallexLocalePrefs.getLocale(context))
        verify(exactly = 2) { editor.apply() }
    }
}
