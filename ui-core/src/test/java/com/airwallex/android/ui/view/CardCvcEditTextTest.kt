package com.airwallex.android.ui.view

import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.ui.R
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class CardCvcEditTextTest {

    private val context = ContextThemeWrapper(
        ApplicationProvider.getApplicationContext(),
        R.style.AirwallexDefaultTheme
    )

    private val cardCvcEditText: CardCvcEditText by lazy {
        CardCvcEditText(context)
    }

    @Test
    fun cvcValueTest() {
        assertNull(cardCvcEditText.cvcValue)
    }

    @Test
    fun isValidTest() {
        cardCvcEditText.setText("123")
        assertTrue(cardCvcEditText.isValid)

        cardCvcEditText.setText("1")
        assertFalse(cardCvcEditText.isValid)
    }
}
