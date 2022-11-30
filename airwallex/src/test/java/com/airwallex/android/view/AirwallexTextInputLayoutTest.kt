package com.airwallex.android.view

import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.R
import com.airwallex.android.view.inputs.AirwallexTextInputLayout
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class AirwallexTextInputLayoutTest {

    private val context = ContextThemeWrapper(
        ApplicationProvider.getApplicationContext(),
        R.style.AirwallexDefaultTheme
    )

    private val airwallexTextInputLayout: AirwallexTextInputLayout by lazy {
        AirwallexTextInputLayout(context, null)
    }

    @Test
    fun airwallexTextInputLayoutTest() {
        airwallexTextInputLayout.value = "123"
        assertEquals("123", airwallexTextInputLayout.value)
    }
}
