package com.airwallex.android.view

import androidx.test.core.app.ApplicationProvider
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class CardCvcEditTextTest {

    private val cardCvcEditText: CardCvcEditText by lazy {
        CardCvcEditText(ApplicationProvider.getApplicationContext())
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
