package com.airwallex.android.view

import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.R
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class CardNumberEditTextTest {

    private lateinit var cardNumberEditText: CardNumberEditText

    private val context = ContextThemeWrapper(
        ApplicationProvider.getApplicationContext(),
        R.style.AirwallexDefaultTheme
    )

    @BeforeTest
    fun setup() {
        cardNumberEditText = CardNumberEditText(context)
        cardNumberEditText.setText("")
    }

    @Test
    fun cardNumberValueTest() {
        cardNumberEditText.setText("4242 4242 4242 4242")
        assertTrue(cardNumberEditText.isCardNumberValid)

        cardNumberEditText.setText("4242424242424242")
        assertTrue(cardNumberEditText.isCardNumberValid)
    }

    @Test
    fun cardNumberFormatTest() {
        cardNumberEditText.setText("4242 4242 4242 4242")
        assertEquals("4242424242424242", cardNumberEditText.cardNumber)
    }
}
