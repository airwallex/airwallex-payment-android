package com.airwallex.android.view

import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.R
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class CardCvcTextInputLayoutTest {

    private val context = ContextThemeWrapper(
        ApplicationProvider.getApplicationContext(),
        R.style.AirwallexDefaultTheme
    )

    private val cardCvcTextInputLayout: CardCvcTextInputLayout by lazy {
        CardCvcTextInputLayout(context, null)
    }

    @Test
    fun cvcValueTest() {
        assertNull(cardCvcTextInputLayout.cvcValue)
    }

    @Test
    fun isValidTest() {
        val cardCvcEditText = cardCvcTextInputLayout.findViewById<CardCvcEditText>(R.id.teInput)
        cardCvcEditText.setText("123")
        assertTrue(cardCvcEditText.isValid)

        cardCvcEditText.setText("1")
        assertFalse(cardCvcEditText.isValid)
    }
}
