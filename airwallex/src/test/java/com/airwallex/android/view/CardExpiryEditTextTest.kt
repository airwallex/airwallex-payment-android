package com.airwallex.android.view

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class CardExpiryEditTextTest {
    private lateinit var cardExpiryEditText: CardExpiryEditText

    @BeforeTest
    fun setup() {
        cardExpiryEditText =
            CardExpiryEditText(ApplicationProvider.getApplicationContext<Context>())
        cardExpiryEditText.setText("")
    }

    @Test
    fun expiryDateValueTest() {
        cardExpiryEditText.append("1")
        cardExpiryEditText.append("1")
        val text = cardExpiryEditText.text.toString()
        assertEquals("11/", text)

        cardExpiryEditText.setText("")
        cardExpiryEditText.append("4")
        assertEquals("04/", cardExpiryEditText.text.toString())

        cardExpiryEditText.setText("")
        cardExpiryEditText.append("1")
        assertEquals("1", cardExpiryEditText.text.toString())

        cardExpiryEditText.setText("")
        cardExpiryEditText.append("1")
        cardExpiryEditText.append("2")
        cardExpiryEditText.append("3")
        assertEquals("12/3", cardExpiryEditText.text.toString())
    }
}