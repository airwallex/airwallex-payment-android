package com.airwallex.android.view

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class CardNumberEditTextTest {

    private lateinit var cardNumberEditText: CardNumberEditText

    @BeforeTest
    fun setup() {
        cardNumberEditText = CardNumberEditText(
            ApplicationProvider.getApplicationContext<Context>()
        )
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
