package com.airwallex.android.view

import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.R
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class CountryAutoCompleteViewTest {

    private val context = ContextThemeWrapper(
        ApplicationProvider.getApplicationContext(),
        R.style.AirwallexDefaultTheme
    )

    private val countryAutoCompleteView: CountryAutoCompleteView by lazy {
        CountryAutoCompleteView(context, null)
    }

    @Test
    fun countryValueTest() {
        countryAutoCompleteView.country = "CN"
        assertEquals("CN", countryAutoCompleteView.country)

        countryAutoCompleteView.country = "CA"
        assertEquals("CA", countryAutoCompleteView.country)
    }
}
